/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.KeywordDao;
import de.hsbo.fbg.sm4c.common.model.Keyword;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateKeywordDao extends HibernateBaseDao<Keyword> implements KeywordDao {

    public HibernateKeywordDao(Session session) {
        super(session);
    }

}
