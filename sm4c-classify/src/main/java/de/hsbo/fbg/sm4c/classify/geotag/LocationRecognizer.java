/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.classify.geotag;

import de.hsbo.fbg.common.config.Configuration;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.geonames.InvalidParameterException;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

/**
 *
 * @author Sebastian Drost
 */
public class LocationRecognizer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(LocationRecognizer.class);

    private StanfordCoreNLP pipeline;

    public LocationRecognizer() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions, parse",
                "ssplit.isOneSentence", "true",
                "parse.model", "edu/stanford/nlp/models/lexparser/germanFactored.ser.gz",
                "pos.model", "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger",
                "ner.model", "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz",
                "tokenize.language", "de",
                "ner.useSUTime", "false");
        this.pipeline = new StanfordCoreNLP(props);
        WebService.setUserName(Configuration.getConfig().getPropertyValue("geonames_user"));
    }

    public List<String> recognizeLocations(String text) {
        List<String> result = new ArrayList();
        
        List<NamedEntity> entities = tagNamedEntities(text);
        entities.forEach(e -> {
            //if named entity annotation has tagged value as location,
            //add the value to the candidate list of locations
            if (e.getTag().equals("LOC")){
                result.add(e.getValue());
            }
            //otherwise check if the value is indexed in geonames.org
            //and add it to candidates if true
            else{
                if(isLocation(e.getValue())){
                    result.add(e.getValue());
                }
            }
        });

        return result;
    }

    private boolean isLocation(String entity) {
        boolean result = false;
        try {
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setNameEquals(entity);
            searchCriteria.setCountryCode("DE");
            searchCriteria.setMaxRows(1);
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            List<Toponym> topos = searchResult.getToponyms();
            result = !topos.isEmpty();
        } catch (InvalidParameterException ex) {
            LOGGER.error("Country code is not supported", ex);
        } catch (Exception ex) {
            LOGGER.error("Search for geoname failed", ex);
        }
        return result;
    }

    private List<NamedEntity> tagNamedEntities(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<NamedEntity> result = new ArrayList();

        sentences.forEach(s -> {
            s.get(CoreAnnotations.MentionsAnnotation.class).forEach(em -> {
                String value = em.get(CoreAnnotations.TextAnnotation.class);
                String tag = em.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                result.add(new NamedEntity(value, tag));
            });
        });
        return result;
    }

}
