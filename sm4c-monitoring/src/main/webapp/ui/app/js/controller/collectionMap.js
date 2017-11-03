angular.module('sm4cMonitoring')
  .controller('CollectionMapCtrl', ['$scope', '$http', '$location', '$routeParams', '$mdDialog', '$mdToast', '$filter', '$interval', '$window', '$mdMedia', '$timeout', 'collectionService', 'esriRegistry', 'esriLoader', function($scope, $http, $location, $routeParams, $mdDialog, $mdToast, $filter, $interval, $window, $mdMedia, $timeout, collectionService, esriRegistry, esriLoader) {
    var rootURL = 'http://localhost:8080/sm4c-monitoring/rest';
    var featureLayer = {};
    var featureLayerURL = 'https://services6.arcgis.com/RF3oqOe1dChQus9k/arcgis/rest/services/sm4c/FeatureServer/0';
    var facebookURL = 'https://www.facebook.com';

    var dates = [];
    var startDate = new Date();
    var endDate = startDate;

    loadDocuments();

    var flag = false;
    if (!$mdMedia('gt-md')) {
      flag = true;
    }

    angular.element($window).on('resize', function() {
      if (!$mdMedia('gt-md') && flag == true) {
        refreshSlider();
        flag = false;
      }
      if ($mdMedia('gt-md') && flag == false) {
        refreshSlider();
        flag = true;
      }
    });

    function refreshSlider() {
      $timeout(function() {
        console.log("Refreshed");
        $scope.$broadcast('rzSliderForceRender');
      }, 500);
    };



    function updateMessages() {
      console.log("Refresh documents and features")
      loadDocuments();
      $scope.featureLayer.refresh();
    }

    var promise;

    $scope.start = function() {
      $scope.stop();
      promise = $interval(updateMessages, 30000);
    };

    $scope.stop = function() {
      $interval.cancel(promise);
    };

    $scope.$on('$destroy', function() {
      $scope.stop();
    });

    $scope.start();


    function loadDocuments() {
      $http.get(rootURL + '/collections/' + $routeParams.id + '/documents/labeled').then(function(response) {
        $scope.documents = response.data;
      }, function(err) {
        console.warn(err);
      });
    }

    initSlider(getDates(startDate, endDate));



    //     $scope.$$postDigest(function () {
    //     $scope.$broadcast('rzSliderForceRender');
    // });

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
          showTicks: false,
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
      $scope.timeDef.startTime = $scope.slider.minValue;
      $scope.timeDef.endTime = $scope.slider.maxValue;
      loadDocuments();
      $scope.featureLayer.setTimeDefinition($scope.timeDef);
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
      "esri/TimeExtent",
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
      PopupTemplate,
      TimeExtent
    ) {
      console.log("Test");

      var customActions;
      $scope.timeDef = new TimeExtent();

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
        $scope.featureLayer = new FeatureLayer(featureLayerURL, {
          mode: FeatureLayer.MODE_SNAPSHOT,
          outFields: ["*"],
          infoTemplate: template
        });
        $scope.featureLayer.on("load", layerLoadHandler)
        $scope.featureLayer.setDefinitionExpression("collectionId = '" + $routeParams.id + "'");
        map.addLayer($scope.featureLayer);
      }


      var onMapClick = function(evt) {
        var graphic = evt.graphic;
      }

      function layerLoadHandler(evt) {
        var layer = evt.layer;
        // var timeDef = evt.layer.timeInfo.timeExtent;
        // var startTime = evt.layer.timeInfo.timeExtent.startTime;
        // var endTime = evt.layer.timeInfo.timeExtent.endTime;
        var startTime = new Date(2013, 5, 1)
        var endTime = new Date(2013, 5, 30)
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
