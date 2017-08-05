/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import de.hsbo.fbg.sm4c.collect.encode.FacebookCSVEncoder;
import de.hsbo.fbg.sm4c.common.coding.FacebookMessageDocumentEncoder;
import de.hsbo.fbg.sm4c.collect.encode.FacebookSimulationEncoder;
import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.GroupPrivacyType;
import facebook4j.Page;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;

/**
 * Collector class for fetching different content from the Facebook Graph API
 *
 * @author Sebastian Drost
 */
public class FacebookCollector {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(FacebookCollector.class);

    private final int GROUP_LIMIT = 10000;
    private final int PAGE_LIMIT = 1000;
    private final int GROUP_POST_LIMIT = 1000;
    private final int PAGE_POST_LIMIT = 100;

    private final Facebook facebook;
    private final Gson gson;
    private final FacebookCSVEncoder fbCsvEncoder;
    private final FacebookSimulationEncoder fbEncoder;
    private final int groupLimit;
    private final int pageLimit;
    private final int groupPostLimit;
    private final int pagePostLimit;

    public FacebookCollector() {
        this.facebook = new FacebookFactory().getInstance();
        gson = new Gson();
        fbCsvEncoder = new FacebookCSVEncoder();
        fbEncoder = new FacebookSimulationEncoder();
        groupLimit = GROUP_LIMIT;
        pageLimit = PAGE_LIMIT;
        groupPostLimit = GROUP_POST_LIMIT;
        pagePostLimit = PAGE_POST_LIMIT;
    }

