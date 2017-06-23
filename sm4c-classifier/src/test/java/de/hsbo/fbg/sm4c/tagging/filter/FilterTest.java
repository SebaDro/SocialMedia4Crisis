/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.sm4c.tagging.filter;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class FilterTest {

    @Before
    public void setup(){
        this.getClass().getResourceAsStream("sample-post.txt");
    }
    
    @Test
    public void roundTrip(){
        
    }
}
