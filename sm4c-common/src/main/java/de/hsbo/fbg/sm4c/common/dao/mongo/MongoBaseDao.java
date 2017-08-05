/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.sm4c.common.dao.BaseDao;
import de.hsbo.fbg.sm4c.common.model.AbstractEntity;
import de.hsbo.fbg.sm4c.common.model.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.core.GenericTypeResolver;

/**
 *
 * @author Sebastian Drost
 */
public class MongoBaseDao<T extends AbstractEntity> {

    private final Class<T> genericType;
    private final MongoCollection mongoCollection;

    public MongoBaseDao(MongoDatabase database, Collection collection) {
        this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), MongoBaseDao.class);
        this.mongoCollection = database.getCollection(collection.getName());
    }

    public Optional retrieveById(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Optional retrieveByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List retrieve() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean exists(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public T store(AbstractEntity o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public T update(AbstractEntity o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void remove(AbstractEntity r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
