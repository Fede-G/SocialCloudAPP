package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.GetSelfProfile;
import com.example.socialcloud.Util.ImageConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to get the profile of the user, just by passing the ID as input
 */
public class GetSelfProfile_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public GetSelfProfile_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler manage the friendship request (both accept and refuse)
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "user_id" -> object: an Integer containing the ID of the user to search the profile of.
     * @return        Secret return:
     *                  SessionUser ID and User are static variables that are being set if it's the first time i request something!
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        GetSelfProfile GSP = new GetSelfProfile();

        JSONObject json;
        User current_user = new User();
        String session = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GSP.getURL(), hashmap[0].get("user_id").toString()));

            /*
                {
                    "email": String,
                    "birth_day": String (format dd/MM/yy),
                    "city": String,
                    "pw": encrypted BCrypt password,
                    "photo": byte[],
                    "id": Integer,
                    "firstName": String,
                    "lastName": String
                }
             */
            current_user.setId_user((Integer) json.get("id"));
            current_user.setBirth_day((String) json.get("birth_day"));
            current_user.setCity((String) json.get("city"));
            current_user.setEmail((String) json.get("email"));
            current_user.setFirstname((String) json.get("firstname"));
            current_user.setLastname((String) json.get("lastname"));
            if(json.has("photo")){
                JSONArray photoarr = (JSONArray)json.get("photo");

                current_user.setPhoto(ImageConverter.convertJSONArray(photoarr));
            }
            current_user.setPw((String) json.get("pw"));

            session = (String)json.get("session");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(SessionUser.hasSession()){
            return data;
        }
        SessionUser.setSessionId(session);
        SessionUser.setUser(current_user);
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
