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
public class Admin1 extends GeonameEntity {

    private String admin1_id;

    public Admin1() {

    }

    public String getAdmin1_id() {
        return admin1_id;
    }

    public void setAdmin1_id(String admin1_id) {
        this.admin1_id = admin1_id;
    }
//
//    public String getAdmin02() {
//        return admin02;
//    }
//
//    public void setAdmin02(String admin02) {
//        this.admin02 = admin02;
//    }
//
//    public String getAdmin03() {
//        return admin03;
//    }
//
//    public void setAdmin03(String admin03) {
//        this.admin03 = admin03;
//    }
//
//    public String getAdmin04() {
//        return admin04;
//    }
//
//    public void setAdmin04(String admin04) {
//        this.admin04 = admin04;
//    }

    @Override
    public GeonameEntity getParent() {
        return null;
    }

}
