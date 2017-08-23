/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 *
 * @author Sebastian Drost
 */
public class TextPreprocessor {

//    private TextTokenizer tokenizer;
    private List<TextFilter> filter;

    public TextPreprocessor() {
//        tokenizer = new TextTokenizer();
//        tokenizer.trainTokenModel(TextTokenizer.DEFAULT_TOKEN_MODEL);
        filter = new ArrayList();
    }

    public TextPreprocessor(List<TextFilter> filter) {
//        tokenizer = new TextTokenizer();
//        tokenizer.trainTokenModel(TextTokenizer.DEFAULT_TOKEN_MODEL);
        this.filter = filter;
    }

    public String preprocessText(String text) {
//        List<String> tokens = tokenizer.tokenize(text);
//        for (TextFilter f : filter) {
//            tokens = f.filter(tokens);
//        }
//        return tokens.stream().collect(Collectors.joining(" "));
        for (TextFilter f : filter) {
            text = f.filter(text);
        }
        return text;
    }

    public void addFilter(TextFilter filter) {
        this.filter.add(filter);
    }

    public void removeFilter(TextFilter filter) {
        this.filter.remove(filter);
    }

}
