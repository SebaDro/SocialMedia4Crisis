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
import de.hsbo.fbg.sm4c.geoparsing.AmbiguityToponym;
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

    public void disambiguateToponyms(List<AmbiguityToponym> toponyms) {

        try (Session session = (Session) daoFactory.initializeContext()) {
            GeonameNode root = new GeonameNode();
            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
            for (AmbiguityToponym t : toponyms) {
                List<Geoname> geonames = geonameDao.retrieveCandidatesByName(t.getToponym());
                for (Geoname geoname : geonames) {
                    GeonameNode treeBranch = new GeonameNode();
                    treeBranch = buildTreeBranch(geoname, treeBranch);
                    GeonameNode treeBranchLeaf = getLeaf(treeBranch);
                    t.addCandidate(treeBranchLeaf);
                    root = buildTree(root, treeBranch);
                }
            }
            for (AmbiguityToponym t : toponyms) {
                // TODO calculate Conceptual Density
                
            }
        }

    }

    private GeonameNode buildTree(GeonameNode subtree, GeonameNode treeBranch) {
        if (subtree.getChilren().containsKey(treeBranch.getEntity().getGeonameid())) {
            GeonameNode parentNode = subtree.getChilren().get(treeBranch.getEntity().getGeonameid());
            GeonameNode childNode = treeBranch.getChilren().values().iterator().next();
            buildTree(parentNode, childNode);
        } else {
            treeBranch.setParent(subtree);
            subtree.getChilren().put(treeBranch.getEntity().getGeonameid(), treeBranch);
        }
        return subtree;
    }

    private GeonameNode buildTreeBranch(GeonameEntity entity, GeonameNode node) {
        if (entity.getParent() != null) {
            GeonameNode parentNode = new GeonameNode();
            node.setParent(parentNode);
            node.setEntity(entity);
            parentNode.addChild(node);
            return buildTreeBranch(entity.getParent(), parentNode);
        } else {
            node.setEntity(entity);
            return node;
        }
    }

//    private GeonameNode buildInternalTree2(GeonameEntity entity, GeonameNode node) {
//        entity
//    }
    private GeonameNode getLeaf(GeonameNode treeBranch) {
        if (treeBranch.getChilren() != null && !treeBranch.getChilren().isEmpty()) {
            return getLeaf(treeBranch.getChilren().values().iterator().next());
        } else {
            return treeBranch;
        }

    }
}
