/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.mining.dao;

import de.hsbo.fbg.sm4c.mining.config.Configuration;

/**
 *
 * @author Sebastian Drost
 */
public class MongoDbDaoFactory implements DaoFactory {

    private String host;
    private int port;
    private String dbName;
    private String colName;

    public MongoDbDaoFactory(String colName) {
        this.host = Configuration.getConfig().getPropertyValue("db_host");
        this.port = Integer.parseInt(Configuration.getConfig().getPropertyValue("db_port"));
        this.dbName = Configuration.getConfig().getPropertyValue("db_name");
        this.colName = colName;
    }

    @Override
    public FacebookDao createFacebookDao() {
        return new MongoDbFacebookDao(host, port, dbName, colName);
    }

}
