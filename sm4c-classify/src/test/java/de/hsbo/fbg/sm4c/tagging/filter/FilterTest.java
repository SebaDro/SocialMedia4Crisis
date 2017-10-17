/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.tagging.filter;

import de.hsbo.fbg.sm4c.classify.filter.LowerCaseFilter;
import de.hsbo.fbg.sm4c.classify.filter.NonAlphabeticFilter;
import de.hsbo.fbg.sm4c.classify.filter.NlpTagger;
import de.hsbo.fbg.sm4c.classify.filter.StopWordRemover;
import de.hsbo.fbg.sm4c.classify.filter.TextPreprocessor;
import de.hsbo.fbg.sm4c.classify.filter.URLFilter;
import de.hsbo.fbg.sm4c.classify.filter.WhitespaceFilter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class FilterTest {

    private String sampleText;
    private StopWordRemover stopWordRemover;
    private LowerCaseFilter lowerCaseFilter;
    private NonAlphabeticFilter nonAlphaFilter;
//    private NLPPartOfSpeechTagger posTagger;
    private TextPreprocessor preProcessor;
    private WhitespaceFilter whitepaceFilter;
    private URLFilter urlFilter;
    private NlpTagger posTagger;
    private String text;

    @Before
    public void setup() throws IOException {

        sampleText = IOUtils.toString(this.getClass().getResourceAsStream("sample-post.txt"), "UTF8");
        stopWordRemover = new StopWordRemover();
        stopWordRemover.loadStopWordList(StopWordRemover.DEFAULT_STOP_WORD_LIST);
        posTagger = new NlpTagger();
//        posTagger = new NLPPartOfSpeechTagger();
//        posTagger.trainPOSModel(NLPPartOfSpeechTagger.DEFAULT_POS_MODEL);
        lowerCaseFilter = new LowerCaseFilter();
        nonAlphaFilter = new NonAlphabeticFilter();
        whitepaceFilter = new WhitespaceFilter();
        urlFilter = new URLFilter();

        preProcessor = new TextPreprocessor();
//        preProcessor.addFilter(lowerCaseFilter);
        preProcessor.addFilter(urlFilter);
        preProcessor.addFilter(nonAlphaFilter);
        preProcessor.addFilter(stopWordRemover);
        preProcessor.addFilter(whitepaceFilter);

        text = "Hallo an alle Leidenden und die ganzen tapferen Helfer!\n"
                + "Wir, das Team der Wing-Tsun Kampfkunstschule Halle möchten euch gerne mit bei der Stabilisierung der Dämme helfen! Wir sind zwischen 5 und 10 Kung Fu Kämpfer, die mehr als genug Antrieb, Kraft und Motivation haben. Da von uns auch viele arbeiten müssen, bieten wir unsere Bereitschaft ab Morgen 15 Uhr an! Sagt bitte bescheid wo noch Hilfe benötigt wird! Ihr Erreicht mich unter der 0173/ 5926103. Ich gebe die Informationen dann weiter an das Team.\n"
                + "Bitte diese Information weiterleiten!\n"
                + "Liebe Grüße\n"
                + "Steve Kraneis( Leiter der Wing Tsun Kampfkunstschule Halle)"
                + "Besucht uns auf:http://www.wing-tsun.com www.wing.tsun.de";
    }

    @Test
    public void roundTrip() {

//        String testStr = "da kommt-was auf uns zu. aber,ab ab der mittagszeit.";
//
////        String res = stopWordRemover.filter(text);
//        String res = nonAlphaFilter.filter(text);
//        res = whitepaceFilter.filter(res);
        String result = preProcessor.preprocessText(text);

        Assert.assertThat(stopWordRemover.getStopWordList().get(0),
                CoreMatchers.equalTo("ab"));
        Assert.assertThat(stopWordRemover.getStopWordList().get(stopWordRemover.getStopWordList().size() - 1),
                CoreMatchers.equalTo("übrigens"));

//        List<String> taggedTokens = posTagger.tagTokens(filteredTokens);
        List<String> posTokens = posTagger.tagPOS(result);

    }
}
