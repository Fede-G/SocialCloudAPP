package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

//Format class to create the second requesto of a new social user, but both inputs are already encrypted
public class CreateSocialUser2 extends FormatGenerator {

    /**
     * Constructor of URL and JSON, with already the addition of params, follows the format:
     * //var encrypted_clientPubKeyToRMS={"clientPubKeyToRMS": clientPubKeyToRMS, "encrypted_symmKey": encrypted_symmKey };
     * @param encrypted_CPKforRMS   Client Public Key encrypted
     * @param encrypted_symmKey     Symmetric Key encrypted
     */
    public CreateSocialUser2(String encrypted_CPKforRMS, String encrypted_symmKey){
        super();
        url = makeStaticURL(Global.getCreateSocialUser2_path());

        addObjectToJSON("clientPubKeyToRMS", encrypted_CPKforRMS);
        addObjectToJSON("encrypted_symmKey", encrypted_symmKey);
    }
}
