package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to request the second upload request to RMS with the keys for RMS and an already encrypted msg
 */
public class UploadReq2 extends FormatGenerator {

    /**
     * Constructor for URL and JSON following the sequent format:
     * //var messageToRMS={"keyRMSencrypt":keyRMSencrypt,"msgRMS":msgRMS};
     * @param encryptedKeyRMS   Keys for RMS already encrypted with RMS params
     * @param encryptedMsgKMS   Message that needs to be decrypted
     */
    public UploadReq2(String encryptedKeyRMS, String encryptedMsgKMS) {
        super();
        url = makeStaticURL(Global.getUploadReq2_path());

        addObjectToJSON("keyRMSencrypt", encryptedKeyRMS);
        addObjectToJSON("msgRMS", encryptedMsgKMS);
    }
}
