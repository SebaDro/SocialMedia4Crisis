/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate.geonames;

import de.hsbo.fbg.sm4c.common.model.geonames.Admin4;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin04Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateBaseDao;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateAdmin04Dao extends HibernateBaseDao<Admin4> implements Admin04Dao {

    public HibernateAdmin04Dao(Session session) {
        super(session);
    }

}
