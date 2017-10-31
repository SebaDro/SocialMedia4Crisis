angular.module('sm4cMonitoring')
  .controller('CollectionTrainerCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', 'collectionService', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, collectionService) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    $scope.currentIndex = 0;
    $scope.collection = collectionService.getCollection();
    $scope.timeDefinition = {};

    $http.get(rootURL + '/collections/' + $routeParams.id + '/model').then(function(response) {
      // $scope.trainingData = response.data;
      // console.log($scope.trainingData.length);
      $scope.collection.modelSummary = response.data;
    }, function(err) {
      console.warn(err);
      loadTrainingData();
    });

    var loadTrainingData = function() {
      $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/training').then(function(response) {
        // $scope.trainingData = response.data;
        // console.log($scope.trainingData.length);
        $scope.trainingDataSize = response.data.length;
      }, function(err) {
        console.warn(err);
      });
    }


    $scope.checkTrainingDataSize = function() {
      if (!$scope.trainingDataSize > 0) {
        return true;
      } else {
        return false;
      }
    }

    $scope.checkTrainingSize = function() {
      if (!$scope.trainingSize > 0) {
        return true;
      } else {
        return false;
      }
    }

    $scope.createModel = function() {
      displayLoadingDialog("Model wird trainiert...");
      var payload = "SVM";
      $http.post(rootURL + '/collections/' + $routeParams.id + '/model', payload)
        .then(function success(response) {
          $mdDialog.hide(true);
          $scope.collection.modelSummary = response.data;

          var toast = $mdToast.simple()
            .textContent('Das Model zur Kategorisierung wurde erfolgreich trainiert.')
            .action('Schliessen')
            .highlightAction(false)
            .highlightClass('md-primary')
            .hideDelay(0)
            .position('bottom right');
          $mdToast.show(toast);
        }, function fail(response) {
          console.log(response);
          $mdDialog.cancel();
        });
    };

    $scope.updateModel = function() {
      displayLoadingDialog("Model wird aktualisiert...");
      var payload = "SVM";
      $http.put(rootURL + '/collections/' + $routeParams.id + '/model', payload)
        .then(function success(response) {
          $mdDialog.hide(true);
          $scope.collection.modelSummary = response.data;

          var toast = $mdToast.simple()
            .textContent('Das Model zur Kategorisierung wurde erfolgreich aktualisiert.')
            .action('Schliessen')
            .highlightAction(false)
            .highlightClass('md-primary')
            .hideDelay(0)
            .position('bottom right');
          $mdToast.show(toast);
        }, function fail(response) {
          console.log(response);
            $mdDialog.cancel();
        });
    };


    $scope.loadTrainingData = function() {

      displayLoadingDialog("Trainingsdaten werden abgerufen...");
      $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/unlabeled/limit/' + $scope.trainingSize).then(function(response) {
        $mdDialog.hide(true);
        $scope.documents = response.data;
        $scope.maxIndex = $scope.documents.length - 1;
      }, function(err) {
        console.warn(err);
        $mdDialog.cancel();
      });
    }

    var displayLoadingDialog = function(dialogContent) {

      $mdDialog.show({
          locals: {
            text: dialogContent
          },
          controller: LoadingDialogController,
          templateUrl: 'templates/dialogs/loadingSourcesDialog.html',
          parent: angular.element(document.body),
          clickOutsideToClose: false,
        })
        .then(function(confirmed) {
          if (confirmed) {

          }
        }, function() {
          console.info('POSTing cancelled');
        });
    };

    var LoadingDialogController = ['$scope', '$mdDialog', 'text', function LoadingDialogController($scope, $mdDialog, text) {
      $scope.loadingDialogContent = text;
      $scope.hide = function() {
        $mdDialog.hide();
      };
      $scope.cancel = function() {
        $mdDialog.cancel();
      };
    }];

    $scope.previous = function() {
      $scope.documents[$scope.currentIndex].label = $scope.classifiedLabel;
      if ($scope.currentIndex === 0) {
        return;
      } else {
        $scope.currentIndex = $scope.currentIndex - 1;
        $scope.classifiedLabel = $scope.documents[$scope.currentIndex].label;
      }
    }

    $scope.next = function(doc) {
      $scope.documents[$scope.currentIndex].label = $scope.classifiedLabel;
      if ($scope.currentIndex === $scope.maxIndex) {
        return;
      } else {
        $scope.currentIndex = $scope.currentIndex + 1;
        $scope.classifiedLabel = $scope.documents[$scope.currentIndex].label;
      }
    }



  }]);
