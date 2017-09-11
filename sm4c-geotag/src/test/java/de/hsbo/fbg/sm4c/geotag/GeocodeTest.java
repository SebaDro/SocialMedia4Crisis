/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag;

//import de.hsbo.fbg.sm4c.geotag.ArcGISGeoTagger;
import de.hsbo.fbg.sm4c.common.model.Location;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geonames.InvalidParameterException;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

/**
 *
 * @author Sebastian Drost
 */
public class GeocodeTest {

    public static void main(String[] args) {
        GeocodeTest geocode = new GeocodeTest();
        geocode.testGeocoding();
    }

    private void testGeocoding() {
        String text = "Hallo.... ich befinde mich momentan in Deutschland auf Montage und muss mit Bedauern ansehen wie Thüringen absäuft....ich möchte Euch anbieten in irgendeiner Form zu helfen....ab Dienstag, 11.6 bin ich wieder in Sonneberg und würde mit ein paar Helfern meiner Firma Euch unterstützen. Aufräumungsarbeiten, Trocknungen ...alles was anfallen würde. ca 1 Woche könnten wir helfen. Wer was weiss oder Hilfe benötigt...bitte meldet Euch an der Schillerstraße. Gruss Illig Tobias";
        LocationRecognizer recognizer = new LocationRecognizer();
        List<String> locations = recognizer.recognizeLocations(text);
        ArcGISGeoCoder geoCoder = new ArcGISGeoCoder();
        locations.forEach(l -> {
            try {
                Location loc = geoCoder.findLocation(l);
            } catch (Exception ex) {
                Logger.getLogger(GeocodeTest.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        locations.toString();
    }

//    private void testArcGIS() {
//        ArcGISGeoTagger tagger = new ArcGISGeoTagger();
//        tagger.geocode("Thüringen");
//
//    }
    private void testGeoNames() {
        String text2 = "Hallo.... ich befinde mich momentan in Deutschland auf Montage und muss mit Bedauern ansehen wie Thüringen absäuft....ich möchte Euch anbieten in irgendeiner Form zu helfen....ab Dienstag, 11.6 bin ich wieder in Sonneberg und würde mit ein paar Helfern meiner Firma Euch unterstützen. Aufräumungsarbeiten, Trocknungen ...alles was anfallen würde. ca 1 Woche könnten wir helfen. Wer was weiss oder Hilfe benötigt...bitte meldet Euch an der Altenessenerstraße. Gruss Illig Tobias";
        try {
            WebService.setUserName("Tortuga");
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
//            searchCriteria.setQ("Peter");
            searchCriteria.setNameEquals("Brückenstraße");
//            searchCriteria.se
//            FeatureClass [] fc = new FeatureClass [] {FeatureClass.A};
//            searchCriteria.setFeatureClass(FeatureClass.A);

            searchCriteria.setCountryCode("DE");
            searchCriteria.setMaxRows(1);
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            List<Toponym> topos = searchResult.getToponyms();
            topos.get(0);
        } catch (InvalidParameterException ex) {
            Logger.getLogger(GeocodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GeocodeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
