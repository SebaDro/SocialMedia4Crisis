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

    $scope.startCollecting = function(collection){
      $http.post(rootURL + '/collections/'+collection.id+'/start').then(function(response) {
        collection.status = "aktiv";
      }, function(err) {
        console.warn(err);
      });

    }


    $scope.stopCollecting = function(collection){
      $http.post(rootURL + '/collections/'+collection.id+'/stop').then(function(response) {
        collection.status = "gestoppt";
      }, function(err) {
        console.warn(err);
      });

    }

    $scope.relocateTo = function(target) {
      $location.path(target);
    };
  }]);
