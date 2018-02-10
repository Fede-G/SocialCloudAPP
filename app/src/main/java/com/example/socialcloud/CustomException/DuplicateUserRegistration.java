package com.example.socialcloud.CustomException;

public class DuplicateUserRegistration extends Exception {
    public DuplicateUserRegistration(){
        super("User with the same Email already exists.");
    }
}
