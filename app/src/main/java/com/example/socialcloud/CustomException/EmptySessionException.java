package com.example.socialcloud.CustomException;

/**
 * Exception thrown when the session data is empty
 */
public class EmptySessionException extends Exception {

    /**
     * Constructor that calls super
     */
    public EmptySessionException(){
        super("Session is empty, exit from app!");
    }
}
