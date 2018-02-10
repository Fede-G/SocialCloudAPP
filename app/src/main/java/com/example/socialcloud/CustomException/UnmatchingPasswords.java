package com.example.socialcloud.CustomException;

/**
 * Exception thrown when the passwords aren't matching
 */
public class UnmatchingPasswords extends Exception {
    /**
     * Constructor that calls super
     */
    public UnmatchingPasswords(){
        super("Passwords aren't matching.");
    }
}
