/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model;

import java.io.Serializable;

/**
 *
 * @author Sebastian Drost
 */
public class SocialMediaService extends AbstractEntity implements Serializable {

    private long id;
//    private SocialMediaServiceType name;
    private String name;

    public SocialMediaService() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public SocialMediaServiceType getName() {
//        return name;
//    }
//
//    public void setName(SocialMediaServiceType name) {
//        this.name = name;
//    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
