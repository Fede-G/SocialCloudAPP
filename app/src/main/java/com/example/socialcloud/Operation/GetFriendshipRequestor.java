package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the requestor for making a friendship
 */
public class GetFriendshipRequestor extends FormatGenerator{
    /**
     * Constructor for URL and JSON
     */
    public GetFriendshipRequestor() {
        super();
        url = makeStaticURL(Global.getGetFriendshipRequestor_path());
    }
}
