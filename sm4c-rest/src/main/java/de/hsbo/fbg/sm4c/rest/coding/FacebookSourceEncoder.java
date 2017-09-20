/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import de.hsbo.fbg.sm4c.rest.view.LocationView;
import de.hsbo.fbg.sm4c.rest.view.SourceTypeView;
import facebook4j.Group;
import facebook4j.Page;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookSourceEncoder {

    public FacebookSourceView encode(FacebookSource s) {
        FacebookSourceView sv = new FacebookSourceView();
        sv.setName(s.getName());
        sv.setDescription(s.getDescription());
        sv.setFacebookId(s.getFacebookId());
        List<LocationView> locations = new ArrayList();
        if (s.getLocations() != null) {
            s.getLocations().forEach(l -> {
                LocationView location = new LocationView();
                location.setLatitude(l.getLatitude());
                location.setLongitude(l.getLongitude());
            });
        }
        sv.setLocation(locations);

        SourceTypeView scv = new SourceTypeView();
        scv.setName(s.getType().getName());
        sv.setSourceCategory(scv);
        return sv;
    }

    public FacebookSourceView encode(Group group) {
        FacebookSourceView s = new FacebookSourceView();
        s.setName(group.getName());
        s.setDescription(group.getDescription());
        s.setFacebookId(group.getId());
        List<LocationView> locations = new ArrayList();
        s.setLocation(locations);
        SourceTypeView sc = new SourceTypeView();
        sc.setName("Group");
        s.setSourceCategory(sc);
        return s;
    }

    public FacebookSourceView encode(Page page) {
        FacebookSourceView s = new FacebookSourceView();
        s.setName(page.getName());
        s.setDescription(page.getAbout());
        s.setFacebookId(page.getId());
        List<LocationView> locations = new ArrayList();
        if (page.getLocation() != null&&page.getLocation().getLatitude()!=null&&page.getLocation().getLongitude()!=null) {
            LocationView location = new LocationView();
            location.setLatitude(page.getLocation().getLatitude());
            location.setLongitude(page.getLocation().getLongitude());
            locations.add(location);
        }
        s.setLocation(locations);
        SourceTypeView sc = new SourceTypeView();
        sc.setName("Page");
        s.setSourceCategory(sc);
        return s;
    }
}
