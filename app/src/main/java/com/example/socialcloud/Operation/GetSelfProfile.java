package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the profile of a person
 */
public class GetSelfProfile extends FormatGenerator {

    /**
     * Constructor for URL and JSON
     */
    public GetSelfProfile(){
        super();
        url = makeStaticURL(Global.getGetSelf_path());
    }
}
