/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.geoparsing;

import de.hsbo.fbg.sm4c.geotag.nlp.NlpTagger;
import de.hsbo.fbg.sm4c.geotag.nlp.NlpTags;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sebastian Drost
 */
public class Geoparser {

    private static final Logger LOGGER = LogManager.getLogger(Geoparser.class);

    private StanfordCoreNLP pipeline;
    private NlpTagger nlpTagger;

    public Geoparser() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions",
                "ssplit.isOneSentence", "true",
                "pos.model", "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger",
                "ner.model", "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz",
                "tokenize.language", "de",
                "ner.useSUTime", "false");
        this.pipeline = new StanfordCoreNLP(props);
        this.nlpTagger = new NlpTagger();
    }

    public List<Toponym> recognizeLocationsExtended(String text) {
        List<Toponym> result = new ArrayList();

        List<NlpTags> tags = nlpTagger.annotateText(text);
        tags.forEach(t -> {
            //if named entity annotation is a location,
            //add the value to the candidate list of locations
            if (t.getNe().equals("I-LOC")) {

                result.add(new Toponym(t.getWord()));
            } //otherwise check if the value is indexed in geonames.org
            //and add it to candidates if true
            else if (t.getPos().equals("NN") || t.getPos().equals("NE")) {
                if (isLocation(t.getWord())) {
                    result.add(new Toponym(t.getWord()));
                }
            }
        });

        return result;
    }

    public List<Toponym> recognizeLocations(String text) {
        List<Toponym> result = new ArrayList();

        List<NlpTags> tags = nlpTagger.annotateText(text);
        tags.forEach(t -> {
            //if named entity annotation is a location,
            //add the value to the candidate list of locations
            if (t.getNe().equals("I-LOC")) {

                result.add(new Toponym(t.getWord()));
            }
        });

        return result;
    }

    private boolean isLocation(String entity) {
        boolean result = false;
        return result;
    }

}
