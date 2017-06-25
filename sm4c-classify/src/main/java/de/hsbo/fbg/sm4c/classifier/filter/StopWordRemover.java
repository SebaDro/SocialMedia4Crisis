/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classifier.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;

/**
 * Remover of stop words
 *
 * @author Sebastian Drost
 */
public class StopWordRemover {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(StopWordRemover.class);

    public static final String DEFAULT_STOP_WORD_LIST = "german_stopwords_plain.txt";

    private ArrayList<String> stopWordList;

    public StopWordRemover() {
        stopWordList = new ArrayList();
    }

    public ArrayList<String> getStopWordList() {
        return stopWordList;
    }

    /**
     * Loads a List of default stop words from a given file
     *
     * @param listPath path starting from the classpath to the stop word list
     */
    public void loadStopWordList(String listPath) {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(listPath)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF8"));
            String stopWord;
            while ((stopWord = reader.readLine()) != null) {
                if (!stopWord.startsWith(";")) {
                    stopWordList.add(stopWord);
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Could not load list of stop words", ex);
        }
    }

    /**
     * Removes the default stop words from a list of tokens
     *
     * @param tokens List of tokens the stop words will be removed of
     * @return List of tokens without the default stop words
     */
    public List<String> removeStopWords(List<String> tokens) {
        return tokens.stream().filter(t -> !stopWordList
                .stream().anyMatch(sw -> sw.equalsIgnoreCase(t)))
                .collect(Collectors.toList());

    }

    public String removeStopWords(String text, String[] stopWords) {
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

    public String removeStopWords(String text, List<String> stopWords) {
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

}
