package com.example.socialcloud.Task;

import android.os.AsyncTask;
import android.util.Base64;

import com.example.socialcloud.Util.AesUtil;
import com.example.socialcloud.CustomException.EmptySessionException;
import com.example.socialcloud.HttpThread.HttpHandler;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.PrivateKey;
import com.example.socialcloud.Model.PublicKey;
import com.example.socialcloud.Model.ServiceName;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.CheckSession;
import com.example.socialcloud.Operation.GetDownload;
import com.example.socialcloud.Operation.GetPK;
import com.example.socialcloud.Operation.GetPKClient;
import com.example.socialcloud.Operation.GetUserViewPhoto;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  Asynchronous Task to pick the resource and decrypt it from the system, but you have to know who owns it, it's name and the passphrase used to encode
 */
//NOTE: This task can't be tested because of errors on the server side (RMS/KMS/PFS) caused by the upgrade of Dropbox API
//UPDATE: 02/02/2018 API fixed on KMS, RMS and PFS, might expect some errors, tested lightly.
public class ShowAlbum_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public ShowAlbum_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "id_user_album" -> object: an Integer describing the user ID that i want to get the Album of
     *                id: "passphrase" -> object: an String containing the input passphrase
     *                id: "filename" -> object: an String that is the name of the file
     * @return        Hashmap<String, Object> result of query.
     *                Params contained:
     *                id: "photo" -> object: byte array representing the photo
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        User user = SessionUser.getUser();
        Integer id_user_album = (Integer)hashmap[0].get("id_user_album");
        String passphrase  = (String)hashmap[0].get("passphrase");
        String file_name = (String)hashmap[0].get("filename");

        //Check the session
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

        //Get the Photo from the user with that name, by sending a request in JSON format like this:
        //var album={"tag":tag,"id":Lockr.get("id"),"fileName":Lockr.get("fileName")};
        GetUserViewPhoto SA = new GetUserViewPhoto(id_user_album, file_name);
        Integer idPhoto = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(SA.getURL(), SA.getJSON()));
            //Response is a JSON with
            // {
            //      idPhoto: ID
            // }
            idPhoto = (Integer)(json.get("idPhoto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get RMS Keys
        GetPK GPKrms = new GetPK();
        String service = null;
        String modulus = null;
        String exponent = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKrms.getURL(), ServiceName.RMS.toString()));
            service = (String) json.get("service");
            modulus = (String) json.get("modulus");
            exponent = (String) json.get("exponent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PublicKey RMS_pk = new PublicKey(service, modulus, exponent);

        //Make a request to RMS by sending what resource you want to get, who wants it and generating a random number for the request, JSON format:
        //var msgRMS={"idRequestor":Lockr.get('id'),'idResource':idPhoto,'N1':n1};
        JSONObject jsonmsgRMS = new JSONObject();
        try {
            jsonmsgRMS.put("idRequestor", user.getId_user());
            jsonmsgRMS.put("idResource", idPhoto);
            jsonmsgRMS.put("N1", Math.floor(Math.random()*100+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encrypt in RSA with RMS keys
        String encryptedMsgRMS = RSA_Util.encryptPublic(RMS_pk.getModulus(), RMS_pk.getExponent(),jsonmsgRMS.toString());

        //Start the download of the resource: you obtain AES parameters for decryption and the message encrypted
        GetDownload GD = new GetDownload();
        String AESParams = null;
        String encrypted_msg_client = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GD.getURL(), encryptedMsgRMS));
            // {
            //      something: xxxx,
            //      data:
            //      {
            //          AESParams: parameters,
            //          encrypted_msg_client: msg
            //      }
            // }
            AESParams = (String)(json.get("AESParams"));
            encrypted_msg_client = (String)(json.get("encrypted_msg_client"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Generate the IV, Salt and variables to store the result of the service call to getting back the private key exponent
        GetPKClient GPKC = new GetPKClient();
        String clientSalt = null;
        String clientIV = null;
        String clientEncryptedPK = null;
        PrivateKey clientPK = new PrivateKey();
        String modulus_public = null;
        String exponent_public = null;
        String private_key = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKC.getURL(), id_user_album.toString()));
            //Response is {"salt":salt, "iv":iv, "private_key":private_key}
            clientSalt = (String)json.get("salt");
            clientIV = (String)json.get("iv");
            clientEncryptedPK  = (String)json.get("private_key");
            AesUtil AES_client = new AesUtil();
            private_key = AES_client.decrypt(clientSalt, clientIV, passphrase, clientEncryptedPK);
            clientPK.setModulus((String)json.get("modulus_public"));
            clientPK.setPublicExponent((String)json.get("exponent_public"));
            //Private or Public exponent? Not clear on the code! Needs to be checked.
            //Should be exponent public.
            clientPK.setPrivateExponent(private_key);

            //Javascript reference code
            //var keyPrivate=aesUtil.decrypt(data.salt,data.iv,Lockr.get('passPhrase'),data.private_key);
            //rsa.setPrivate(data.modulus_public,data.exponent_public, keyPrivate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Here AES parameters are getting decrypted using RSA and our modulus and private exponent
        String AESsalt = null;
        String AESIV = null;
        String AESpassphrase = null;
        try {
            json = new JSONObject(RSA_Util.decrypt(clientPK.getModulus(), clientPK.getPrivateExponent(), AESParams));
            AESsalt = (String) json.get("salt");
            AESIV = (String) json.get("iv");
            AESpassphrase = (String) json.get("passphrase");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Here i decrypt the message to the client using the new parameters of AES that i got just before
        AesUtil AES = new AesUtil();
        String secret_owner = null;
        String msgFromKMS = null;
        try {
            json = new JSONObject(AES.decrypt(AESsalt, AESIV, AESpassphrase, encrypted_msg_client));
            //Reference javascript for the JSON result:
            //var secretOwner=decryptedMessageFromRMS.secret_owner;
            //var msgFromKMS=decryptedMessageFromRMS.msgFromKMS;
            secret_owner = (String)json.get("secret_owner");
            msgFromKMS = (String)json.get("msgFromKMS");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //In the end the message from KMS that we just decrypted gets us the token and the url of the encrypted resource
        String token = null;
        String url_encryptedrsc = null;
        String secret = null;
        try {
            //json = new JSONObject(RSA_Util.decrypt(clientPK.getModulus(), clientPK.getPrivateExponent(), msgFromKMS));
            json = new JSONObject(msgFromKMS);
            token = (String)json.get("token");
            url_encryptedrsc = (String)json.get("url_encryptedrsc");
            secret = secret_owner.concat(token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Here we ask for the photo at the URL
        String encryptedPhoto = new HttpHandler().makeServiceCall(url_encryptedrsc);
        AesUtil AESPhoto = new AesUtil();

        //The photo is encrypted, i decrypt it using the secret_owner (salt), token (IV) and secret (passphrase)
        byte[] photo = Base64.decode(AESPhoto.decrypt(secret_owner, token, secret, encryptedPhoto).getBytes(), Base64.DEFAULT | Base64.NO_WRAP);

        data.put("photo", photo);

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
