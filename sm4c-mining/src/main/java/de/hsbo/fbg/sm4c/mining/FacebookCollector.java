/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import de.hsbo.fbg.sm4c.mining.encode.FacebookCSVEncoder;
import de.hsbo.fbg.sm4c.mining.encode.FacebookJSONEncoder;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.GroupPrivacyType;
import facebook4j.Page;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Collector class for fetching different content from the Facebook Graph API
 *
 * @author Sebastian Drost
 */
public class FacebookCollector {

    private final int GROUP_LIMIT = 10;
    private final int PAGE_LIMIT = 10;
    private final int POST_LIMIT = 10;
    private final Facebook facebook;
    private final Gson gson;
    private final FacebookCSVEncoder fbCsvEncoder;
    private final FacebookJSONEncoder fbJsonEncoder;

    public FacebookCollector() {
        this.facebook = new FacebookFactory().getInstance();
        gson = new Gson();
        fbCsvEncoder = new FacebookCSVEncoder();
        fbJsonEncoder = new FacebookJSONEncoder();
    }

    /**
     * Search for facebook groups whose names contain the specified keyords.
     *
     * @param keywords keywords to search for
     * @return List of facebook groups
     */
    public List<Group> getGroups(String keywords) {
        List result = new ArrayList();
        try {
            ResponseList<Group> groups = facebook.searchGroups(keywords, new Reading()
                    .limit(GROUP_LIMIT)
                    .fields("id", "description", "email", "name", "privacy", "updated_time", "city"));
            groups.removeIf(g -> g.getPrivacy() == GroupPrivacyType.CLOSED);
            result = groups.stream().collect(Collectors.toList());
        } catch (FacebookException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Search for facebook groups whose names contain the specified keyords and
     * encodes the result to JSON.
     *
     * @param keywords keywords to search for
     * @return List of facebook groups as JSON
     */
    public String getGroupsAsJSON(String keywords) {
        List<Group> groups = getGroups(keywords);
        String groupsJSON = gson.toJson(groups);
        return groupsJSON;
    }

    /**
     * Search for facebook groups whose names contain the specified keyords and
     * encodes the result to CSV.
     *
     * @param keywords keywords to search for
     * @return List of facebook groups as JSON
     */
    public String getGroupsAsCSV(String keywords) {
        List<Group> groups = getGroups(keywords);
        String groupsCsv = fbCsvEncoder.createGroupCSV(groups);
        return groupsCsv;
    }

    /**
     * Search for facebook pages whose names contain the specified keyords.
     *
     * @param keywords keywords to search for
     * @return List of facebook pages
     */
    public List<Page> getPages(String keywords) {
        List result = new ArrayList();
        try {
            ResponseList<Page> pages = facebook.searchPages(keywords, new Reading()
                    .limit(PAGE_LIMIT)
                    .fields("id", "description", "emails", "about", "category", "location", "name"));
            result = pages.stream().collect(Collectors.toList());
        } catch (FacebookException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Search for facebook pages whose names contain the specified keyords and
     * encodes the result to JSON.
     *
     * @param keywords keywords to search for
     * @return List of facebook groups as JSON
     */
    public String getPagesAsJSON(String keywords) {
        List<Page> pages = getPages(keywords);
        String groupsJSON = gson.toJson(pages);
        return groupsJSON;
    }

    /**
     * Fecthes posts from the specified group between for a specified time
     * period.
     *
     * @param group Facebook group
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return
     */
    public List<Post> getPostsFromSingleGroup(Group group, Date startDate, Date endDate) {
        List result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getGroupFeed(group.getId(), new Reading()
                    .limit(POST_LIMIT)
                    .fields("id", "created_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            result = feeds.stream().collect(Collectors.toList());
        } catch (FacebookException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Fecthes posts from the specified list of groups for a specified time
     * period.
     *
     * @param groups Facebook groups
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return List of posts from facebook groups
     */
    public List<Post> getPostsFromGroups(List<Group> groups, Date startDate, Date endDate) {
        List<Post> resultList = new ArrayList();
        groups.forEach(g -> {
            List<Post> posts = getPostsFromSingleGroup(g, startDate, endDate);
            resultList.addAll(posts);
        });
        return resultList;
    }

    /**
     * Fetches posts from the specified list of groups for a specified time
     * period and encode it to JSON.
     *
     * @param groups Facebook groups
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return posts from facebook groups as JSON
     */
    public String getPostsFromGroupsAsJSON(List<Group> groups, Date startDate, Date endDate) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode postArray = mapper.createArrayNode();
        groups.forEach(g -> {
            List<Post> posts = getPostsFromSingleGroup(g, startDate, endDate);
            postArray.addAll(fbJsonEncoder.createPostArrayNode(posts, g));
        });
        String result = "";
        try {
            result = mapper.writeValueAsString(postArray);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Fecthes posts from the specified page for a specified time period.
     *
     * @param page Facebook page
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return List of posts from the facebook page
     */
    public List<Post> getPostsFromSinglePage(Page page, Date startDate, Date endDate) {
        List result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getFeed(page.getId(), new Reading()
                    .limit(POST_LIMIT)
                    .fields("id", "created_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            result = feeds.stream().collect(Collectors.toList());
        } catch (FacebookException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Fecthes posts from the specified list of pages for a specified time
     * period.
     *
     * @param pages Facebook pages
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return List of posts from facebook pages
     */
    public List<Post> getPostsFromPages(List<Page> pages, Date startDate, Date endDate) {
        List<List<Post>> resultList = new ArrayList();
        pages.forEach(p -> {
            List<Post> posts = getPostsFromSinglePage(p, startDate, endDate);
            resultList.add(posts);
        });
        return resultList.stream()
                .flatMap(p -> p.stream()).collect(Collectors.toList());
    }

    /**
     * Fetches posts from the specified list of pages for a specified time
     * period and encode it to JSON.
     *
     * @param groups Facebook pages
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return posts from facebook pages as JSON
     */
    public String getPostsFromPagesAsJSON(List<Page> pages, Date startDate, Date endDate) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode postArray = mapper.createArrayNode();
        pages.forEach(p -> {
            List<Post> posts = getPostsFromSinglePage(p, startDate, endDate);
            postArray.addAll(fbJsonEncoder.createPostArrayNode(posts, p));
        });
        String result = "";
        try {
            result = mapper.writeValueAsString(postArray);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(FacebookCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
