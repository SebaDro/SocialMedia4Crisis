angular.module('sm4cMonitoring')
  .controller('CollectionDetailsCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', function($scope, $http, $location, $routeParams, $mdDialog) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    $http.get(rootURL + '/collections/'+$routeParams.id).then(function(response) {
      $scope.collection = response.data;
      $scope.groups = $scope.collection.facebookSources.filter(function(value){
        return value.sourceType.name === "group"
      });
      $scope.selectedGroups = new Array();
      $scope.groups.forEach(function(element) {
        $scope.selectedGroups.push(element);
      });
      $scope.pages = $scope.collection.facebookSources.filter(function(value){
        return value.sourceType.name === "page"
      });
      $scope.selectedPages = new Array();
      $scope.pages.forEach(function(element) {
        $scope.selectedPages.push(element);
      });
    }, function(err) {
      console.warn(err);
    });

    $scope.checkDocuments = function() {
      if($scope.collection.documentCount === 0){
        displayInitialCollectionDialog();
      }
    }

    var displayInitialCollectionDialog = function() {
      $mdDialog.show({
          controller: LoadingDialogController,
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

    var LoadingDialogController = ['$scope', '$mdDialog', function LoadingDialogController($scope, $mdDialog) {
      $scope.hide = function() {
        $mdDialog.hide();
      };
      $scope.cancel = function() {
        $mdDialog.cancel();
      };
      $scope.confirm = function() {
        $mdDialog.hide(true);
      };
    }];

    // /*
    //  * this method is used to display a confirmation dialog
    //  * before sending out the JSON payload to the kernel API
    //  */
    // var displayIntitalCollectionDialog = function() {
    //   $mdDialog.show({
    //       controller: DialogController,
    //       templateUrl: 'templates/partials/checkSourcesDialog.html',
    //       parent: angular.element(document.body),
    //       clickOutsideToClose: true,
    //       // locals: {
    //       //   tasks: [payload]
    //       // }
    //     })
    //     .then(function(confirmed) {
    //       if (confirmed) {
    //         console.info('POSTing confirmed');
    //         // postToKernel(payload);
    //       }
    //     }, function() {
    //       console.info('POSTing cancelled');
    //     });
    // };
    //
    // /*
    //  * controller used in the above dialog
    //  */
    // var DialogController = ['$scope', '$mdDialog', function DialogController($scope, $mdDialog) {
    //   $scope.hide = function() {
    //     $mdDialog.hide();
    //   };
    //
    //   $scope.cancel = function() {
    //     $mdDialog.cancel();
    //   };
    //
    //   $scope.confirm = function() {
    //     $mdDialog.hide(true);
    //   };
    // }];

    $scope.toggle = function(item, list) {
      var idx = list.indexOf(item);
      if (idx > -1) {
        list.splice(idx, 1);
      } else {
        list.push(item);
      }
    };

    $scope.exists = function(item, list) {
      return list.indexOf(item) > -1;
    };
  }]);
