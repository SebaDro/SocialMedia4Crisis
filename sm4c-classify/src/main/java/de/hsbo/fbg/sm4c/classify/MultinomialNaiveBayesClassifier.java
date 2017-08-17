/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;

/**
 *
 * @author Sebastian Drost
 */
public class MultinomialNaiveBayesClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MultinomialNaiveBayesClassifier.class);

    public MultinomialNaiveBayesClassifier() {
        super();
        transformer.getFilter().setIDFTransform(true);
    }

    public MultinomialNaiveBayesClassifier(Classifier classifier) {
        super(classifier);
        transformer.getFilter().setIDFTransform(true);
    }

    @Override
    protected Classifier instantiateClassifier() {
        return new NaiveBayesMultinomial();
    }

}
