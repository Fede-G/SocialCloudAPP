package com.example.socialcloud.Model;

//Enumeration for the various type of Service that exist
public enum ServiceName {
    //List of types and a description of them
    OSN("Online Service Network"), RMS("Rule Manager System"), PFS("Path Finder Service"), KMS("Key Manager System");

    private final String fieldDescription;


    ServiceName(String value) {
        fieldDescription = value;
    }

    /**
     * Getter for the decription of the field
     * @return Description of the field
     */
    public String getFieldDescription() {
        return fieldDescription;
    }
}
