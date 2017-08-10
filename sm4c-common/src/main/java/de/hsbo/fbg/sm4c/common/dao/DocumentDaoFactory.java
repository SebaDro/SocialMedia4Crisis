/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.sm4c.common.model.Collection;

/**
 *
 * @author Sebastian Drost
 */
public interface DocumentDaoFactory<T> {

    T getContext(Collection collection);

    MessageDocumentDao createFacebookMessageDocumentDao(T daoContext);

}
