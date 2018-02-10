package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to make the session happening on server side
 */
public class LoginGet extends FormatGenerator {

    /**
     * Constructor for URL
     */
    public LoginGet() {
        super();
        url = makeStaticURL(Global.getLoginGet_path());
    }
}
