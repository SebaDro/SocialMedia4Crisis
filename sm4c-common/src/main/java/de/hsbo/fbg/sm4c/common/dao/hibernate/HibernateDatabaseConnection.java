/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateDatabaseConnection implements InitializingBean, DisposableBean {

    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("/hibernate.cfg.xml");

        this.sessionFactory = cfg.buildSessionFactory();
    }

    @Override
    public void destroy() throws Exception {
        this.sessionFactory.close();
    }

    public Session createSession() throws HibernateException {
        return this.sessionFactory.openSession();
    }
}
