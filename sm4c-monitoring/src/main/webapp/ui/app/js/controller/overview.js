angular.module('sm4cMonitoring')
  .controller('OverviewCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    $http.get(rootURL + '/collections').then(function(response) {
      $scope.collections = response.data;
    }, function(err) {
      console.warn(err);
    });

    $scope.isActive = function(collection){
      if(collection.status==="aktiv"){
        return true;
      }
      else{
        return false;
      }
    }
    $scope.relocateTo = function(target) {
      $location.path(target);
    };
  }]);
