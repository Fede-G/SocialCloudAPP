package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

//Format class for checking a session
public class CheckSession extends FormatGenerator {

    /**
     * Constructor for URL and JSON
     */
    public CheckSession(){
        super();
        url = makeStaticURL(Global.getCheckSession_path());
    }
}
