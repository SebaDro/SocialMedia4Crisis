/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geocoding;

import java.util.HashMap;

/**
 *
 * @author Sebastian Drost
 */
public class GeonameTree {

    private String geonameId;
    private HashMap<String, GeonameTree> nodes;

    public GeonameTree() {
        this.nodes = new HashMap();
    }

    public String getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(String geonameId) {
        this.geonameId = geonameId;
    }

    public void addNode(GeonameTree node) {
        this.nodes.put(node.getGeonameId(), node);
    }

    public HashMap<String, GeonameTree> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<String, GeonameTree> nodes) {
        this.nodes = nodes;
    }

}
