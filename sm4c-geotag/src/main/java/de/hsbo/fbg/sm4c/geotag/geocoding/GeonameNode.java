/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geocoding;

import de.hsbo.fbg.sm4c.common.model.geonames.GeonameEntity;
import java.util.HashMap;

/**
 *
 * @author Sebastian Drost
 */
public class GeonameNode {

    private GeonameEntity entity;
    private GeonameNode parent;
    private HashMap<String, GeonameNode> chilren;

    public GeonameNode() {
        this.chilren = new HashMap();
    }

    public GeonameEntity getEntity() {
        return entity;
    }

    public void setEntity(GeonameEntity entity) {
        this.entity = entity;
    }

    public GeonameNode getParent() {
        return parent;
    }

    public void setParent(GeonameNode parent) {
        this.parent = parent;
    }

    public HashMap<String, GeonameNode> getChilren() {
        return chilren;
    }

    public void setChilren(HashMap<String, GeonameNode> chilren) {
        this.chilren = chilren;
    }

    public void addChild(GeonameNode node) {
        this.chilren.put(node.getEntity().getGeonameid(), node);
    }
}
