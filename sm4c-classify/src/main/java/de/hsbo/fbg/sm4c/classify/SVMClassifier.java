/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;

/**
 *
 * @author Sebastian Drost
 */
public class SVMClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(SVMClassifier.class);

    public SVMClassifier() {
        super();
    }

    public SVMClassifier(Classifier classifier) {
        super(classifier);
    }

    @Override
    protected Classifier instantiateClassifier() {
        return new SMO();
    }

}
