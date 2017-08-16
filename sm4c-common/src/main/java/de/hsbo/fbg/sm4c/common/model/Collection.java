/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Sebastian Drost
 */
public class Collection extends AbstractEntity implements Serializable {

    private String name;
    private String description;
    private Date creation;
    private CollectionStatus status;
//    private CollectionStatusType status;
    private Set<Label> labels;
    private Set<Keyword> keywords;
    private Set<SocialMediaService> services;
    private Set<FacebookSource> sources;
    private Model model;

    public Collection() {

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

//    public CollectionStatusType getStatus() {
//        return status;
//    }
//
//    public void setStatus(CollectionStatusType status) {
//        this.status = status;
//    }
    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public Set<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Set<SocialMediaService> getServices() {
        return services;
    }

    public void setServices(Set<SocialMediaService> services) {
        this.services = services;
    }

    public Set<FacebookSource> getSources() {
        return sources;
    }

    public void setSources(Set<FacebookSource> sources) {
        this.sources = sources;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
