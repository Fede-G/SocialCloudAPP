package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Operation.GetFriendRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Asynchronous Task read the request of friendship to the session user.
 */
public class GetFriendRequest_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

    /**
     * Interface to delegate the process to the activity caller
     */
    public interface AsyncResponse {
        void processFinish(HashMap<String, Object> output);
    }

    public AsyncResponse delegate = null;

    /**
     * Constructor
     * @param delegate activity that i need to delegate to
     */
    public GetFriendRequest_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler manage the friendship request (both accept and refuse)
     * @param hashmap empty
     * @return        Hashmap<String, Object> of the users that want to be your friend
     *                Params contained:
     *                id: "user" -> object: JSONArray that is composed as follows:
     *                  [
     *                      {
     *                          "email": String,
     *                          "birth_day": String (format dd/MM/yy),
     *                          "city": String,
     *                          "pw": encrypted BCrypt password,
     *                          "photo": byte[],
     *                          "id": Integer,
     *                          "firstName": String,
     *                          "lastName": String
     *                      }
     *                  ]
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        GetFriendRequest GFR = new GetFriendRequest();

        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(new HttpPostHandler().makeServiceCall(GFR.getURL(), SessionUser.getUser().getId_user().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.put("users", jsonarray);

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
