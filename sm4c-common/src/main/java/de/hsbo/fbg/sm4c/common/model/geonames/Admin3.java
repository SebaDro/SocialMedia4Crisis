/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model.geonames;

import de.hsbo.fbg.sm4c.common.model.AbstractEntity;

/**
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class Admin3 extends GeonameEntity {

    private String admin3_id;
    private Admin2 admin2;
    private Admin1 admin1;

    public Admin3() {

    }

    public String getAdmin3_id() {
        return admin3_id;
    }

    public void setAdmin3_id(String admin3_id) {
        this.admin3_id = admin3_id;
    }

    public Admin2 getAdmin2() {
        return admin2;
    }

    public void setAdmin2(Admin2 admin2) {
        this.admin2 = admin2;
    }

    public Admin1 getAdmin1() {
        return admin1;
    }

    public void setAdmin1(Admin1 admin1) {
        this.admin1 = admin1;
    }

    @Override
    public GeonameEntity getParent() {
        if (this.getAdmin2().getName().equals("Unknown")) {
            return this.getAdmin1();
        } else {
            return this.getAdmin2();
        }
    }

}
