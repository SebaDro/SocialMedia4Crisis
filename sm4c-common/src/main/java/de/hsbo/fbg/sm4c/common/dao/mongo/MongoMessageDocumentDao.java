/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import java.util.Date;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.coding.MessageDocumentDecoder;
import de.hsbo.fbg.sm4c.common.coding.MessageDocumentEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Indexes;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sebastian Drost
 */
public class MongoMessageDocumentDao implements MessageDocumentDao {

    private static final Logger LOGGER = LogManager.getLogger(MongoMessageDocumentDao.class);

    private final MongoCollection<Document> dbCollection;
    private final MessageDocumentEncoder messageDocEncoder;
    private final MessageDocumentDecoder messageDocDecoder;

    public MongoMessageDocumentDao(MongoCollection dbCollection, MessageDocumentDecoder messageDocDecoder) {
        this.dbCollection = dbCollection;
        this.messageDocEncoder = new MessageDocumentEncoder();
        this.messageDocDecoder = messageDocDecoder;
    }

    public MongoMessageDocumentDao(String dbHost, int dbPort, String dbName, String dbCollection) {
        MongoClient mongoClient = new MongoClient(dbHost, dbPort);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        this.dbCollection = database.getCollection(dbCollection);
        messageDocEncoder = new MessageDocumentEncoder();
        messageDocDecoder = new MessageDocumentDecoder();
    }

    @Override
    public MessageDocument retrieveById(String id) {
        Document document = dbCollection.find(eq("messageId", id)).first();
        if(document != null){
            return messageDocDecoder.decodeFacebookMessage(document);
        }
        else{
            return null;
        }
    }

    @Override
    public List<MessageDocument> retrieveTrainingData() {

        FindIterable<Document> documents = dbCollection.find(eq("training", true));
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookMessages(documents);
        return messages;
    }

    @Override
    public List<MessageDocument> retrieve() {
        FindIterable<Document> documents = dbCollection.find();
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookMessages(documents);
        return messages;
    }

    @Override
    public List<MessageDocument> retrieveSimulationData() {
        FindIterable<Document> documents = dbCollection.find();
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookSimulationMessages(documents);
        return messages;
    }

    @Override
    public List<MessageDocument> retrieveForTimeSpan(Date startTime, Date endTime) {

        FindIterable<Document> documents = dbCollection.find(Filters.and(
                Filters.gte("creationTime", startTime),
                Filters.lte("creationTime", endTime)));
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookMessages(documents);
        return messages;
    }

    @Override
    public List<MessageDocument> retrieveUnlabeledData() {
        FindIterable<Document> documents = dbCollection.find(
                Filters.and(
                        Filters.eq("training", false),
                        Filters.eq("label", "")));
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookMessages(documents);

        return messages;
    }

    @Override
    public List<MessageDocument> retrieveLabeledData() {
        FindIterable<Document> documents = dbCollection.find(
                Filters.and(
                        Filters.eq("training", false),
                        Filters.not(Filters.eq("label", ""))
                ));
        List<MessageDocument> messages = messageDocDecoder.decodeFacebookMessages(documents);
        return messages;
    }

    @Override
    public boolean exists(MessageDocument doc) {
        return dbCollection.find(eq("messageId", doc.getId())).first() != null;
    }

    @Override
    public boolean containsSource(String sourceId) {
        return dbCollection.find(eq("source.id", sourceId)).first() != null;
    }

    @Override
    public void store(MessageDocument doc) {
        Document postDoc = messageDocEncoder.encodeMessageToDocument((FacebookMessageDocument) doc);
        dbCollection.insertOne(postDoc);
    }

    @Override
    public void store(List<MessageDocument> docs) {
        List<Document> postDocs = docs.stream().map(m -> messageDocEncoder
                .encodeMessageToDocument((FacebookMessageDocument) m))
                .collect(Collectors.toList());
        dbCollection.insertMany(postDocs);
    }

    @Override
    public void remove(MessageDocument doc) {
        dbCollection.deleteOne(eq("messageId", doc.getId()));
    }

    @Override
    public void removeAll() {
        dbCollection.drop();
    }

    @Override
    public void update(MessageDocument doc, String field) {
        Document updatedDoc = messageDocEncoder.encodeMessageToDocument((FacebookMessageDocument) doc);
        dbCollection.updateOne(
                eq("messageId", doc.getId()),
                new Document("$set", new Document(field, updatedDoc.get(field))));
    }

    @Override
    public void updateDefault(MessageDocument doc) {
        Document updatedDoc = messageDocEncoder.encodeMessageToDocument((FacebookMessageDocument) doc);
        dbCollection.updateOne(
                eq("messageId", doc.getId()),
                new Document("$set", new Document("label", updatedDoc.getString("label"))
                        .append("training", updatedDoc.getBoolean("training"))));
    }

    @Override
    public long count() {
        return dbCollection.count();
    }

    @Override
    public void createIndex() {
//        IndexOptions indexOptions = new IndexOptions().unique(true);
        dbCollection.createIndex(Indexes.hashed("messageId"));
    }

    @Override
    public long countUnlabeled() {
        return dbCollection.count(Filters.and(
                Filters.eq("training", false),
                Filters.eq("label", "")
        ));
    }

}
