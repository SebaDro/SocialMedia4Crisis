/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.train;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.StopwordsHandler;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Sebastian Drost
 */
public class DtmTransformer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DtmTransformer.class);

    /**
     * The filter used to generate the word counts.
     */
    private StringToWordVector filter;
    private Dataset dataset;

    public DtmTransformer(StringToWordVector filter) {
        this.filter = filter;
    }

    public StringToWordVector getFilter() {
        return filter;
    }

    public void setInputFormat(Dataset dataset) {
        try {
            filter.setInputFormat(dataset.getModelDataset());
        } catch (Exception ex) {
            LOGGER.error("Could not set input format", ex);
        }
        this.dataset = dataset;
    }

    public DocumentTermMatrix createDocumentTermMatrix(Dataset dataset) {
        DocumentTermMatrix result = null;
        try {
            Instances filteredInstances = Filter.useFilter(dataset.getModelDataset(), this.filter);
            result = new DocumentTermMatrix(filteredInstances);
        } catch (Exception ex) {
            LOGGER.error("Could not create document term matrix", ex);
        }
        return result;
    }

    private Instances createDocumentTermMatrix(Instances instances) {
//        DocumentTermMatrix result = null;
        Instances filteredInstances = null;
        try {
            filteredInstances = Filter.useFilter(instances, this.filter);
//            result = new DocumentTermMatrix(filteredInstances);
        } catch (Exception ex) {
            LOGGER.error("Could not create document term matrix", ex);
        }
        return filteredInstances;
    }

    public Instance createdWordVector(Instance instance) {
        Instance filteredInstance = null;
        try {
            this.filter.input(instance);
            filteredInstance = this.filter.output();
        } catch (Exception ex) {
            LOGGER.error("Could not create document term matrix", ex);
        }
        return filteredInstance;
    }

}
