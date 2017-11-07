/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class MessageDocumentView {

    private String id;
    private String content;
    private String label;
    private String service;
    private List<LocationView> locations;
    private boolean training;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date creationTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date updateTime;

    private String type;
    private FacebookSourceView facebookSource;

    public MessageDocumentView() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public boolean getTraining() {
        return training;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FacebookSourceView getFacebookSource() {
        return facebookSource;
    }

    public void setFacebookSource(FacebookSourceView facebookSource) {
        this.facebookSource = facebookSource;
    }

    public List<LocationView> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationView> locations) {
        this.locations = locations;
    }

}
