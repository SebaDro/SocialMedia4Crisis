/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateDaoFactory implements DaoFactory<Session> {

    @Autowired
    private HibernateDatabaseConnection hibernateConnection;

    @Override
    public Session initializeContext() {
        return hibernateConnection.createSession();
    }

    @Override
    public CollectionDao createCollectionDao(Session daoContext) {
        return new HibernateCollectionDao(daoContext);
    }

}
