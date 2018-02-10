package com.example.socialcloud.HttpThread;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

//Class that has the role of serving any POST request to all other servers, plus it needs to get the response from them
public class HttpPostHandler {

    //Reference to the class itself
    private static final String TAG = HttpPostHandler.class.getSimpleName();

    /**
     * Void constructor
     */
    public HttpPostHandler() {
    }

    /**
     * Caller of the POST service via HTTP 1.1
     * NOTE: This should be the only method, but we need a "singledata" method too
     * @param url   URL that i send the request to
     * @param json  param data that i pass to the URL
     * @return      the response from the URL
     */
    public String makeServiceCall(URL url, JSONObject json) {
        String json_response = "{\"error\":\"error\"}";

        try {
            //Create the connection and set the input and output + request property and method
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput (true);
            conn.setDoOutput (true);
            conn.setUseCaches (false);
            conn.setRequestProperty("Content-Type","application/json");
            conn.connect();
            conn.setRequestMethod("POST");

            //Start of the POST input
            DataOutputStream printout;
            printout = new DataOutputStream(conn.getOutputStream ());
            printout.writeBytes(json.toString());
            printout.flush();
            printout.close();

            //Read the response code
            json_response="";
            //Read POST output
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                json_response += text;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return json_response;
    }

    /**
     * Caller of the POST service via HTTP 1.1
     * @param url           URL that i send the request to
     * @param singledata    single data that i pass to the URL
     * @return              the response from the URL
     */
    public String makeServiceCall(URL url, String singledata) {
        String json_response = "{\"error\":\"error\"}";

        try {
            //Create the connection and set the input and output + request property and method
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput (true);
            conn.setDoOutput (true);
            conn.setUseCaches (false);
            conn.setRequestProperty("Content-Type","application/json");
            conn.connect();
            conn.setRequestMethod("POST");

            //Start of the POST input
            DataOutputStream printout;
            printout = new DataOutputStream(conn.getOutputStream());
            printout.writeBytes(singledata);
            printout.flush();
            printout.close();

            //Read the response code
            json_response = "";
            //Read POST output
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                json_response += text;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return json_response;
    }
}
