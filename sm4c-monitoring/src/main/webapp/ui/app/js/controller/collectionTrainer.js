angular.module('sm4cMonitoring')
  .controller('CollectionTrainerCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', 'collectionService', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, collectionService) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

    $scope.collection= collectionService.getCollection();

    $scope.timeDefinition = {};
    $scope.sliderChanged = function() {
      console.log($scope.trainingSize);
    };

    $scope.checkTrainingSize = function(){
      if (!$scope.trainingSize>0){
        return true;
      }
      else{
        return false;
      }
    }

    $scope.loadTrainingData = function(){
      $http.get(rootURL + '/collections/'+$routeParams.id+'/documents/limit/'+  $scope.trainingSize).then(function(response) {
        $scope.documents = response.data;
      }, function(err) {
        console.warn(err);
      });

    }




  }]);
