package com.example.socialcloud.Task;

import android.os.AsyncTask;

import com.example.socialcloud.CustomException.UploadFail;
import com.example.socialcloud.Util.AesUtil;
import com.example.socialcloud.CustomException.EmptySessionException;
import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.PrivateKey;
import com.example.socialcloud.Model.PublicKey;
import com.example.socialcloud.Model.ServiceName;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.CheckSession;
import com.example.socialcloud.Operation.GetPK;
import com.example.socialcloud.Operation.GetPKClient;
import com.example.socialcloud.Operation.SaveAlbum;
import com.example.socialcloud.Operation.UploadReq1;
import com.example.socialcloud.Operation.UploadReq2;
import com.example.socialcloud.Util.ImageConverter;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  Asynchronous Task to upload the photo
 */
//NOTE: This task can't be tested because of errors on the server side (RMS/KMS/PFS) caused by the upgrade of Dropbox API
//UPDATE: 02/02/2018 API fixed on KMS, RMS and PFS, might expect some errors, tested lightly.
public class UploadPhoto_task extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

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
    public UploadPhoto_task(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /**
     * Asynchronous operation that calls the handler to create a pending friendship
     * @param hashmap Hashmap<String, Object> where you can get the params from. It's always one, so it can be found at index 0.
     *                Params contained:
     *                id: "rule" -> object: an Integer describing how far i want this resource be seen
     *                id: "passphrase" -> object: an String containing the input passphrase
     *                id: "image" -> object: a byte array that is the image itself
     *                id: "image_name" -> object: a String that is the name of the file, with format
     *                id: "tag" -> object: a String of the tag associated to the file
     * @return        Hashmap<String, Object> result of query.
     *                Params contained:
     *                id: "photo" -> object: byte array representing the photo
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hashmap) {

        HashMap<String, Object> data = new HashMap<>();

        User user = SessionUser.getUser();
        String passphrase = (String)hashmap[0].get("passphrase");
        Integer rule = (Integer)hashmap[0].get("rule");
        byte[] image = (byte[])hashmap[0].get("image");
        String tag = (String)hashmap[0].get("tag");
        String image_name = (String)hashmap[0].get("image_name");

        //Check for the session
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

        //I post the request to save the file, i get back an ID for future operations
        SaveAlbum SA = new SaveAlbum(tag, user.getId_user(), image_name);
        Integer idResource = -1;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(SA.getURL(), SA.getJSON()));
            if((Boolean)json.get("successful")) {
                idResource = Integer.parseInt((String) json.get("idAlbum"));
            }else{
                new UploadFail();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get the Public keys of KMS
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

        //Get the Public keys of RMS
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

        //Generate a new JSON for the request to chain together the user and the resource, adding a random identifier and a session token
        //var msgKms={"idu":Lockr.get("id"),"idR":Lockr.get("idResource"),"n2":n2,'session_token':self.user.session};
        JSONObject JSONmsgToKMS = new JSONObject();
        try {
            JSONmsgToKMS.put("idu", user.getId_user());
            JSONmsgToKMS.put("idR", idResource);
            JSONmsgToKMS.put("n2", Math.floor(Math.random()*100+1));
            JSONmsgToKMS.put("session_token", SessionUser.getSessionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //encrypting the generated JSON via RSA using KMS keys
        String encryptedMsgToKMS = RSA_Util.encryptPublic(KMS_pk.getModulus(), KMS_pk.getExponent(), JSONmsgToKMS.toString());

        //Encapsulating the message into a message for RMS with the same data
        //var msgRMS={"idu":Lockr.get("id"),"n1":n1,"idR":Lockr.get("idResource"),"msgKMS":msgKMSEncrypted,'session_token':self.user.session};
        JSONObject JSONmsgToRMS = new JSONObject();
        try {
            JSONmsgToRMS.put("idu", user.getId_user());
            JSONmsgToRMS.put("n1", Math.floor(Math.random()*100+1));
            JSONmsgToRMS.put("idR", idResource);
            JSONmsgToRMS.put("msgKMS", encryptedMsgToKMS);
            JSONmsgToRMS.put("session_token", SessionUser.getSessionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //This time the encryption is done via AES by generating salt, IV and using the original passphrase
        String salt = AesUtil.random();
        String IV = AesUtil.random();
        AesUtil AES = new AesUtil();
        String encrypted_JSONmsgToRMS = AES.encrypt(salt, IV, passphrase, JSONmsgToRMS.toString());

        //Generating the params that only RMS will read
        //var paramRMS={"salt":salt,"iv":iv,"passPhrase":passPhrase};
        JSONObject paramsForRMS = new JSONObject();
        try {
            paramsForRMS.put("salt", salt);
            paramsForRMS.put("iv", IV);
            paramsForRMS.put("passPhrase", passphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encrypting the params using RSA (secure) ans RMS keys
        String encryptedParamsRMS = RSA_Util.encryptPublic(RMS_pk.getModulus(), RMS_pk.getExponent(), paramsForRMS.toString());


        //First upload of the request done to RMS
        //JSONmsgToRMS is not encoded
        UploadReq1 UR1 = new UploadReq1(encryptedParamsRMS, encrypted_JSONmsgToRMS);

        String key = null;
        String encrypted_msg_client = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(UR1.getURL(), UR1.getJSON()));
            key = (String)json.get("AESParams");
            encrypted_msg_client = (String)json.get("encrypted_msg_client");
            //Answer will follow (most likely) the sequent format:
            //var key=data.AESParams;
            //var encrypted_msg_client=data.encrypted_msg_client;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Here I get back my private key to decrypt the messages from other sources
        GetPKClient GPKC = new GetPKClient();
        String clientSalt = null;
        String clientIV = null;
        String clientEncryptedPK = null;
        PrivateKey clientPK = new PrivateKey();
        String modulus_public = null;
        String exponent_public = null;
        String private_key = null;
        try {
            json = new JSONObject(new HttpPostHandler().makeServiceCall(GPKC.getURL(), user.getId_user().toString()));
            clientSalt = (String)json.get("salt");
            clientIV = (String)json.get("iv");
            clientSalt = (String)json.get("salt");
            modulus_public = (String)json.get("modulus_public");
            exponent_public = (String)json.get("exponent_public");
            clientEncryptedPK  = (String)json.get("private_key");
            //Toget the private key i have to decrypt using salt and IV received from the request, plus the passphrase that only i know
            AesUtil AES_client = new AesUtil();
            private_key = AES_client.decrypt(clientSalt, clientIV, passphrase, clientEncryptedPK);
            clientPK.setPrivateExponent(private_key);
            clientPK.setModulus(modulus_public);
            clientPK.setPublicExponent(exponent_public);
            //Example of the passage but in javascript
            //var keyPrivate=aesUtil.decrypt(data.salt,data.iv,Lockr.get('passPhrase'),data.private_key);
            //rsa.setPrivate(data.modulus_public,data.exponent_public, keyPrivate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //To obtain now the new key that i got before but that is crypted, i need to use the private exponent that i just obtained
        //key=rsa.decrypt(key);
        JSONObject decryptedKey;
        String newIV = null;
        String newSalt = null;
        String newPassphrase = null;
        /*var iv=key.iv;
        var salt=key.salt;
        var passphrase=key.passphrase;*/
        try {
            //decryption using RSA and Client's private key
            decryptedKey = new JSONObject(RSA_Util.decrypt(clientPK.getModulus(), clientPK.getPrivateExponent(), key));
            newIV = (String)decryptedKey.get("iv");
            newSalt = (String)decryptedKey.get("salt");
            newPassphrase = (String)decryptedKey.get("passphrase");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //With the new obtained AES data i can now decrypt the data from RMS
        String secret_user = null;
        Integer nonce_plus_one = null;
        String kms_msg = null;
        /*var secret_user=decrypted_msg.secretUser;
        var nonce_plus_one=decrypted_msg.nonce_one_plus_one;
        var kms_msg=decrypted_msg.KMSmsg;*/
        AesUtil AES2 = new AesUtil();
        try {
            JSONObject jsonMsgClient = new JSONObject(AES2.decrypt(newSalt, newIV, newPassphrase, encrypted_msg_client));
            secret_user = (String)jsonMsgClient.get("secretUser");
            nonce_plus_one = (Integer)jsonMsgClient.get("nonce_one_plus_one");
            kms_msg = (String)jsonMsgClient.get("KMSmsg");
            kms_msg = kms_msg.replaceAll("(\\r|\\n)", ""); //At the end of KMSmsg there is a /r
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //With the new obtained AES data i can now decrypt the data from KMS
        JSONObject decryptedKMSmsg;
        String passsecret = null;
        String secret_resource = null;
        Integer nonce_two_plus_one = null;
        try {
            decryptedKMSmsg = new JSONObject(RSA_Util.decrypt(clientPK.getModulus(), clientPK.getPrivateExponent(), kms_msg));
            secret_resource = (String)decryptedKMSmsg.get("secretRsc");
            nonce_two_plus_one = (Integer)decryptedKMSmsg.get("nonce_two_plus_one");
            passsecret = secret_user.concat(secret_resource);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encryption for the photo
        AesUtil AES_encryptphoto = new AesUtil();
        String encryptedPhoto = AES_encryptphoto.encrypt(secret_user, secret_resource, passsecret, new String(ImageConverter.bytesToBase64(image)));

        //Msg to KMS
        //var msgKMS={"id":Lockr.get("id"),"idResource":Lockr.get("idResource"),"n2_2":(kms_msg.nonce_two_plus_one+1),"encryptedPhoto":encryptedPhoto};
        JSONObject jsonphoto = new JSONObject();
        try {
            jsonphoto.put("id", user.getId_user());
            jsonphoto.put("idResource", idResource);
            jsonphoto.put("n2_2", (nonce_two_plus_one+1));
            jsonphoto.put("encryptedPhoto", encryptedPhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JSON of the Photo and Resource plus nonce for KMS
        AesUtil AES_msgKMS = new AesUtil();
        String salt_msgKMS = AesUtil.random();
        String IV_msgKMS = AesUtil.random();
        String encryptedPhotoWithResource = AES_msgKMS.encrypt(salt_msgKMS, IV_msgKMS, passphrase, jsonphoto.toString());

        //Needs to follow the format:
        //var keyKMS={"iv":iv,"salt":salt,"passPhrase":Lockr.get("passPhrase")};
        JSONObject jsonkeyforEPWR = new JSONObject();
        try {
            jsonkeyforEPWR.put("iv", IV_msgKMS);
            jsonkeyforEPWR.put("salt", salt_msgKMS);
            jsonkeyforEPWR.put("passPhrase", passphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Encryption of the Params for KMS
        String encryptedkeyforEPWR = RSA_Util.encryptPublic(KMS_pk.getModulus(), KMS_pk.getExponent(), jsonkeyforEPWR.toString());

        //Adding both params and photoresource to a JSON
        JSONObject jsonkeysandphoto = new JSONObject();
        //var messageToKMS={"keyKMSencrypt":keyKMSencrypt,"msgKMSEncrypt":msgKMSEncrypt};
        try {
            jsonkeysandphoto.put("keyKMSencrypt", encryptedkeyforEPWR);
            jsonkeysandphoto.put("msgKMSEncrypt", encryptedPhotoWithResource);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encapsulation of said message for RMS
        //var msgRMS={"id":Lockr.get("id"),"N1_2":(nonce_plus_one+1),"idResource":Lockr.get("idResource"),"rule":Lockr.get("rule"),"messageToKMS":JSON.stringify(messageToKMS)};
        JSONObject jsonUpperlayermsgtoKMS = new JSONObject();
        try {
            jsonUpperlayermsgtoKMS.put("id", user.getId_user());
            jsonUpperlayermsgtoKMS.put("N1_2", (nonce_plus_one+1));
            jsonUpperlayermsgtoKMS.put("idResource", idResource);
            jsonUpperlayermsgtoKMS.put("rule", rule);
            jsonUpperlayermsgtoKMS.put("messageToKMS", jsonkeysandphoto.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Encryption of the message for RMS
        AesUtil AES_msgKMSUpperLayer = new AesUtil();
        String upperLayerSalt = AesUtil.random();
        String upperLayerIV = AesUtil.random();
        String encryptedMsgKMS = AES_msgKMSUpperLayer.encrypt(upperLayerSalt, upperLayerIV, passphrase, jsonUpperlayermsgtoKMS.toString());

        //JSON containing the params for RMS
        //var keyRMS={"iv":iv,"salt":salt,"passPhrase":Lockr.get("passPhrase")};
        JSONObject jsonKeyRMS = new JSONObject();
        try {
            jsonKeyRMS.put("iv", upperLayerIV);
            jsonKeyRMS.put("salt", upperLayerSalt);
            jsonKeyRMS.put("passPhrase", passphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Encryption of the params
        String encryptedKeyRMS = RSA_Util.encryptPublic(RMS_pk.getModulus(), RMS_pk.getExponent(), jsonKeyRMS.toString());

        //Send of the message to RMS
        UploadReq2 UR2 = new UploadReq2(encryptedKeyRMS, encryptedMsgKMS);
        new HttpPostHandler().makeServiceCall(UR2.getURL(), UR2.getJSON());

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
