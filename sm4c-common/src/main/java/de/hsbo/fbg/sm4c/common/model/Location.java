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
public class Location {
    private double longitude;
    private double latitude;
    
    public Location(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    
}