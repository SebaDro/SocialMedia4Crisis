/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.FacebookMessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.SourceCategory;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDaoIT {

    private MongoDocumentDaoFactory documentDaoFactory;
    private FacebookMessageDocumentDao documentDao;

    @Before
    public void setup() {
        MongoDatabaseConnection con = new MongoDatabaseConnection();
        con.afterPropertiesSet();
        MongoDatabase db = con.getDatabase();
        documentDaoFactory = new MongoDocumentDaoFactory(db);
    }

    @Test
    public void roundTrip() {
        Collection col = new Collection();
        col.setName("Test Collection");
        MongoCollection mongoCol = documentDaoFactory.getContext(col);
        documentDao = documentDaoFactory.createFacebookMessageDocumentDao(mongoCol);
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
        SourceCategory category = new SourceCategory();
        category.setName("Groupe");
        source.setCategory(category);
        document.setSource(source);
        document.setType("Link");
        document.setUpdateTime(dt);

        documentDao.store(document);

        Assert.assertThat(documentDao.retrieve().isEmpty(), CoreMatchers.equalTo(false));
        Assert.assertThat(documentDao.retrieve().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(documentDao.exists(document), CoreMatchers.equalTo(true));
        Assert.assertThat(documentDao.retrieveById(document.getId()).getId(),
                CoreMatchers.equalTo(document.getId()));
//        Assert.assertThat(fbDao.getValuesForTimeSpan(startDate.toDate(), endDate.toDate()).size(), CoreMatchers.equalTo(messages.size()));
    }

    @After
    public void shutDown() {
        documentDao.removeAll();
    }

}
