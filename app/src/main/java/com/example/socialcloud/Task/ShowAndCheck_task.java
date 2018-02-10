package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.CustomException.EmptySessionException;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.PublicKey;
import com.example.socialcloud.Model.ServiceName;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.CheckSession;
import com.example.socialcloud.Operation.EvalutatorFriendship;
import com.example.socialcloud.Operation.GetFriendshipRequestor;
import com.example.socialcloud.Operation.GetPK;
import com.example.socialcloud.Operation.PendingRequest;
import com.example.socialcloud.Util.ImageConverter;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  Asynchronous Task to check if there is any kind of relationship between the user and the searched user, plus i can get the album from here
 */
//NOTE: This task can't be tested because of errors on the server side (RMS/KMS/PFS) caused by the upgrade of Dropbox API
//UPDATE: 02/02/2018 API fixed on KMS, RMS and PFS, might expect some errors, tested lightly.
public class ShowAndCheck_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public ShowAndCheck_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "id_searched_user" -> object: an Integer of the searched user's ID
     * @return        Hashmap<String, Object> result of query.
     *                Params contained:
     *                id: "deactivate" -> object: boolean that tells me if there is a request pending or they are already friends
     *                id: "user_data" -> object: User object describing the User searched
     *                id: "album" -> object: JSONArray containing the album of searched user, but only if they are friends
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        User user = SessionUser.getUser();
        Integer id_searched_user = (Integer)hashmap[0].get("id_searched_user");

        //Session check
        JSONObject json = null;
        CheckSession CS = new CheckSession();
        String session = "";
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(CS.getURL(), user.getId_user().toString()));
            session = (String)(json.get("session"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(session.length()==0){
            new EmptySessionException();
            return null;
        }

        //Get the user you are trying to get the request of
        GetFriendshipRequestor GFR = new GetFriendshipRequestor();
        User searched_user = new User();
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GFR.getURL(), id_searched_user.toString()));
            searched_user.setId_user((Integer) json.get("id"));
            searched_user.setBirth_day((String) json.get("birth_day"));
            searched_user.setCity((String) json.get("city"));
            searched_user.setEmail((String) json.get("email"));
            searched_user.setFirstname((String) json.get("firstname"));
            searched_user.setLastname((String) json.get("lastname"));
            if(json.has("photo")){
                JSONArray photoarr = (JSONArray)json.get("photo");

                searched_user.setPhoto(ImageConverter.convertJSONArray(photoarr));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Check if there is already an existing request between logged user and the requested one
        PendingRequest PR = new PendingRequest(SessionUser.getUser().getId_user(), id_searched_user);
        Boolean isPending = true;
        String result = new HttpPostHandler().makeServiceCall(PR.getURL(), PR.getJSON());
        //if there is, then don't do anything else
        isPending = (!result.equals("0"));
        data.put("user_data", searched_user);

        //If there is no request pending, then they are strangers or friends
        if(!isPending){
            //Get the public keys of PFS
            GetPK GPKpfs = new GetPK();
            String service = null;
            String modulus = null;
            String exponent = null;
            try {
                json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKpfs.getURL(), ServiceName.PFS.toString()));
                service = (String) json.get("service");
                modulus = (String) json.get("modulus");
                exponent = (String) json.get("exponent");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PublicKey PFS_pk = new PublicKey(service, modulus, exponent);

            //Send a JSON request that follows this rule:
            //var messageToPFS={"sessionUser":Lockr.get("sessionUser"),"searchedUser":Lockr.get("searchedUser")
            JSONObject jsonmsgToPFS = new JSONObject();
            try {
                jsonmsgToPFS.put("sessionUser", SessionUser.getUser().getId_user());
                jsonmsgToPFS.put("searchedUser", id_searched_user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Encrypt the message with PFS keys
            String encryptedFriendship = RSA_Util.encryptPublic(PFS_pk.getModulus(), PFS_pk.getExponent(), jsonmsgToPFS.toString());

            //Evalutate the friendship with the JSON generated
            EvalutatorFriendship EF = new EvalutatorFriendship();
            Boolean isFriend = false;
            String friendstring = new HttpPostHandler().makeServiceCall(EF.getURL(), encryptedFriendship);
            isFriend = (!friendstring.equals("0"));

            //if they are friends than deactivate procedures, else they can be asked. If they are friends you can get his album too.
            if(isFriend){
                data.put("deactivate", true);
                //Here i COULD put the album, but i have instruction to modify less code possible
            }else{
                data.put("deactivate", false);
            }
        }else{
            data.put("deactivate", true);
        }

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
