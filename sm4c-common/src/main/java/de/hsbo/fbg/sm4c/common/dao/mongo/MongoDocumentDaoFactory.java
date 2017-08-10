/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDocumentDaoFactory implements DocumentDaoFactory<MongoCollection> {
//    @Autowired
    private MongoDatabaseConnection mongoConnection;

    private MongoDatabase database;

    public MongoDocumentDaoFactory() {
        database = mongoConnection.getDatabase();
    }

    public MongoDocumentDaoFactory(MongoDatabaseConnection connection) {
        this.mongoConnection = connection;
        this.database = connection.getDatabase();
    }

    @Override
    public MessageDocumentDao createFacebookMessageDocumentDao(MongoCollection daoContext) {
        return new MongoMessageDocumentDao(daoContext);
    }

    public MongoCollection getContext(Collection collection) {
        return database.getCollection(collection.getName());
    }

    public MongoCollection getContext(String collection) {
        return database.getCollection(collection);
    }

}
