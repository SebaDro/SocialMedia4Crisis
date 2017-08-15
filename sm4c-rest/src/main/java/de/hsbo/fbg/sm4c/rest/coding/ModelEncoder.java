/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.Model;
import de.hsbo.fbg.sm4c.rest.view.ModelView;

/**
 *
 * @author Sebastian Drost
 */
public class ModelEncoder {
    
    public ModelView encode(Model model){
        ModelView mv = new ModelView();
        mv.setClassDetails(model.getEvaluation().getClassDetails());
        mv.setConfusionMatrix(model.getEvaluation().getConfusionMatrix());
        mv.setSummary(model.getEvaluation().getSummary());
        return mv;
    }    

}
