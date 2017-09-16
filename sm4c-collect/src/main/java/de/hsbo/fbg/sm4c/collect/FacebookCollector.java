/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.collect.encode.FacebookDecoder;
import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import facebook4j.Post;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookCollector implements Collector {

    private static final Logger LOGGER = LogManager.getLogger(FacebookCollector.class);
    
    private Collection collection;
    private FacebookApi api;
    private FacebookDecoder decoder;

    public FacebookCollector(Collection collection) {
        this.collection = collection;
        this.api = new FacebookApi();
        this.decoder = new FacebookDecoder();
    }

    @Override
    public List<MessageDocument> collectMessages(Date startTime, Date endTime) {
        List<MessageDocument> result = new ArrayList();
        collection.getSources().forEach(s -> {
            List<Post> posts = api.getPostsFromSingleSource(s, startTime, endTime);
            posts.forEach(p -> {
                result.add(decoder.createMessage(p, s));
            });
        });
        return result;
    }
}
