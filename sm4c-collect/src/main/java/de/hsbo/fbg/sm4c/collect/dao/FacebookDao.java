/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.dao;

import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import facebook4j.Group;
import facebook4j.Page;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface FacebookDao {

    public void storeSingleFacebookMessage(FacebookMessageDocument message);

    public void storeFacebookMessages(List<FacebookMessageDocument> messages);

    public List<String> getValues();
    
    public FacebookMessageDocument getValueByFbId(String fbId);
    
    public List<FacebookMessageDocument> getValuesForTimeSpan(Date startTime, Date endTime);

    public boolean containsPage(Page page);

    public boolean containsGroup(Group group);

    public boolean containsMessage(FacebookMessageDocument message);

    public void deleteAllValues();
    
    public void deleteValue(FacebookMessageDocument message);
}
