/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.FacebookMessageDocument;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import de.hsbo.fbg.sm4c.common.model.Services;
import de.hsbo.fbg.sm4c.rest.view.MessageDocumentView;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sebastian Drost
 */
public class MessageDocumentEncoder {
    
    @Autowired
    private FacebookSourceEncoder facebookSourceEncoder;
    
    public MessageDocumentView encode(MessageDocument md) {
        MessageDocumentView mdv = new MessageDocumentView();
        mdv.setContent(md.getContent());
        mdv.setCreationTime(md.getCreationTime().toDate());
        mdv.setId(md.getId());
        mdv.setLabel(md.getLabel());
        mdv.setService(md.getService());
        mdv.setTraining(md.isTraining());
        mdv.setUpdateTime(md.getUpdateTime().toDate());
        if (md.getService().equals(Services.FACEBOOK.toString())) {
            mdv.setType(((FacebookMessageDocument) md).getType());
            mdv.setFacebookSource(facebookSourceEncoder.encode(((FacebookMessageDocument) md).getSource()));
        }     
        return mdv;
    }
    
}
