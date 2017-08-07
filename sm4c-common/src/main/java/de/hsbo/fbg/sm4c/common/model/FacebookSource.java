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
public class FacebookSource extends AbstractEntity implements Serializable {

    private long id;
    private String facebookId;
    private String name;
    private String description;
    private SourceType type;

    public FacebookSource() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public SourceCategoryType getType() {
//        return type;
//    }
//
//    public void setType(SourceCategoryType type) {
//        this.type = type;
//    }
    public SourceType getType() {
        return type;
    }

    public void setType(SourceType type) {
        this.type = type;
    }
}
