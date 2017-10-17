/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.geotag.geocoding;

import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface GeoTagger {
    public Location geocode(String address);
    
    public List<Location> geocode(List<String> addresses);
}
