/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate.geonames;

import de.hsbo.fbg.sm4c.common.model.geonames.Admin2;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin02Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateBaseDao;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateAdmin02Dao extends HibernateBaseDao<Admin2> implements Admin02Dao {

    public HibernateAdmin02Dao(Session session) {
        super(session);
    }

}
