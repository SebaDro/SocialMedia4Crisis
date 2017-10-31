/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface Collector {
    
    public abstract List<MessageDocument> collectMessages(Date startTime, Date endTime); 
}
