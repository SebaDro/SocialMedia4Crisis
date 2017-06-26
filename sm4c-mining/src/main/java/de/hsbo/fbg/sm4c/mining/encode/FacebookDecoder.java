/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.encode;

import de.hsbo.fbg.sm4c.mining.model.FacebookMessage;
import de.hsbo.fbg.sm4c.mining.model.FacebookSource;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.joda.time.DateTime;

/**
 * Class for decoding Faceook messages
 *
 * @author Sebastian Drost
 */
public class FacebookDecoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookDecoder.class);

    private Facebook facebook;

    public FacebookDecoder() {
        facebook = new FacebookFactory().getInstance();
    }

    /**
     * Decodes a MongoDB document to a a FacebookMessage
     * @param doc MongoDB document
     * @return decoded FacebookMessage
     */
    public FacebookMessage decodeFacebookMessage(Document doc) {
        FacebookMessage message = new FacebookMessage();
        message.setId(doc.getString("messageId"));
        message.setContent(doc.getString("content"));
        message.setLabel(doc.getString("label"));
        FacebookSource source = new FacebookSource();
        Document sourceDoc = (Document) doc.get("source");
        source.setId(sourceDoc.getString("sourceId"));
        source.setName(sourceDoc.getString("sourceName"));
        source.setType(sourceDoc.getString("sourceType"));
        message.setSource(source);
        message.setCreationTime(new DateTime(doc.getDate("timeStamp")));
        return message;
    }
}
