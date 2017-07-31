/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.model.Collection;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateDaoIT {

    private HibernateDatabaseConnection hibernateDatabaseConnection;
    private Session session;
    private HibernateCollectionDao collectionDao;

    @Before
    public void setup() throws Exception {
        hibernateDatabaseConnection = new HibernateDatabaseConnection();
        hibernateDatabaseConnection.afterPropertiesSet();
        session = hibernateDatabaseConnection.createSession();
        collectionDao = new HibernateCollectionDao(session);
    }

    @Test
    public void roundTrip() {
        Collection collection = new Collection();
        collection.setName("Hochwasser");
        collection.setDescription("Hochwasser 2013");

        collectionDao.store(collection);
        Optional<Collection> collectionEntity = collectionDao.retrieveById(collection.getId());

        Assert.assertThat(collectionEntity.get().getName(), CoreMatchers.equalTo(collection.getName()));
        Assert.assertThat(collectionEntity.get().getDescription(), CoreMatchers.equalTo(collection.getDescription()));

        collectionDao.remove(collection);
    }

    @After
    public void shutDown() {
        try {
            hibernateDatabaseConnection.destroy();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
