/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.model.SourceType;
import org.hibernate.Session;
import de.hsbo.fbg.sm4c.common.dao.SourceTypeDao;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateSourceTypeDao extends HibernateBaseDao<SourceType> implements SourceTypeDao {

    public HibernateSourceTypeDao(Session session) {
        super(session);
    }

}
