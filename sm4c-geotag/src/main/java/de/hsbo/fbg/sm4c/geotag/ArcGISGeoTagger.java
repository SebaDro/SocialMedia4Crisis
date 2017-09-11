///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.hsbo.fbg.sm4c.geotag;
//
//import com.esri.arcgisruntime.concurrent.ListenableFuture;
//import com.esri.arcgisruntime.geometry.Point;
//import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
//import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
//import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
//import de.hsbo.fbg.sm4c.common.model.Location;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
///**
// *
// * @author Seba
// */
//public class ArcGISGeoTagger {
//
//    private static final Logger LOGGER = LogManager.getLogger(ArcGISGeoTagger.class);
//
//    private final String locatorURL = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
//    private final LocatorTask locator;
//    private Runnable doneListener;
//
//    public ArcGISGeoTagger() {
//        this.locator = new LocatorTask(locatorURL);
//
//    }
//
//    public void geocode(String address) {
//        Location resultLocation = null;
//        GeocodeParameters params = new GeocodeParameters();
//        params.setCountryCode("de");
//        params.setMaxResults(5);
//        List<String> resultAttributeNames = params.getResultAttributeNames();
//        resultAttributeNames.add("Place_addr");
//        resultAttributeNames.add("Match_addr");
//        resultAttributeNames.add("extent");
//        final ListenableFuture<List<GeocodeResult>> geocodeFuture = this.locator.geocodeAsync(address, params);
//        geocodeFuture.addDoneListener(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    List<GeocodeResult> geocodeResults = geocodeFuture.get();
//                    if (geocodeResults.size() > 0) {
//                        GeocodeResult topResult = geocodeResults.get(0);
//                        String place_addr = (String) topResult.getAttributes().get("Place_addr");
//                        String match_addr = (String) topResult.getAttributes().get("Match_addr");
//                        double score = topResult.getScore();
//                        Point p = topResult.getDisplayLocation();
//                        Location loc = new Location();
//                        loc.setLatitude(p.getX());
//                        loc.setLongitude(p.getY());
//                        LOGGER.info("Found location x(" + loc.getLatitude() + ")"
//                                + " (" + loc.getLongitude() + ")");
//                    }
//                } catch (InterruptedException ex) {
//                    LOGGER.error("Geocoding was interrupted", ex);
//                } catch (ExecutionException ex) {
//                    LOGGER.error("There occured an error during geocoding", ex);
//                }
//            }
//        });
//    }
//
//    public List<Location> geocode(List<String> addresses) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
