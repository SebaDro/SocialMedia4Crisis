/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.model.geonames.Admin4;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin03Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin04Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
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
public class HibernateGeonamesDaoIT {

    HibernateDatabaseConnection dbc;
    DaoFactory daoFactory;
    Session session;

    @Before
    public void setup() throws Exception {
        dbc = new HibernateDatabaseConnection();
        dbc.afterPropertiesSet();

        daoFactory = new HibernateDaoFactory(dbc);
        session = (Session) daoFactory.initializeContext();
    }

    @Test
    public void roundTrip() {
        Admin04Dao admin04Dao = daoFactory.createAdmin04Dao(session);
        Admin03Dao admin03Dao = daoFactory.createAdmin03Dao(session);
        Admin4 admin4 = admin04Dao.retrieveByName("Wonneberg").get();

        Assert.assertThat(admin4.getName(), CoreMatchers.equalTo("Wonneberg"));
        Assert.assertThat(admin4.getAdmin3().getName(), CoreMatchers.equalTo("Landkreis Traunstein"));
        Assert.assertThat(admin4.getAdmin3().getAdmin2().getName(), CoreMatchers.equalTo("Upper Bavaria"));
        Assert.assertThat(admin4.getAdmin3().getAdmin2().getAdmin1().getName(), CoreMatchers.equalTo("Bavaria"));

        GeonameDao geonameDao = daoFactory.createGeonameDao(session);
        Geoname geoname = geonameDao.retrieveByName("Zwieselhof").get();
        Assert.assertThat(geoname.getName(), CoreMatchers.equalTo("Zwieselhof"));

        Geoname geoname2 = geonameDao.retrieveByName("Holste").get();
        Assert.assertThat(geoname2.getParent().getFeatureCode(), CoreMatchers.equalTo("ADM3"));
        Assert.assertThat(geoname2.getParent().getParent().getFeatureCode(), CoreMatchers.equalTo("ADM1"));

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
