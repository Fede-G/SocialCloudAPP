package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.CustomException.UnmatchingPasswords;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.Login;
import com.example.socialcloud.Operation.LoginGet;
import com.example.socialcloud.Util.BCryptMS;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to Login into the social service, it does create the session server-side too
 */
public class Login_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public Login_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler manage the friendship request (both accept and refuse)
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "email" -> object: a String representing the email of the login activity
     *                id: "password" -> object: a String representing the password (plaintext) of the login activity
     * @return        User object that has that specific email and password
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        String email = (String)hashmap[0].get("email");
        String password = (String)hashmap[0].get("password");

        User current_user = new User("","",email,"","",password);

        JSONObject json;

        //Returns an user
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
        Login L = new Login(current_user);
        try {
            String i = L.getJSON().toString();
            String response = new HttpPostHandler().makeServiceCall(L.getURL(), L.getJSON());
            json = new JSONObject(response);
            current_user.setId_user((Integer) json.get("id"));
            current_user.setEmail((String) json.get("email"));
            current_user.setFirstname((String) json.get("firstname"));
            current_user.setLastname((String) json.get("lastname"));
            String fetched_password = (String)json.get("pw");
            if(BCryptMS.checkpw(current_user.getPw(), fetched_password)){
                LoginGet LG = new LoginGet();
                new HttpPostHandler().makeServiceCall(LG.getURL(), current_user.getEmail());
            }else{
                throw new UnmatchingPasswords();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnmatchingPasswords unmatchingPasswords) {
            unmatchingPasswords.printStackTrace();
        }

        data.put("user", current_user);
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
