/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.classifier.geotag;

import de.hsbo.fbg.sm4c.common.model.Location;

/**
 *
 * @author Sebastian Drost
 */
public interface GeoTagger {
    public Location geocode(String address);
}
