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
public class Geoname extends GeonameEntity {

    private Admin1 admin1;
    private Admin2 admin2;
    private Admin3 admin3;
    private Admin4 admin4;

    public Geoname() {

    }

    public Admin1 getAdmin1() {
        return admin1;
    }

    public void setAdmin1(Admin1 admin1) {
        this.admin1 = admin1;
    }

    public Admin2 getAdmin2() {
        return admin2;
    }

    public void setAdmin2(Admin2 admin2) {
        this.admin2 = admin2;
    }

    public Admin3 getAdmin3() {
        return admin3;
    }

    public void setAdmin3(Admin3 admin3) {
        this.admin3 = admin3;
    }

    public Admin4 getAdmin4() {
        return admin4;
    }

    public void setAdmin4(Admin4 admin4) {
        this.admin4 = admin4;
    }

    @Override
    public GeonameEntity getParent() {
        if (this.getAdmin4() != null) {
            return this.getAdmin4();
        } else if (this.getAdmin3() != null) {
            return this.getAdmin3();
        } else if (this.getAdmin2() != null) {
            return this.getAdmin2();
        } else {
            return this.getAdmin1();
        }
    }

}
