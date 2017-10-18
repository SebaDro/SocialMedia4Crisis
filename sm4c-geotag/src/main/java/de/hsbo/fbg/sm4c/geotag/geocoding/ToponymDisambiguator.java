/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geocoding;

import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
import de.hsbo.fbg.sm4c.geotag.geoparsing.Toponym;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class ToponymDisambiguator {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ToponymDisambiguator.class);
    
    private DaoFactory daoFactory;
    
    public ToponymDisambiguator() {
        HibernateDatabaseConnection dbc;
        dbc = new HibernateDatabaseConnection();
        try {
            dbc.afterPropertiesSet();
        } catch (Exception ex) {
            LOG.error("Can't instantiate DB session.", ex);
        }
        
        daoFactory = new HibernateDaoFactory(dbc);
    }
    
    public GeonameEntity disambiguateSingleToponym(Toponym toponym) {
        try (Session session = (Session) daoFactory.initializeContext()) {
            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
            List<Geoname> geonameCandidates = geonameDao.retrieveCandidatesByName(toponym.getToponym());
            List<GeonameEntity> candidates = geonameCandidates.stream().map(g -> (GeonameEntity) g).collect(Collectors.toList());
            if (candidates.isEmpty()) {
                return null;
            } else {
                GeonameEntity result = null;
                result = calculateHighestPopulationEntity(candidates);
                return result;
            }
        }
    }
    
    public List<GeonameEntity> disambiguateToponyms(List<Toponym> toponyms) {
        List<GeonameEntity> disambiguatedToponyms = new ArrayList();
        
        try (Session session = (Session) daoFactory.initializeContext()) {
//            GeonameNode root = new GeonameNode();
            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
            GeonameNode root = buildTree(toponyms, geonameDao);
            
            for (Toponym t : toponyms) {
                if (t.getCandidates().isEmpty()) {
                    continue;
                }
                if (t.getCandidates().size() == 1) {
                    disambiguatedToponyms.add(t.getCandidates().get(0).getEntity());
                    continue;
                }
                HashMap<GeonameNode, ConceptualDensityParameters> candidateParameters = new HashMap();
                for (GeonameNode candidate : t.getCandidates()) {
                    GeonameNode synsetHierarchy = getSynsetHierarchy(candidate);
                    ConceptualDensityParameters parameters = calculateCDParameters(synsetHierarchy);
                    candidateParameters.put(candidate, parameters);
                }
//                LinkedHashMap<GeonameNode, ConceptualDensityParameters> sortedParameterMap = sortParameterMap(candidateParameters);
                GeonameEntity disambiguatedToponym = calculateHighestCDEntity(candidateParameters);
                disambiguatedToponyms.add(disambiguatedToponym);
            }
        } catch (Exception ex) {
            LOG.error("Could not disambiguate toponyms", ex);
        }
        return disambiguatedToponyms;
    }
    
    public GeonameNode addToTree(GeonameNode subtree, GeonameNode treeBranch) {
        if (subtree.getChilren().containsKey(treeBranch.getEntity().getGeonameid())) {
            GeonameNode parentNode = subtree.getChilren().get(treeBranch.getEntity().getGeonameid());
            if (treeBranch.getChilren().values().iterator().hasNext()) {
                GeonameNode childNode = treeBranch.getChilren().values().iterator().next();
                addToTree(parentNode, childNode);
            } else {
                LOG.debug("No children");
                addToTree(parentNode, treeBranch);
            }
            
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
    
    public GeonameNode buildTree(List<Toponym> toponyms, GeonameDao geonameDao) {
        GeonameNode root = new GeonameNode();
        for (Toponym t : toponyms) {
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
    
    public LinkedHashMap<GeonameNode, ConceptualDensityParameters> sortParameterMap(HashMap<GeonameNode, ConceptualDensityParameters> resultMap) {
        LinkedHashMap<GeonameNode, ConceptualDensityParameters> sortedResultMap = resultMap.entrySet().stream()
                .sorted(Collections.reverseOrder((e1, e2) -> Double.compare(e1.getValue().getTotalSynsets(),
                        e2.getValue().getTotalSynsets())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sortedResultMap;
    }
    
    public GeonameEntity calculateHighestCDEntity(Map<GeonameNode, ConceptualDensityParameters> candidateParameterMap) {
        double alpha = 0.1;
        double maxCD = 0;
        GeonameEntity resultEntity = null;
        int frequencyRank = 1;
//        double lastSubHierarchyFrequency = 0;
        for (Map.Entry<GeonameNode, ConceptualDensityParameters> entry : candidateParameterMap.entrySet()) {
            double baseCD = (double) entry.getValue().getRelevantSynsets() / (double) entry.getValue().getTotalSynsets();
            
            if (baseCD > maxCD) {
                maxCD = baseCD;
                resultEntity = entry.getKey().getEntity();
            } else if (baseCD == maxCD) {
                long population = 0;
                long resultPopulation = 0;
                if (entry.getKey().getEntity().getPopulation() == 0 && entry.getKey().getEntity().getParent() != null) {
                    population = entry.getKey().getEntity().getParent().getPopulation();
                } else {
                    population = entry.getKey().getEntity().getPopulation();
                }
                if (resultEntity.getPopulation() == 0 && resultEntity.getParent() != null) {
                    resultPopulation = resultEntity.getParent().getPopulation();
                } else {
                    resultPopulation = resultEntity.getPopulation();
                }
                if (population > resultPopulation) {
                    resultEntity = entry.getKey().getEntity();
                }
            } else {
                ;
            }
        }
        return resultEntity;
    }
    
    public GeonameEntity calculateHighestPopulationEntity(List<GeonameEntity> candidates) {
        GeonameEntity result = null;
        for (GeonameEntity candidate : candidates) {
            if (result == null) {
                result = candidate;
            } else {
                long population = 0;
                long resultPopulation = 0;
                if (candidate.getPopulation() == 0 && candidate.getParent() != null) {
                    population = candidate.getParent().getPopulation();
                } else {
                    population = candidate.getPopulation();
                }
                if (result.getPopulation() == 0 && result.getParent() != null) {
                    resultPopulation = result.getParent().getPopulation();
                } else {
                    resultPopulation = result.getPopulation();
                }
                if (population > resultPopulation) {
                    result = candidate;
                }
            }
        }
        return result;
    }
    
}
