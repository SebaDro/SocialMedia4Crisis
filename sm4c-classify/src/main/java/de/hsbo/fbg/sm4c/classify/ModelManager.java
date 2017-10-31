/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify;

import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.Model;
import java.io.File;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author Sebastian Drost
 */
public class ModelManager {

    private static final String WEKA_PATH_KEY = "weka_path";
    
    public ModelManager(){
        
    }

    public String[] serializeModel(AbstractClassifier classifier, Collection collection) throws Exception {
        String[] paths = new String[2];
        String userHome = System.getProperty("user.home");
        String wekaPath = Configuration.getConfig().getPropertyValue(WEKA_PATH_KEY);
        StringBuilder builder = new StringBuilder();
        builder.append(userHome).append(wekaPath).append(collection.getName());
        String classifierFileName = builder.toString() + ".model";
        SerializationHelper.write(classifierFileName, classifier.getClassifier());

        String datasetFilename = builder.toString() + ".arff";
        ArffSaver saver = new ArffSaver();
        saver.setInstances(classifier.getTrainingData().getModelDataset());
        saver.setFile(new File(datasetFilename));
        saver.writeBatch();

        paths[0] = classifierFileName.replace("\\", "/");
        paths[1] = datasetFilename.replace("\\", "/");
        return paths;
    }

    public AbstractClassifier deserializeModel(Model model) throws Exception {
        Classifier cls = (Classifier) SerializationHelper.read(model.getClassifierPath());

        DataSource source = new DataSource(model.getInputDataPath());
        Instances instances = source.getDataSet();
        Dataset dataset = new Dataset(instances);

        ClassifierFactory factory = new ClassifierFactory();
        AbstractClassifier classifier = factory.createClassifier(model.getClassifier().getName(), cls, dataset);

        return classifier;
    }

}
