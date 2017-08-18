/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;

/**
 *
 * @author Sebastian Drost
 */
public class ClassifierFactory {
    
    public static final String K_NEAREST="K_Nearest";
//    private static final String NAIVE_BAYES="Naive_Bayes";
    public static final String NAIVE_BAYES_MULTINOMIAL="Naive_Bayes_Multinomial";
    public static final String SVM="SVM";
    

    public static AbstractClassifier createClassifier(String classifierType) {
        switch (classifierType) {
            case K_NEAREST:
                return new KNearestClassifier();
            case NAIVE_BAYES_MULTINOMIAL:
                return new MultinomialNaiveBayesClassifier();
            case SVM:
                return new SVMClassifier();
            default:
                return new MultinomialNaiveBayesClassifier();
        }
    }
    
        public static AbstractClassifier createClassifier(String classifierType, Classifier classifier, Dataset trainingData) {
        switch (classifierType) {
            case K_NEAREST:
                return new KNearestClassifier(classifier, trainingData);
            case NAIVE_BAYES_MULTINOMIAL:
                return new MultinomialNaiveBayesClassifier(classifier, trainingData);
            case SVM:
                return new SVMClassifier(classifier, trainingData);
            default:
                return new MultinomialNaiveBayesClassifier(classifier, trainingData);
        }
    }
}
