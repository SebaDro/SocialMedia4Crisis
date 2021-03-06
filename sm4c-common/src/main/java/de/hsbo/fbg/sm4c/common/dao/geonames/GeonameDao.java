/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.geonames;

import de.hsbo.fbg.sm4c.common.dao.BaseDao;
import de.hsbo.fbg.sm4c.common.model.geonames.Geoname;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Seba
 */
public interface GeonameDao extends BaseDao<Geoname> {

    List<Geoname> retrieveCandidatesByName(String name);
}
