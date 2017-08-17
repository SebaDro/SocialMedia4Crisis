/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.ClassifierDao;
import de.hsbo.fbg.sm4c.common.model.Classifier;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateClassifierDao extends HibernateBaseDao<Classifier> implements ClassifierDao{

    public HibernateClassifierDao(Session session) {
        super(session);
    }

}
