/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.LabelDao;
import de.hsbo.fbg.sm4c.common.model.Label;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateLabelDao extends HibernateBaseDao<Label> implements LabelDao {

    public HibernateLabelDao(Session session) {
        super(session);
    }

}
