/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geocoding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sebastian Drost
 */
class FeatureCreator {

    private static final Logger LOGGER = LogManager.getLogger(FeatureCreator.class);

    private ObjectMapper mapper;

    public FeatureCreator() {
        mapper = new ObjectMapper();
    }

    public String createFeature(MessageDocument document) {
        String result = null;
        ArrayNode featuresNode = mapper.createArrayNode();
        ObjectNode attributeNode = mapper.createObjectNode();
        attributeNode.put("messageid", document.getId());
//            attributeNode.put("collection", "test");
        attributeNode.put("creation", document.getCreationTime().toString());
        document.getLocations().forEach(l -> {
            ObjectNode featureNode = mapper.createObjectNode();
            ObjectNode geometryNode = mapper.createObjectNode();
            geometryNode.put("x", l.getLongitude());
            geometryNode.put("y", l.getLatitude());
            featureNode.set("geometry", geometryNode);
            featureNode.set("attributes", attributeNode);
            featuresNode.add(featureNode);
        });
        try {
            result = mapper.writeValueAsString(featuresNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not encode feature", ex);
        }
        return result;
    }
}
