/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.filter;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Sebastian Drost
 */
public class PartOfSpeechTagger {

    private StanfordCoreNLP pipeline;

    public PartOfSpeechTagger() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize, ssplit, pos",
                "ssplit.isOneSentence", "true",
                "pos.model", "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger",
                "ner.model", "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz",
                "tokenize.language", "de",
                "ner.useSUTime", "false");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> tagPOS(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.TokensAnnotation.class).forEach(t -> {
                String value = t.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                result.add(value);
            });

        });

//        sentences.forEach(s -> {
//            s.get(CoreAnnotations.MentionsAnnotation.class).forEach(em -> {
//                String value = em.get(CoreAnnotations.TextAnnotation.class);
//                String tag = em.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                result.add(new NamedEntity(value, tag));
//            });
//        });
        return result;
    }

}
