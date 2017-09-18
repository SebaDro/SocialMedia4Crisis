/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate.geonames;

import de.hsbo.fbg.sm4c.common.model.geonames.Admin1;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin01Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateBaseDao;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateAdmin01Dao extends HibernateBaseDao<Admin1> implements Admin01Dao {

    public HibernateAdmin01Dao(Session session) {
        super(session);
    }

}
