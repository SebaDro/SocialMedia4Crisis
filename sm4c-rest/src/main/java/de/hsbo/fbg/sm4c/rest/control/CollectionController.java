/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.sm4c.collect.FacebookCollector;
import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionStatusDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
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
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDatabaseConnection;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.rest.coding.MessageDocumentEncoder;
import de.hsbo.fbg.sm4c.rest.coding.ModelEncoder;
import de.hsbo.fbg.sm4c.rest.view.MessageDocumentView;
import de.hsbo.fbg.sm4c.rest.view.TimeDefinitionView;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"application/json"})
public class CollectionController implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(CollectionController.class);

    @Autowired
    private DaoFactory<Session> daoFactory;

    @Autowired
    private CollectionDecoder collectionDecoder;

    @Autowired
    private CollectionEncoder collectionEncoder;

    @Autowired
    private ModelEncoder modelEncoder;

    @Autowired
    private MessageDocumentEncoder messageDocumentEncoder;

    DocumentDaoFactory documentDaoFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        MongoDatabaseConnection con = new MongoDatabaseConnection();
        con.afterPropertiesSet();
        documentDaoFactory = new MongoDocumentDaoFactory(con);
    }

    @RequestMapping(value = "/collections", method = RequestMethod.POST)
    public ResponseEntity initiateCollection(@RequestBody CollectionView req) {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);

            Collection collection = collectionDecoder.decode(req);
            CollectionStatus status = retrieveCollectionStatus(req.getStatus(), session);
            Set<Keyword> keywords = retrieveKeywords(req.getKeywords(), session);
            Set<Label> labels = retrieveLabels(req.getLabels(), session);
            Set<FacebookSource> sources = retrieveSources(req.getFacebookSources(), session);
            Set<SocialMediaService> services = retreiveServices(req.getServices(), session);

            collection.setStatus(status);
            collection.setKeywords(keywords);
            collection.setLabels(labels);
            collection.setSources(sources);
            collection.setServices(services);

            collectionDao.store(collection);
        } catch (RessourceNotFoundException ex) {
            LOGGER.error("Could not store collection", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/collections/{id}/documents", method = RequestMethod.POST)
    public ResponseEntity initiateCollection(@PathVariable("id") String id, @RequestBody TimeDefinitionView req) throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
//                FacebookCollector collector = new FacebookCollector(collection.get());
//                List<MessageDocument> documents = collector.collectMessages(req.getStartTime(), req.getEndTime());

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createFacebookMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieve();
//                documentDao.store(documents);
                return new ResponseEntity(documents.size(), HttpStatus.OK);
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    public List<CollectionView> getCollections() {
        List<CollectionView> result = new ArrayList();
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            List<Collection> collections = collectionDao.retrieve();
            result = collections.stream()
                    .map(c -> collectionEncoder.encode(c))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @RequestMapping(value = "/collections/{id}", method = RequestMethod.GET)
    public CollectionView getCollection(@PathVariable("id") String id) throws RessourceNotFoundException {
        List<CollectionView> result = new ArrayList();
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createFacebookMessageDocumentDao(mongoCollection);

                CollectionView cv = collectionEncoder.encode(collection.get());
                if (collection.get().getModel() != null) {
                    cv.setModel(modelEncoder.encode(collection.get().getModel()));
                }
                cv.setDocumentCount(documentDao.count());
                return cv;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/documents/limit/{limit}", method = RequestMethod.GET)
    public List<MessageDocumentView> getCollectionDocuments(@PathVariable("id") String id, @PathVariable("limit") String limit) throws RessourceNotFoundException {
        List<MessageDocumentView> result = new ArrayList();

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createFacebookMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveUnlabeledData(Integer.parseInt(limit));
                result = documents.stream()
                        .map(d -> messageDocumentEncoder.encode(d))
                        .collect(Collectors.toList());
                return result;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
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
                Optional<FacebookSource> source = sourceDao.retrieveByName(s.getName());
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

}
