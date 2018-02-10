package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Operation.Logout;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task for the logout operation: it deletes the current session from the server before the app closes
 */
public class Logout_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

    /**
     * Interface to delegate the process to the activity caller
     */
    public interface AsyncResponse {
        /**
         * interface method to be called at the end of the task for the delegation
         * @param output data of output
         */
        void processFinish(HashMap<String, Object> output);
    }

    public AsyncResponse delegate = null;

    /**
     * Constructor
     * @param delegate activity to be delegated
     */
    public Logout_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to delete the session from the server
     * @param hashmap not currently used
     * @return not currently used
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        JSONObject json = null;

        Logout L = new Logout();
        new HttpPostHandler().makeServiceCall(L.getURL(), SessionUser.getUser().getId_user().toString());
        return data;
    }

    /**
     * Operation called at the end of the task
     * Delegates all the operations to the caller activity
     * @param result not currently used
     */
    @Override
    protected void onPostExecute(HashMap<String, Object> result) {
        delegate.processFinish(result);
    }
}
