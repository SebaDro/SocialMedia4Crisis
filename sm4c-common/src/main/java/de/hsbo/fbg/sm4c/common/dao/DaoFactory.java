/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao;

import de.hsbo.fbg.sm4c.common.dao.geonames.Admin01Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin02Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin03Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.Admin04Dao;
import de.hsbo.fbg.sm4c.common.dao.geonames.GeonameDao;

/**
 *
 * @author Sebastian Drost
 */
public interface DaoFactory<T extends AutoCloseable> {

    T initializeContext();

    CollectionDao createCollectionDao(T daoContext);

    SocialMediaServiceDao createSocialMediaServiceDao(T daoContext);

    SourceTypeDao createSourceTypeDao(T daoContext);

    CollectionStatusDao createCollectioStatusDao(T daoContext);

    LabelDao createLabelDao(T daoContext);

    KeywordDao createKeywordDao(T daoContext);

    FacebookSourceDao createFacebookSourceDao(T daoContext);

    ModelDao createModelDao(T daoContext);

    ClassifierDao createClassifierDao(T daoContext);

    EvaluationResultDao createEvaluationResultDao(T daoContext);

    Admin01Dao createAdmin01Dao(T daoContext);

    Admin02Dao createAdmin02Dao(T daoContext);

    Admin03Dao createAdmin03Dao(T daoContext);

    Admin04Dao createAdmin04Dao(T daoContext);

    GeonameDao createGeonameDao(T daoContext);

}
