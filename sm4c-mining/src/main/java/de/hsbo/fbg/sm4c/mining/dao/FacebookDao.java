/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.dao;

import de.hsbo.fbg.sm4c.mining.model.FacebookMessage;
import facebook4j.Group;
import facebook4j.Page;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface FacebookDao {

    public void storeSingleFacebookMessage(FacebookMessage message);

    public void storeFacebookMessages(List<FacebookMessage> messages);

    public List<String> getValues();
    
    public FacebookMessage getValueByFbId(String fbId);
    
    public List<FacebookMessage> getValuesForTimeSpan(Date startTime, Date endTime);

    public boolean containsPage(Page page);

    public boolean containsGroup(Group group);

    public boolean containsMessage(FacebookMessage message);

    public void deleteAllValues();
    
    public void deleteValue(FacebookMessage message);
}
