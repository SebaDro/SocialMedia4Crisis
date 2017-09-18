/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.ClassifierDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionDao;
import de.hsbo.fbg.sm4c.common.dao.CollectionStatusDao;
import de.hsbo.fbg.sm4c.common.dao.DaoFactory;
import de.hsbo.fbg.sm4c.common.dao.EvaluationResultDao;
import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.dao.KeywordDao;
import de.hsbo.fbg.sm4c.common.dao.LabelDao;
import de.hsbo.fbg.sm4c.common.dao.ModelDao;
import de.hsbo.fbg.sm4c.common.dao.SocialMediaServiceDao;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import de.hsbo.fbg.sm4c.common.dao.SourceTypeDao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin01Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin02Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin03Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin04Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.geonames.HibernateAdmin01Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.geonames.HibernateAdmin02Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.geonames.HibernateAdmin03Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.geonames.HibernateAdmin04Dao;
import de.hsbo.fbg.sm4c.common.dao.hibernate.geonames.HibernateGeonameDao;

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

    @Override
    public ModelDao createModelDao(Session daoContext) {
        return new HibernateModelDao(daoContext);
    }

    @Override
    public ClassifierDao createClassifierDao(Session daoContext) {
        return new HibernateClassifierDao(daoContext);
    }

    @Override
    public EvaluationResultDao createEvaluationResultDao(Session daoContext) {
        return new HibernateEvaluationResultDao(daoContext);
    }

    @Override
    public Admin01Dao createAdmin01Dao(Session daoContext) {
        return new HibernateAdmin01Dao(daoContext);
    }

    @Override
    public Admin02Dao createAdmin02Dao(Session daoContext) {
        return new HibernateAdmin02Dao(daoContext);
    }

    @Override
    public Admin03Dao createAdmin03Dao(Session daoContext) {
        return new HibernateAdmin03Dao(daoContext);
    }

    @Override
    public Admin04Dao createAdmin04Dao(Session daoContext) {
        return new HibernateAdmin04Dao(daoContext);
    }

    @Override
    public GeonameDao createGeonameDao(Session daoContext) {
        return new HibernateGeonameDao(daoContext);
    }

}
