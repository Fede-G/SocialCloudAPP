package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to request an upload to RMS with params already in string and an already encrypted msg
 */
public class UploadReq1 extends FormatGenerator {

    /**
     * Constructor for URL and JSON following the sequent format:
     * //var message={'paramRMS':paramRMS,'encryptmsgRMS':encryptmsgRMS};
     * @param paramRMS          parameters for RMS
     * @param encryptmsgRMS     the encrypted msg for RMS
     */
    public UploadReq1(String paramRMS, String encryptmsgRMS) {
        super();
        url = makeStaticURL(Global.getUploadReq1_path());

        addObjectToJSON("paramRMS", paramRMS);
        addObjectToJSON("encryptmsgRMS", encryptmsgRMS);
    }
}
