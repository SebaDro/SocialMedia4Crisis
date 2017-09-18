/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model.geonames;

import de.hsbo.fbg.sm4c.common.model.AbstractEntity;

/**
 *
 * @author Sebastian Drost
 */
public class Admin2 extends GeonameEntity {

    private String admin2_id;

    private Admin1 admin1;

    public Admin2() {

    }

    public String getAdmin2_id() {
        return admin2_id;
    }

    public void setAdmin2_id(String admin2_id) {
        this.admin2_id = admin2_id;
    }

    public Admin1 getAdmin1() {
        return admin1;
    }

    public void setAdmin1(Admin1 admin1) {
        this.admin1 = admin1;
    }

    @Override
    public GeonameEntity getParent() {
        return this.getAdmin1();
    }

}
