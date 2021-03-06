/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model;

import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.io.Serializable;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookMessageDocument extends MessageDocument implements Serializable{

    private FacebookSource source;
    private String type;

    public FacebookSource getSource() {
        return source;
    }

    public void setSource(FacebookSource source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String messageType) {
        this.type = messageType;
    }

}
