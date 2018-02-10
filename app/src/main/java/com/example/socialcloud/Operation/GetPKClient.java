package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class for getting the Keys of the client
 */
public class GetPKClient extends FormatGenerator {

    /**
     * Constructor for URL and JSON
     */
    public GetPKClient() {
        super();
        url = makeStaticURL(Global.getGetPKClient_path());
    }
}