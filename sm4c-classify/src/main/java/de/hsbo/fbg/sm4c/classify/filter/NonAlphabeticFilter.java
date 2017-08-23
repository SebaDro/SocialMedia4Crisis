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
public class NonAlphabeticFilter implements TextFilter {

    @Override
    public List<String> filter(List<String> tokens) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String filter(String text) {
        return text.replaceAll("[^a-zA-Z äÄöÖüÜß-]", " ");
    }

}
