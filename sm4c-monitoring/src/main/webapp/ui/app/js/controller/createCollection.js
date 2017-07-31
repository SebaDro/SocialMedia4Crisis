angular.module('sm4cMonitoring')
  .controller('CreateCollectionCtrl', ['$scope', '$mdDialog', '$http', function($scope, $mdDialog, $http) {
    self.readonly = false;
    $scope.newCollection = {
      tags: []
    };
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    /*
     * this method is used to display a confirmation dialog
     * before sending out the JSON payload to the kernel API
     */
    var displayConfirmationDialog = function() {
      $mdDialog.show({
          controller: DialogController,
          templateUrl: 'templates/partials/checkSourcesDialog.html',
          parent: angular.element(document.body),
          clickOutsideToClose: true,
          // locals: {
          //   tasks: [payload]
          // }
        })
        .then(function(confirmed) {
          if (confirmed) {
            console.info('POSTing confirmed');
            // postToKernel(payload);
          }
        }, function() {
          console.info('POSTing cancelled');
        });
    };

    /*
     * controller used in the above dialog
     */
    var DialogController = ['$scope', '$mdDialog', function DialogController($scope, $mdDialog) {
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

    var displayLoadingDialog = function() {
      $mdDialog.show({
          controller: LoadingDialogController,
          templateUrl: 'templates/partials/loadingSourcesDialog.html',
          parent: angular.element(document.body),
          clickOutsideToClose: false,
          // locals: {
          //   tasks: [payload]
          // }
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
      // $scope.confirm = function() {
      //   $mdDialog.hide(true);
      // };
    }];

    $scope.submit = function() {
      if (!$scope.newCollection.name || !$scope.newCollection.description) {
        return
      }
      displayConfirmationDialog();

      console.log($scope.newCollection.name);
    };

    $scope.reset = function() {
      $scope.newCollection = {
        name: "",
        description: "",
        tags: []
      };
    };

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

    $scope.checkSources = function() {
      if (!$scope.groups) {
        return false;
      } else {
        return true;
      }
    }

    $scope.loadSources = function() {
      var groupsLoaded = false;
      var pagesLoaded = false;
      displayLoadingDialog();
      var queryTags = "";
      $scope.newCollection.tags.forEach(function(element) {
        queryTags = queryTags + element +"+";
      });
      queryTags = queryTags.slice(0, -1);
      $http.get(rootURL + '/facebook/groups/' + queryTags + '/json')
        .then(function success(response) {
          groupsLoaded = true;
          $scope.groups = response.data;
          $scope.selectedGroups = $scope.groups.slice();
          if (pagesLoaded) {
            $mdDialog.hide(true);
          }
        }, function fail(response) {
          $mdDialog.cancel();
        });
      $http.get(rootURL + '/facebook/pages/' + queryTags + '+hilfe')
        .then(function success(response) {
          pagesLoaded = true;
          $scope.pages = response.data;
          $scope.selectedPages = $scope.pages.slice();
          if (groupsLoaded) {
            $mdDialog.hide(true);
          }
        }, function fail(response) {
          $mdDialog.cancel();
        });
    }
  }]);
