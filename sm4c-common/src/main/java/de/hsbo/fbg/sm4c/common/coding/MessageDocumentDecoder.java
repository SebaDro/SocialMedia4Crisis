/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.coding;

import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class for decoding Faceook messages
 *
 * @author Sebastian Drost
 */
public class FacebookMessageDocumentDecoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookMessageDocumentDecoder.class);

    private DaoFactory<Session> daoFactory;

    public FacebookMessageDocumentDecoder() {

    }

    /**
     * Decodes a MongoDB document to a a FacebookMessage
     *
     * @param doc MongoDB document
     * @return decoded FacebookMessage
     */
    public FacebookMessageDocument decodeFacebookMessage(Document doc) {

        FacebookMessageDocument message = new FacebookMessageDocument();
        message.setId(doc.getString("messageId"));
        message.setLabel(doc.getString("label"));
        message.setContent(doc.getString("content"));
        message.setType(doc.getString("type"));
        Document sourceDoc = (Document) doc.get("source");
//        FacebookSource source;
//        try {
//            source = retrieveFacebookSource(sourceDoc.getString("id"));
//            message.setSource(source);
//        } catch (Exception ex) {
//            LOGGER.error("Can not decode referenced document", ex);
//        }
//        FacebookSource source = new 
        message.setCreationTime(new DateTime(doc.getDate("creationTime")));
        message.setUpdateTime(new DateTime(doc.getDate("updateTime")));
        return message;
    }

    private FacebookSource retrieveFacebookSource(String facebookId) throws RessourceNotFoundException {
        HibernateDatabaseConnection dbc = new HibernateDatabaseConnection();
        try {
            dbc.afterPropertiesSet();
            daoFactory = new HibernateDaoFactory(dbc);
        } catch (Exception ex) {
            LOGGER.error("Could not instantiate DB connection", ex);
        }
        FacebookSource result;
        try (Session session = (Session) daoFactory.initializeContext()) {

            FacebookSourceDao sourceDao = daoFactory.createFacebookSourceDao(session);
            Optional<FacebookSource> source = sourceDao.retrieveByFacebookId(facebookId);
            if (!source.isPresent()) {
                throw new RessourceNotFoundException("The referenced source is not available");
            }
            result = source.get();
        }
        return result;
    }

}
