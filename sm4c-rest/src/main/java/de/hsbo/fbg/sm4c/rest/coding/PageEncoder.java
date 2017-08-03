/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import de.hsbo.fbg.sm4c.rest.view.SourceCategoryView;
import facebook4j.Page;

/**
 *
 * @author Sebastian Drost
 */
public class PageEncoder {

    public FacebookSourceView encode(Page page) {
        FacebookSourceView s = new FacebookSourceView();
        s.setName(page.getName());
        s.setDescription(page.getAbout());
        s.setFacebookId(page.getId());
        SourceCategoryView sc = new SourceCategoryView();
        sc.setName("Page");
        s.setSourceCategory(sc);
        return s;
    }
}
