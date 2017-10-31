/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.sm4c.classify.AbstractClassifier;
import de.hsbo.fbg.sm4c.classify.ClassifierFactory;
import de.hsbo.fbg.sm4c.classify.ModelManager;
import de.hsbo.fbg.sm4c.classify.train.Dataset;
import de.hsbo.fbg.sm4c.classify.train.DatasetBuilder;
import de.hsbo.fbg.sm4c.common.dao.ClassifierDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDatabaseConnection;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.model.Classifier;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.EvaluationResult;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Model;
import de.hsbo.fbg.sm4c.rest.coding.MessageDocumentEncoder;
import de.hsbo.fbg.sm4c.rest.view.MessageDocumentView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ModelController implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(ModelController.class);

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

    @RequestMapping(value = "/collections/{id}/model/evaluate", method = RequestMethod.POST, produces = {"text/plain"})
    public String evaluateCollectionModel(@PathVariable("id") String id, @RequestBody String req) throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection.get());
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveTrainingData();

                ClassifierFactory factory = new ClassifierFactory();
                AbstractClassifier classifier = factory.createClassifier(ClassifierFactory.NAIVE_BAYES_MULTINOMIAL);
                DatasetBuilder builder = new DatasetBuilder();
                Dataset trainingData = builder.createDataset(collection.get());
                builder.add(trainingData, documents);
                EvaluationResult evalResult = classifier.evaluate(trainingData);

//                ModelView modelView = new ModelView();
//                modelView.setClassDetails(evalResult.getClassDetails());
//                modelView.setConfusionMatrix(evalResult.getConfusionMatrix());
//                modelView.setSummary(evalResult.getSummary());
                return evalResult.getClassDetails();
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/model", method = RequestMethod.POST, produces = {"text/plain"})
    public String initiateCollectionModel(@PathVariable("id") String id, @RequestBody String req) throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> col = collectionDao.retrieveById(Long.parseLong(id));
            if (col.isPresent()) {
                Collection collection = col.get();
                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection);
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveTrainingData();

                ClassifierFactory factory = new ClassifierFactory();
                AbstractClassifier classifier = factory.createClassifier(ClassifierFactory.SVM);
                DatasetBuilder builder = new DatasetBuilder();
                Dataset trainingData = builder.createDataset(collection);
                builder.add(trainingData, documents);

                classifier.setFormat(trainingData);
                classifier.trainClassifier();

                EvaluationResult result = classifier.evaluate(trainingData);
//                EvaluationResultDao evalResultDao = daoFactory.createEvaluationResultDao(session);
//                result = evalResultDao.store(result);

                ModelManager manager = new ModelManager();
                String[] paths = manager.serializeModel(classifier, collection);

                Model model = new Model();
                Classifier cls = retrieveClassifier(ClassifierFactory.SVM, session);
                model.setClassifier(cls);
                model.setClassifierPath(paths[0]);
                model.setInputDataPath(paths[1]);
                model.setEvaluation(result);

                collection.setModel(model);
                collectionDao.update(collection);

//                ModelEncoder modelEncoder = new ModelEncoder();
//                ModelView modelView = modelEncoder.encode(model);
                return result.getClassDetails();
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/model", method = RequestMethod.PUT, produces = {"text/plain"})
    public String updateCollectionModel(@PathVariable("id") String id, @RequestBody String req) throws Exception {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> col = collectionDao.retrieveById(Long.parseLong(id));
            if (col.isPresent()) {
                Collection collection = col.get();
                MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(collection);
                MessageDocumentDao documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);
                List<MessageDocument> documents = documentDao.retrieveTrainingData();

                ClassifierFactory factory = new ClassifierFactory();
                AbstractClassifier classifier = factory.createClassifier(ClassifierFactory.SVM);
                DatasetBuilder builder = new DatasetBuilder();
                Dataset trainingData = builder.createDataset(collection);
                builder.add(trainingData, documents);

                classifier.setFormat(trainingData);
                classifier.trainClassifier();

                EvaluationResult result = classifier.evaluate(trainingData);
//                EvaluationResultDao evalResultDao = daoFactory.createEvaluationResultDao(session);
//                result = evalResultDao.store(result);

                ModelManager manager = new ModelManager();
                String[] paths = manager.serializeModel(classifier, collection);

                Classifier cls = retrieveClassifier(ClassifierFactory.SVM, session);
                collection.getModel().setClassifier(cls);
                collection.getModel().setClassifierPath(paths[0]);
                collection.getModel().setInputDataPath(paths[1]);
                collection.getModel().setEvaluation(result);

                collectionDao.update(collection);

//                ModelEncoder modelEncoder = new ModelEncoder();
//                ModelView modelView = modelEncoder.encode(model);
                return result.getClassDetails();
            }
            throw new RessourceNotFoundException("The referenced collection is not available");
        }
    }

    @RequestMapping(value = "/collections/{id}/model", method = RequestMethod.GET, produces = {"plain/text"})
    public String getModelStatistics(@PathVariable("id") String id) throws RessourceNotFoundException {
        List<MessageDocumentView> result = new ArrayList();

        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);
            Optional<Collection> collection = collectionDao.retrieveById(Long.parseLong(id));
            if (collection.isPresent()) {
                if (collection.get().getModel() != null) {
                    return collection.get().getModel().getEvaluation().getClassDetails();
                }
                else{
                    throw new RessourceNotFoundException("The referenced model is not available");
                }
            } else {
                throw new RessourceNotFoundException("The referenced collection is not available");
            }

        }
    }

    private Classifier retrieveClassifier(String type, Session session) throws RessourceNotFoundException {
        ClassifierDao classifierDao = daoFactory.createClassifierDao(session);
        Optional<Classifier> classifier = classifierDao.retrieveByName(type);
        if (!classifier.isPresent()) {
            throw new RessourceNotFoundException("The specified classifier is not available");
        }
        return classifier.get();
    }
}
