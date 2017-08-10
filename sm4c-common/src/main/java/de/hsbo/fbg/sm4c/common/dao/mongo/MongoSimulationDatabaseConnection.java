/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.hsbo.fbg.common.config.Configuration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Sebastian Drost
 */
public class MongoSimulationDatabaseConnection implements InitializingBean, DisposableBean {

    private MongoClient mongoClient;
    private String dbName;

    @Override
    public void afterPropertiesSet() {
        String dbHost = Configuration.getConfig().getPropertyValue("db_host");
        String dbPort = Configuration.getConfig().getPropertyValue("db_port");
        dbName = Configuration.getConfig().getPropertyValue("db_sim_name");
        this.mongoClient = new MongoClient(dbHost, Integer.parseInt(dbPort));
    }

    @Override
    public void destroy() throws Exception {
        mongoClient.close();
    }
    
    public MongoDatabase getDatabase(){
        return mongoClient.getDatabase(dbName);
    }

}
