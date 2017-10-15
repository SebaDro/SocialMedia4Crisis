angular.module('sm4cMonitoring')
  .controller('CollectionMapCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', '$interval', 'collectionService', 'esriRegistry', 'esriLoader', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, $interval, collectionService, esriRegistry, esriLoader) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
    var featureLayer = {};
    var featureLayerURL = 'https://services6.arcgis.com/RF3oqOe1dChQus9k/arcgis/rest/services/sm4c/FeatureServer/0';
    var facebookURL = 'https://www.facebook.com';

    $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/labeled').then(function(response) {
      $scope.documents = response.data;
    }, function(err) {
      console.warn(err);
    });

    var dates = [];
    var startDate = new Date();
    var endDate = startDate;
    // var promise;
    //
    // function updateTime() {
    //   console.log(new Date());
    // }
    // $scope.start = function() {
    //   $scope.stop();
    //   promise = $interval(updateTime, 1000);
    // };
    //
    // $scope.stop = function() {
    //   $interval.cancel(promise);
    // };
    //
    // $scope.$on('$destroy', function() {
    //   $scope.stop();
    // });
    //
    // $scope.start();

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

    function getDateTimeStringForPopup(date) {
      var year = date.getFullYear();
      var month = (((date.getMonth() + 1) < 10) ? '0' : '') + (date.getMonth() + 1);
      var day = ((date.getDate() < 10) ? '0' : '') + date.getDate();
      var hours = ((date.getHours() < 10) ? '0' : '') + date.getHours();
      var minutes = ((date.getMinutes() < 10) ? '0' : '') + date.getMinutes();
      var seconds = ((date.getSeconds() < 10) ? '0' : '') + date.getSeconds();
      var dateStr = day + "." + month + "." + year + ", " + hours + ":" + minutes + ":" + seconds + " Uhr";
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
      "esri/dijit/Popup",
      "esri/dijit/PopupTemplate",
      "dojo/domReady!"
    ], function(
      Map,
      arcgisPortal,
      arcgisUtils,
      Query,
      FeatureLayer,
      Extent,
      InfoTemplate,
      InfoWindow,
      Popup,
      PopupTemplate
    ) {
      console.log("Test");

      var customActions;

      var map = new Map("overviewMap", {
        basemap: "osm",
        center: [11.0, 51.5], // longitude, latitude
        zoom: 5,
        sliderStyle: 'small'
      });
      map.on("load", mapLoadHandler);
      map.infoWindow.resize(300, 200);

      map.infoWindow.on("selection-change", function() {
        map.infoWindow.removeActions(customActions);
        var selectedFeature = map.infoWindow.getSelectedFeature();
        var facebookAction = {
          title: "Öffne Facebook",
          className: "facebook-action",
          callback: function() {
            window.open(facebookURL + "/" + selectedFeature.attributes.messageid);
          }
        };
        customActions = map.infoWindow.addActions([facebookAction]);
      });

      var template = new PopupTemplate();
      template.setContent(getTextContent);

      function getDocById(id) {
        return function(doc) {
          return doc.id === id;
        }
      }

      function getTextContent(graphic) {
        var selectedDoc = $scope.documents.filter(getDocById(graphic.attributes.messageid));
        var timeString = getDateTimeStringForPopup(new Date(selectedDoc[0].creationTime));
        var popupContent = "<h3 class='title'>" + selectedDoc[0].facebookSource.name + "</h3><br><div>" + selectedDoc[0].content + "</div><br><div class='timeInfo'>" + timeString + "</div>";
        return popupContent;
      }

      function mapLoadHandler(evt) {
        var map = evt.map;
        map.on("click", onMapClick);
        featureLayer = new FeatureLayer(featureLayerURL, {
          mode: FeatureLayer.MODE_ONDEMAND,
          outFields: ["*"],
          infoTemplate: template
        });
        featureLayer.on("load", layerLoadHandler)
        featureLayer.setDefinitionExpression("collectionId = '" + $routeParams.id + "'");
        map.addLayer(featureLayer);

      }


      var onMapClick = function(evt) {
        var graphic = evt.graphic;
        // var docs =  $scope.documents;
        // var query = new Query();
        // query.geometry = pointToExtent(map, evt.mapPoint, 10);
        // // var deferred = featureLayer.selectFeatures(query,
        // //   FeatureLayer.SELECTION_NEW);
        // map.infoWindow.setTitle("Hochwasser-Helfer<br> Halle und Saalekreis");
        // map.infoWindow.setContent("<h4>An der Peißnitzbrücke werden noch dringend Helfer für das Befüllen von Sandsäcken gebraucht!!!</h4><br>06.06.2013 13:06 Uhr");
        // // map.infoWindow.setFeatures([deferred]);
        // map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.mapPoint));
        // map.infoWindow.set("anchor", "right");
      }

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
    });

    function updateSlider(dates) {
      $scope.slider.minValue = dates[0];
      $scope.slider.maxValue = dates[dates.length - 1];
      $scope.slider.value = dates[0];
      $scope.slider.options.stepsArray = dates;
      $scope.$broadcast('rzSliderForceRender');
    }

    $scope.relocateTo = function(target) {
      $interval.cancel();
      $location.path(target);
    };

  }]);
