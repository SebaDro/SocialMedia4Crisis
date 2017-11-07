/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao;

import static com.mongodb.client.model.Filters.eq;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Sebastian Drost
 */
public interface MessageDocumentDao {

    MessageDocument retrieveById(String id);

    List<MessageDocument> retrieve();

    List<MessageDocument> retrieveSimulationData();

    List<MessageDocument> retrieveTrainingData();

    List<MessageDocument> retrieveUnlabeledData();

    List<MessageDocument> retrieveLabeledData();

    List<MessageDocument> retrieveForTimeSpan(Date startTime, Date endTime);

    long count();
    
    long countUnlabeled();

    boolean exists(MessageDocument doc);

    public boolean containsSource(String sourceId);

    void store(MessageDocument doc);

    void store(List<MessageDocument> docs);
    
    void updateDefault(MessageDocument doc);

    void update(MessageDocument doc, String field);

    void remove(MessageDocument doc);

    void removeAll();

    void createIndex();

}
