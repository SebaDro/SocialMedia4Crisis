angular.module('sm4cMonitoring')
  .controller('HeaderCtrl', ['$scope', 'sidenavService', function($scope, sidenavService) {
    $scope.toggleMenu = function() {
      //service is required to reach beyoung the scope of this controller
      sidenavService.toggleSideNav();
    };
  }]);
