/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.classify.AbstractClassifier;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.Location;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
import de.hsbo.fbg.sm4c.geotag.geocoding.FeatureServicePersister;
import de.hsbo.fbg.sm4c.geotag.geocoding.ToponymDisambiguator;
import de.hsbo.fbg.sm4c.geotag.geoparsing.Geoparser;
import de.hsbo.fbg.sm4c.geotag.geoparsing.Toponym;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookMessageHandler implements MessageHandler, InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(FacebookMessageHandler.class);

    private AbstractClassifier classifier;
    private Geoparser geoparser;
    private ToponymDisambiguator toponymDisambiguator;
    private FeatureServicePersister fsPersister;
    private MessageDocumentDao documentDao;

    public FacebookMessageHandler(AbstractClassifier classifier, MessageDocumentDao documentDao) {
        this.classifier = classifier;
        this.documentDao = documentDao;
        this.geoparser = new Geoparser();
        this.toponymDisambiguator = new ToponymDisambiguator();
        this.fsPersister = new FeatureServicePersister(Configuration.getConfig().getPropertyValue("featureService"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void processMessages(List<MessageDocument> documents, Collection collection) {

        documents.forEach(document -> {

            //do classyfying
            if (document == null || document.getContent() == null || document.getContent().isEmpty()) {
                return;
            }
            String label = classifier.classify(document);
            LOGGER.info("Document " + document.getId() + " was classified as " + label);
            document.setLabel(label);

            if (!document.getLabel().equals("Sonstiges")) {
                //find location entities in message content
                List<Toponym> toponyms = geoparser.recognizeLocations(document.getContent());

                //if no entities found and message document is from Facebook
                //get location from source
                if (toponyms.isEmpty()) {
                    //TODO determination of location from Facebook page or group
                } else if (toponyms.size() == 1) {
                    GeonameEntity entity = toponymDisambiguator.disambiguateSingleToponym(toponyms.get(0));
                    if (entity != null) {
                        Location location = new Location();
                        location.setLatitude(entity.getLatitude());
                        location.setLongitude(entity.getLongitude());
                        document.getLocations().add(location);
                    }
                } else {
                    List<GeonameEntity> entities = toponymDisambiguator.disambiguateToponyms(toponyms);
                    List<Location> locations = entities.stream().filter(e -> e != null).map(e -> {
                        Location location = new Location();
                        location.setLatitude(e.getLatitude());
                        location.setLongitude(e.getLongitude());
                        return location;
                    }).collect(Collectors.toList());
                    document.getLocations().addAll(locations);
                }
                if (!document.getLocations().isEmpty()) {
                    this.fsPersister.addDocumentAsFeature(document, collection);
                    LOGGER.info("Add document " + document.getId() + " to Feature-Service");
                }
            }
            documentDao.store(document);
            LOGGER.info("Store new document " + document.getId());

        });
    }

}
