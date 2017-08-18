/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.collect.FacebookTestCollector;
import de.hsbo.fbg.sm4c.collect.dao.FacebookSimulationDao;
import de.hsbo.fbg.sm4c.collect.dao.MongoDbFacebookSimulationDao;
import de.hsbo.fbg.sm4c.collect.encode.FacebookSimulationEncoder;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.rest.view.TrainingDataView;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
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

    private FacebookSimulationDao fbDao;
    private FacebookTestCollector fbCollector;
    private ObjectMapper mapper;
    private FacebookSimulationEncoder fbEncoder;

    @RequestMapping(produces = {"application/json"}, value = "/posts", method = RequestMethod.GET)
    public String getPosts() {
        ArrayNode jsonArray = mapper.createArrayNode();
        fbDao.getValues().forEach(new Consumer<String>() {
            @Override
            public void accept(String d) {
                try {
                    jsonArray.add(mapper.readTree(d));
                } catch (IOException ex) {
                    LOGGER.error("Could not parse document", ex);
                }
            }
        });
        String result = "";
        try {
            result = mapper.writeValueAsString(jsonArray);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Could not write json array", ex);
        }
        return result;
    }

    @RequestMapping(value = "/posts/all", method = RequestMethod.DELETE)
    public ResponseEntity getFacebookSimulationPosts() {
        fbDao.deleteAllValues();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    public ResponseEntity getFacebookSimulationPosts(@RequestBody TrainingDataView req) {

        List<String> keywords = req.getKeywords();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Started collecting posts for simulation");
                keywords.forEach(k -> {
                    List<Group> groups = fbCollector.getGroups(k);
                    groups.forEach(g -> {
                        if (!fbDao.containsGroup(g)) {
                            List<Post> posts = fbCollector.getMessagesFromSingleGroup(g,
                                    req.getStartDate(), req.getEndDate());
                            List<FacebookSimulationMessageDocument> messages = posts.stream()
                                    .map(p -> fbEncoder.createMessage(p, g))
                                    .collect(Collectors.toList());
                            if (posts != null && !posts.isEmpty()) {
                                fbDao.storeFacebookMessages(messages);
                            }
                        }
                    });
                    List<Page> pages = fbCollector.getPages(k);
                    pages.forEach(fP -> {
                        if (!fbDao.containsPage(fP)) {
                            List<Post> posts = fbCollector.getPostsFromSinglePage(fP,
                                    req.getStartDate(), req.getEndDate());
                            List<FacebookSimulationMessageDocument> messages = posts.stream()
                                    .map(p -> fbEncoder.createMessage(p, fP))
                                    .collect(Collectors.toList());
                            if (posts != null && !posts.isEmpty()) {
                                fbDao.storeFacebookMessages(messages);
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
        fbDao = new MongoDbFacebookSimulationDao(
                conf.getPropertyValue("db_host"), 
                Integer.parseInt(conf.getPropertyValue("db_port")),
                conf.getPropertyValue("db_sim_name"),
                conf.getPropertyValue("db_sim_collection"));
        fbCollector = new FacebookTestCollector();
        mapper = new ObjectMapper();
        fbEncoder = new FacebookSimulationEncoder();
    }
}
