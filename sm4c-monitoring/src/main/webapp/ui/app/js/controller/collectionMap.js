angular.module('sm4cMonitoring')
  .controller('CollectionMapCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', 'collectionService', 'esriRegistry', 'esriLoader', 'rzModule', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, collectionService, esriRegistry, esriLoader, rzModule) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
    var featureLayer = {};
    $scope.priceSlider = 150;

    $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/training').then(function(response) {
      $scope.documents = new Array();
      $scope.documents[0] = response.data[0];
      $scope.documents[1] = response.data[1];
      $scope.documents[2] = response.data[2];

    }, function(err) {
      console.warn(err);
    });

    $scope.timeSliderChanged = function(){

    }

    console.log("Test");
    esriLoader.require([
      "esri/map",
      "esri/arcgis/Portal",
      "esri/arcgis/utils",
      "esri/layers/FeatureLayer",
      "dojo/domReady!"
    ], function(
      Map,
      arcgisPortal,
      arcgisUtils,
      FeatureLayer
    ) {
      console.log("Test");

      $scope.test = function(){
          featureLayer.setDefinitionExpression("creation < date '2013-06-03 20:00:00'");
      }

      // var createMapOptions = {
      //   mapOptions: {
      //     slider: true
      //   },
      //   ignorePopups: true
      // };
      //
      // arcgisUtils.createMap("0102f0594f3849959ae97101d1c4dfa0", "overviewMap", createMapOptions).then(function(response) {
      //   var map = response.map;
      //   // var layers = arcgisUtils.getLayerList(response);
      //   var featureLayer = map.getLayer(map.layerIds[0]);
      //   featureLayer.setDefinitionExpression("messageId = '457286994363580_457550961003850'");
      //   // var element = angular.element( document.querySelector( '#timeSliderDiv' ) );
      //   // var timeSlider = new TimeSlider({
      //   //   style: "width: 100%;"
      //   // },"timeSliderDiv");
      //   //
      //   //
      //   // // var timeSlider = new TimeSlider({}, document.getElementById("timeSliderDiv"));
      //   // map.setTimeSlider(timeSlider);
      //   // timeSlider.setThumbCount(1);
      //   // timeSlider.createTimeStopsByTimeInterval(layers[0].layer.timeInfo.timeExtent, 1, 'esriTimeUnitsHours');
      //   // timeSlider.startup();
      //
      //   console.log(layers);
      // });
      //
      // // //instantiate a new map as soon as the container that shoul hold the map has been loaded
      // // $scope.onMapContainerReady = function() {
      // //   console.log("Map loaded");
      // //   map.on("load", mapLoadHandler, mapElement);
      // // };

      var map = new Map("overviewMap", {
        basemap: "osm",
        center: [11.0, 51.5], // longitude, latitude
        zoom: 5,
        sliderStyle: 'small'
      });
      map.on("load", mapLoadHandler);

      function mapLoadHandler(evt) {
        var map = evt.map;
        featureLayer = new FeatureLayer("https://services6.arcgis.com/RF3oqOe1dChQus9k/arcgis/rest/services/sm4c/FeatureServer/0?token=xGdxjbjRohPYuq16fCTdQTMMa1CfNjERRyhiQWGTySCWYGabOslY9oMY_XE-xn9mUtzPrsOks7mGz_CfH6YK6vguAVnosnMiuJj6HtSH4OXE6VZ8DRcbL3JRirKDtd-52wVG96gg9BHg1V7fa_yh54S-wWxKx_M6XRlQpG0sdxwy3t9xiPFBECzf5ZMF3qsMXtyL_MVwgj7w1UE8rMcw1OORe23ZFes_-Dpf91Xza7A.");
        featureLayer.setDefinitionExpression("creation < date '2013-06-02 20:00:00'");
        map.addLayer(featureLayer);

        console.log("Map loaded");
      }
    });

    $scope.relocateTo = function(target) {
      $location.path(target);
    };

  }]);
