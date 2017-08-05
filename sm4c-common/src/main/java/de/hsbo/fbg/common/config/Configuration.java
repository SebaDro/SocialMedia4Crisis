/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsbo.fbg.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sebastian Drost
 */
public class Configuration {

    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);
    
    private final String CONFIG_FILE="config.properties";
    private Properties properties;
    
    private static Configuration config= new Configuration();
    
    private Configuration(){
        properties= new Properties();
        try {
            InputStream stream = this.getClass().getResourceAsStream("/"+CONFIG_FILE);
            properties.load(stream);
        } catch (IOException ex) {
            LOGGER.error("Configuration file could not be found",ex.getMessage());
        }
    }
    
    public static Configuration getConfig(){
        return config;
    }
    
    public String getPropertyValue(String key){
        return properties.getProperty(key);
    }
}
