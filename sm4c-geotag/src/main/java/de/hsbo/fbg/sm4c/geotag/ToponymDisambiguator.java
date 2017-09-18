/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag;

import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
import java.util.List;
import java.util.TreeMap;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class ToponymDisambiguator {

    private DaoFactory daoFactory;

    public ToponymDisambiguator() throws Exception {
        HibernateDatabaseConnection dbc;

        Session session;

        dbc = new HibernateDatabaseConnection();
        dbc.afterPropertiesSet();

        daoFactory = new HibernateDaoFactory(dbc);

    }

    public void disambiguateToponyms(List<String> toponyms) {
//        GeonameNode root = new GeonameNode();
//        try (Session session = (Session) daoFactory.initializeContext()) {
//            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
//            toponyms.forEach(t -> {
//                Geoname geoname = geonameDao.retrieveByName(t).get();
//                GeonameNode internalRoot = new GeonameNode();
//                GeonameNode tree = buildInternalTree(geoname, internalRoot);
//                tree.getEntity();
//            });
//        }

    }

    private GeonameNode buildInternalTree(GeonameEntity entity, GeonameNode node) {
        if (entity.getParent() != null) {
            GeonameNode parentNode = new GeonameNode();
            node.setParent(parentNode);
            node.setEntity(entity);
            parentNode.addChild(node);
            return buildInternalTree(entity.getParent(), parentNode);
        } else {
            node.setEntity(entity);
            return node;
        }
    }

}
