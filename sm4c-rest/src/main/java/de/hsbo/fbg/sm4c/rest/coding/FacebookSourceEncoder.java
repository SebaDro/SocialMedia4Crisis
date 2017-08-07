/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import de.hsbo.fbg.sm4c.rest.view.SourceTypeView;

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
        SourceTypeView scv = new SourceTypeView();
        scv.setName(s.getType().getName());
        sv.setSourceCategory(scv);
        return sv;
    }
}
