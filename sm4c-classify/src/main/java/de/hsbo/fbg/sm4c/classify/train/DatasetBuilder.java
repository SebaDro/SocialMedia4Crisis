/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.train;

import de.hsbo.fbg.sm4c.classify.filter.LowerCaseFilter;
import de.hsbo.fbg.sm4c.classify.filter.NonAlphabeticFilter;
import de.hsbo.fbg.sm4c.classify.filter.StopWordRemover;
import de.hsbo.fbg.sm4c.classify.filter.TextPreprocessor;
import de.hsbo.fbg.sm4c.classify.filter.URLFilter;
import de.hsbo.fbg.sm4c.classify.filter.WhitespaceFilter;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Sebastian Drost
 */
public class DatasetBuilder {

    private TextPreprocessor preProcessor;

    public DatasetBuilder(TextPreprocessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    public DatasetBuilder() {
        StopWordRemover stopWordRemover = new StopWordRemover();
        stopWordRemover.loadStopWordList(StopWordRemover.DEFAULT_STOP_WORD_LIST);
        LowerCaseFilter lowerCaseFilter = new LowerCaseFilter();
        NonAlphabeticFilter nonAlphaFilter = new NonAlphabeticFilter();
        WhitespaceFilter whitepaceFilter = new WhitespaceFilter();
        URLFilter urlFilter = new URLFilter();

        preProcessor = new TextPreprocessor();
        preProcessor.addFilter(lowerCaseFilter);
        preProcessor.addFilter(urlFilter);
        preProcessor.addFilter(nonAlphaFilter);
        preProcessor.addFilter(stopWordRemover);
        preProcessor.addFilter(whitepaceFilter);
    }

    public Dataset createDataset(Collection collection) {
        // Create vector of attributes.
        ArrayList<Attribute> attributes = new ArrayList();

        // Add attribute for holding messages.
        attributes.add(new Attribute(Dataset.MESSAGE_ATTRIBUTE, (ArrayList<String>) null));

        // Add class attribute.
        ArrayList<String> classValues = (ArrayList) collection.getLabels().stream()
                .map(l -> l.getName())
                .collect(Collectors.toList());
        attributes.add(new Attribute("class", classValues));
        Instances instances = new Instances(collection.getName(), attributes, 10000);
        instances.setClassIndex(instances.numAttributes() - 1);
        return new Dataset(instances);
    }

    public void add(Dataset dataset, List<MessageDocument> messages) {
        messages.forEach(m -> {
            add(dataset, m);
        });
    }

    public void add(Dataset dataset, MessageDocument messageDoc) {
        // Create instance of length two.
        Instance instance = new DenseInstance(2);

        // Set value for message attribute
        Attribute messageAtt = dataset.getModelDataset().attribute(Dataset.MESSAGE_ATTRIBUTE);

        //Preprocess the message content
        String filteredContent = preProcessor.preprocessText(messageDoc.getContent());
        instance.setValue(messageAtt, messageAtt.addStringValue(filteredContent));

        // Give instance access to attribute information from the dataset.
        instance.setDataset(dataset.getModelDataset());
        instance.setClassValue(messageDoc.getLabel());

        dataset.getModelDataset().add(instance);
    }

}
