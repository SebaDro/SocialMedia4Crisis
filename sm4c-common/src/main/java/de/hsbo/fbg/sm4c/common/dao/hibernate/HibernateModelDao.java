/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.ModelDao;
import de.hsbo.fbg.sm4c.common.model.Model;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateModelDao extends HibernateBaseDao<Model> implements ModelDao{

    public HibernateModelDao(Session session) {
        super(session);
    }

}
