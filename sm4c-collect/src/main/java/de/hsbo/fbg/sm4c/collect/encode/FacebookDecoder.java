/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.Location;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Services;
import de.hsbo.fbg.sm4c.common.model.SourceType;
import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookDecoder {

    /**
     * Creates a Facebook message from a Facebook post
     *
     * @param post Facebook post
     * @param source the page or group the post was published
     * @return Facebook message
     */
    public MessageDocument createMessage(Post post, FacebookSource source) {
        MessageDocument message = new FacebookMessageDocument();
        message.setId(post.getId());
        message.setContent(post.getMessage());
        DateTime creationTime = new DateTime(post.getCreatedTime());
        message.setCreationTime(creationTime);
        DateTime updateTime = new DateTime(post.getUpdatedTime());
        message.setUpdateTime(updateTime);
        message.setService(Services.FACEBOOK.toString());
        message.setTraining(false);
        List<Location> locations = new ArrayList<Location>();
        if (post.getPlace() != null) {
            if (post.getPlace().getLocation().getLatitude() != null
                    && post.getPlace().getLocation().getLongitude() != null) {
                Location location = new Location();
                location.setLatitude(post.getPlace().getLocation().getLatitude());
                location.setLongitude(post.getPlace().getLocation().getLongitude());
                locations.add(location);
            }
        }
        message.setLocations(locations);
        ((FacebookMessageDocument) message).setType(post.getType());
        ((FacebookMessageDocument) message).setSource(source);
        return message;
    }

    public FacebookSource createFacebookSource(Group g) {
        FacebookSource source = new FacebookSource();
        source.setFacebookId(g.getId());
        source.setName(g.getName());
        source.setDescription(g.getDescription());
        Set<Location> locations = new HashSet();
        source.setLocations(locations);
        SourceType sourceType = new SourceType();
        sourceType.setName("Group");
        source.setType(sourceType);
        return source;
    }

    public FacebookSource createFacebookSource(Page p) {
        FacebookSource source = new FacebookSource();
        source.setFacebookId(p.getId());
        source.setName(p.getName());
        source.setDescription(p.getAbout());
        Set<Location> locations = new HashSet();
        if (p.getLocation() != null) {
            if (p.getLocation().getLatitude() != null && p.getLocation().getLongitude() != null) {
                Location location = new Location();
                location.setLatitude(p.getLocation().getLatitude());
                location.setLongitude(p.getLocation().getLongitude());
                locations.add(location);
            }
        }
        source.setLocations(locations);
        SourceType sourceType = new SourceType();
        sourceType.setName("Page");
        source.setType(sourceType);
        return source;
    }
}
