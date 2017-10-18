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
public class Admin4 extends GeonameEntity {

    private String admin4_id;
    private Admin3 admin3;

    public Admin4() {

    }

    public String getAdmin4_id() {
        return admin4_id;
    }

    public void setAdmin4_id(String admin4_id) {
        this.admin4_id = admin4_id;
    }

    public Admin3 getAdmin3() {
        return admin3;
    }

    public void setAdmin3(Admin3 admin3) {
        this.admin3 = admin3;
    }

    @Override
    public GeonameEntity getParent() {
        return this.getAdmin3();
    }

}
