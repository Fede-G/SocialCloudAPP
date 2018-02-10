package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the pending friend requests
 */
public class GetFriendRequest extends FormatGenerator{
    /**
     * Constructor for URL and JSON
     */
    public GetFriendRequest(){
        super();
        url = makeStaticURL(Global.getGetFriendRequest_path());
    }
}
