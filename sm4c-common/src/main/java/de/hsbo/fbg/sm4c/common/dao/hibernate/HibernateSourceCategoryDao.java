/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.SourceCategoryDao;
import de.hsbo.fbg.sm4c.common.model.SourceCategory;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateSourceCategoryDao extends HibernateBaseDao<SourceCategory> implements SourceCategoryDao {

    public HibernateSourceCategoryDao(Session session) {
        super(session);
    }

}
