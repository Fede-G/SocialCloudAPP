package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class that asks PFS for an insertion of the user in the system
 */
public class PFSInsertUser extends FormatGenerator {

    /**
     * Constructor for URL
     */
    public PFSInsertUser(){
        super();

        url = makeStaticURL(Global.getPFSInsertUser_path());
    }
}
