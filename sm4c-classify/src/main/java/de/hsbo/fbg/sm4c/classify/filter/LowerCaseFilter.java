/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.filter;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Sebastian Drost
 */
public class LowerCaseFilter implements TextFilter {

    @Override
    public List<String> filter(List<String> tokens) {
        List<String> result = tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public String filter(String text) {
        return text.toLowerCase();
    }
}
