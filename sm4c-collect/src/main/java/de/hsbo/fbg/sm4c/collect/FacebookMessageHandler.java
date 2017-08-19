/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.classify.AbstractClassifier;
import de.hsbo.fbg.sm4c.classify.geotag.ArcGISGeoTagger;
import de.hsbo.fbg.sm4c.classify.geotag.GeoTagger;
import de.hsbo.fbg.sm4c.classify.geotag.LocationRecognizer;
import de.hsbo.fbg.sm4c.common.model.Location;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookMessageHandler implements MessageHandler{

    private static final Logger LOGGER = LogManager.getLogger(FacebookMessageHandler.class);

    private MessageDocument document;
    private AbstractClassifier classifier;
    private LocationRecognizer recognizer;
    private ArcGISGeoTagger geoTagger;

    public FacebookMessageHandler(AbstractClassifier classifier) {
        this.classifier = classifier;
        this.recognizer = new LocationRecognizer();
        this.geoTagger = new ArcGISGeoTagger();
    }

    public void processMessages(List<MessageDocument> documents) {
        documents.forEach(document -> {
            //do classyfying
            if (document.getContent()==null || document.getContent().isEmpty()){
                return;
            }
            String label = classifier.classify(document);
            LOGGER.info("Document " + document.getId() + " was classified as " + label);
            document.setLabel(label);

            //find location entities in message content
            List<String> locationEntities = recognizer.recognizeLocations(document.getContent());
            //if no entities found and message document is from Facebook
            //get location from source
            if (locationEntities.isEmpty()) {
//                    if(document.getService().equals(Services.FACEBOOK.toString())){
//                        
//                    }
            } else {
//                    List<Location> locations = geoTagger.geocode(locationEntities);
                geoTagger.geocode(locationEntities.get(0));

            }

        });
    }

}
