/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.logging.log4j.LogManager;

/**
 * Tokenizer for texts
 *
 * @author Sebastian Drost
 */
public class TextTokenizer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(TextTokenizer.class);

    public static final String DEFAULT_TOKEN_MODEL = "de-token.bin";

    private TokenizerModel tokenModel;
    private Tokenizer tokenizer;

    /**
     * Trains the tokenizer with a given tokenizer model
     *
     * @param modelPath path starting from the classpath to the model
     */
    public void trainTokenModel(String modelPath) {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(modelPath)) {
            tokenModel = new TokenizerModel(in);
            tokenizer = new TokenizerME(tokenModel);
        } catch (IOException ex) {
            LOGGER.error("Could not load tokenizer model", ex);
        }
    }

    /**
     * Tokenizes a given text
     *
     * @param text Text to tokenize
     * @return List of tokens
     */
    public List<String> tokenize(String text) {
        String[] tokens = null;
        try {
            tokens = tokenizer.tokenize(text);

        } catch (NullPointerException ex) {
            LOGGER.error("No tokenizer model has been trained", ex);
        }
        return Arrays.asList(tokens);
    }

}
