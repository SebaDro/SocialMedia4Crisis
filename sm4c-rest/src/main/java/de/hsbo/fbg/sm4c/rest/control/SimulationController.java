/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.collect.FacebookApi;
import de.hsbo.fbg.sm4c.collect.encode.FacebookDecoder;
import de.hsbo.fbg.sm4c.common.dao.DocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoMessageDocumentDao;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.Location;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.SourceType;
import de.hsbo.fbg.sm4c.rest.coding.MessageDocumentEncoder;
import de.hsbo.fbg.sm4c.rest.view.MessageDocumentView;
import de.hsbo.fbg.sm4c.rest.view.SimulationDataParameterView;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
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
@RequestMapping(value = "/simulation")
public class SimulationController implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(SimulationController.class);

    @Autowired
    MessageDocumentEncoder messageDocumentEncoder;

    @Autowired
    private FacebookDecoder fbDecoder;

    private FacebookApi fbApi;
    private MessageDocumentDao messageDocumentDao;

    DocumentDaoFactory documentDaoFactory;

    @RequestMapping(produces = {"application/json"}, value = "/posts", method = RequestMethod.GET)
    public List<MessageDocumentView> getPosts() {
        List<MessageDocumentView> result = messageDocumentDao.retrieveSimulationData().stream()
                .map(md -> messageDocumentEncoder.encode(md))
                .collect(Collectors.toList());
        return result;
    }

    @RequestMapping(value = "/posts/all", method = RequestMethod.DELETE)
    public ResponseEntity deleteFacebookSimulationPosts() {
        messageDocumentDao.removeAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    public ResponseEntity collectFacebookSimulationPosts(@RequestBody SimulationDataParameterView req) {

        List<String> keywords = req.getKeywords();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Started collecting posts for simulation");
                keywords.forEach(k -> {
                    List<Group> groups = fbApi.getGroups(k);
                    groups.forEach(g -> {
                        if (!messageDocumentDao.containsSource(g.getId())) {
                            FacebookSource source = fbDecoder.createFacebookSource(g);

                            List<Post> posts = fbApi.getPostsFromSingleGroup(g,
                                    req.getStartDate(), req.getEndDate());

                            List<MessageDocument> messages = posts.stream()
                                    .map(p -> fbDecoder.createMessage(p, source))
                                    .collect(Collectors.toList());
                            if (posts != null && !posts.isEmpty()) {
                                messageDocumentDao.store(messages);
                            }
                        }
                    });
                    List<Page> pages = fbApi.getPages(k);
                    pages.forEach(fP -> {

                        if (!messageDocumentDao.containsSource(fP.getId())) {
                            FacebookSource source = fbDecoder.createFacebookSource(fP);
                            List<Post> posts = fbApi.getPostsFromSinglePage(fP,
                                    req.getStartDate(), req.getEndDate());
                            List<MessageDocument> messages = posts.stream()
                                    .map(p -> fbDecoder.createMessage(p, source))
                                    .collect(Collectors.toList());
                            if (posts != null && !posts.isEmpty()) {
                                messageDocumentDao.store(messages);
                            }
                        }
                    });
                });
                LOGGER.info("Finish collecting posts for simulation");
            }
        }).start();

        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration conf = Configuration.getConfig();
        messageDocumentDao = new MongoMessageDocumentDao(
                conf.getPropertyValue("db_host"),
                Integer.parseInt(conf.getPropertyValue("db_port")),
                conf.getPropertyValue("db_sim_name"),
                conf.getPropertyValue("db_sim_collection"));
        fbApi = new FacebookApi();

    }
}
