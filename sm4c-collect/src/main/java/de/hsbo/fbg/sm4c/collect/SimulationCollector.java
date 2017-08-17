/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoSimulationDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.Date;
import java.util.List;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;

/**
 *
 * @author Seba
 */
public class SimulationCollector extends Collector {

    private MessageDocumentDao documentDao;

    public SimulationCollector(Collection collection) {
        super(collection);
        MongoSimulationDatabaseConnection con = new MongoSimulationDatabaseConnection();
        con.afterPropertiesSet();
        DocumentDaoFactory documentDaoFactory = new MongoDocumentDaoFactory(con);
        MongoCollection col = (MongoCollection) documentDaoFactory.getContext(collection);
        documentDao = documentDaoFactory.createMessageDocumentDao(con);
    }

    @Override
    public List<MessageDocument> collectMessages(Date startTime, Date endTime) {
        return documentDao.retrieveForTimeSpan(startTime, endTime);
    }

}
