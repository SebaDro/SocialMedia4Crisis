/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoSimulationDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.Date;
import java.util.List;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Sebastian Drost
 */
public class SimulationCollector implements Collector, InitializingBean {

    private MessageDocumentDao documentDao;

    public SimulationCollector() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MongoSimulationDatabaseConnection con = new MongoSimulationDatabaseConnection();
        con.afterPropertiesSet();
        DocumentDaoFactory documentDaoFactory = new MongoDocumentDaoFactory(con);
        MongoCollection col = (MongoCollection) documentDaoFactory.getContext(Configuration.getConfig().getPropertyValue("db_sim_collection"));
        documentDao = documentDaoFactory.createMessageDocumentDao(col);
    }

    @Override
    public List<MessageDocument> collectMessages(Date startTime, Date endTime) {
        return documentDao.retrieveForTimeSpan(startTime, endTime);
    }

}
