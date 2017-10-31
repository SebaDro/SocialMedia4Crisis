/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag;

import de.hsbo.fbg.sm4c.geotag.geocoding.ToponymDisambiguator;
import de.hsbo.fbg.sm4c.geotag.geocoding.GeonameNode;
import de.hsbo.fbg.sm4c.geotag.geocoding.ConceptualDensityParameters;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.hibernate.HibernateDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
import de.hsbo.fbg.sm4c.geotag.geoparsing.Toponym;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Seba
 */
public class ToponymDisambiguatorTest {

    HibernateDatabaseConnection dbc;
    DaoFactory daoFactory;
    Session session;
//

    @Before
    public void setup() throws Exception {
        dbc = new HibernateDatabaseConnection();
        dbc.afterPropertiesSet();

        daoFactory = new HibernateDaoFactory(dbc);
        session = (Session) daoFactory.initializeContext();
    }

    @Test
    public void disambiguationTest() throws Exception {
        ToponymDisambiguator disambiguator = new ToponymDisambiguator();
        Toponym toponym1 = new Toponym("Altenessen");
        Toponym toponym2 = new Toponym("Katernberg");
        List<Toponym> toponyms = new ArrayList();
        toponyms.add(toponym1);
        toponyms.add(toponym2);
        try (Session session = (Session) daoFactory.initializeContext()) {
            GeonameDao geonameDao = daoFactory.createGeonameDao(session);
            GeonameNode tree = disambiguator.buildTree(toponyms, geonameDao);
            Assert.assertThat(tree.getChilren().values().iterator().next().getEntity().getName(),
                    CoreMatchers.equalTo("Nordrhein-Westfalen"));

            GeonameNode candidate_01 = toponyms.get(0).getCandidates().get(0);
            GeonameNode synset_01 = disambiguator.getSynsetHierarchy(candidate_01);
            Assert.assertThat(synset_01.getEntity().getName(),
                    CoreMatchers.equalTo("Nordrhein-Westfalen"));

            GeonameNode candidate_02a = toponyms.get(1).getCandidates().get(0);
            GeonameNode synset_02a = disambiguator.getSynsetHierarchy(candidate_02a);
            Assert.assertThat(synset_02a.getEntity().getName(),
                    CoreMatchers.equalTo("Kreisfreie Stadt Essen"));

            GeonameNode candidate_02b = toponyms.get(1).getCandidates().get(1);
            GeonameNode synset_02b = disambiguator.getSynsetHierarchy(candidate_02b);
            Assert.assertThat(synset_02b.getEntity().getName(),
                    CoreMatchers.equalTo("Kreisfreie Stadt Wuppertal"));

            int count_01 = disambiguator.calculateTotalSynsets(synset_01, 1);
            Assert.assertThat(count_01, CoreMatchers.equalTo(9));

            ConceptualDensityParameters parameters_02a = disambiguator.calculateCDParameters(synset_02a);
            Assert.assertThat(parameters_02a.getTotalSynsets(), CoreMatchers.equalTo(4));
            Assert.assertThat(parameters_02a.getRelevantSynsets(), CoreMatchers.equalTo(2));

            ConceptualDensityParameters parameters_02b = disambiguator.calculateCDParameters(synset_02b);
            Assert.assertThat(parameters_02b.getTotalSynsets(), CoreMatchers.equalTo(3));
            Assert.assertThat(parameters_02b.getRelevantSynsets(), CoreMatchers.equalTo(1));

            HashMap<GeonameNode, ConceptualDensityParameters> candidateParameters = new HashMap();
            candidateParameters.put(candidate_02a, parameters_02a);
            candidateParameters.put(candidate_02b, parameters_02b);

            GeonameEntity result02 = disambiguator.calculateHighestCDEntity(candidateParameters);
            Assert.assertThat(result02.getGeonameid(), CoreMatchers.equalTo("2892422"));

            List<GeonameEntity> result = disambiguator.disambiguateToponyms(toponyms);
            Assert.assertThat(result.get(0).getGeonameid(), CoreMatchers.equalTo("2957704"));
            Assert.assertThat(result.get(1).getGeonameid(), CoreMatchers.equalTo("2892422"));

            GeonameEntity singleResult = disambiguator.disambiguateSingleToponym(toponym2);
            Assert.assertThat(singleResult.getGeonameid(), CoreMatchers.equalTo("2892422"));

            Toponym toponym11 = new Toponym("Kröllwitz");
            Toponym toponym12 = new Toponym("Gesundbrunnen");

            List<Toponym> toponyms1 = new ArrayList();
            toponyms1.add(toponym11);
            toponyms1.add(toponym12);
            List<GeonameEntity> res = disambiguator.disambiguateToponyms(toponyms1);
            res.size();
        }

    }

    @After
    public void shutDown() {
        try {
            dbc.destroy();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
