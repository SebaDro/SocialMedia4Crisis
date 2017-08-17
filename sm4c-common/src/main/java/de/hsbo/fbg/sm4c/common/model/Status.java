/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.common.model;

/**
 *
 * @author Sebastian Drost
 */
public enum Services {
    TWITTER("Twitter"),
    FACEBOOK("Facebook");

    private final String name;

    private Services(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
