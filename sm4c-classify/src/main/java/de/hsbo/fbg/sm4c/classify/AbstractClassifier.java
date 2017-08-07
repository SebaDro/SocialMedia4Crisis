/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.classify.train.DocumentTermMatrix;
import de.hsbo.fbg.sm4c.classify.train.DtmTransformer;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
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

/**
 *
 * @author Sebastian Drost
 */
public abstract class AbstractClassifier {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(AbstractClassifier.class);

    protected Classifier classifier;
    protected Dataset trainingData;
    protected DtmTransformer transformer;

    /**
     * Trains the classifier with a labeled training dataset
     *
     * @param trainingData the labeled training dataset
     */
    public void trainClassifier(Dataset trainingData) {
        this.trainingData = trainingData;
        this.transformer.setInputFormat(trainingData);
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
        Instance instance = createInstance(document);
        Instance filteredInstance = transformer.createdWordVector(instance);
        try {
            double predicted = classifier.classifyInstance(filteredInstance);
            result = trainingData.getModelDataset().classAttribute().value((int) predicted);
        } catch (Exception ex) {
            LOGGER.error("Could not classify the referenced document", ex);
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
        this.transformer.setInputFormat(trainingData);
        try {
            DocumentTermMatrix matrix = transformer.createDocumentTermMatrix(trainingData);
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

    protected Instance createInstance(MessageDocument document) {
        // Create instance of length two.
        Instance instance = new DenseInstance(2);

        // Set value for message attribute
        Instances instances = this.trainingData.getModelDataset().stringFreeStructure();
        Attribute messageAtt = instances.attribute(Dataset.MESSAGE_ATTRIBUTE);
        instance.setValue(messageAtt, messageAtt.addStringValue(document.getContent()));

        // Give instance access to attribute information from the dataset.
        instance.setDataset(instances);

        return instance;
    }

    protected abstract Classifier instantiateClassifier();

}
