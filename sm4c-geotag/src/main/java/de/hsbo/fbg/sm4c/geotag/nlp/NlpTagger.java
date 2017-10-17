/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.nlp;

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
public class NlpTagger {

    private StanfordCoreNLP pipeline;

    public NlpTagger() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize, ssplit, pos, lemma, ner",
                "ssplit.isOneSentence", "true",
                "pos.model", "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger",
                "ner.model", "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz",
                "tokenize.language", "de",
                "ner.useSUTime", "false");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> tokenize(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.TokensAnnotation.class).forEach(t -> {
                String value = t.get(CoreAnnotations.TextAnnotation.class);
                result.add(value);
            });

        });
        return result;
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
        return result;
    }

    public List<String> lemmatize(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.TokensAnnotation.class).forEach(t -> {
                String value = t.get(CoreAnnotations.LemmaAnnotation.class);
                result.add(value);
            });

        });
        return result;
    }

    public List<String> annotateNamedEntities(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.TokensAnnotation.class).forEach(t -> {
                String value = t.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                result.add(value);
            });

        });
        return result;
    }

    public List<NlpTags> annotateText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<NlpTags> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.TokensAnnotation.class).forEach(t -> {
                String word = t.get(CoreAnnotations.TextAnnotation.class);
                String pos = t.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String lemma = t.get(CoreAnnotations.LemmaAnnotation.class);
                String ne = t.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                NlpTags tags = new NlpTags(word, pos, lemma, ne);
                result.add(tags);
            });

        });
        return result;
    }

}
