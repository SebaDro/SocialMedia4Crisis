/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.sm4c.collect.FacebookCollector;
import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDatabaseConnection;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.rest.coding.MessageDocumentEncoder;
import de.hsbo.fbg.sm4c.rest.view.MessageDocumentView;
import de.hsbo.fbg.sm4c.rest.view.TimeDefinitionView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"application/json"})
public class DocumentController implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(DocumentController.class);

    @Autowired
    private DaoFactory<Session> daoFactory;

    @Autowired
    private MessageDocumentEncoder messageDocumentEncoder;

    @Autowired
    DocumentDaoFactory documentDaoFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
//        MongoDatabaseConnection con = new MongoDatabaseConnection();
//        con.afterPropertiesSet();
//        documentDaoFactory = new MongoDocumentDaoFactory(con);
    }

    @RequestMapping(value = "/collections/{id}/documents", method = RequestMethod.POST)
    public ResponseEntity initiateCollectionDocuments(@PathVariable("id") String id, @RequestBody TimeDefinitionView req) throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
                FacebookCollector collector = new FacebookCollector(collection.get());
                List<MessageDocument> documents = collector.collectMessages(req.getStartTime(), req.getEndTime());

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
//                List<MessageDocument> documents = documentDao.retrieve();
                documentDao.store(documents);
                documentDao.createIndex();
                return new ResponseEntity(documents.size(), HttpStatus.OK);
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/documents/unlabeled/limit/{limit}", method = RequestMethod.GET)
    public List<MessageDocumentView> getUnlabeledCollectionDocuments(@PathVariable("id") String id, @PathVariable("limit") String limit) throws RessourceNotFoundException {
        List<MessageDocumentView> result = new ArrayList();

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveUnlabeledData();

                if (documents.isEmpty()) {
                    throw new RessourceNotFoundException("There are no unlabeled documents");
                }

                Random random = new Random();
                random.setSeed(42);
                Collections.shuffle(documents, random);

                documents = documents.subList(0, Integer.parseInt(limit));
                result = documents.stream()
                        .map(d -> messageDocumentEncoder.encode(d))
                        .collect(Collectors.toList());
                return result;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/documents/labeled", method = RequestMethod.GET)
    public List<MessageDocumentView> getLabeledCollectionDocuments(@PathVariable("id") String id) throws RessourceNotFoundException {
        List<MessageDocumentView> result = new ArrayList();

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveLabeledData();

                if (documents.isEmpty()) {
                    throw new RessourceNotFoundException("There are no labeled documents");
                }

                result = documents.stream()
                        .map(d -> messageDocumentEncoder.encode(d))
                        .collect(Collectors.toList());
                return result;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/documents/training", method = RequestMethod.GET)
    public List<MessageDocumentView> getTrainingCollectionDocuments(@PathVariable("id") String id) throws RessourceNotFoundException {
        List<MessageDocumentView> result = new ArrayList();

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveTrainingData();
                result = documents.stream()
                        .map(d -> messageDocumentEncoder.encode(d))
                        .collect(Collectors.toList());
                return result;
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{collection_id}/documents/{id}", method = RequestMethod.GET)
    public MessageDocumentView getTrainingCollectionDocument(@PathVariable("collection_id") String collectionId, @PathVariable("id") String id) throws RessourceNotFoundException {

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(collectionId));
            if (collection.isPresent()) {

                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                MessageDocument result = documentDao.retrieveById(id);

                return messageDocumentEncoder.encode(result);
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

}
