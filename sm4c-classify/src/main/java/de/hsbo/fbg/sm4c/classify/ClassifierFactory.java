/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;

/**
 *
 * @author Sebastian Drost
 */
public class ClassifierFactory {

    public AbstractClassifier createClassifier(ClassifierTypes classifier) {
        switch (classifier) {
            case KNearest:
                return new KNearestClassifier();
            case MULTINOMIAL_NAIVE_BAYES:
                return new MultinomialNaiveBayesClassifier();
            case SVM:
                return new SVMClassifier();
            default:
                return new MultinomialNaiveBayesClassifier();
        }
    }
}
