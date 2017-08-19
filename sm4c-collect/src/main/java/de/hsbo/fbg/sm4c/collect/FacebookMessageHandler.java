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
public class MessageHandler {
    
    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private MessageDocument document;
    private AbstractClassifier classifier;
    private LocationRecognizer recognizer;
    private GeoTagger geoTagger;

    public MessageHandler(AbstractClassifier classifier) {
        this.classifier = classifier;
        this.recognizer = new LocationRecognizer();
        this.geoTagger = new ArcGISGeoTagger();
    }

    public Runnable getHandlingRoutine(MessageDocument document) {
        return new Runnable() {
            @Override
            public void run() {
                //do classyfying
                String label = classifier.classify(document);
                LOGGER.info("Document "+document.getId()+" was calssified as "+label);
                document.setLabel(label);
                
                //find location entities in message content
                List<String> locationEntities = recognizer.recognizeLocations(document.getContent());
                //if no entities found and message document is from Facebook
                //get location from source
                if (locationEntities.isEmpty()) {
//                    if(document.getService().equals(Services.FACEBOOK.toString())){
//                        
//                    }
                    return;
                } else {
//                    List<Location> locations = geoTagger.geocode(locationEntities);
                    Location loc = geoTagger.geocode(locationEntities.get(0));
                    LOGGER.info("Found location x("+loc.getLatitude()+")"
                            + " ("+loc.getLongitude()+") for document "+document.getId());
                }
            }
        };
    }
}
