/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.geotag;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorAttribute;
import com.esri.arcgisruntime.tasks.geocode.LocatorInfo;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Seba
 */
public class ArcGISGeoTagger implements GeoTagger {

    private final String locatorURL = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
    private final LocatorTask locator;
    private Runnable doneListener;

    public ArcGISGeoTagger() {
        this.locator = new LocatorTask(locatorURL);

    }

    public void addDoneListener(Runnable doneListener) {
        this.doneListener = doneListener;
    }

    @Override
    public Location geocode(String address) {

//        this.locator.addDoneLoadingListener(() -> {
//            if (this.locator.getLoadStatus() == LoadStatus.LOADED) {
//                // Get LocatorInfo from a loaded LocatorTask
//                LocatorInfo locatorInfo = this.locator.getLocatorInfo();
//                List<String> resultAttributeNames = new ArrayList();
//
//                // Loop through all the attributes available
//                for (LocatorAttribute resultAttribute : locatorInfo.getResultAttributes()) {
//                    resultAttributeNames.add(resultAttribute.getDisplayName());
//                    // Use in adapter etc...
//                }
//                resultAttributeNames.size();
//            } else {
//
//            }
//        });
//        this.locator.loadAsync();
        GeocodeParameters params = new GeocodeParameters();
        params.setCountryCode("de");
        params.setMaxResults(0);
        List<String> resultAttributeNames = params.getResultAttributeNames();
        resultAttributeNames.add("Place_addr");
        resultAttributeNames.add("Match_addr");
        resultAttributeNames.add("extent");
        final ListenableFuture<List<GeocodeResult>> geocodeFuture = this.locator.geocodeAsync(address,params);

        geocodeFuture.addDoneListener(() -> {
            try {
                // Get the results of the async operation
                List<GeocodeResult> geocodeResults = geocodeFuture.get();

                if (geocodeResults.size() > 0) {
                    // Use the first result - for example display in an existing Graphics Overlay
                    GeocodeResult topResult = geocodeResults.get(0);
                    Map<String, Object> attributes = topResult.getAttributes();
                    double score = topResult.getScore();
                    Point p = topResult.getInputLocation();
                    attributes.size();
                }
                this.notify();
            } catch (InterruptedException | ExecutionException e) {
                // ... deal with exception appropriately
            }
        });
        try {
            geocodeFuture.wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArcGISGeoTagger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
