package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Operation.SearchFriend;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Asynchronous Task to search an user by ID
 */
public class SearchUser_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public SearchUser_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "friend_data" -> object: a String that can contain part of the name/surname of the other user (SQL LIKE x%)
     * @return        Hashmap<String, Object> result of query.
     *                Params contained:
     *                id: "friends_array" -> object: JSONArray of friends:
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

        SearchFriend SF = new SearchFriend();

        String friend_data = (String)hashmap[0].get("friend_data");

        JSONArray jsonarray = null;
        try {
            String result = new HttpPostHandler().makeServiceCall(SF.getURL(), friend_data);
            jsonarray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.put("friends_array", jsonarray);
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
