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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookDao {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public FacebookDao() {
        mongoClient = new MongoClient(
                Configuration.getConfig().getPropertyValue("db_host"),
                Integer.parseInt(Configuration.getConfig().getPropertyValue("db_port")));
        database = mongoClient.getDatabase(
                Configuration.getConfig().getPropertyValue("db_name"));
        collection = database.getCollection(
                Configuration.getConfig().getPropertyValue("db_fb_collection"));
    }

    public void storeFacebookPosts(List<String> postsJoson) {
        List<Document> documents = postsJoson.stream()
                .map(p -> Document.parse(p)).collect(Collectors.toList());
        collection.insertMany(documents);
    }

    public void storeSingleFacebookPost(String postJson) {
        Document postDoc = Document.parse(postJson);
        collection.insertOne(postDoc);
    }

    public ArrayList<String> getValues() {
        MongoCursor<Document> cursor = collection.find().iterator();
        ArrayList<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            resultList.add(doc.toJson());
        }
        return resultList;
    }

}
