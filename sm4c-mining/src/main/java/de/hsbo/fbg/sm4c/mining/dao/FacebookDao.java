/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.dao;

import facebook4j.Group;
import facebook4j.Page;
import facebook4j.Post;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface FacebookDao {

    public void storeSingleFacebookPost(Post post, Object source);

    public void storeFaceBookPosts(List<Post> posts, Object source);

    public List<String> getValues();

    public boolean containsPage(Page page);

    public boolean containsGroup(Group group);

    public boolean containsPost(Post post);

    public void deleteValues();
}
