/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.model.AbstractEntity;
import java.util.List;
import java.util.Optional;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.core.GenericTypeResolver;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateBaseDao<T extends AbstractEntity> {

    private final Class<T> genericType;
    private final Session session;

    public HibernateBaseDao(Session session) {
        this.session = session;
        this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), HibernateBaseDao.class);
    }

    public Optional<T> retrieveById(long id) {
        Query<T> query = session.createQuery("select o " + "from " + this.genericType.getSimpleName() + " o " + "where o.id = :id")
                .setParameter("id", id);
        List<T> result = query.getResultList();
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }

    public Optional<T> retrieveByName(String name) {
        return retrieveByKey("name", name);
    }

    public Optional<T> retrieveByKey(String key, String value) {
        Query<T> query = session.createQuery("select o "
                + "from " + this.genericType.getSimpleName() + " o "
                + "where o." + key + " = :value")
                .setParameter("value", value);
        List<T> result = query.getResultList();
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }

    public List<T> retrieve() {
        List<T> results = session.createQuery("from " + this.genericType.getSimpleName()).getResultList();
        return results;
    }

    public boolean exists(long id) {
        return retrieveById(id).isPresent();
    }

//    public boolean exists(String name) {
//
//    }
    public T store(T o) {
        Transaction t = session.beginTransaction();
        session.save(o);
        t.commit();
        return o;

    }

    public T update(T o){
        Transaction t = session.beginTransaction();
        session.update(o);
        t.commit();
        return o;
    }

    public void remove(T r) throws PersistenceException {
        Transaction t = session.beginTransaction();
        session.delete(r);
        t.commit();
    }

}
