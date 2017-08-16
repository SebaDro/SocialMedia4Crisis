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
public class Model extends AbstractEntity implements Serializable{
    
    private String path;
    private EvaluationResult evaluation;
    
    public Model(){
        
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public EvaluationResult getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationResult evaluation) {
        this.evaluation = evaluation;
    }

}
