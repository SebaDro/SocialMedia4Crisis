/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.collect.dao;

import de.hsbo.fbg.sm4c.collect.model.FacebookSimulationMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import facebook4j.Group;
import facebook4j.Page;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface FacebookSimulationDao {

    public void storeSingleFacebookMessage(FacebookSimulationMessageDocument message);

    public void storeFacebookMessages(List<FacebookSimulationMessageDocument> messages);

    public List<String> getValues();
    
    public FacebookSimulationMessageDocument getValueByFbId(String fbId);
    
    public List<FacebookSimulationMessageDocument> getValuesForTimeSpan(Date startTime, Date endTime);

    public boolean containsPage(Page page);

    public boolean containsGroup(Group group);

    public boolean containsMessage(FacebookSimulationMessageDocument message);

    public void deleteAllValues();
    
    public void deleteValue(FacebookMessageDocument message);
}
