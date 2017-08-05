/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import java.util.Date;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import java.util.function.Consumer;
import java.util.logging.Level;
import de.hsbo.fbg.sm4c.common.dao.FacebookMessageDocumentDao;
import static com.mongodb.client.model.Filters.eq;
import de.hsbo.fbg.sm4c.common.coding.FacebookMessageDocumentDecoder;
import de.hsbo.fbg.sm4c.common.coding.FacebookMessageDocumentEncoder;
import de.hsbo.fbg.sm4c.common.model.Collection;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.GenericTypeResolver;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;

/**
 *
 * @author Sebastian Drost
 */
public class MongoFacebookMessageDocumentDao implements FacebookMessageDocumentDao {

    private static final Logger LOGGER = LogManager.getLogger(MongoFacebookMessageDocumentDao.class);

    private final MongoCollection<Document> dbCollection;
    private final FacebookMessageDocumentEncoder messageDocEncoder;
    private final FacebookMessageDocumentDecoder messageDocDecoder;

    public MongoFacebookMessageDocumentDao(MongoCollection dbCollection) {
        this.dbCollection = dbCollection;
        messageDocEncoder = new FacebookMessageDocumentEncoder();
        messageDocDecoder = new FacebookMessageDocumentDecoder();
    }

    @Override
    public FacebookMessageDocument retrieveById(String id) {
        Document doc = dbCollection.find(eq("messageId", id)).first();
        FacebookMessageDocument message = null;
        if (doc != null) {
            try {
                message = messageDocDecoder.decodeFacebookMessage(doc);
            } catch (Exception ex) {
                LOGGER.error("Could not load the refenrenced message", ex);
                java.util.logging.Logger.getLogger(MongoFacebookMessageDocumentDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return message;
    }

    @Override
    public List<FacebookMessageDocument> retrieve() {
        ArrayList<FacebookMessageDocument> messages = new ArrayList<FacebookMessageDocument>();
        FindIterable<Document> documents = dbCollection.find();
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document d) {
                try {
                    messages.add(messageDocDecoder.decodeFacebookMessage((Document) d));
                } catch (Exception ex) {
                    LOGGER.error("Could not load the referenced message", ex);
                }
            }
        });
        return messages;
    }

    @Override
    public List<FacebookMessageDocument> retrieveForTimeSpan(Date startTime, Date endTime) {
        ArrayList<FacebookMessageDocument> messages = new ArrayList<FacebookMessageDocument>();
        FindIterable<Document> documents = dbCollection.find(Filters.and(
                Filters.gte("creationTime", startTime),
                Filters.lte("creationTime", endTime)));
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document d) {
                try {
                    messages.add(messageDocDecoder.decodeFacebookMessage((Document) d));
                } catch (Exception ex) {
                    LOGGER.error("Could not load the referenced message", ex);
                }
            }
        });
        return messages;
    }

    @Override
    public boolean exists(FacebookMessageDocument doc) {
        return dbCollection.find(eq("messageId", doc.getId())).first() != null;
    }

    @Override
    public void store(FacebookMessageDocument doc) {
        Document postDoc = messageDocEncoder.encodeMessageToDocument(doc);
        dbCollection.insertOne(postDoc);
    }

    @Override
    public void store(List<FacebookMessageDocument> docs) {
        List<Document> postDocs = docs.stream().map(m -> messageDocEncoder
                .encodeMessageToDocument(m))
                .collect(Collectors.toList());
        dbCollection.insertMany(postDocs);
    }

    @Override
    public void remove(FacebookMessageDocument doc) {
        dbCollection.deleteOne(eq("messageId", doc.getId()));
    }

    @Override
    public void removeAll() {
        dbCollection.drop();
    }

    //    @Override
//    public boolean containsPage(Page page) {
//        return dbCollection.find(eq("source.id", page.getId())).first() != null;
//    }
//
//    @Override
//    public boolean containsGroup(Group group) {
//        return dbCollection.find(eq("source.id", group.getId())).first() != null;
//    }
}
