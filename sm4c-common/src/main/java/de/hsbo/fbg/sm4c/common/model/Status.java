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
public enum Status {
    ACTIVE("aktiv"),
    STOPPED("gestoppt");

    private final String name;

    private Status(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
