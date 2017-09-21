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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
//            GeonameNode root = new GeonameNode();
            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
            GeonameNode root = buildTree(toponyms, geonameDao);

            for (AmbiguityToponym t : toponyms) {
                HashMap<GeonameNode, ConceptualDensityParameters> resultMap = new HashMap();
                for (GeonameNode candidate : t.getCandidates()) {
                    GeonameNode synsetHierarchy = getSynsetHierarchy(candidate);
                    ConceptualDensityParameters parameters = calculateCDParameters(synsetHierarchy);
                    resultMap.put(candidate, parameters);
                }
                LinkedHashMap sortedResultMap = sortResultMap(resultMap);
                sortedResultMap.values().size();
            }
        }

    }

    public GeonameNode addToTree(GeonameNode subtree, GeonameNode treeBranch) {
        if (subtree.getChilren().containsKey(treeBranch.getEntity().getGeonameid())) {
            GeonameNode parentNode = subtree.getChilren().get(treeBranch.getEntity().getGeonameid());
            GeonameNode childNode = treeBranch.getChilren().values().iterator().next();
            addToTree(parentNode, childNode);
        } else {
            treeBranch.setParent(subtree);
            subtree.getChilren().put(treeBranch.getEntity().getGeonameid(), treeBranch);
        }
        return subtree;
    }

    public GeonameNode buildTreeBranch(GeonameEntity entity, GeonameNode node) {
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

    public GeonameNode getLeaf(GeonameNode treeBranch) {
        if (treeBranch.getChilren() != null && !treeBranch.getChilren().isEmpty()) {
            return getLeaf(treeBranch.getChilren().values().iterator().next());
        } else {
            return treeBranch;
        }

    }

    public GeonameNode buildTree(List<AmbiguityToponym> toponyms, GeonameDao geonameDao) {
        GeonameNode root = new GeonameNode();
        for (AmbiguityToponym t : toponyms) {
            List<Geoname> geonames = geonameDao.retrieveCandidatesByName(t.getToponym());
            for (Geoname geoname : geonames) {
                GeonameNode treeBranch = new GeonameNode();
                treeBranch = buildTreeBranch(geoname, treeBranch);
                GeonameNode treeBranchLeaf = getLeaf(treeBranch);
                t.addCandidate(treeBranchLeaf);
                root = addToTree(root, treeBranch);
            }
        }
        return root;
    }

    public GeonameNode getSynsetHierarchy(GeonameNode candidate) {
        boolean isSynset = true;
        GeonameNode rootToProof = candidate;
        GeonameNode result = candidate;

        while (isSynset) {
            if (rootToProof.getParent().getEntity() == null) {
                result = rootToProof;
                break;
            }

            isSynset = !exists(rootToProof.getParent(), candidate);
            result = rootToProof;
            rootToProof = rootToProof.getParent();
        }
        return result;
    }

    public ConceptualDensityParameters calculateCDParameters(GeonameNode synsetHierarchy) {
        ConceptualDensityParameters parameters = new ConceptualDensityParameters();
        int startCount = 0;
        int relevantSynsets = calculateRelevantSynsets(synsetHierarchy, startCount, false);
        if (relevantSynsets == 0) {
            relevantSynsets++;
        }
        startCount = 1;
        int totalSynsets = calculateTotalSynsets(synsetHierarchy, startCount);
        parameters.setRelevantSynsets(relevantSynsets);
        parameters.setTotalSynsets(totalSynsets);
        return parameters;
    }

    public int calculateTotalSynsets(GeonameNode tree, int start) {
        for (GeonameNode child : tree.getChilren().values()) {
            start++;
            start = calculateTotalSynsets(child, start);
        }
        return start;
    }

    public int calculateRelevantSynsets(GeonameNode tree, int start, boolean relevantSynsetsStarted) {
        if (tree.getChilren().size() > 1) {
            relevantSynsetsStarted = true;
        }
        for (GeonameNode child : tree.getChilren().values()) {
            if (relevantSynsetsStarted) {
                start++;
            }
            start = calculateRelevantSynsets(child, start, relevantSynsetsStarted);
        }
        return start;
    }

    public boolean exists(GeonameNode synsetRoot, GeonameNode candidate) {
        if (synsetRoot != candidate && synsetRoot.getEntity().getName().equals(candidate.getEntity().getName())) {
            return true;
        }
        boolean found = false;
        for (GeonameNode child : synsetRoot.getChilren().values()) {
            if (!found) {
                found = exists(child, candidate);
            }
        }
        return found;
    }

    public LinkedHashMap<GeonameNode, ConceptualDensityParameters> sortResultMap(HashMap<GeonameNode, ConceptualDensityParameters> resultMap) {
        LinkedHashMap<GeonameNode, ConceptualDensityParameters> sortedResultMap = resultMap.entrySet().stream()
                .sorted(Collections.reverseOrder((e1, e2) -> Double.compare(e1.getValue().getTotalSynsets(),
                        e2.getValue().getTotalSynsets())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sortedResultMap;
    }

}
