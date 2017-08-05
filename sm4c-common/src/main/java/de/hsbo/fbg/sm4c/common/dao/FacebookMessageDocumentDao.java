/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao;

import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Sebastian Drost
 */
public interface FacebookMessageDocumentDao {

    FacebookMessageDocument retrieveById(String id);

    List<FacebookMessageDocument> retrieve();

    List<FacebookMessageDocument> retrieveForTimeSpan(Date startTime, Date endTime);

    boolean exists(FacebookMessageDocument doc);

    void store(FacebookMessageDocument doc);

    void store(List<FacebookMessageDocument> docs);

    void remove(FacebookMessageDocument doc);
    
    void removeAll();

}
