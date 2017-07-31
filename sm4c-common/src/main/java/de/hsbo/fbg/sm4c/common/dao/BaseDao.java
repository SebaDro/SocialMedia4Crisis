/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao;

import de.hsbo.fbg.sm4c.common.model.AbstractEntity;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Sebastian Drost
 */
public interface BaseDao<T extends AbstractEntity> {

    Optional<T> retrieveById(long id);

    List<T> retrieve();

    boolean exists(long id);

//    boolean exists(String name);

    T store(T o);

    T update(T o);

    void remove(T r);

}
