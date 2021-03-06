angular.module('sm4cMonitoring')
  .controller('MapCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', 'collectionService', 'esriRegistry', 'esriLoader', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, collectionService, esriRegistry, esriLoader) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
    var featureLayer = {};
    var featureLayerURL = 'https://services6.arcgis.com/RF3oqOe1dChQus9k/arcgis/rest/services/sm4c/FeatureServer/0';

    var dates = [];
    var startDate = new Date();
    var endDate = startDate;

    initSlider(getDates(startDate, endDate));

    this.refreshSlider = function() {
      $timeout(function() {
        $scope.$broadcast('rzSliderForceRender');
      });
    };

    Date.prototype.addDays = function(days) {
      var date = new Date(this.valueOf());
      date.setDate(date.getDate() + days);
      return date;
    }

    Date.prototype.addHours = function(hours) {
      var date = new Date(this.valueOf());
      date.setTime(date.getTime() + (hours * 60 * 60 * 1000));
      return date;
    }

    function getDates(startDate, endDate) {
      var dateArray = new Array();
      var currentDate = startDate;
      currentDate = currentDate
      while (currentDate < endDate) {
        dateArray.push(new Date(currentDate))
        currentDate = currentDate.addHours(6);
      }
      dateArray.push(endDate);
      return dateArray;
    }

    function getDateTimeString(date) {
      var year = date.getFullYear();
      var month = (((date.getMonth() + 1) < 10) ? '0' : '') + (date.getMonth() + 1);
      var day = ((date.getDate() < 10) ? '0' : '') + date.getDate();
      var hours = ((date.getHours() < 10) ? '0' : '') + date.getHours();
      var minutes = ((date.getMinutes() < 10) ? '0' : '') + date.getMinutes();
      var seconds = ((date.getSeconds() < 10) ? '0' : '') + date.getSeconds();
      var dateStr = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
      return dateStr;
    }

    function initSlider(dates) {
      $scope.slider = {
        minValue: dates[0],
        maxValue: dates[dates.length - 1],
        value: dates[0],
        options: {
          stepsArray: dates,
          noSwitching: true,
          onEnd: timeIntervalChanged,
          showTicks: true,
          disabled: true,
          translate: function(date) {
            if (date != null)
              return date.toLocaleString();
            return '';
          }
        }
      };
    }

    // $http.get(rootURL + '/collections/34/documents/training').then(function(response) {
    //   $scope.documents = new Array();
    //   $scope.documents[0] = response.data[0];
    //   $scope.documents[1] = response.data[1];
    //   $scope.documents[2] = response.data[2];
    //
    // }, function(err) {
    //   console.warn(err);
    // });

    function timeIntervalChanged() {
      var minDateStr = getDateTimeString($scope.slider.minValue);
      var maxDateStr = getDateTimeString($scope.slider.maxValue);
      var expr = "creation >= date '" + minDateStr + "' AND creation <= date '" + maxDateStr + "'";
      featureLayer.setDefinitionExpression(expr);
      // featureLayer.setDefinitionExpression("creation < date '2013-06-02 20:00:00'");
    }

    $scope.test = function() {
      featureLayer.setDefinitionExpression("creation < date '2013-06-03 20:00:00.000Z' AND");
    }

    esriLoader.require([
      "esri/map",
      "esri/arcgis/Portal",
      "esri/arcgis/utils",
      "esri/tasks/query",
      "esri/layers/FeatureLayer",
      "esri/geometry/Extent",
      "esri/InfoTemplate",
      "esri/dijit/InfoWindow",
      "dojo/domReady!"
    ], function(
      Map,
      arcgisPortal,
      arcgisUtils,
      Query,
      FeatureLayer,
      Extent,
      InfoTemplate,
      InfoWindow
    ) {
      console.log("Test");



      var map = new Map("overviewMap", {
        basemap: "osm",
        center: [11.0, 51.5], // longitude, latitude
        zoom: 5,
        sliderStyle: 'small'
      });
      map.on("load", mapLoadHandler);

      function mapLoadHandler(evt) {
        var map = evt.map;
        map.on("click", onMapClick);
        featureLayer = new FeatureLayer(featureLayerURL);
        featureLayer.on("load", layerLoadHandler)
        map.addLayer(featureLayer);

        console.log("Map loaded");

        function onMapClick(evt) {
          var query = new Query();
          query.geometry = pointToExtent(map, evt.mapPoint, 10);
          // var deferred = featureLayer.selectFeatures(query,
          //   FeatureLayer.SELECTION_NEW);
          map.infoWindow.setTitle("Hochwasser-Helfer<br> Halle und Saalekreis");
          map.infoWindow.setContent("<h4>An der Peißnitzbrücke werden noch dringend Helfer für das Befüllen von Sandsäcken gebraucht!!!</h4><br>06.06.2013 13:06 Uhr");
          // map.infoWindow.setFeatures([deferred]);
          map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.mapPoint));
          map.infoWindow.set("anchor", "right");

        }

        function pointToExtent(map, point, toleranceInPixel) {
          var pixelWidth = map.extent.getWidth() / map.width;
          var toleranceInMapCoords = toleranceInPixel * pixelWidth;
          return new Extent(point.x - toleranceInMapCoords,
            point.y - toleranceInMapCoords,
            point.x + toleranceInMapCoords,
            point.y + toleranceInMapCoords,
            map.spatialReference);
        }

      }
    });



    function layerLoadHandler(evt) {
      var layer = evt.layer;
      var timeDef = evt.layer.timeInfo.timeExtent;
      var startTime = evt.layer.timeInfo.timeExtent.startTime;
      // var endTime = evt.layer.timeInfo.timeExtent.endTime;
      var endTime = new Date(2013, 5, 4)
      dates = getDates(startTime, endTime);
      $scope.slider.options.disabled = false;
      updateSlider(dates);
      $scope.$broadcast('rzSliderForceRender');
      console.log("Layer loaded");
    }

    function updateSlider(dates) {
      $scope.slider.minValue = dates[0];
      $scope.slider.maxValue = dates[dates.length - 1];
      $scope.slider.value = dates[0];
      $scope.slider.options.stepsArray = dates;
      $scope.$broadcast('rzSliderForceRender');
    }

    $scope.relocateTo = function(target) {
      $location.path(target);
    };

  }]);
