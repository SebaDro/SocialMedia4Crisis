/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.encode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import facebook4j.Post;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookEncoder {

    private Gson gson;
    ObjectMapper mapper;

    public FacebookEncoder() {
        gson = new Gson();
        mapper = new ObjectMapper();
    }

    public String encodePost(Post post) {
        String result = "";
        ObjectNode postNode = createPostNode(post);
        try {
            result = mapper.writeValueAsString(postNode);
        } catch (IOException ex) {
            Logger.getLogger(FacebookEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public ObjectNode createPostNode(Post post) {
        String postJson = gson.toJson(post);
        ObjectNode root = mapper.createObjectNode();

        try {
            JsonNode fbPostNode = mapper.readTree(postJson);
            root.put("id", fbPostNode.get("id").asText());
            root.put("label", "");
            root.set("fb_post", fbPostNode);
        } catch (IOException ex) {
            Logger.getLogger(FacebookEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return root;
    }

    public ArrayNode createPostArrayNode(List<Post> posts) {
        ArrayNode postArray = mapper.createArrayNode();
        posts.forEach(p -> {
            ObjectNode node = createPostNode(p);
            postArray.add(node);
        });
        return postArray;
    }
}
