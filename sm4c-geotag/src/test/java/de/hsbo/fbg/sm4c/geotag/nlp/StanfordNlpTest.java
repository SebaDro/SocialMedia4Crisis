/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag.nlp;

import com.mongodb.client.MongoCollection;
import de.hsbo.fbg.common.config.Configuration;
import de.hsbo.fbg.sm4c.geotag.nlp.NlpTagger;
import de.hsbo.fbg.sm4c.geotag.nlp.NlpTags;
import de.hsbo.fbg.sm4c.common.dao.MessageDocumentDao;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoDocumentDaoFactory;
import de.hsbo.fbg.sm4c.common.dao.mongo.MongoSimulationDatabaseConnection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.EntityMentionsAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class StanfordNlpTest {

    @Test
    public void testNlpTagging() {
        String text = "Der Ortsteil Bennewitz kann noch aktive Hilfe gebrauchen.";
        NlpTagger tagger = new NlpTagger();
        List<NlpTags> tags = tagger.annotateText(text);

        Assert.assertThat(tags.get(0).getWord(), CoreMatchers.equalTo("Der"));
        Assert.assertThat(tags.get(0).getPos(), CoreMatchers.equalTo("ART"));
        Assert.assertThat(tags.get(0).getLemma(), CoreMatchers.equalTo("der"));
        Assert.assertThat(tags.get(0).getNe(), CoreMatchers.equalTo("O"));
    }
}
