package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the Album request
 */
public class GetAlbum extends FormatGenerator{

    /**
     * Constructor for URL and JSON
     */
    public GetAlbum(){
        super();
        url = makeStaticURL(Global.getGetAlbum_path());
    }
}
