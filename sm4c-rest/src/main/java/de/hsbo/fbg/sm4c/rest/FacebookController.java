/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest;

import de.hsbo.fbg.sm4c.mining.FacebookCollector;
import facebook4j.Group;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.PathVariable;
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

    private FacebookCollector facebookCollector;

    @RequestMapping(value = "/groups/{keywords}", method = RequestMethod.GET)
    public String getGroups(@PathVariable("keywords") String keywords) {
        String groups = facebookCollector.getGroupsAsJSON(keywords);
        return groups;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        facebookCollector = new FacebookCollector();
    }

}
