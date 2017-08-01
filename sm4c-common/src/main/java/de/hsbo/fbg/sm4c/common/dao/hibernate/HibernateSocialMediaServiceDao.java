/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.SocialMediaServiceDao;
import de.hsbo.fbg.sm4c.common.model.SocialMediaService;
import de.hsbo.fbg.sm4c.common.model.SocialMediaServiceType;
import java.util.Optional;
import org.hibernate.Session;

/**
 *
 * @author Sebastian Drost
 */
public class HibernateSocialMediaServiceDao extends HibernateBaseDao<SocialMediaService> implements SocialMediaServiceDao {

    public HibernateSocialMediaServiceDao(Session session) {
        super(session);
    }

}
