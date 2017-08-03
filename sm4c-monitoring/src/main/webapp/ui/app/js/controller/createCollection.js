angular.module('sm4cMonitoring')
  .controller('CreateCollectionCtrl', ['$scope', '$mdDialog', '$http', '$mdToast','$location', function($scope, $mdDialog, $http, $mdToast, $location) {
      self.readonly = false;
      $scope.newCollection = {
        tags: [],
        labels: []
      };
      var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

      var toast = $mdToast.simple()
        .textContent("Collection konnte nicht angelegt werden")
        .action('Schliessen')
        .highlightAction(false)
        .highlightClass('md-primary')
        .hideDelay(0)
        .position('bottom right');

      $scope.submit = function(ev) {
        if (!$scope.newCollection.name || !$scope.newCollection.description) {
          return
        }
        if ($scope.newCollection.tags.length === 0) {
          toast.textContent("Geht nicht");
          $mdToast.show(toast);
          return;
        }
        showConfirm(ev);
      };

      $scope.reset = function() {
        $scope.newCollection = {
          name: "",
          description: "",
          labels: [],
          tags: [],
          fbCb: true,
          facebookSources: []
        };
        $scope.groups = null;
        $scope.selectedGroups = null;
        $scope.pages = null;
        $scope.selectedPages = null;
      };

      var createPayload = function() {
        var sources = new Array();
        $scope.selectedGroups.forEach(function(element) {
          sources.push(element);
        });
        $scope.selectedPages.forEach(function(element) {
          sources.push(element);
        });
        var payload = {
          name: $scope.newCollection.name,
          description: $scope.newCollection.description,
          collectionStatus: "gestoppt",
          labels: $scope.newCollection.labels,
          keywords: $scope.newCollection.tags,
          services: ["Facebook"],
          facebookSources: sources
        };
        return (payload);
      }



      var showConfirm = function(ev) {
        // Appending dialog to document.body to cover sidenav in docs app
        var confirm = $mdDialog.confirm()
          .title('Collection anlegen?')
          .textContent('Möchten Sie die Collection wirklich anlegen oder noch einmal überprüfen?')
          .ariaLabel('Lucky day')
          .targetEvent(ev)
          .ok('Anlegen')
          .cancel('Überprüfen');

        $mdDialog.show(confirm).then(function() {
            $scope.status = 'Collection wird angelegt.';
            var payload = createPayload();
            $http.post(rootURL + '/collections', payload)
              .then(function(response) {
                var toast = $mdToast.simple()
                  .textContent('Collection erfolgreich angelegt!')
                  .action('Collections anzeigen')
                  .highlightAction(false)
                  .highlightClass('md-primary')
                  .hideDelay(5000)
                  .position('bottom right');

                $mdToast.show(toast).then(function(result) {
                  if (result === 'ok') {
                    //go the overview page
                    $location.path('/overview');
                  }
                });

                $scope.reset();
              }, function(err) {
                console.warn(err);
                var toast = $mdToast.simple()
                  .textContent('Auftrag konnte nicht angelegt werden!')
                  .action('Schliessen')
                  .highlightAction(false)
                  .highlightClass('md-primary')
                  .hideDelay(0)
                  .position('bottom right');

                $mdToast.show(toast);
              });
          });
        };

        // /*
        //  * this method is used to display a confirmation dialog
        //  * before sending out the JSON payload to the kernel API
        //  */
        // var displayConfirmationDialog = function() {
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
          var payload = new Array();
          $scope.newCollection.tags.forEach(function(element) {
            var obj = {};
            obj['name'] = element;
            payload.push(obj);
          });
          $http.post(rootURL + '/facebook/groups', payload)
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
          $http.post(rootURL + '/facebook/pages', payload)
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
