/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.common.dao.hibernate;

import de.hsbo.fbg.sm4c.common.dao.EvaluationResultDao;
import de.hsbo.fbg.sm4c.common.model.EvaluationResult;
import org.hibernate.Session;

/**
 *
 * @author Seba
 */
public class HibernateEvaluationResultDao extends HibernateBaseDao<EvaluationResult> implements EvaluationResultDao{

    public HibernateEvaluationResultDao(Session session) {
        super(session);
    }

}