    public FacebookCollector(int groupLimit, int pageLimit, int groupPostLimit, int pagePostLimit) {
        this.facebook = new FacebookFactory().getInstance();
        gson = new Gson();
        fbCsvEncoder = new FacebookCSVEncoder();
        fbEncoder = new FacebookSimulationEncoder();
        this.groupLimit = groupLimit;
        this.pageLimit = pageLimit;
        this.groupPostLimit = groupPostLimit;
        this.pagePostLimit = pagePostLimit;
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
                    .limit(groupLimit)
                    .fields("id", "description", "email", "name", "privacy", "updated_time", "city"));
            groups.removeIf(g -> g.getPrivacy() == GroupPrivacyType.CLOSED);
            result = groups.stream().collect(Collectors.toList());
            LOGGER.info("Retrieved groups for keywords [" + keywords + "]: " + groups.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not search for groups", ex);
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
                    .limit(pageLimit)
                    .fields("id", "description", "emails", "about", "category", "location", "name"));
            result = pages.stream().collect(Collectors.toList());
            LOGGER.info("Retrieved pages for keywords [" + keywords + "]: " + pages.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not search for pages", ex);
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
     * Fetches posts from the specified group for a specified time period.
     *
     * @param group Facebook group
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return
     */
    public List<Post> getMessagesFromSingleSource(FacebookSource source, Date startTime, Date endTime) {
        List<Post> result = new ArrayList();
        try {
            ResponseList<Post> feeds;
            if (source.getCategory().getName().equals("Group")) {
                feeds = facebook.getGroupFeed(source.getFacebookId(), new Reading()
                        .limit(groupPostLimit)
                        .fields("id", "created_time", "description", "link", "type", "updated_time", "caption", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                        .since(startTime).until(endTime));
            } else {
                feeds = facebook.getFeed(source.getFacebookId(), new Reading()
                        .limit(pagePostLimit)
                        .fields("id", "created_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                        .since(startTime).until(endTime));
            }
            if (feeds != null && !feeds.isEmpty()) {
                result = feeds.stream().collect(Collectors.toList());
                ResponseList<Post> f = facebook.fetchNext(feeds.getPaging());
                while (f != null && !f.isEmpty()) {
                    LOGGER.info("Retrieved partial posts from group [" + source.getFacebookId() + "]");
                    result.addAll(f.stream().collect(Collectors.toList()));
                    f = facebook.fetchNext(f.getPaging());
                }
            }
            LOGGER.info("Retrieved posts from source [" + source.getFacebookId() + "]: " + result.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not retrieve posts from source", ex);
        }
        return result;
    }

    /**
     * Fetches posts from the specified group for a specified time period.
     *
     * @param group Facebook group
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return
     */
    public List<Post> getMessagesFromSingleGroup(Group group, Date startDate, Date endDate) {
        List<Post> result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getGroupFeed(group.getId(), new Reading()
                    .limit(groupPostLimit)
                    .fields("id", "created_time", "description", "link", "type", "updated_time", "caption", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            if (feeds != null && !feeds.isEmpty()) {
                result = feeds.stream().collect(Collectors.toList());
                ResponseList<Post> f = facebook.fetchNext(feeds.getPaging());
                while (f != null && !f.isEmpty()) {
                    LOGGER.info("Retrieved partial posts from group [" + group.getId() + "]");
                    result.addAll(f.stream().collect(Collectors.toList()));
                    f = facebook.fetchNext(f.getPaging());
                }
            }
            LOGGER.info("Retrieved posts from group [" + group.getId() + "]: " + result.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not retrieve posts from group", ex);
        }
        return result;
    }

    /**
     * Fetches posts from the specified group for a specified time period. The
     * number of posts that will be fetched is limited.
     *
     * @param group Facebook group
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return
     */
    public List<Post> getLimitedPostsFromSingleGroup(Group group, Date startDate, Date endDate) {
        List result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getGroupFeed(group.getId(), new Reading()
                    .limit(groupPostLimit)
                    .fields("id", "created_time", "link", "caption", "type", "updated_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            result = feeds.stream().collect(Collectors.toList());
            LOGGER.info("Retrieved posts from group [" + group.getId() + "]: " + feeds.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not retrieve posts from group[" + group.getId() + "]", ex);
        }
        return result;
    }

    /**
     * Fetches posts from the specified list of groups for a specified time
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
            List<Post> posts = getMessagesFromSingleGroup(g, startDate, endDate);
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
            List<Post> posts = getMessagesFromSingleGroup(g, startDate, endDate);
            List<FacebookSimulationMessageDocument> messages = posts.stream()
                    .map(p -> fbEncoder.createMessage(p, g))
                    .collect(Collectors.toList());
            postArray.addAll(fbEncoder.createPostArrayNode(messages));
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
     * Fetches posts from the specified page for a specified time period.
     *
     * @param page Facebook page
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return List of posts from the facebook page
     */
    public List<Post> getPostsFromSinglePage(Page page, Date startDate, Date endDate) {
        List<Post> result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getFeed(page.getId(), new Reading()
                    .limit(pagePostLimit)
                    .fields("id", "created_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            if (feeds != null && !feeds.isEmpty()) {
                result = feeds.stream().collect(Collectors.toList());
                ResponseList<Post> f = facebook.fetchNext(feeds.getPaging());
                while (f != null && !f.isEmpty()) {
                    LOGGER.info("Retrieved partial posts from page [" + page.getId() + "]: " + result.size());
                    result.addAll(f.stream().collect(Collectors.toList()));
                    f = facebook.fetchNext(f.getPaging());
                }
            }
            LOGGER.info("Retrieved posts from page [" + page.getId() + "]: " + result.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not retrieve posts from page [" + page.getId() + "]", ex);
        }
        return result;
    }

    /**
     * Fecthes posts from the specified page for a specified time period. The
     * number of posts that will be fetched is limited.
     *
     * @param page Facebook page
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return List of posts from the facebook page
     */
    public List<Post> getLimitedPostsFromSinglePage(Page page, Date startDate, Date endDate) {
        List<Post> result = new ArrayList();
        try {
            ResponseList<Post> feeds = facebook.getFeed(page.getId(), new Reading()
                    .limit(pagePostLimit)
                    .fields("id", "created_time", "description", "from", "likes", "message", "parent_id", "picture", "place", "reactions")
                    .since(startDate).until(endDate));
            result = feeds.stream().collect(Collectors.toList());
            LOGGER.info("Retrieved posts from page [" + page.getId() + "]: " + result.size());
        } catch (FacebookException ex) {
            LOGGER.error("Could not retrieve posts", ex);
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
     * @param pages Facebook pages
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return posts from facebook pages as JSON
     */
    public String getPostsFromPagesAsJSON(List<Page> pages, Date startDate, Date endDate) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode postArray = mapper.createArrayNode();
        pages.forEach(p -> {
            List<Post> posts = getPostsFromSinglePage(p, startDate, endDate);
            List<FacebookSimulationMessageDocument> messages = posts.stream()
                    .map(fP -> fbEncoder.createMessage(fP, p))
                    .collect(Collectors.toList());
            postArray.addAll(fbEncoder.createPostArrayNode(messages));
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
