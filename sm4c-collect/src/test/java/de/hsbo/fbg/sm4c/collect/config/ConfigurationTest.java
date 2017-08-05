/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.config;

import de.hsbo.fbg.common.config.Configuration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Sebastian Drost
 */
public class ConfigurationTest {

    @Test
    public void configTest() {
        String database = Configuration.getConfig().getPropertyValue("test");
        Assert.assertThat(database, CoreMatchers.equalTo("Test"));
    }

}
