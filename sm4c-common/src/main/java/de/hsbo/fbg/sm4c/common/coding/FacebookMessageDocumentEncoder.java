/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationSource;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.collect.model.Link;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Class to encode facebook objects
 *
 * @author Sebastian Drost
 */
public class FacebookEncoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookEncoder.class);

    private ObjectMapper mapper;

    public FacebookEncoder() {
        mapper = new ObjectMapper();
    }

    /**
     * Creates a Facebook message from a Facebook post
     *
     * @param post Facebook post
     * @param source the page or group the post was published
     * @return Facebook message
     */
    public FacebookMessageDocument createMessage(Post post, FacebookSource source) {
        FacebookMessageDocument message = new FacebookMessageDocument();
        message.setId(post.getId());
        message.setContent(post.getMessage());
        DateTime creationTime = new DateTime(post.getCreatedTime());
        message.setCreationTime(creationTime);
        DateTime updateTime = new DateTime(post.getUpdatedTime());
        message.setUpdateTime(updateTime);
        message.setType(post.getType());
        message.setSource(source);
        return message;
    }

    /**
     * Encodes a Facebook message into a JSON string
     *
     * @param message message to encode
     * @return message as JSON string
     */
    public String encodeMessageToJson(FacebookMessageDocument message) {
        String result = "";
        ObjectNode postNode = createMessageNode(message);
        try {
            result = mapper.writeValueAsString(postNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not encode post", ex);
        }
        return result;
    }

    /**
     * Encodes a list of Facebook messages into a JSON Array string
     *
     * @param messages messages to encode
     * @return messages as JSON Array string
     */
    public String encodeMessagesToJson(List<FacebookMessageDocument> messages) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode postArray = mapper.createArrayNode();
        messages.forEach(p -> {
            ObjectNode node = createMessageNode(p);
            postArray.add(node);
        });
        String result = "";
        try {
            result = mapper.writeValueAsString(postArray);
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
    public ObjectNode createMessageNode(FacebookMessageDocument message) {
        ObjectNode root = mapper.createObjectNode();

        root.put("messageId", message.getId());
        if (message.getLabel() != null && !message.getLabel().equals("")) {
            root.put("label", message.getLabel());
        } else {
            root.put("label", "");
        }
        root.put("content", message.getContent());
        root.put("type", message.getType());
        ObjectNode sourceNode = mapper.createObjectNode();
        sourceNode.put("id", message.getSource().getId());
        sourceNode.put("name", message.getSource().getName());
        sourceNode.put("type", message.getSource().getCategory().getName());
        root.set("source", sourceNode);
        return root;
    }
  

    /**
     * Creates a JSON ArrayNode from a List of Facebook messages
     *
     * @param messages List of Facebook messages
     * @return JSON ArrayNode
     */
    public ArrayNode createPostArrayNode(List<FacebookMessageDocument> messages) {
        ArrayNode postArray = mapper.createArrayNode();
        messages.forEach(m -> {
            ObjectNode node = createMessageNode(m);
            postArray.add(node);
        });
        return postArray;
    }   

}
