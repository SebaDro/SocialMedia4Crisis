/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import static de.hsbo.fbg.sm4c.classify.train.DtmTransformer.DEFAULT_STOP_WORD_LIST;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Sebastian Drost
 */
public class SVMClassifier extends AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(SVMClassifier.class);

    public SVMClassifier() {
        this.classifier = instantiateClassifier();
        this.transformer = createBasicTransformer();
    }

    @Override
    protected Classifier instantiateClassifier() {
        return new SMO();
    }

}
