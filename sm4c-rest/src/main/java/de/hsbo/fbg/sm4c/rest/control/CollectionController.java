/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.classify.AbstractClassifier;
import de.hsbo.fbg.sm4c.classify.ModelManager;
import de.hsbo.fbg.sm4c.collect.AbstractReceiver;
import de.hsbo.fbg.sm4c.collect.Collector;
import de.hsbo.fbg.sm4c.collect.FacebookMessageHandler;
import de.hsbo.fbg.sm4c.collect.MessageSimulationReceiver;
import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionStatusDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.DatabaseException;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.dao.KeywordDao;
import de.hsbo.fbg.sm4c.common.dao.LabelDao;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.SocialMediaServiceDao;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.CollectionStatus;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.Keyword;
import de.hsbo.fbg.sm4c.common.model.Label;
import de.hsbo.fbg.sm4c.common.model.SocialMediaService;
import de.hsbo.fbg.sm4c.common.model.SourceType;
import de.hsbo.fbg.sm4c.rest.coding.CollectionDecoder;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.rest.coding.CollectionEncoder;
import de.hsbo.fbg.sm4c.rest.view.CollectionView;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.hsbo.fbg.sm4c.common.dao.SourceTypeDao;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoMessageDocumentDao;
import de.hsbo.fbg.sm4c.common.model.Status;
import de.hsbo.fbg.sm4c.rest.coding.MessageDocumentEncoder;
import de.hsbo.fbg.sm4c.rest.coding.ModelEncoder;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"application/json"})
public class CollectionController implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LogManager.getLogger(CollectionController.class);

    //time period for receiving Social Media messages in seconds
    private static final int RECEIVER_PERIOD = 30;

    @Autowired
    private DaoFactory<Session> daoFactory;

    @Autowired
    private CollectionDecoder collectionDecoder;

    @Autowired
    private CollectionEncoder collectionEncoder;

    @Autowired
    private ModelEncoder modelEncoder;

    @Autowired
    private ModelManager modelManager;

    @Autowired
    private MessageDocumentEncoder messageDocumentEncoder;

    @Autowired
    DocumentDaoFactory documentDaoFactory;

    @Autowired
    Collector collector;

    private HashMap<Long, AbstractReceiver> receiverList;

    private ScheduledExecutorService scheduler;

    @Override
    public void afterPropertiesSet() {
        if (Configuration.getConfig().getPropertyValue("mode").equals("simulation")) {
            try {
                this.scheduler = Executors.newScheduledThreadPool(1);
                prepareCollectionsForSimulation();
            } catch (Exception ex) {
                LOGGER.error("Can not prepare collections for simulation.", ex);
            }
        }
        receiverList = new HashMap();
    }

    @RequestMapping(value = "/collections", method = RequestMethod.POST)
    public ResponseEntity initiateCollection(@RequestBody CollectionView req) throws DatabaseException {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);

            Collection collection = collectionDecoder.decode(req);
            CollectionStatus status = retrieveCollectionStatus(Status.STOPPED.toString(), session);
            Set<Keyword> keywords = retrieveKeywords(req.getKeywords(), session);
            Set<Label> labels = retrieveLabels(req.getLabels(), session);
            Set<FacebookSource> sources = retrieveSources(req.getFacebookSources(), session);
            Set<SocialMediaService> services = retreiveServices(req.getServices(), session);

            collection.setStatus(status);
            collection.setKeywords(keywords);
            collection.setLabels(labels);
            collection.setSources(sources);
            collection.setServices(services);

            Collection coll = collectionDao.store(collection);
            if (coll == null) {
                throw new DatabaseException("Could not store collection");
            }
        } catch (RessourceNotFoundException ex) {
            LOGGER.error("The referenced ressource is not available", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    public List<CollectionView> getCollections() {
        List<CollectionView> result = new ArrayList();
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            List<Collection> collections = collectionDao.retrieve();
            result = collections.stream()
                    .map(c -> collectionEncoder.encodeReduced(c))
                    .sorted((e1, e2) -> Double.compare(e1.getId(), e2.getId()))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @RequestMapping(value = "/collections/{id}", method = RequestMethod.GET)
    public CollectionView getCollection(@PathVariable("id") String id) throws RessourceNotFoundException {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);

                CollectionView cv = collectionEncoder.encode(collection.get());
//                if (collection.get().getModel() != null) {
//                    cv.setModel(modelEncoder.encode(collection.get().getModel()));
//                    cv.setModelSummary(collection.get().getModel().getEvaluation().getClassDetails());
//                }
                cv.setDocumentCount(documentDao.count());
                cv.setUnlabeledCount(documentDao.countUnlabeled());
                return cv;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/start", method = RequestMethod.POST)
    public ResponseEntity startCollecting(@PathVariable("id") String id) throws DatabaseException, Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> col = collectionDao.retrieveById(Long.parseLong(id));
            if (col.isPresent()) {
                Collection collection = col.get();

                if (receiverList.containsKey(collection.getId())) {
                    if (!collection.getStatus().getName().equals("aktiv")) {
                        MessageSimulationReceiver receiver = (MessageSimulationReceiver) receiverList.get(collection.getId());
                        if (receiver.runSimulationReceiver(scheduler)) {
                            receiverList.put(collection.getId(), receiver);
                            CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
                            CollectionStatus status = statusDao.retrieveByName(Status.ACTIVE.toString()).get();
                            collection.setStatus(status);
                            collectionDao.update(collection);
                        }
                    }

                } else {

                    MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection);
                    MongoMessageDocumentDao documentDao = (MongoMessageDocumentDao) documentDaoFactory.createMessageDocumentDao(mongoCollection);
                    AbstractClassifier classifier = modelManager.deserializeModel(collection.getModel());

                    FacebookMessageHandler handler = new FacebookMessageHandler(classifier, documentDao);
                    DateTime start = new DateTime("2013-06-05T00:00:00.000+02:00");
                    DateTime end = new DateTime("2013-06-06T00:00:00.000+02:00");
                    MessageSimulationReceiver receiver = new MessageSimulationReceiver(collector, 30, start, end, collection);
                    receiver.addMessageHandler(handler);
                    if (receiver.runSimulationReceiver(scheduler)) {
                        receiverList.put(collection.getId(), receiver);
                        CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
                        CollectionStatus status = statusDao.retrieveByName(Status.ACTIVE.toString()).get();
                        collection.setStatus(status);
                        collectionDao.update(collection);
                    }
                }

            } else {
                throw new RessourceNotFoundException("The specified collection is not available");
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/collections/{id}/stop", method = RequestMethod.POST)
    public ResponseEntity stopCollecting(@PathVariable("id") String id) throws DatabaseException, Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> col = collectionDao.retrieveById(Long.parseLong(id));
            if (col.isPresent()) {
                Collection collection = col.get();
                if (receiverList.containsKey(collection.getId()) && !collection.getStatus().getName().equals("gestoppt")) {

                    MessageSimulationReceiver receiver = (MessageSimulationReceiver) receiverList.get(collection.getId());
                    if (receiver.stopReceiving()) {
                        CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
                        CollectionStatus status = statusDao.retrieveByName(Status.STOPPED.toString()).get();
                        collection.setStatus(status);
                        collectionDao.update(collection);
                    }
                }
            } else {
                throw new RessourceNotFoundException("The specified status is not available");
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private CollectionStatus retrieveCollectionStatus(String status, Session session) throws RessourceNotFoundException {
        CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
        Optional<CollectionStatus> result = statusDao.retrieveByName(status);
        if (!result.isPresent()) {
            throw new RessourceNotFoundException("The specified status is not available");
        }
        return result.get();
    }

    private Set<Keyword> retrieveKeywords(List<String> keywords, Session session) {
        KeywordDao keywordDao = daoFactory.createKeywordDao(session);
        Set<Keyword> result = new HashSet<>();
        keywords.forEach(k -> {
            Optional<Keyword> keyword = keywordDao.retrieveByName(k);
            if (keyword.isPresent()) {
                result.add(keyword.get());
            } else {
                Keyword keyw = new Keyword();
                keyw.setName(k);
                result.add(keyw);
            }
        });
        return result;
    }

    private Set<Label> retrieveLabels(List<String> labels, Session session) {
        LabelDao labelDao = daoFactory.createLabelDao(session);
        Set<Label> result = new HashSet<>();
        labels.forEach(l -> {
            Optional<Label> label = labelDao.retrieveByName(l);
            if (label.isPresent()) {
                result.add(label.get());
            } else {
                Label lab = new Label();
                lab.setName(l);
                result.add(lab);
            }
        });
        return result;
    }

    private Set<FacebookSource> retrieveSources(List<FacebookSourceView> facebookSources, Session session) {
        FacebookSourceDao sourceDao = daoFactory.createFacebookSourceDao(session);
        SourceTypeDao categoryDao = daoFactory.createSourceTypeDao(session);
        Set<FacebookSource> result = new HashSet<>();
        facebookSources.forEach(s -> {
            try {
                Optional<FacebookSource> source = sourceDao.retrieveByFacebookId(s.getFacebookId());
                if (source.isPresent()) {
                    result.add(source.get());
                } else {
                    FacebookSource sou = new FacebookSource();
                    Optional<SourceType> category = categoryDao.retrieveByName(s.getSourceType().getName());
                    if (!category.isPresent()) {
                        throw new RessourceNotFoundException("The specified source is not supported.");
                    }
                    sou.setType(category.get());
                    sou.setName(s.getName());
                    sou.setDescription(s.getDescription());
                    sou.setFacebookId(s.getFacebookId());
                    result.add(sou);

                }
            } catch (Exception ex) {
                LOGGER.error("Could not store source", ex);
            }

        });
        return result;
    }

    private Set<SocialMediaService> retreiveServices(List<String> services, Session session) {
        SocialMediaServiceDao serviceDao = daoFactory.createSocialMediaServiceDao(session);
        Set<SocialMediaService> result = new HashSet<>();
        services.forEach(s -> {
            try {
                Optional<SocialMediaService> service = serviceDao.retrieveByName(s);
                if (!service.isPresent()) {
                    throw new RessourceNotFoundException("The specified service is not available");
                }
                result.add(service.get());
            } catch (RessourceNotFoundException ex) {
                LOGGER.error("Could not find service", ex);
            }
        });
        return result;
    }

    private void prepareCollectionsForSimulation() throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
            CollectionStatus stoppedStatus = statusDao.retrieveByName(Status.STOPPED.toString()).get();

            List<Collection> collections = collectionDao.retrieve();
            collections.forEach(c -> {
                if (c.getStatus().getName().equals(Status.ACTIVE.toString())) {
                    c.setStatus(stoppedStatus);
                    collectionDao.update(c);
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        this.receiverList.values().forEach(r -> ((AbstractReceiver) r).stopReceiving());
    }

}
