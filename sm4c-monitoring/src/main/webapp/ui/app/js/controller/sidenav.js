angular.module('sm4cMonitoring')
  .controller('SidenavCtrl', ['$scope', '$mdSidenav', '$log', '$location', function($scope, $mdSidenav, $log, $location) {
    $scope.close = function() {
      // Component lookup should always be available since we are not using `ng-if`
      $mdSidenav('left').close()
        .then(function() {
          $log.debug("close LEFT is done");
        });

    };
    $scope.relocateTo = function(target) {
      $location.path(target);
    };
  }]);
