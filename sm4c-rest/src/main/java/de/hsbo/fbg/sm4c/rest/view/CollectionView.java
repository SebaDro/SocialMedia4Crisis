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
public class CollectionView {

    public CollectionView() {

    }

    private long id;
    private String name;
    private String description; 
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss Z")
    private Date creation;
    private String status;
    private List<String> labels;
    private List<String> keywords;
    private List<String> services;
    private List<FacebookSourceView> facebookSources;
    private long documentCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<FacebookSourceView> getFacebookSources() {
        return facebookSources;
    }

    public void setFacebookSources(List<FacebookSourceView> facebookSources) {
        this.facebookSources = facebookSources;
    }
    
    public long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(long documentCount) {
        this.documentCount = documentCount;
    }

}
