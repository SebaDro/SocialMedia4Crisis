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
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Services;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.hibernate.Session;
import org.joda.time.DateTime;

/**
 * Class for decoding Faceook messages
 *
 * @author Sebastian Drost
 */
public class MessageDocumentDecoder {

    private static final Logger LOGGER = LogManager.getLogger(MessageDocumentDecoder.class);

    private DaoFactory<Session> daoFactory;

    public MessageDocumentDecoder() {

    }

    /**
     * Decodes a MongoDB document to a a FacebookMessage
     *
     * @param doc MongoDB document
     * @return decoded FacebookMessage
     */
    public MessageDocument decodeFacebookMessage(Document doc) {
        MessageDocument message = null;
        if (doc.getString("service").equals(Services.FACEBOOK)) {
            message = new FacebookMessageDocument();
            message.setService(Services.FACEBOOK.toString());
            ((FacebookMessageDocument) message).setType(doc.getString("type"));
            Document sourceDoc = (Document) doc.get("source");
            FacebookSource source;
            try {
                source = retrieveFacebookSource(sourceDoc.getString("id"));
                ((FacebookMessageDocument) message).setSource(source);
            } catch (Exception ex) {
                LOGGER.error("Can not decode referenced document", ex);
            }
        }
        message.setId(doc.getString("messageId"));
        message.setLabel(doc.getString("label"));
        message.setContent(doc.getString("content"));

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
