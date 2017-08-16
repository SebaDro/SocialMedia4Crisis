angular.module('sm4cMonitoring')
  .controller('CollectionManagerCtrl', ['$scope',  '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', 'collectionService', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, collectionService) {


var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
    $http.get(rootURL + '/collections/' + $routeParams.id).then(function(response) {
      collectionService.setCollection(response.data);
      $scope.collection = collectionService.getCollection();
    }, function(err) {
      console.warn(err);
    });



    $scope.onTrainerSelected = function(){
      if ($scope.collection.documentCount === 0) {
          displayInitialCollectionDialog();
        }
    };

    var displayInitialCollectionDialog = function() {
      $mdDialog.show({
          controller: InitialCollectionDialogController,
          templateUrl: 'templates/dialogs/initialCollectionDialog.html',
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

    var InitialCollectionDialogController = ['$scope', '$mdDialog', function LoadingDialogController($scope, $mdDialog) {
      $scope.hide = function() {
        $mdDialog.hide();
      };
      $scope.cancel = function() {
        $mdDialog.cancel();
      };
      $scope.confirm = function() {
        if (!$scope.startDate || !$scope.endDate) {
          return;
        }
        var isoStart = $scope.startDate.toISOString();
        var isoEnd = $scope.endDate.toISOString();
        var payload = {
          startTime: isoStart,
          endTime: isoEnd
        };
        $mdDialog.hide(true);
        collectDocumentPost(payload);
        displayLoadingDialog();
      };
    }];

    var collectDocumentPost = function(payload) {
      $http.post(rootURL + '/collections/' + $routeParams.id + '/documents', payload)
        .then(function(response) {
          $mdDialog.hide(true);
          $scope.collection.documentCount = response.data;

          var toast = $mdToast.simple()
            .textContent('Es wurden ' + $scope.collection.documentCount + ' Nachrichten erfolgreich abgerufen.')
            .action('Schliessen')
            .highlightAction(false)
            .highlightClass('md-primary')
            .hideDelay(0)
            .position('bottom right');
          $mdToast.show(toast);
        }, function(error) {
          $mdDialog.cancel();
        });
    };

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
      $scope.loadingDialogContent = "Facebook Nachrichten werden ermittelt...";
      $scope.hide = function() {
        $mdDialog.hide();
      };
      $scope.cancel = function() {
        $mdDialog.cancel();
      };
    }];


  }]);
