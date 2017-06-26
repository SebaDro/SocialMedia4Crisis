/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.mining.model;


/**
 *
 * @author Sebastian Drost
 */
public class FacebookMessage extends Message{

    private FacebookSource source;
  

    public FacebookSource getSource() {
        return source;
    }

    public void setSource(FacebookSource source) {
        this.source = source;
    }

}
