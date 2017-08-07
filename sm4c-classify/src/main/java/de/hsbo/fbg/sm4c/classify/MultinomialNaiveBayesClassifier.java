/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import static de.hsbo.fbg.sm4c.classify.train.DtmTransformer.DEFAULT_STOP_WORD_LIST;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Sebastian Drost
 */
public class MultinomialNaiveBayesClassifier extends AbstractClassifier {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MultinomialNaiveBayesClassifier.class);
    
    public MultinomialNaiveBayesClassifier() {
        this.classifier = instantiateClassifier();
        this.transformer = createTransfomer();
    }
    
    private DtmTransformer createTransfomer() {
        DtmTransformer transformer = createBasicTransformer();
        transformer.getFilter().setOutputWordCounts(true);
        return transformer;
    }
    
    @Override
    protected Classifier instantiateClassifier() {
        return new NaiveBayesMultinomial();
    }
    
}
