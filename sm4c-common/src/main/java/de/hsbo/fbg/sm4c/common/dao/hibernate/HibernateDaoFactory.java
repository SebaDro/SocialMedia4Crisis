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
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import de.hsbo.fbg.sm4c.common.dao.SourceTypeDao;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateDaoFactory implements DaoFactory<Session> {

    @Autowired
    private HibernateDatabaseConnection hibernateConnection;

    public HibernateDaoFactory() {
    }

    public HibernateDaoFactory(HibernateDatabaseConnection hibernateConnection) {
        this.hibernateConnection = hibernateConnection;
    }

    @Override
    public Session initializeContext() {
        return hibernateConnection.createSession();
    }

    @Override
    public CollectionDao createCollectionDao(Session daoContext) {
        return new HibernateCollectionDao(daoContext);
    }

    @Override
    public SocialMediaServiceDao createSocialMediaServiceDao(Session daoContext) {
        return new HibernateSocialMediaServiceDao(daoContext);
    }

    @Override
    public SourceTypeDao createSourceTypeDao(Session daoContext) {
        return new HibernateSourceTypeDao(daoContext);
    }

    @Override
    public CollectionStatusDao createCollectioStatusDao(Session daoContext) {
        return new HibernateCollectionStatusDao(daoContext);
    }

    @Override
    public LabelDao createLabelDao(Session daoContext) {
        return new HibernateLabelDao(daoContext);
    }

    @Override
    public KeywordDao createKeywordDao(Session daoContext) {
        return new HibernateKeywordDao(daoContext);
    }

    @Override
    public FacebookSourceDao createFacebookSourceDao(Session daoContext) {
        return new HibernateFacebookSourceDao(daoContext);
    }

}
