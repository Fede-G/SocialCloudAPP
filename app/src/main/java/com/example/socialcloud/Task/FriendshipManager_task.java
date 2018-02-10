package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.CustomException.EmptySessionException;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.PublicKey;
import com.example.socialcloud.Model.ServiceName;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.CheckSession;
import com.example.socialcloud.Operation.DeletePendingFriendship;
import com.example.socialcloud.Operation.FriendshipCreation;
import com.example.socialcloud.Operation.GetFriendshipRequestor;
import com.example.socialcloud.Operation.GetPK;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to manage any type or friendship request, that can be accepted or refused.
 * In both cases you need to do the same operations, except that after you need to save the friendship if you accept it.
 */
public class FriendshipManager_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public FriendshipManager_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler manage the friendship request (both accept and refuse)
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "accept" -> object: a Boolean that tells if the request is accepted or refused-
     *                id: "id_requestor" -> object: an Integer containing the ID of the user that i have to link the friendship to.
     * @return        empty
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        Boolean isAccepted = (Boolean)hashmap[0].get("accept");

        Integer id_requestor = (Integer)hashmap[0].get("id_requestor");

        //Here I pick the user from the current session
        User current_user = SessionUser.getUser();

        //On both cases the session has to be checked and the friendship request needs to be deleted from pending
        //Checking session
        JSONObject json;
        CheckSession CS = new CheckSession();
        String session = "";
        //The response from the POST request is a JSON containing a single identifier called "session" that echos out the ID of my current session
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(CS.getURL(), current_user.getId_user().toString()));
            session = (String)(json.get("session"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //if the session is empty than i don't have one, i call a new exception
        if(session.length()==0){
            try {
                throw new EmptySessionException();
            } catch (EmptySessionException e) {
                e.printStackTrace();
            }
            return null;
        }

        //Deleting pending request
        DeletePendingFriendship DPF = new DeletePendingFriendship(id_requestor, current_user.getId_user());
        new HttpPostHandler().makeServiceCall(DPF.getURL(), DPF.getJSON());
        //Response is void, cannot be handled

        //Here needs to be checked if the request is accepted. If not, then i don't do anything else and skit to return
        if(isAccepted){
            //If the request is accepted then I trigger the procedure for a new friendship:
            //I ask the who is the friend i want to add and store all data in a new local user
            GetFriendshipRequestor GFR = new GetFriendshipRequestor();
            User requestor = new User();
            try {
                json = new JSONObject(new HttpPostHandler().makeServiceCall(GFR.getURL(), id_requestor.toString()));
                requestor.setId_user((Integer) json.get("id"));
                requestor.setEmail((String) json.get("email"));
                requestor.setFirstname((String) json.get("firstname"));
                requestor.setLastname((String) json.get("lastname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Getting the PFS keys and saving them
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

            //Here I create the message that needs to be sent to PFS, it's a JSON that follow the sequent format:
            //var messagetoPFS={"idRequestor":id,"idOwner":id_acceptor,"emailRequestor":emailRequestor,"nameRequestor":nameRequestor,
            // "surnameRequestor":surnameRequestor,"nameSearched":self.user.firstname,"surnameSearched":self.user.lastname};
            JSONObject msgToPFS = new JSONObject();
            try {
                msgToPFS.put("idRequestor", requestor.getId_user());
                msgToPFS.put("idOwner", current_user.getId_user());
                msgToPFS.put("emailRequestor", requestor.getEmail());
                msgToPFS.put("nameRequestor", requestor.getFirstname());
                msgToPFS.put("surnameRequestor", requestor.getLastname());
                msgToPFS.put("nameSearched", current_user.getFirstname());
                msgToPFS.put("surnameSearched", current_user.getLastname());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //RSA with PFS public key the JSON and obtain an encrypted message
            String encrypetedMsgToPFS = RSA_Util.encryptPublic(PFS_pk.getModulus(), PFS_pk.getExponent(), msgToPFS.toString());

            //Ask to create a new friendship between me and the user that made the request
            FriendshipCreation FC = new FriendshipCreation();
            new HttpPostHandler().makeServiceCall(FC.getURL(), encrypetedMsgToPFS);
        }

        //Data is empty
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
