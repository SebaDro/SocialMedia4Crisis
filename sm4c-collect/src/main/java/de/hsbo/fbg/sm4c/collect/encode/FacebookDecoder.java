/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Services;
import facebook4j.Post;
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
        ((FacebookMessageDocument) message).setType(post.getType());
        ((FacebookMessageDocument) message).setSource(source);
        return message;
    }
}
