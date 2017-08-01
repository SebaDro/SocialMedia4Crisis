/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.CollectionStatusDao;
import de.hsbo.fbg.sm4c.common.model.CollectionStatus;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateCollectionStatusDao extends HibernateBaseDao<CollectionStatus> implements CollectionStatusDao {

    public HibernateCollectionStatusDao(Session session) {
        super(session);
    }

}
