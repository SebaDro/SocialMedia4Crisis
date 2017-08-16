angular.module('sm4cMonitoring')
  .controller('CollectionTrainerCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', 'collectionService',  function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, collectionService) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    $scope.collection = collectionService.getCollection();

    $scope.currentIndex = 0;

    $scope.timeDefinition = {};
    $scope.sliderChanged = function() {
      console.log($scope.trainingSize);
    };

    $scope.checkTrainingSize = function() {
      if (!$scope.trainingSize > 0) {
        return true;
      } else {
        return false;
      }
    }

    $scope.loadTrainingData = function() {
      displayLoadingDialog();
      $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/limit/' + $scope.trainingSize).then(function(response) {
        $mdDialog.hide(true);
        $scope.documents = response.data;
        $scope.maxIndex = $scope.documents.length - 1;
      }, function(err) {
        console.warn(err);
        $mdDialog.cancel();
      });
    }

    var displayLoadingDialog = function() {
      $mdDialog.show({
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

    var LoadingDialogController = ['$scope', '$mdDialog', function LoadingDialogController($scope, $mdDialog) {
      $scope.loadingDialogContent = "Trainingsdaten werden abgerufen...";
      $scope.hide = function() {
        $mdDialog.hide();
      };
      $scope.cancel = function() {
        $mdDialog.cancel();
      };
    }];

    $scope.previous = function() {
      $scope.documents[$scope.currentIndex].label=$scope.classifiedLabel;
      if ($scope.currentIndex === 0) {
        return;
      } else {
        $scope.currentIndex = $scope.currentIndex - 1;
        $scope.classifiedLabel=$scope.documents[$scope.currentIndex].label;
      }
    }

    $scope.next = function(doc) {
      $scope.documents[$scope.currentIndex].label=$scope.classifiedLabel;
      if ($scope.currentIndex === $scope.maxIndex) {
        return;
      } else {
        $scope.currentIndex = $scope.currentIndex + 1;
        $scope.classifiedLabel=$scope.documents[$scope.currentIndex].label;
      }
    }



  }]);