package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.Util.AesUtil;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.PrivateKey;
import com.example.socialcloud.Model.PublicKey;
import com.example.socialcloud.Model.ServiceName;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.ClientKeys;
import com.example.socialcloud.Operation.CreateSocialUser2;
import com.example.socialcloud.Operation.LoginGet;
import com.example.socialcloud.Operation.PFSInsertUser;
import com.example.socialcloud.Operation.SavePKClient;
import com.example.socialcloud.Operation.UserGenerator;
import com.example.socialcloud.Operation.GetPK;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Asynchronous Task to make a registration in one go, by creating the user on all platforms (RMS and KMS)
 */
public class UnifiedRegistration_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public UnifiedRegistration_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "user" -> object: an User object containing all data of the new user, except for the ID
     *                id: "passphrase" -> object: an String containing the input passphrase
     * @return        Hashmap<String, Object> result of query.
     *                Params contained:
     *                id: "photo" -> object: byte array representing the photo
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        User new_user = (User)hashmap[0].get("user");
        String passphrase = (String)hashmap[0].get("passphrase");

        JSONObject json = null;

        //Save the user into the OSN for the first time, i receive an Autoincrement identifier that will be the user's ID
        UserGenerator UG = new UserGenerator(new_user);
        String result = new HttpPostHandler().makeServiceCall(UG.getURL(), UG.getJSON());
        if(result==null){
            return null;
        }
        try {
            json = new JSONObject(result);
            new_user.setId_user((Integer) json.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //I perform the login to get the session going serverside
        LoginGet LG = new LoginGet();
        result = new HttpPostHandler().makeServiceCall(LG.getURL(), new_user.getEmail());

        //Getting the public key of KMS
        GetPK GPKkms = new GetPK();
        String service = null;
        String modulus = null;
        String exponent = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKkms.getURL(), ServiceName.KMS.toString()));
            service = (String) json.get("service");
            modulus = (String) json.get("modulus");
            exponent = (String) json.get("exponent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PublicKey KMS_pk = new PublicKey(service, modulus, exponent);

        //Getting the public key of RMS
        GetPK GPKrms = new GetPK();
        service = null;
        modulus = null;
        exponent = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKrms.getURL(), ServiceName.RMS.toString()));
            service = (String) json.get("service");
            modulus = (String) json.get("modulus");
            exponent = (String) json.get("exponent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PublicKey RMS_pk = new PublicKey(service, modulus, exponent);

        //Generating the new user for KMS, by sending a JSON like this one
        //var message={"iduser":idUser,"iv":iv,"salt":salt,"keySize":keySize,"iterationCount":iterationCount,"passPhrase":passphrase};
        String IV = AesUtil.random();
        String salt = AesUtil.random();
        ClientKeys CK = new ClientKeys(new_user, KMS_pk.getModulus(), KMS_pk.getExponent(), passphrase, IV, salt);
        String response = null;
        JSONObject CKjson = CK.getJSON();
        response = new HttpPostHandler().makeServiceCall(CK.getURL(), CKjson);

        //the response i get is encrypted via AES and the salt + IV and passphrase i used
        AesUtil AES = new AesUtil();
        String decrypted = AES.decrypt(salt, IV, passphrase, response);
        String client_public_exponent = null;
        String client_modulus = null;
        String client_private_exponent = null;

        //Here i get the JSON result of the previous decryption that contains my public and private key pairs
        PrivateKey PrK = null;
        try {
            json = new JSONObject(decrypted); //Decrypted = ""
            client_public_exponent = (String)json.get("client_public_exponent");
            client_modulus = (String)json.get("client_modulus");
            client_private_exponent = (String)json.get("client_private_exponent");

            PrK = new PrivateKey(client_modulus, client_public_exponent, client_private_exponent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //i need to encrypt my private key using the public key, so i can send it to RMS
        String cpe_encrypted = AES.encrypt(salt, IV, passphrase, PrK.getPrivateExponent());

        //i generate the message to save the pair key of the user to OSN for future retrieving
        SavePKClient SPKC = new SavePKClient(new_user.getId_user(), PrK.getPublicExponent(), PrK.getModulus(), cpe_encrypted, salt, IV);
        new HttpPostHandler().makeServiceCall(SPKC.getURL(), SPKC.getJSON());

        AesUtil AES2 = new AesUtil();
        String IV2 = AesUtil.random();
        String salt2 = AesUtil.random();
        String OTPassphrase = AesUtil.random();
        //here instead i save the client key associated to the ID of the user to RMS via JSON that needs to be encrypted
        //var clientPubKeyToRMS={"client_pub_exp": client_exponent, "client_mod": client_modulus, "idu": idUser};
        JSONObject clientPubKeyforRMSJSON = new JSONObject();
        try {
            clientPubKeyforRMSJSON.put("client_pub_exp", client_public_exponent);
            clientPubKeyforRMSJSON.put("client_mod", client_modulus);
            clientPubKeyforRMSJSON.put("idu", new_user.getId_user());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //encryption done via AES
        String encrypted_CPKforRMS = AES2.encrypt(salt2, IV2, OTPassphrase, clientPubKeyforRMSJSON.toString());

        //generation of the symmetric key that is going to be used for the decryption and JSON of the data
        JSONObject symmetricKey = new JSONObject();
        //var symmKey={
        //"iv": iv2,
        //"salt": salt2,
        //"keySize": keySize,
        //"iterationCount": iterationCount,
        //"passPhrase": x,
        //}
        try {
            symmetricKey.put("iv", IV2);
            symmetricKey.put("salt", salt2);
            symmetricKey.put("keySize", AesUtil.DEFAULT_KEYSIZE);
            symmetricKey.put("iterationCount", AesUtil.DEFAULT_ITERATIONCOUNT);
            symmetricKey.put("passPhrase", OTPassphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encryption via RSA using RMS public key
        String encrypted_symmKey = RSA_Util.encryptPublic(RMS_pk.getModulus(), RMS_pk.getExponent(), symmetricKey.toString());

        //creation of the user on RMS
        CreateSocialUser2 CSU2 = new CreateSocialUser2(encrypted_CPKforRMS, encrypted_symmKey.toString());
        new HttpPostHandler().makeServiceCall(CSU2.getURL(), CSU2.getJSON());

        //insertion of the ID of the user on PFS
        PFSInsertUser PFSIU = new PFSInsertUser();
        new HttpPostHandler().makeServiceCall(PFSIU.getURL(), new_user.getId_user().toString());

        data.put("user", new_user);
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
