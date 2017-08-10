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

/**
 *
 * @author Sebastian Drost
 */
public class FacebookCollector extends Collector {

    private FacebookAPI api;
    private FacebookDecoder decoder;

    public FacebookCollector(Collection collection) {
        super(collection);
        this.api = new FacebookAPI();
        this.decoder = new FacebookDecoder();
    }

    @Override
    public List<MessageDocument> collectMessages(Date startTime, Date endTime) {
        List<MessageDocument> result = new ArrayList();
        collection.getSources().forEach(s -> {
            List<Post> posts = api.getMessagesFromSingleSource(s, startTime, endTime);
            posts.forEach(p ->{
                result.add(decoder.createMessage(p, s));
            });
        });
        return result;
    }
}
