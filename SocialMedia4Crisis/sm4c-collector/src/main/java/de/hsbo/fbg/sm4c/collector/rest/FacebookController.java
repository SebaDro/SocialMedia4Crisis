/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collector.rest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sebastian Drost
 */
@RestController
@RequestMapping(produces = {"text/plain"})
public class FacebookController implements InitializingBean{

    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    public String printHello() {
        return "hello";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
