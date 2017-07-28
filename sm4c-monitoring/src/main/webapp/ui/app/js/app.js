// Declare app level module which depends on views, and components
var sm4cMonitoring = angular.module('sm4cMonitoring', [
  'ngRoute',
  'ngMaterial'
]);

sm4cMonitoring.config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  $routeProvider
    .when('/overview', {
      templateUrl: 'templates/view/overview.html'
    })
    .when('/create', {
      templateUrl: 'templates/view/createCollection.html',
    })
    .when('/collection', {
      templateUrl: 'templates/view/collectionDetails.html',
    }).otherwise({
      redirectTo: '/overview'
    });
}]);
