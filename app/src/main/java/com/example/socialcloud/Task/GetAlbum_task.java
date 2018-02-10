package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.GetAlbum;
import com.example.socialcloud.Operation.GetSelfProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to get the album of the current user, but not made to show the images
 */
//NOTE: This task can't be tested because of errors on the server side (RMS/KMS/PFS) caused by the upgrade of Dropbox API
//UPDATE: 02/02/2018 API fixed on KMS, RMS and PFS but too late to test it out. Might expect some errors.
public class GetAlbum_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

    /**
     * Interface to delegate the process to the activity caller
     */
    public interface AsyncResponse {
        void processFinish(HashMap<String, Object> output);
    }

    public AsyncResponse delegate = null;

    /**
     * Constructor
     * @param delegate activity to be delegated
     */
    public GetAlbum_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to get the album from the server of the user
     * @param hashmap   empty
     * @return          HashMap<String, Object>
     *                  Params contained:
     *                  id: "album" -> object: JSONArray that has the sequent format for each of his sub JSONObjects
     *                  [
     *                      {
     *                          "metaTag": String,
     *                          "user":{
     *                              "email": ...
     *                               etc...
     *                          }
     *                          "fileName": String (with .format),
     *                          "id": Integer
     *                      }
     *                  ]
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        GetAlbum GA = new GetAlbum();

        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(new HttpPostHandler().makeServiceCall(GA.getURL(), SessionUser.getUser().getId_user().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.put("album", jsonarray);

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
