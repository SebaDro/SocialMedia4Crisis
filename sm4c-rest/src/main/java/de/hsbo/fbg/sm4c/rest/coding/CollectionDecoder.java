/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.rest.view.CollectionView;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class CollectionDecoder {

    public Collection decode(CollectionView cv) {
        Collection col = new Collection();
        col.setName(cv.getName());
        col.setDescription(cv.getDescription());
        col.setCreation(DateTime.now().toDate());
        return col;
    }
}
