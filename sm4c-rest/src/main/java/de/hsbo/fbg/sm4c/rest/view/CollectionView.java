/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.view;

import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class CollectionView {

    public CollectionView() {

    }

    private String name;
    private String description;
    private DateTime creation;
    private String collectionStatus;
    private List<String> labels;
    private List<String> keywords;
    private List<String> services;
    private List<FacebookSourceView> facebookSources;

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

    public DateTime getCreation() {
        return creation;
    }

    public void setCreation(DateTime creation) {
        this.creation = creation;
    }

    public String getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
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

}
