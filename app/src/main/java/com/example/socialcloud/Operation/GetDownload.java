package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the download of the images
 */
public class GetDownload extends FormatGenerator{
    /**
     * Constructor for URL and JSON
     */
    public GetDownload(){
        super();
        url = makeStaticURL(Global.getGetDownload_path());
    }
}
