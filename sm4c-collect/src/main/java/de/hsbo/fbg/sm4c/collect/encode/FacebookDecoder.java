/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import java.util.Optional;
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
public class FacebookDecoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookDecoder.class);

    private Facebook facebook;
    @Autowired
    private DaoFactory<Session> daoFactory;

    public FacebookDecoder() {
        facebook = new FacebookFactory().getInstance();
    }

    /**
     * Decodes a MongoDB document to a a FacebookMessage
     *
     * @param doc MongoDB document
     * @return decoded FacebookMessage
     */
    public FacebookMessageDocument decodeFacebookMessage(Document doc) throws Exception{

        FacebookMessageDocument message = new FacebookMessageDocument();
        message.setId(doc.getString("messageId"));
        message.setLabel(doc.getString("label"));
        message.setContent(doc.getString("content"));
        message.setType(doc.getString("type"));
        Document sourceDoc = (Document) doc.get("source");
        FacebookSource source = retrieveFacebookSource(sourceDoc.getString("id"));
//        FacebookSource source = new FacebookSource();
//        source.setId(sourceDoc.getString("id"));
//        source.setName(sourceDoc.getString("name"));
//        source.setType(sourceDoc.getString("type"));
        message.setSource(source);
        message.setCreationTime(new DateTime(doc.getDate("creationTime")));
        message.setUpdateTime(new DateTime(doc.getDate("updateTime")));
        return message;
    }

    private FacebookSource retrieveFacebookSource(String facebookId) throws Exception {
        HibernateDatabaseConnection dbc = new HibernateDatabaseConnection();
        dbc.afterPropertiesSet();
        daoFactory = new HibernateDaoFactory(dbc);
        try (Session session = (Session) daoFactory.initializeContext()) {
            FacebookSourceDao sourceDao = daoFactory.createFacebookSourceDao(session);
            Optional<FacebookSource> source = sourceDao.retrieveByFacebookId(facebookId);
            if (!source.isPresent()) {
                throw new RessourceNotFoundException("The referenced source is not available");
            }
            return source.get();
        }

    }
}
