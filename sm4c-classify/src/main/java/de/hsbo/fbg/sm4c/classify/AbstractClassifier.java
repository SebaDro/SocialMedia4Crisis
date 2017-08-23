/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.filter.LowerCaseFilter;
import de.hsbo.fbg.sm4c.classify.filter.NonAlphabeticFilter;
import de.hsbo.fbg.sm4c.classify.filter.StopWordRemover;
import de.hsbo.fbg.sm4c.classify.filter.TextPreprocessor;
import de.hsbo.fbg.sm4c.classify.filter.URLFilter;
import de.hsbo.fbg.sm4c.classify.filter.WhitespaceFilter;
import de.hsbo.fbg.sm4c.common.model.EvaluationResult;
import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.classify.train.DocumentTermMatrix;
import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.Attributes;
import org.apache.logging.log4j.LogManager;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Sebastian Drost
 */
public abstract class AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(AbstractClassifier.class);

    public static final String DEFAULT_STOP_WORD_LIST = "german_stopwords_plain.txt";

    protected Classifier classifier;
    protected Dataset trainingData;
//    protected DocumentTermMatrix matrix;
    protected DtmTransformer transformer;
    protected TextPreprocessor preProcessor;

    public AbstractClassifier() {
        classifier = instantiateClassifier();
        preProcessor = createTextPreProcessor();
        transformer = createBasicTransformer();
    }

    public AbstractClassifier(Classifier classifier, Dataset trainingData) {
        this.classifier = classifier;
        preProcessor = createTextPreProcessor();
        this.transformer = createBasicTransformer();
        setFormat(trainingData);
        this.transformer.createDocumentTermMatrix(trainingData);
    }

    public void setFormat(Dataset trainingData) {
        this.trainingData = trainingData;
        this.transformer.setInputFormat(trainingData);
    }

    public Dataset getTrainingData() {
        return trainingData;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    /**
     * Trains the classifier with a labeled training dataset
     *
     * @param trainingData the labeled training dataset
     */
    public void trainClassifier() {
        DocumentTermMatrix matrix = this.transformer.createDocumentTermMatrix(trainingData);
        try {
            classifier.buildClassifier(matrix.getDtm());
        } catch (Exception ex) {
            LOGGER.error("Could not build classifier", ex);
        }
    }

    /**
     * Classifies a single message document by the use of the trained classifier
     *
     * @param document the unlabeled message document
     * @return the predicted class
     */
    public String classify(MessageDocument document) {
        String result = null;
        try {
            Instance instance = createInstance(document);
            Instance filteredInstance = transformer.createdWordVector(instance);

            double predicted = classifier.classifyInstance(filteredInstance);
            result = trainingData.getModelDataset().classAttribute().value((int) predicted);
        } catch (Exception ex) {
            LOGGER.error("Could not classify the referenced document " + document.getId(), ex);
        }
        return result;
    }

    /**
     * Evaluates the classifier by performing a 10-fold cross-validation with
     * the specified training dataset
     *
     * @param trainingData the labeled training dataset
     * @return List of
     */
    public EvaluationResult evaluate(Dataset trainingData) {
        EvaluationResult result = new EvaluationResult();
        this.transformer.setInputFormat(this.trainingData);
        try {
            DocumentTermMatrix matrix = this.transformer.createDocumentTermMatrix(trainingData);
            Evaluation evaluation = new Evaluation(matrix.getDtm());
            Classifier classifier = instantiateClassifier();
            evaluation.crossValidateModel(classifier, matrix.getDtm(), 10, new Random(42));
            result.setSummary(evaluation.toSummaryString());
            result.setConfusionMatrix(evaluation.toMatrixString());
            result.setClassDetails(evaluation.toClassDetailsString());
        } catch (Exception ex) {
            LOGGER.error("Could not instantiate evaluation", ex);
        }

        return result;
    }

    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> result = new ArrayList<>();
        DocumentTermMatrix matrix = this.transformer.createDocumentTermMatrix(trainingData);
        Enumeration<Attribute> ettributeEnmu = matrix.getDtm().enumerateAttributes();
        while (ettributeEnmu.hasMoreElements()) {
            result.add(ettributeEnmu.nextElement());
        }
        return result;
    }

    protected Instance createInstance(MessageDocument document) {
        // Create instance of length two.
        Instance instance = new DenseInstance(2);

        // Set value for message attribute
        Instances instances = this.trainingData.getModelDataset().stringFreeStructure();
        Attribute messageAtt = instances.attribute(Dataset.MESSAGE_ATTRIBUTE);
        String filteredContent = preProcessor.preprocessText(document.getContent());
        instance.setValue(messageAtt, messageAtt.addStringValue(filteredContent));

        // Give instance access to attribute information from the training dataset.
        instance.setDataset(instances);

        return instance;
    }

    protected DtmTransformer createBasicTransformer() {
        StringToWordVector filter = new StringToWordVector();
        //downcase tokens
//        filter.setLowerCaseTokens(true);

        //binary weighting of word occurence
        filter.setOutputWordCounts(false);

        //Set german Snowball stemmer
//        SnowballStemmer stemmer = new SnowballStemmer("german");
//        filter.setStemmer((Stemmer) stemmer);
        //Set tokenizer per word
//        NGramTokenizer tokenizer = new NGramTokenizer();
//        tokenizer.setNGramMinSize(2);
//        tokenizer.setNGramMaxSize(2);
        WordTokenizer tokenizer = new WordTokenizer();
        filter.setTokenizer(tokenizer);

        filter.setWordsToKeep(20000);

        //Set stopwords from a file
//        WordsFromFile stopwordHandler = new WordsFromFile();
//        URL url = this.getClass().getClassLoader().getResource(DEFAULT_STOP_WORD_LIST);
//        File stopWordFile;
//        try {
//            stopWordFile = new File(url.toURI());
//            stopwordHandler.setStopwords(stopWordFile);
//            filter.setStopwordsHandler(stopwordHandler);
//
//        } catch (URISyntaxException ex) {
//            LOGGER.error("Could not load stop words", ex);
//        }
        return new DtmTransformer(filter);
    }

    private TextPreprocessor createTextPreProcessor() {
        StopWordRemover stopWordRemover = new StopWordRemover();
        LowerCaseFilter lowerCaseFilter = new LowerCaseFilter();
        NonAlphabeticFilter nonAlphaFilter = new NonAlphabeticFilter();
        TextPreprocessor preProcessor = new TextPreprocessor();
        WhitespaceFilter whitepaceFilter = new WhitespaceFilter();
        URLFilter urlFilter = new URLFilter();

        preProcessor = new TextPreprocessor();
        preProcessor.addFilter(lowerCaseFilter);
        preProcessor.addFilter(urlFilter);
        preProcessor.addFilter(nonAlphaFilter);
        preProcessor.addFilter(stopWordRemover);
        preProcessor.addFilter(whitepaceFilter);
        return preProcessor;
    }

    protected abstract Classifier instantiateClassifier();

}
