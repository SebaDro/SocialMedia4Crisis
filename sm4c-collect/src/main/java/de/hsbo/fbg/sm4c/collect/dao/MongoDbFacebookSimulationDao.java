/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.dao;

import de.hsbo.fbg.sm4c.common.dao.mongo.MongoMessageDocumentDao;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import de.hsbo.fbg.sm4c.collect.encode.FacebookSimulationDecoder;
import de.hsbo.fbg.sm4c.collect.encode.FacebookSimulationEncoder;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import facebook4j.Group;
import facebook4j.Page;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDbFacebookSimulationDao implements FacebookSimulationDao {

    private static final Logger LOGGER = LogManager.getLogger(MongoMessageDocumentDao.class);

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    private final FacebookSimulationEncoder fbEncoder;
    private final FacebookSimulationDecoder fbDecoder;

    public MongoDbFacebookSimulationDao(String dbHost, int dbPort, String dbName, String dbCollection) {
        mongoClient = new MongoClient(dbHost, dbPort);
        database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(dbCollection);
        fbEncoder = new FacebookSimulationEncoder();
        fbDecoder = new FacebookSimulationDecoder();
    }

    @Override
    public void storeFacebookMessages(List<FacebookSimulationMessageDocument> messages) {
        List<Document> postDocs = messages.stream().map(m -> Document.parse(fbEncoder
                .encodeMessageToJson(m))
                .append("creationTime", m.getCreationTime().toDate())
                .append("updateTime", m.getUpdateTime().toDate()))
                .collect(Collectors.toList());
        collection.insertMany(postDocs);
    }

    @Override
    public void storeSingleFacebookMessage(FacebookSimulationMessageDocument message) {
        Document postDoc = Document.parse(fbEncoder.encodeMessageToJson(message))
                .append("creationTime", message.getCreationTime().toDate())
                .append("updateTime", message.getUpdateTime().toDate());
        collection.insertOne(postDoc);
    }

    @Override
    public List<String> getValues() {
        MongoCursor<Document> cursor = collection.find().iterator();
        ArrayList<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            resultList.add(doc.toJson());
        }
        return resultList;
    }

    @Override
    public List<FacebookSimulationMessageDocument> getValuesForTimeSpan(Date startTime, Date endTime) {
        ArrayList<FacebookSimulationMessageDocument> messages = new ArrayList<FacebookSimulationMessageDocument>();
        FindIterable<Document> documents = collection.find(Filters.and(
                Filters.gte("creationTime", startTime),
                Filters.lte("creationTime", endTime)));
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document d) {
                messages.add(fbDecoder.decodeFacebookMessage((Document) d));
            }
        });
        return messages;
    }

    @Override
    public boolean containsPage(Page page) {
        return collection.find(eq("source.id", page.getId())).first() != null;
    }

    @Override
    public boolean containsGroup(Group group) {
        return collection.find(eq("source.id", group.getId())).first() != null;
    }

    @Override
    public boolean containsMessage(FacebookSimulationMessageDocument message) {
        return collection.find(eq("messageId", message.getId())).first() != null;
    }

    @Override
    public void deleteAllValues() {
        collection.drop();
    }

    @Override
    public FacebookSimulationMessageDocument getValueByFbId(String fbId) {
        Document doc = collection.find(eq("messageId", fbId)).first();
        FacebookSimulationMessageDocument message = null;
        if (doc != null) {
            message = fbDecoder.decodeFacebookMessage(doc);
        }
        return message;
    }

    @Override
    public void deleteValue(FacebookMessageDocument message) {
        collection.deleteOne(eq("messageId", message.getId()));
    }
}
