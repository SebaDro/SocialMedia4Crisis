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
import weka.core.SelectedTag;
import weka.core.neighboursearch.KDTree;

/**
 *
 * @author Sebastian Drost
 */
public class KNearestClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(KNearestClassifier.class);
    private static final int K = 7;

    private int k;

    public KNearestClassifier() {
        super();
        this.k = K;
        transformer.getFilter().setOutputWordCounts(true);
        transformer.getFilter().setIDFTransform(true);
    }

    public KNearestClassifier(Classifier classifier, Dataset trainingData) {
        super(classifier, trainingData);
    }

    public void setKNN(int k) {
        this.k = k;
        ((IBk) classifier).setKNN(k);
    }

    public void useKDTree() throws Exception {
        KDTree tree = new KDTree();
        tree.setInstances(this.getTrainingData().getModelDataset());
        ((IBk) classifier).setNearestNeighbourSearchAlgorithm(tree);
    }

    @Override
    protected Classifier instantiateClassifier() {
        IBk ibk = new IBk(k);
        ibk.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
        return ibk;
    }

}
