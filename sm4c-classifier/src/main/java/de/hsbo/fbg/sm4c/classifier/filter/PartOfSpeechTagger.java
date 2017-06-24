/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classifier.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import org.apache.logging.log4j.LogManager;

/**
 * Class for identifying the part of speech of a text
 *
 * @author Sebastian Drost
 */
public class PartOfSpeechTagger {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PartOfSpeechTagger.class);

    public static final String DEFAULT_POS_MODEL = "de-pos-maxent.bin";

    private POSModel posModel;
    private POSTagger posTagger;

    /**
     * Trains the part of speech tagger with a given POS Model.
     *
     * @param modelPath path starting from the classpath to the model
     */
    public void trainPOSModel(String modelPath) {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(modelPath)) {
            posModel = new POSModel(in);
            posTagger = new POSTaggerME(posModel);
        } catch (IOException ex) {
            LOGGER.error("Could not load tokenizer model", ex);
        }
    }

    /**
     * Tags the part of speech for given tokens.
     *
     * @param tokens Tokens to tag
     * @return List of tags for the tokens
     */
    public List<String> tagTokens(List<String> tokens) {
        return Arrays.asList(posTagger.tag(tokens.toArray(new String[0])));
    }

}
