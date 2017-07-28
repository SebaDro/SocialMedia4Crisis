angular.module('sm4cMonitoring')
  .controller('CreateCollectionCtrl', ['$scope', function($scope) {
    self.readonly = false;
    $scope.newCollection = {
      tags : []
    };

    $scope.submit = function() {
      console.log($scope.newCollection.name);
    };

    $scope.reset = function() {
      $scope.newCollection = {
        name: "",
        description: "",
        tags: []
      };
    };
  }]);
