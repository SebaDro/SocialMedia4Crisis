/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect.encode;

import facebook4j.Group;
import facebook4j.Page;
import java.util.List;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookCSVEncoder {

    public String createGroupCSV(List<Group> facebookGroups) {
        StringBuilder builder = new StringBuilder("id;name;update_time");
        builder.append(System.getProperty("line.separator"));
        facebookGroups.forEach(g -> {
            builder.append(g.getId());
            builder.append(";");
            builder.append(g.getName());
            builder.append(";");
            builder.append(g.getUpdatedTime());
            builder.append(System.getProperty("line.separator"));
        });
        builder.setLength(builder.length() - 1);
        String result = builder.toString();
        return result;
    }

    public String createPageCSV(List<Page> facebookGroups) {
        StringBuilder builder = new StringBuilder("id;name;created_time");
        builder.append(System.getProperty("line.separator"));
        facebookGroups.forEach(p -> {
            builder.append(p.getId());
            builder.append(";");
            builder.append(p.getName());
            builder.append(";");
            builder.append(p.getCreatedTime());
            builder.append(System.getProperty("line.separator"));
        });
        builder.setLength(builder.length() - 1);
        String result = builder.toString();
        return result;
    }
}
