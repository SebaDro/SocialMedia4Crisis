/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.view;

import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookSourceView {

    public FacebookSourceView() {

    }

    private String facebookId;
    private String name;
    private String description;
    private SourceTypeView sourceType;
    private List<LocationView> location;

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

    public SourceTypeView getSourceType() {
        return sourceType;
    }

    public void setSourceCategory(SourceTypeView sourceType) {
        this.sourceType = sourceType;
    }

    public List<LocationView> getLocation() {
        return location;
    }

    public void setLocation(List<LocationView> location) {
        this.location = location;
    }

}
