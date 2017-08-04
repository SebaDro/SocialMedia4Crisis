/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

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
import de.hsbo.fbg.sm4c.rest.coding.CollectionDecoder;
import de.hsbo.fbg.sm4c.common.dao.RessourceNotFoundException;
import de.hsbo.fbg.sm4c.rest.view.CollectionView;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"application/json"})
public class CollectionController {

    private static final Logger LOGGER = LogManager.getLogger(CollectionController.class);

    @Autowired
    private DaoFactory<Session> daoFactory;

    @Autowired
    private CollectionDecoder collectionDecoder;

    @RequestMapping(value = "/collections", method = RequestMethod.POST)
    public ResponseEntity initiateCollection(@RequestBody CollectionView req) {
        try (Session session = daoFactory.initializeContext()) {
            CollectionDao collectionDao = daoFactory.createCollectionDao(session);

            Collection collection = collectionDecoder.decode(req);
            CollectionStatus status = retrieveCollectionStatus(req.getCollectionStatus(), session);
            Set<Keyword> keywords = retrieveKeywords(req.getKeywords(), session);
            Set<Label> labels = retrieveLabels(req.getLabels(), session);
            Set<FacebookSource> sources = retrieveSources(req.getFacebookSources(), session);
            Set<SocialMediaService> services = retreiveServices(req.getServices(), session);

            collection.setStatus(status);
            collection.setKeywords(keywords);
            collection.setLabels(labels);
            collection.setSources(sources);
            collection.setServices(services);

            collectionDao.store(collection);
        } catch (RessourceNotFoundException ex) {
            LOGGER.error("Could not store collection", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private CollectionStatus retrieveCollectionStatus(String status, Session session) throws RessourceNotFoundException {
        CollectionStatusDao statusDao = daoFactory.createCollectioStatusDao(session);
        Optional<CollectionStatus> result = statusDao.retrieveByName(status);
        if (!result.isPresent()) {
            throw new RessourceNotFoundException("The specified status is not available");
        }
        return result.get();
    }

    private Set<Keyword> retrieveKeywords(List<String> keywords, Session session) {
        KeywordDao keywordDao = daoFactory.createKeywordDao(session);
        Set<Keyword> result = new HashSet<>();
        keywords.forEach(k -> {
            Optional<Keyword> keyword = keywordDao.retrieveByName(k);
            if (keyword.isPresent()) {
                result.add(keyword.get());
            } else {
                Keyword keyw = new Keyword();
                keyw.setName(k);
                result.add(keyw);
            }
        });
        return result;
    }

    private Set<Label> retrieveLabels(List<String> labels, Session session) {
        LabelDao labelDao = daoFactory.createLabelDao(session);
        Set<Label> result = new HashSet<>();
        labels.forEach(l -> {
            Optional<Label> label = labelDao.retrieveByName(l);
            if (label.isPresent()) {
                result.add(label.get());
            } else {
                Label lab = new Label();
                lab.setName(l);
                result.add(lab);
            }
        });
        return result;
    }

    private Set<FacebookSource> retrieveSources(List<FacebookSourceView> facebookSources, Session session) {
        FacebookSourceDao sourceDao = daoFactory.createFacebookSourceDao(session);
        SourceCategoryDao categoryDao = daoFactory.createSourceCategoryDao(session);
        Set<FacebookSource> result = new HashSet<>();
        facebookSources.forEach(s -> {
            try {
                Optional<FacebookSource> source = sourceDao.retrieveByName(s.getName());
                if (source.isPresent()) {
                    result.add(source.get());
                } else {
                    FacebookSource sou = new FacebookSource();
                    Optional<SourceCategory> category = categoryDao.retrieveByName(s.getSourceCategory().getName());
                    if (!category.isPresent()) {
                        throw new RessourceNotFoundException("The specified source is not supported.");
                    }
                    sou.setCategory(category.get());
                    sou.setName(s.getName());
                    sou.setDescription(s.getDescription());
                    sou.setFacebookId(s.getFacebookId());
                    result.add(sou);

                }
            } catch (Exception ex) {
                LOGGER.error("Could not store source", ex);
            }

        });
        return result;
    }

    private Set<SocialMediaService> retreiveServices(List<String> services, Session session) {
        SocialMediaServiceDao serviceDao = daoFactory.createSocialMediaServiceDao(session);
        Set<SocialMediaService> result = new HashSet<>();
        services.forEach(s -> {
            try {
                Optional<SocialMediaService> service = serviceDao.retrieveByName(s);
                if (!service.isPresent()) {
                    throw new RessourceNotFoundException("The specified service is not available");
                }
                result.add(service.get());
            } catch (RessourceNotFoundException ex) {
                LOGGER.error("Could not find service", ex);
            }
        });
        return result;
    }

}
