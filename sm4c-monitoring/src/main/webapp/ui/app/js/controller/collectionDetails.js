angular.module('sm4cMonitoring')
  .controller('CollectionDetailsCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';

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

    // $scope.checkDocuments = function() {
    //   if ($scope.collection.documentCount === 0) {
    //     displayInitialCollectionDialog();
    //   }
    //   // displayInitialCollectionDialog();
    // }
    //
    // $scope.timeChange = function() {
    //   console.log($scope.collection.startDate);
    // }


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
