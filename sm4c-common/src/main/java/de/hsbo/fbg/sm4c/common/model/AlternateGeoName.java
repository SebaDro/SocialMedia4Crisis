/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.common.model;

/**
 *
 * @author Sebastian Drost
 */
public class AlternateGeoName {
    
    String name;
    GeoName geoName;
    
    public AlternateGeoName(){
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoName getGeoName() {
        return geoName;
    }

    public void setGeoName(GeoName geoName) {
        this.geoName = geoName;
    }

    
}
