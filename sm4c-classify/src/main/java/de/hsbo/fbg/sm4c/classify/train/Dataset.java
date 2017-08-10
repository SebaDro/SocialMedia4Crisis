/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.train;

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
public class Dataset {

    public static final String MESSAGE_ATTRIBUTE = "message_content";
    
    private Instances modelDataset;

    public Dataset(Collection col) {
        // Create vector of attributes.
        ArrayList<Attribute> attributes = new ArrayList();

        // Add attribute for holding messages.
        attributes.add(new Attribute(MESSAGE_ATTRIBUTE, (ArrayList<String>) null));

        // Add class attribute.
        ArrayList<String> classValues = (ArrayList) col.getLabels().stream()
                .map(l -> l.getName())
                .collect(Collectors.toList());
        attributes.add(new Attribute("class", classValues));
        modelDataset = new Instances(col.getName(), attributes, 10000);
        modelDataset.setClassIndex(modelDataset.numAttributes()-1);
    }

    public void add(List<MessageDocument> messages) {
        messages.forEach(m -> {
            add(m);
        });
    }

    private void add(MessageDocument messageDoc) {
        // Create instance of length two.
        Instance instance = new DenseInstance(2);

        // Set value for message attribute
        Attribute messageAtt = modelDataset.attribute(MESSAGE_ATTRIBUTE);
        instance.setValue(messageAtt, messageAtt.addStringValue(messageDoc.getContent()));

        // Give instance access to attribute information from the dataset.
        instance.setDataset(modelDataset);
        instance.setClassValue(messageDoc.getLabel());
        
        modelDataset.add(instance);
    }

    public Instances getModelDataset() {
        return modelDataset;
    }

}
