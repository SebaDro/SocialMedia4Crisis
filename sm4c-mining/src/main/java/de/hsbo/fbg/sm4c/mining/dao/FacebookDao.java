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
import static com.mongodb.client.model.Filters.eq;
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
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookDao {

    private static final Logger LOGGER = LogManager.getLogger(FacebookDao.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private FacebookJSONEncoder fbEncoder;

    public FacebookDao(String collectionName) {
        mongoClient = new MongoClient(
                Configuration.getConfig().getPropertyValue("db_host"),
                Integer.parseInt(Configuration.getConfig().getPropertyValue("db_port")));
        database = mongoClient.getDatabase(
                Configuration.getConfig().getPropertyValue("db_name"));
        collection = database.getCollection(collectionName);
        fbEncoder = new FacebookJSONEncoder();
    }

    public void storeFaceBookPosts(List<Post> posts, Object source) {
        List<Document> postDocs = posts.stream().map(p -> Document.parse(fbEncoder
                .encodePost(p, source)))
                .collect(Collectors.toList());
        collection.insertMany(postDocs);
    }

    public void storeSingleFacebookPost(Post post, Object source) {
        Document postDoc = Document.parse(fbEncoder.encodePost(post, source));
        collection.insertOne(postDoc);
    }

    public List<String> getValues() {
        MongoCursor<Document> cursor = collection.find().iterator();
        ArrayList<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            resultList.add(doc.toJson());
        }
        return resultList;
    }

    public boolean existsPage(Page p) {
        return collection.find(eq("source.id", p.getId())).first() != null;

    }

    public boolean existsGroup(Group g) {
        return collection.find(eq("source.id", g.getId())).first() != null;
    }

    public void dropCollection() {
        collection.drop();
    }

}
