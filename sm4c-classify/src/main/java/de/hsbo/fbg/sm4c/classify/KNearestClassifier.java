/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.classify.train.DocumentTermMatrix;
import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import static de.hsbo.fbg.sm4c.classify.train.DtmTransformer.DEFAULT_STOP_WORD_LIST;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Seba
 */
public class KNearestClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(KNearestClassifier.class);

    private int k = 1;

    public KNearestClassifier() {
        this.classifier = instantiateClassifier();
        this.transformer = createBasicTransformer();
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
