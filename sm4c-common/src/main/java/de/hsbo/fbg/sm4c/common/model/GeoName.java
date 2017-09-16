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
public class GeoName extends AbstractEntity {

    private String name;
    private double latitude;
    private double longitude;
    private String featureClass;
    private String featureCode;
    private String admin01;
    private String admin02;
    private String admin03;
    private String admin04;
    
    public GeoName(){
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFeatureClass() {
        return featureClass;
    }

    public void setFeatureClass(String featureClass) {
        this.featureClass = featureClass;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getAdmin01() {
        return admin01;
    }

    public void setAdmin01(String admin01) {
        this.admin01 = admin01;
    }

    public String getAdmin02() {
        return admin02;
    }

    public void setAdmin02(String admin02) {
        this.admin02 = admin02;
    }

    public String getAdmin03() {
        return admin03;
    }

    public void setAdmin03(String admin03) {
        this.admin03 = admin03;
    }

    public String getAdmin04() {
        return admin04;
    }

    public void setAdmin04(String admin04) {
        this.admin04 = admin04;
    }
    
    

}
