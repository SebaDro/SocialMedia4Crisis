/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag;

import com.esri.core.geometry.Point;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public class ArcGISGeoCoder {

    private static final int MAX_RESULTS = 1;
    private static final String COUNTRY_CODE = "DE";

    private Locator locator;
    private LocatorFindParameters findParameters;

    public ArcGISGeoCoder() {
        this.locator = Locator.createOnlineLocator();
        findParameters = new LocatorFindParameters("");
        findParameters.setMaxLocations(MAX_RESULTS);
        findParameters.setSourceCountry(COUNTRY_CODE);
        List<String> fields = new ArrayList();
        fields.add("*");     
        findParameters.setOutFields(fields);
        
    }

    public Location findLocation(String toponym) throws Exception {
        this.findParameters.setText(toponym);
        List<LocatorGeocodeResult> results = locator.find(findParameters);
        LocatorGeocodeResult topResult = results.get(0);
        Point locationPoint = topResult.getLocation();
        Location location = new Location();
        location.setLatitude(locationPoint.getX());
        location.setLongitude(locationPoint.getY());
        return location;
    }

}
