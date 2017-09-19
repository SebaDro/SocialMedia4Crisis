/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate.geonames;

import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateBaseDao;
import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Seba
 */
public class HibernateGeonameDao extends HibernateBaseDao<Geoname> implements GeonameDao {

    public HibernateGeonameDao(Session session) {
        super(session);
    }

    @Override
    public List<Geoname> retrieveCandidatesByName(String name) {
        return retrieveAllByKey("name", name);
    }

}
