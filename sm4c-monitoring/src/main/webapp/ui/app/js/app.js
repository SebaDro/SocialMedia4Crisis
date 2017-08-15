// Declare app level module which depends on views, and components
var sm4cMonitoring = angular.module('sm4cMonitoring', [
  'ngRoute',
  'ngMaterial',
  'ngMessages'
]);

sm4cMonitoring.config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  $routeProvider
    .when('/overview', {
      templateUrl: 'templates/view/overview.html'
    })
    .when('/create', {
      templateUrl: 'templates/view/createCollection.html',
    })
    .when('/collection/:id', {
      templateUrl: 'templates/view/collectionManager.html',
    }).when('/collection/:id/details', {
      templateUrl: 'templates/partials/collectionDetails.html',
    }).when('/collection/:id/trainer', {
      templateUrl: 'templates/partials/collectionTrainer.html',
    }).when('/collection/:id/map', {
      templateUrl: 'templates/partials/collectionMap.html',
    }).otherwise({
      redirectTo: '/overview'
    });
}]);

sm4cMonitoring.service('collectionService', function() {
  var collection = {};

  var setCollection = function(col) {
      collection = col;
  };

  var getCollection = function(){
      return collection;
  };

  return {
    setCollection: setCollection,
    getCollection: getCollection
  };

});


sm4cMonitoring.config(function($mdThemingProvider) {
  $mdThemingProvider.definePalette('sm4cPalette', {
    '50': '#f3e0e0',
    '100': '#e0b3b3',
    '200': '#cc8080',
    '300': '#b84d4d',
    '400': '#a82626',
    '500': '#990000',
    '600': '#910000',
    '700': '#860000',
    '800': '#7c0000',
    '900': '#6b0000',
    'A100': '#ff9a9a',
    'A200': '#ff6767',
    'A400': '#ff3434',
    'A700': '#ff1a1a',
    'contrastDefaultColor': 'light', // whether, by default, text (contrast)
    // on this palette should be dark or light
    'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
      '200', '300', '400', 'A100'
    ],
    'contrastLightColors': undefined // could also specify this if default was 'dark'
  });
  $mdThemingProvider.theme('default')
    .primaryPalette('sm4cPalette')
    .accentPalette('sm4cPalette')
});
