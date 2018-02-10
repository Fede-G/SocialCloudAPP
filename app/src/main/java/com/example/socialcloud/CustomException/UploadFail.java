package com.example.socialcloud.CustomException;

public class UploadFail extends Exception {
    public UploadFail(){
        super("Failed to upload the resource.");
    }
}
