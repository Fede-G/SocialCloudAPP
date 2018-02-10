package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.CustomException.EmptySessionException;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.AskFriendshipCreation;
import com.example.socialcloud.Operation.CheckSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to request a friendship creation that is pending in the server
 */
public class RequestFriendshipCreation_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public RequestFriendshipCreation_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "id_requested" -> object: an Integer containing the ID of the user to send the request to.
     * @return        empty
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        User current_user = SessionUser.getUser();

        Integer id_requested = (Integer)hashmap[0].get("id_requested");

        //Checks the session
        JSONObject json = null;
        CheckSession CS = new CheckSession();
        String session = "";
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(CS.getURL(), current_user.getId_user().toString()));
            session = (String)(json.get("session"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(session.length()==0){
            new EmptySessionException();
            return null;
        }

        //Create the friendship
        AskFriendshipCreation AFC = new AskFriendshipCreation(current_user.getId_user(), id_requested);
        new HttpPostHandler().makeServiceCall(AFC.getURL(), AFC.getJSON());

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
