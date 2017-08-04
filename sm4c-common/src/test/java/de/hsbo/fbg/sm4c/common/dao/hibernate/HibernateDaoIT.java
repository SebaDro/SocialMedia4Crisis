/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionStatusDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.dao.KeywordDao;
import de.hsbo.fbg.sm4c.common.dao.LabelDao;
import de.hsbo.fbg.sm4c.common.dao.SocialMediaServiceDao;
import de.hsbo.fbg.sm4c.common.dao.SourceCategoryDao;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.CollectionStatus;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.Keyword;
import de.hsbo.fbg.sm4c.common.model.Label;
import de.hsbo.fbg.sm4c.common.model.SocialMediaService;
import de.hsbo.fbg.sm4c.common.model.SourceCategory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateDaoIT {

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
        CollectionDao collectionDao = daoFactory.createCollectionDao(session);
        SocialMediaServiceDao socialMediaServiceDao = daoFactory.createSocialMediaServiceDao(session);
        SourceCategoryDao sourceCategoryDao = daoFactory.createSourceCategoryDao(session);
        CollectionStatusDao collectionStatusDao = daoFactory.createCollectioStatusDao(session);
        LabelDao labelDao = daoFactory.createLabelDao(session);
        KeywordDao keywordDao = daoFactory.createKeywordDao(session);
        FacebookSourceDao facebookSourceDao = daoFactory.createFacebookSourceDao(session);

        Keyword keyword1 = new Keyword();
        Keyword keyword2 = new Keyword();
        keyword1.setName("hochwasser");
        keyword2.setName("hilfe");
        Set<Keyword> keywords = new HashSet();
        Collections.addAll(keywords, keyword1, keyword2);

        Label label1 = new Label();
        Label label2 = new Label();
        label1.setName("relief");
        label2.setName("non relief");
        Set<Label> labels = new HashSet();
        Collections.addAll(labels, label1, label2);

        SourceCategory category1 = sourceCategoryDao.retrieveByName("Group").get();
        SourceCategory category2 = sourceCategoryDao.retrieveByName("Page").get();

        FacebookSource source1 = new FacebookSource();
        source1.setFacebookId("123Test");
        source1.setName("Hochwasserhilfe Magdeburg");
        source1.setDescription("Hier werden Sie geholfen");
        source1.setCategory(category1);
        FacebookSource source2 = new FacebookSource();
        source2.setFacebookId("456Test");
        source2.setName("Hochwasser 2013");
        source2.setDescription("Aktuelle Updates");
        source2.setCategory(category2);
        Set<FacebookSource> sources = new HashSet();
        Collections.addAll(sources, source1, source2);

        SocialMediaService service1 = socialMediaServiceDao.retrieveByName("Facebook").get();
        Set<SocialMediaService> services = new HashSet();
        Collections.addAll(services, service1);

        CollectionStatus status = collectionStatusDao.retrieveByName("gestoppt").get();

        Collection collection = new Collection();
        collection.setName("Hochwasser");
        collection.setDescription("Hochwasser 2013");
        collection.setCreation(DateTime.now().toDate());
        collection.setLabels(labels);
        collection.setStatus(status);
        collection.setKeywords(keywords);
        collection.setServices(services);
        collection.setSources(sources);

        collectionDao.store(collection);
        Optional<Collection> collectionEntity = collectionDao.retrieveById(collection.getId());

        Assert.assertThat(collectionEntity.get().getName(), CoreMatchers.equalTo(collection.getName()));
        Assert.assertThat(collectionEntity.get().getDescription(), CoreMatchers.equalTo(collection.getDescription()));
        Assert.assertThat(collectionEntity.get().getCreation(), CoreMatchers.equalTo(collection.getCreation()));
        Assert.assertThat(collectionEntity.get().getKeywords(), CoreMatchers.equalTo(collection.getKeywords()));
        Assert.assertThat(collectionEntity.get().getLabels(), CoreMatchers.equalTo(collection.getLabels()));
        Assert.assertThat(collectionEntity.get().getServices(), CoreMatchers.equalTo(collection.getServices()));
        Assert.assertThat(collectionEntity.get().getSources(), CoreMatchers.equalTo(collection.getSources()));
        Assert.assertThat(collectionEntity.get().getStatus(), CoreMatchers.equalTo(collection.getStatus()));

        collectionDao.remove(collection);
        collection.getSources().forEach(s -> facebookSourceDao.remove(s));
        collection.getKeywords().forEach(k -> keywordDao.remove(k));
        collection.getLabels().forEach(l -> labelDao.remove(l));

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