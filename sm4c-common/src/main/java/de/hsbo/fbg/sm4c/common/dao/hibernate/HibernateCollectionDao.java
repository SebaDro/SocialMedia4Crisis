/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.model.Collection;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateCollectionDao extends HibernateBaseDao<Collection> implements CollectionDao {

    public HibernateCollectionDao(Session session) {
        super(session);
    }

}
