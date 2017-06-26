/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.sm4c.mining.config.Configuration;
import de.hsbo.fbg.sm4c.mining.encode.FacebookJSONEncoder;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDbFacebookDao implements FacebookDao {

    private static final Logger LOGGER = LogManager.getLogger(MongoDbFacebookDao.class);

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    private final FacebookJSONEncoder fbEncoder;

    public MongoDbFacebookDao(String dbHost, int dbPort, String dbName, String dbCollection) {
        mongoClient = new MongoClient(dbHost, dbPort);
        database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(dbCollection);
        fbEncoder = new FacebookJSONEncoder();
    }

    @Override
    public void storeFaceBookPosts(List<Post> posts, Object source) {
        List<Document> postDocs = posts.stream().map(p -> Document.parse(fbEncoder
                .encodePost(p, source)))
                .collect(Collectors.toList());
        collection.insertMany(postDocs);
    }

    @Override
    public void storeSingleFacebookPost(Post post, Object source) {
        Document postDoc = Document.parse(fbEncoder.encodePost(post, source));
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
    public boolean containsPage(Page page) {
        return collection.find(eq("source.id", page.getId())).first() != null;
    }

    @Override
    public boolean containsGroup(Group group) {
        return collection.find(eq("source.id", group.getId())).first() != null;
    }

    @Override
    public boolean containsPost(Post post) {
        return collection.find(eq("fb_post.id", post.getId())).first() != null;
    }

    @Override
    public void deleteValues() {
        collection.drop();
    }

}
