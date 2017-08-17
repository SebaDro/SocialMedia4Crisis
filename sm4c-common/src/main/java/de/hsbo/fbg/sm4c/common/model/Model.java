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
public class Model extends AbstractEntity implements Serializable {

    private String classifierPath;
    private String inputDataPath;
    private Classifier classifier;
    private EvaluationResult evaluation;

    public Model() {

    }

    public String getClassifierPath() {
        return classifierPath;
    }

    public void setClassifierPath(String classifierPath) {
        this.classifierPath = classifierPath;
    }

    public String getInputDataPath() {
        return inputDataPath;
    }

    public void setInputDataPath(String inputDataPath) {
        this.inputDataPath = inputDataPath;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public EvaluationResult getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationResult evaluation) {
        this.evaluation = evaluation;
    }

}
