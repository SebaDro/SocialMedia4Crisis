/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geocoding;

import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Sebastian Drost
 */
public class FeatureServicePersister {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureServicePersister.class);

    private static final String F_PARAM = "f";
    private static final String F_VALUE = "json";
    private static final String ROLLBACK_PARAM = "rollbackOnFailure";
    private static final String ROLLBACK_VALUE = "false";
    private static final String TOKEN_PARAM = "token";
    private static final String TOKEN_VALUE = "";
    private static final String FEATURES_PARAM = "features";

    private final HttpHeaders headers;
    private final String featureServiceUrl;
    private final FeatureCreator featureCreator;

    public FeatureServicePersister(String featureServiceUrl) {
        this.featureServiceUrl = featureServiceUrl;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        featureCreator = new FeatureCreator();
    }

    public void addDocumentAsFeature(MessageDocument document, Collection collection) {
        String featureValue = featureCreator.createFeature(document, collection);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add(F_PARAM, F_VALUE);
        map.add(ROLLBACK_PARAM, ROLLBACK_VALUE);
        map.add(TOKEN_PARAM, TOKEN_VALUE);
        map.add(FEATURES_PARAM, featureValue);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(featureServiceUrl, request, String.class);
        LOG.info("Document " + document.getId() + " has been added to FeatureService wirh response code: " + response.getStatusCodeValue());
//        return response;
    }

}
