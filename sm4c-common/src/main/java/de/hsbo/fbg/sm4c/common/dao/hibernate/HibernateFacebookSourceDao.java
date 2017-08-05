/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.FacebookSourceDao;
import de.hsbo.fbg.sm4c.common.model.FacebookSource;
import java.util.Optional;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateFacebookSourceDao extends HibernateBaseDao<FacebookSource> implements FacebookSourceDao {

    public HibernateFacebookSourceDao(Session session) {
        super(session);
    }

    @Override
    public Optional<FacebookSource> retrieveByFacebookId(String facebookId) {
        return retrieveByKey("facebookId", facebookId);
    }

}
