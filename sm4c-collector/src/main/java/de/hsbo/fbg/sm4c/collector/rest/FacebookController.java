/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collector.rest;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.GroupPrivacyType;
import facebook4j.Reading;
import facebook4j.ResponseList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping(produces = {"text/plain"}, value = "/facebook")
public class FacebookController implements InitializingBean {

    private Facebook facebook;

    @RequestMapping(value = "/groups/{keywords}", method = RequestMethod.GET)
    public String getGroups(@PathVariable("keywords") String keywords) {
        String groupList = "";
        try {
            ResponseList<Group> groups = facebook.searchGroups(keywords, new Reading()
                    .limit(1000)
                    .fields("id", "description", "email", "name", "privacy", "updated_time"));
            groups.removeIf(g -> g.getPrivacy() == GroupPrivacyType.CLOSED);
            for (Group g : groups) {
                groupList = groupList + "\n" + g.getName();
            }
        } catch (FacebookException ex) {
            Logger.getLogger(FacebookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return groupList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        facebook = new FacebookFactory().getInstance();
    }

}
