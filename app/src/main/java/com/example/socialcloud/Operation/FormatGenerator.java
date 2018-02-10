package com.example.socialcloud.Operation;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

//Abstract class that creates a die to be used in all other operations
public abstract class FormatGenerator {

    protected static URL url;
    private JSONObject JSON;

    /**
     * Constructor
     */
    public FormatGenerator(){
        JSON = new JSONObject();
    }

    /**
     * Method to create a static URL from a string
     * @param url string of the complete URL
     * @return URL object of that string
     */
    protected static URL makeStaticURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Getter of URL
     * @return URL
     */
    public URL getURL(){
        return this.url;
    }

    /**
     * Method to add to the JSON(Object) any Object with relative identifier (or key)
     * @param identifier String of the key/identifier
     * @param JSONData   Object that can be saved into a JSONObject
     */
    public void addObjectToJSON(String identifier, Object JSONData){
        try {
            JSON.put(identifier, JSONData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter of the JSON
     * @return JSONObject
     */
    public JSONObject getJSON(){
        return JSON;
    }
}
