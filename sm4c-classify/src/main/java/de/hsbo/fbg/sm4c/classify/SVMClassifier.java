/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;

/**
 *
 * @author Sebastian Drost
 */
public class SVMClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(SVMClassifier.class);

    public SVMClassifier() {
        super();
    }

    public SVMClassifier(Classifier classifier, Dataset trainingData) {
        super(classifier, trainingData);
    }

    @Override
    protected Classifier instantiateClassifier() {
        SMO smo = new SMO();
        PolyKernel kernel = new PolyKernel();
        kernel.setExponent(1);
        smo.setC(1);
        smo.setKernel(kernel);

        return smo;
    }

}
