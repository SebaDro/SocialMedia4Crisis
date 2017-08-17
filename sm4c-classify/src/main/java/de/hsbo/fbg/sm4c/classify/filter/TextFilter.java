/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.classify.filter;

import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public interface TextFilter {

    public List<String> filter(List<String> tokens);
    
}
