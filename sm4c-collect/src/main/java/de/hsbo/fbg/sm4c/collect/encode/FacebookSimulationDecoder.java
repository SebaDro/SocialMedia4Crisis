/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.joda.time.DateTime;

/**
 *
 * @author Seba
 */
public class FacebookSimulationDecoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookDecoder.class);

    /**
     * Decodes a MongoDB document to a a FacebookMessage
     *
     * @param doc MongoDB document
     * @return decoded FacebookMessage
     */
    public FacebookSimulationMessageDocument decodeFacebookMessage(Document doc) {

        FacebookSimulationMessageDocument message = new FacebookSimulationMessageDocument();
        message.setId(doc.getString("messageId"));
        message.setLabel(doc.getString("label"));
        message.setContent(doc.getString("content"));
        message.setType(doc.getString("type"));
        Document sourceDoc = (Document) doc.get("source");
        FacebookSimulationSource source = new FacebookSimulationSource();
        source.setId(sourceDoc.getString("id"));
        source.setName(sourceDoc.getString("name"));
        source.setType(sourceDoc.getString("type"));
        message.setSource(source);
        message.setCreationTime(new DateTime(doc.getDate("creationTime")));
        message.setUpdateTime(new DateTime(doc.getDate("updateTime")));
        return message;
    }

}
