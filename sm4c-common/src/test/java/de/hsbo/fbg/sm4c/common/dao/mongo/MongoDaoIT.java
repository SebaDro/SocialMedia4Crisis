/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.SourceType;
import java.util.Locale;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.ArrayList;
import java.util.List;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Sebastian Drost
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/test/resources/application-context-test.xml"})
public class MongoDaoIT {

    @Autowired
    DocumentDaoFactory documentDaoFactory;

    private MessageDocumentDao documentDao;

    @Test
    public void roundTrip() {
        Collection col = new Collection();
        col.setName("Test Collection");
        MongoCollection mongoCollection = (MongoCollection) documentDaoFactory.getContext(col);

        documentDao = documentDaoFactory.createMessageDocumentDao(mongoCollection);

        FacebookMessageDocument document = new FacebookMessageDocument();
        document.setContent("Test content");
        DateTime dt = DateTime.parse("May 28, 2013 11:21:54 PM",
                DateTimeFormat.forPattern("MMM dd, yyyy hh:mm:ss a")
                .withLocale(Locale.ENGLISH));
        document.setCreationTime(dt);
        document.setId("42");
        document.setLabel("relief");
        FacebookSource source = new FacebookSource();
        source.setFacebookId("1149262121802727");
        source.setName("Hochwasser in Deutschland");
        source.setDescription("Ihr könnt Hilfe anbieten oder benötigt Hilfe?\n"
                + "MELDET EUCH PER PN!\n"
                + "Wir posten dann das ihr Hilfe benötigt oder Hilfe anbieten kann. ");
        SourceType category = new SourceType();
        category.setName("Groupe");
        source.setType(category);
        document.setSource(source);
        document.setType("Link");
        document.setUpdateTime(dt);
        document.setService("Facebook");
        List<Location> locations = new ArrayList();
        document.setLocations(locations);

        documentDao.store(document);

        Assert.assertThat(documentDao.retrieve().isEmpty(), CoreMatchers.equalTo(false));
        Assert.assertThat(documentDao.retrieve().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(documentDao.exists(document), CoreMatchers.equalTo(true));
        Assert.assertThat(documentDao.retrieveById(document.getId()).getId(),
                CoreMatchers.equalTo(document.getId()));
//        Assert.assertThat(fbDao.getValuesForTimeSpan(startDate.toDate(), endDate.toDate()).size(), CoreMatchers.equalTo(messages.size()));
        document.setLabel("non relief");
        documentDao.update(document, "label");

        Assert.assertThat(documentDao.retrieveById(document.getId()).getLabel(),
                CoreMatchers.equalTo(document.getLabel()));
    }

    @After
    public void shutDown() {
        documentDao.removeAll();
    }
}
