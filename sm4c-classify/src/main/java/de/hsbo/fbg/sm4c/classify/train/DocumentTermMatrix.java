/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.classify.train;

import weka.core.Instances;

/**
 *
 * @author Sebastian Drost
 */
public class DocumentTermMatrix {

    private Instances dtm;
    
    public DocumentTermMatrix (Instances dtm ){
        this.dtm = dtm;
    }

    public Instances getDtm() {
        return dtm;
    }

    public void setDtm(Instances dtm) {
        this.dtm = dtm;
    }
}
