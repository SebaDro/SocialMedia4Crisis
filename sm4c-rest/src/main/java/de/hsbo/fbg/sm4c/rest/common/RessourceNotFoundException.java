/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.rest.common;

/**
 *
 * @author Sebastian Drost
 */
public class RessourceNotFoundException extends Exception {

    public RessourceNotFoundException(String message) {
        super(message);
    }

    public RessourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
