/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate.geonames;

import de.hsbo.fbg.sm4c.common.model.geonames.Admin3;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin03Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateBaseDao;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateAdmin03Dao extends HibernateBaseDao<Admin3> implements Admin03Dao {

    public HibernateAdmin03Dao(Session session) {
        super(session);
    }

}
