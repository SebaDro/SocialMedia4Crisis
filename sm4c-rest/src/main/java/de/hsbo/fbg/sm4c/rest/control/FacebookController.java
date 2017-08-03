/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.control;

import de.hsbo.fbg.sm4c.mining.collect.FacebookCollector;
import de.hsbo.fbg.sm4c.rest.coding.GroupEncoder;
import de.hsbo.fbg.sm4c.rest.coding.PageEncoder;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import de.hsbo.fbg.sm4c.rest.view.KeywordView;
import facebook4j.Group;
import facebook4j.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"application/json"}, value = "/facebook")
public class FacebookController implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(FacebookController.class);

    private FacebookCollector facebookCollector;

    @RequestMapping(value = "/groups/{keywords}/json", method = RequestMethod.GET)
    public String getGroupsAsJSON(@PathVariable("keywords") String keywords) {
        String groups = facebookCollector.getGroupsAsJSON(keywords);
        return groups;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public List<FacebookSourceView> collectGroups(@RequestBody List<KeywordView> req) {
        List<FacebookSourceView> facebookSources = new ArrayList();
        GroupEncoder groupEncoder = new GroupEncoder();
        req.forEach(k -> {
            List<FacebookSourceView> sources = facebookCollector.getGroups(k.getName())
                    .stream()
                    .map(g -> groupEncoder.encode(g))
                    .filter(g -> !facebookSources.stream().anyMatch(fbS -> fbS.getFacebookId().equals(g.getFacebookId())))
                    .collect(Collectors.toList());
            facebookSources.addAll(sources);
        });
        return facebookSources;
    }

    @RequestMapping(value = "/pages", method = RequestMethod.POST)
    public List<FacebookSourceView> collectPages(@RequestBody List<KeywordView> req) {
        List<FacebookSourceView> facebookSources = new ArrayList();
        PageEncoder pageEncoder = new PageEncoder();
        req.forEach(k -> {
            List<FacebookSourceView> sources = facebookCollector.getPages(k.getName())
                    .stream()
                    .map(p -> pageEncoder.encode(p))
                    .filter(p -> !facebookSources.stream().anyMatch(fbS -> fbS.getFacebookId().equals(p.getFacebookId())))
                    .collect(Collectors.toList());
            facebookSources.addAll(sources);
        });
        return facebookSources;
    }

    @RequestMapping(produces = {"text/csv"}, value = "/groups/{keywords}/csv", method = RequestMethod.GET)
    public String getGroupsAsCSV(@PathVariable("keywords") String keywords) {
        String groups = facebookCollector.getGroupsAsCSV(keywords);
        return groups;
    }

    @RequestMapping(value = "/pages/{keywords}", method = RequestMethod.GET)
    public String getPages(@PathVariable("keywords") String keywords) {
        String pages = facebookCollector.getPagesAsJSON(keywords);
        return pages;
    }

    @RequestMapping(value = "/groups/{keywords}/posts/startTime/{startTime}/endTime/{endTime}",
            method = RequestMethod.GET)
    public String getPostsFromGroups(@PathVariable("keywords") String keywords,
            @PathVariable("startTime") String startTime,
            @PathVariable("endTime") String endTime) {
        List<Group> groups = facebookCollector.getGroups(keywords);
        String posts = facebookCollector.getPostsFromGroupsAsJSON(groups,
                new DateTime(startTime).toDate(),
                new DateTime(endTime).toDate());
        return posts;
    }

    @RequestMapping(value = "/pages/{keywords}/posts/startTime/{startTime}/endTime/{endTime}",
            method = RequestMethod.GET)
    public String getPostsFromPages(@PathVariable("keywords") String keywords,
            @PathVariable("startTime") String startTime,
            @PathVariable("endTime") String endTime) {
        List<Page> pages = facebookCollector.getPages(keywords);
        String posts = facebookCollector.getPostsFromPagesAsJSON(pages,
                new DateTime(startTime).toDate(),
                new DateTime(endTime).toDate());
        return posts;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        facebookCollector = new FacebookCollector();
    }

}
