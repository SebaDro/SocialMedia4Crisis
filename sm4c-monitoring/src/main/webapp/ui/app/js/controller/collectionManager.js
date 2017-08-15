angular.module('sm4cMonitoring')
  .controller('CollectionDetailsCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast) {
      var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
      $scope.timeDefinition = {};

      $http.get(rootURL + '/collections/' + $routeParams.id).then(function(response) {
        $scope.collection = response.data;
        $scope.groups = $scope.collection.facebookSources.filter(function(value) {
          return value.sourceType.name === "group"
        });
        $scope.selectedGroups = new Array();
        $scope.groups.forEach(function(element) {
          $scope.selectedGroups.push(element);
        });
        $scope.pages = $scope.collection.facebookSources.filter(function(value) {
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
        if ($scope.collection.documentCount === 0) {
          displayInitialCollectionDialog();
        }
        displayInitialCollectionDialog();
      }

      $scope.timeChange = function() {
        console.log($scope.collection.startDate);
      }


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
          if (!$scope.startDate||!$scope.endDate){
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
      }]);
