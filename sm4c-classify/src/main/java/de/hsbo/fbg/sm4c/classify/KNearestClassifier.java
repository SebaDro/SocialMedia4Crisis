/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;

/**
 *
 * @author Sebastian Drost
 */
public class KNearestClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(KNearestClassifier.class);
    private int k = 1;

    public KNearestClassifier() {
        super();
    }

    public KNearestClassifier(Classifier classifier, Dataset trainingData) {
        super(classifier, trainingData);
    }

    public void setKNN(int k) {
        this.k = k;
        ((IBk) classifier).setKNN(k);
    }

    @Override
    protected Classifier instantiateClassifier() {
        return new IBk(k);
    }

}
