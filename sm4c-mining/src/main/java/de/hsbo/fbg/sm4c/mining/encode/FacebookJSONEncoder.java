/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.encode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookJSONEncoder {

    private static final Logger LOGGER = LogManager.getLogger(FacebookJSONEncoder.class);

    private Gson gson;
    private ObjectMapper mapper;

    public FacebookJSONEncoder() {
        gson = new Gson();
        mapper = new ObjectMapper();
    }

    public String encodePost(Post post, Object group) {
        String result = "";
        ObjectNode postNode = createPostNode(post, group);
        try {
            result = mapper.writeValueAsString(postNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not encode post", ex);
        }
        return result;
    }

    public String encodePostArray(List<Post> posts, Object source) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode postArray = mapper.createArrayNode();
        posts.forEach(p -> {
            ObjectNode node = createPostNode(p, source);
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

    public ObjectNode createPostNode(Post post, Object source) {
        String postJson = gson.toJson(post);
        ObjectNode root = mapper.createObjectNode();

        try {
            JsonNode fbPostNode = mapper.readTree(postJson);
            String createdTime = fbPostNode.get("createdTime").asText();
            DateTime dt = DateTime.parse(createdTime, 
                    DateTimeFormat.forPattern("MMM dd, yyyy hh:mm:ss a")
                    .withLocale(Locale.ENGLISH));
            ((ObjectNode) fbPostNode).put("createdTime", dt.toDateTime(DateTimeZone.UTC).toString());
            root.put("id", fbPostNode.get("id").asText());
            ObjectNode sourceNode = mapper.createObjectNode();

            if (source != null) {
                if (source instanceof Group) {
                    sourceNode.put("type", "group");
                    sourceNode.put("id", ((Group) source).getId());
                    sourceNode.put("name", ((Group) source).getName());
                } else if (source instanceof Page) {
                    sourceNode.put("type", "page");
                    sourceNode.put("id", ((Page) source).getId());
                    sourceNode.put("name", ((Page) source).getName());
                } else {
                    sourceNode.put("type", "none");
                    sourceNode.put("id", "");
                    sourceNode.put("name", "");
                }
            }
            root.set("source", sourceNode);
            root.put("label", "");
            root.set("fb_post", fbPostNode);
        } catch (Exception ex) {
            LOGGER.error("Could not parse JSON post", ex);
        }
        return root;
    }

    public ArrayNode createPostArrayNode(List<Post> posts, Object source) {
        ArrayNode postArray = mapper.createArrayNode();
        posts.forEach(p -> {
            ObjectNode node = createPostNode(p, source);
            postArray.add(node);
        });
        return postArray;
    }

    public ArrayNode createPostArrayNodeForGroup(List<Post> posts, Group group) {
        ArrayNode postArray = mapper.createArrayNode();
        posts.forEach(p -> {
            ObjectNode node = createPostNode(p, group);
            postArray.add(node);
        });
        return postArray;
    }
}
