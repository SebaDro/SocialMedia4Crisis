/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.hsbo.fbg.sm4c.mining.collect.FacebookCollector;
import de.hsbo.fbg.sm4c.mining.dao.DaoFactory;
import de.hsbo.fbg.sm4c.mining.dao.FacebookDao;
import de.hsbo.fbg.sm4c.mining.dao.MongoDbDaoFactory;
import de.hsbo.fbg.sm4c.rest.view.TrainingDataView;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
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

    private final String COLLECTION_NAME = "hw_2013_sim_col";
    private FacebookDao fbDao;
    private FacebookCollector fbCollector;
    private ObjectMapper mapper;

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
        fbDao.deleteValues();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    public ResponseEntity getFacebookSimulationPosts(@RequestBody TrainingDataView req) {
        LOGGER.info("Started collecting posts for simulation");
        List<String> keywords = req.getKeywords();
        keywords.forEach(k -> {
            List<Group> groups = fbCollector.getGroups(k);
            groups.forEach(g -> {
                if (!fbDao.containsGroup(g)) {
                    List<Post> posts = fbCollector.getPostsFromSingleGroup(g, req.getStartDate(), req.getEndDate());
                    if (!posts.isEmpty()) {
                        fbDao.storeFaceBookPosts(posts, g);
                    }
                }
            });
            List<Page> pages = fbCollector.getPages(k);
            pages.forEach(p -> {
                if (!fbDao.containsPage(p)) {
                    List<Post> posts = fbCollector.getPostsFromSinglePage(p, req.getStartDate(), req.getEndDate());
                    if (!posts.isEmpty()) {
                        fbDao.storeFaceBookPosts(posts, p);
                    }
                }
            });
        });
        LOGGER.info("Finishe collecting posts for simulation");
        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DaoFactory daoFactory = new MongoDbDaoFactory(COLLECTION_NAME);
        fbDao = daoFactory.createFacebookDao();
        fbCollector = new FacebookCollector();
        mapper = new ObjectMapper();
    }
}
