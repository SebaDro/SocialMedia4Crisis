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
import de.hsbo.fbg.sm4c.common.dao.FacebookMessageDocumentDao;
import de.hsbo.fbg.sm4c.common.model.Collection;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDocumentDaoFactory implements DocumentDaoFactory<MongoCollection> {

    private MongoDatabase database;

    public MongoDocumentDaoFactory() {

    }

    public MongoDocumentDaoFactory(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public FacebookMessageDocumentDao createFacebookMessageDocumentDao(MongoCollection daoContext) {
        return new MongoFacebookMessageDocumentDao(daoContext);
    }

    public MongoCollection getContext(Collection collection) {
        return database.getCollection(collection.getName());
    }

}
