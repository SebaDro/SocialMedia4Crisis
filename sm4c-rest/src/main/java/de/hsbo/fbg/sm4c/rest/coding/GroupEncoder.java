/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import de.hsbo.fbg.sm4c.rest.view.SourceCategoryView;
import facebook4j.Group;

/**
 *
 * @author Sebastian Drost
 */
public class GroupEncoder {

    public FacebookSourceView encode(Group group) {
        FacebookSourceView s = new FacebookSourceView();
        s.setName(group.getName());
        s.setDescription(group.getDescription());
        s.setFacebookId(group.getId());
        SourceCategoryView sc = new SourceCategoryView();
        sc.setName("Group");
        s.setSourceCategory(sc);
        return s;
    }

}
