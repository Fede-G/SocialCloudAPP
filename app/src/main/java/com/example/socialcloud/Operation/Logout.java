package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

//Logout format class, doesn't have a JSON itself
public class Logout extends FormatGenerator{

    /**
     * Constructor for the URL and JSON
     */
    public Logout() {
        super();
        url = makeStaticURL(Global.getLogout_path());
    }
}
