/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.geotag;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Seba
 */
public class ArcGISGeoTagger implements GeoTagger {
    
   private static final Logger LOGGER = LogManager.getLogger(ArcGISGeoTagger.class);

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
        Location resultLocation = null;
        GeocodeParameters params = new GeocodeParameters();
        params.setCountryCode("de");
        params.setMaxResults(0);
        List<String> resultAttributeNames = params.getResultAttributeNames();
        resultAttributeNames.add("Place_addr");
        resultAttributeNames.add("Match_addr");
        resultAttributeNames.add("extent");
        final ListenableFuture<List<GeocodeResult>> geocodeFuture = this.locator.geocodeAsync(address, params);
        try {
            List<GeocodeResult> geocodeResults = geocodeFuture.get();
            if (geocodeResults.size() > 0) {
                // Use the first result - for example display in an existing Graphics Overlay
                GeocodeResult topResult = geocodeResults.get(0);
                double score = topResult.getScore();
                Point p = topResult.getDisplayLocation();
                resultLocation = new Location(p.getX(), p.getY());
            }
        } catch (InterruptedException ex) {
            LOGGER.error("Geocoding was interrupted", ex);
        } catch (ExecutionException ex) {
            LOGGER.error("There occured an error during geocoding", ex);
        }
        return resultLocation;
    }

    @Override
    public List<Location> geocode(List<String> addresses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
