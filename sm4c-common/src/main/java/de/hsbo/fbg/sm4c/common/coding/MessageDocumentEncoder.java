/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.coding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Services;
import java.math.BigDecimal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

/**
 * Class to encode facebook objects
 *
 * @author Sebastian Drost
 */
public class MessageDocumentEncoder {

    private static final Logger LOGGER = LogManager.getLogger(MessageDocumentEncoder.class);

    private ObjectMapper mapper;

    public MessageDocumentEncoder() {
        mapper = new ObjectMapper();
    }

    public Document encodeMessageToDocument(MessageDocument message) {
        Document doc = Document.parse(encodeMessageToJson(message))
                .append("creationTime", message.getCreationTime().toDate())
                .append("updateTime", message.getUpdateTime().toDate());
        return doc;
    }

    /**
     * Encodes a Facebook message into a JSON string
     *
     * @param message message to encode
     * @return message as JSON string
     */
    public String encodeMessageToJson(MessageDocument message) {
        String result = "";
        ObjectNode postNode = null;
        if (message.getService().equals(Services.FACEBOOK.toString())) {
            postNode = createFacebookMessageNode((FacebookMessageDocument) message);
        }
        try {
            result = mapper.writeValueAsString(postNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not encode post", ex);
        }
        return result;
    }

    /**
     * Creates a JSON ObjectNode from a Facebook message
     *
     * @param message Facebook message
     * @return JSON ObjectNode
     */
    public ObjectNode createFacebookMessageNode(FacebookMessageDocument message) {
        ObjectNode root = mapper.createObjectNode();

        root.put("messageId", message.getId());
        if (message.getLabel() != null && !message.getLabel().equals("")) {
            root.put("label", message.getLabel());
        } else {
            root.put("label", "");
        }
        root.put("content", message.getContent());
        root.put("service", message.getService());
        root.put("type", message.getType());

        ObjectNode sourceNode = mapper.createObjectNode();
        sourceNode.put("id", message.getSource().getFacebookId());
        sourceNode.put("name", message.getSource().getName());
        sourceNode.put("type", message.getSource().getType().getName());
        root.set("source", sourceNode);
        return root;
    }

//    /**
//     * Encodes a list of Facebook messages into a JSON Array string
//     *
//     * @param messages messages to encode
//     * @return messages as JSON Array string
//     */
//    public String encodeMessagesToJson(List<FacebookMessageDocument> messages) {
//        ObjectNode root = mapper.createObjectNode();
//        ArrayNode postArray = mapper.createArrayNode();
//        messages.forEach(p -> {
//            ObjectNode node = createFacebookMessageNode(p);
//            postArray.add(node);
//        });
//        String result = "";
//        try {
//            result = mapper.writeValueAsString(postArray);
//        } catch (JsonProcessingException ex) {
//            LOGGER.error("Could not encode post", ex);
//        }
//        return result;
//    }
//    /**
//     * Creates a JSON ArrayNode from a List of Facebook messages
//     *
//     * @param messages List of Facebook messages
//     * @return JSON ArrayNode
//     */
//    public ArrayNode createPostArrayNode(List<FacebookMessageDocument> messages) {
//        ArrayNode postArray = mapper.createArrayNode();
//        messages.forEach(m -> {
//            ObjectNode node = createFacebookMessageNode(m);
//            postArray.add(node);
//        });
//        return postArray;
//    }
}
