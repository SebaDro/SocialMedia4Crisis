/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.rest.coding;

import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.rest.view.CollectionView;
import de.hsbo.fbg.sm4c.rest.view.FacebookSourceView;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class CollectionEncoder {
    
    private FacebookSourceEncoder sourceEncoder;
    
    public CollectionEncoder (){
        sourceEncoder = new FacebookSourceEncoder();
    }
    
    public CollectionView encode (Collection c){
        CollectionView cv = new CollectionView();
        cv.setId(c.getId());
        cv.setName(c.getName());
        cv.setDescription(c.getDescription());
        cv.setStatus(c.getStatus().getName());
        List<FacebookSourceView> fsv = c.getSources().stream()
                .map(s -> sourceEncoder.encode(s))
                .collect(Collectors.toList());
        cv.setFacebookSources(fsv);
        cv.setCreation(c.getCreation());
        List<String> keywords = c.getKeywords().stream().map(k -> k.getName()).collect(Collectors.toList());
        cv.setKeywords(keywords);
        List<String> labels = c.getLabels().stream().map(l -> l.getName()).collect(Collectors.toList());
        cv.setLabels(labels);
        List<String> services = c.getServices().stream().map(s -> s.getName()).collect(Collectors.toList());
        cv.setServices(services);
        return cv;
    }
    
        public CollectionView encodeReduced (Collection c){
        CollectionView cv = new CollectionView();
        cv.setId(c.getId());
        cv.setName(c.getName());
        cv.setDescription(c.getDescription());
        cv.setStatus(c.getStatus().getName());       
        cv.setCreation(c.getCreation());
        List<String> keywords = c.getKeywords().stream().map(k -> k.getName()).collect(Collectors.toList());
        cv.setKeywords(keywords);
        List<String> labels = c.getLabels().stream().map(l -> l.getName()).collect(Collectors.toList());
        cv.setLabels(labels);
        List<String> services = c.getServices().stream().map(s -> s.getName()).collect(Collectors.toList());
        cv.setServices(services);
        return cv;
    }

}
