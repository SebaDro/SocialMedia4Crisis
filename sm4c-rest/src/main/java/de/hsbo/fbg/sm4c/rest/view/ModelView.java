/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.rest.view;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 *
 * @author Sebastian Drost
 */
public class ModelView {
    @JsonRawValue
    private String summary;
    @JsonRawValue
    private String classDetails;
    @JsonRawValue
    private String confusionMatrix;
    
    public ModelView(){
        
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getClassDetails() {
        return classDetails;
    }

    public void setClassDetails(String classDetails) {
        this.classDetails = classDetails;
    }

    public String getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(String confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }
    
    
}
