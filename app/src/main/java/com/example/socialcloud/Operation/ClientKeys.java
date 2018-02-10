package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.AesUtil;
import com.example.socialcloud.Util.Global;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Util.RSA_Util;

import org.json.JSONException;
import org.json.JSONObject;

//Format class to create the encrypted message to send to RMS for getting your keys
public class ClientKeys extends FormatGenerator {

    /**
     * Costructor for URL and JSON
     * @param new_user      The user you wanted to be saved
     * @param modulus       Modulus of KMS
     * @param exponent      Exponent of KMS
     * @param passphrase    Passphrase input of the user
     * @param IV            Initialization Vector for AES generated previously
     * @param salt          Salt for AES generated previously
     */
    public ClientKeys(User new_user, String modulus, String exponent, String passphrase, String IV, String salt){
        //Create URL and JSON
        super();
        url = makeStaticURL(Global.getClientKeys_path());

        //Create the JSON that needs to be sent to KMS
        JSONObject JSONforKMS = createJSONforKMS(new_user, IV, salt, passphrase);

        String msgforRMS = RSA_Util.encryptPublic(modulus, exponent, JSONforKMS.toString());

        createJSONforRMS(new_user, msgforRMS);
    }

    /**
     * Here happens the encapsulation of the previous JSON into a message for RMS following the sequent format:
     * //var msgtoRMS={"idu": idUser, "encryptedmsgtoKMS": messageCripted};
     * @param new_user      user that needs to be created
     * @param msgforRMS     encrypted previous JSON
     */
    private void createJSONforRMS(User new_user, String msgforRMS) {
        addObjectToJSON("idu", new_user.getId_user());
        addObjectToJSON("encryptedmsgtoKMS", msgforRMS);
    }

    /**
     * Here the JSON for KMS gets created and follows the sequent format:
     * var message={"iduser":idUser,"iv":iv,"salt":salt,"keySize":keySize,"iterationCount":iterationCount,"passPhrase":passphrase};
     * @param new_user      user that needs to be created, i pick his ID
     * @param IV            Initialization vector
     * @param salt          Salt
     * @param passphrase    Passphrase
     * @return              A JSONObject following the upper format
     */
    private JSONObject createJSONforKMS(User new_user, String IV, String salt, String passphrase) {
        JSONObject JSON = new JSONObject();
        try {
            JSON.put("iduser", new_user.getId_user());
            JSON.put("iv", IV);
            JSON.put("salt", salt);
            JSON.put("keySize", AesUtil.DEFAULT_KEYSIZE);
            JSON.put("iterationCount", AesUtil.DEFAULT_ITERATIONCOUNT);
            JSON.put("passPhrase", passphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSON;
    }

}
