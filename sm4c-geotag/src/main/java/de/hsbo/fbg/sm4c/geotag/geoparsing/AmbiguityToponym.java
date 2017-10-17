/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geoparsing;

import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import de.hsbo.fbg.sm4c.geotag.geocoding.GeonameNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public class AmbiguityToponym {

    private String toponym;
    private List<GeonameNode> candidates;

    public AmbiguityToponym(String toponym) {
        this.toponym = toponym;
        candidates = new ArrayList();
    }

    public void addCandidate(GeonameNode candidate) {
        this.candidates.add(candidate);
    }

    public String getToponym() {
        return toponym;
    }

    public List<GeonameNode> getCandidates() {
        return candidates;
    }

}
