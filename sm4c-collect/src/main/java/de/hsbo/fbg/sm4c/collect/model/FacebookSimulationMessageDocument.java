/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.model;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookSimulationMessageDocument extends SimulationMessageDocument {

    private FacebookSimulationSource source;
    private String type;

    public FacebookSimulationSource getSource() {
        return source;
    }

    public void setSource(FacebookSimulationSource source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String messageType) {
        this.type = messageType;
    }

}
