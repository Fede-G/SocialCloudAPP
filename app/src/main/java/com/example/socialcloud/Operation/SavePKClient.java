package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to save the Private Key (crypted) with other params
 */
public class SavePKClient extends FormatGenerator {

    /**
     * Constructor for URL and JSON that follows the format:
     * //var keys={"id":self.id,"exponent":client_exponent,"modulus":client_modulus,"private":client_privateCrypted,"salt":salt,"iv":iv};
     * @param id_user                   ID of the user that wants to be saved
     * @param exponent                  Public exponent
     * @param modulus                   Modulus
     * @param client_private_exponent   Private exponent
     * @param salt                      Salt
     * @param IV                        Initialization Vector
     */
    public SavePKClient(Integer id_user, String exponent, String modulus, String client_private_exponent, String salt, String IV){
        super();
        url = makeStaticURL(Global.getSavePKClient_path());

        addObjectToJSON("id", id_user);
        addObjectToJSON("exponent", exponent);
        addObjectToJSON("modulus", modulus);
        addObjectToJSON("private", client_private_exponent);
        addObjectToJSON("salt", salt);
        addObjectToJSON("iv", IV);
    }
}
