package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class used to create a new pending friendship
 */
public class FriendshipCreation extends FormatGenerator{

    /**
     * Constructor for URL and JSON
     */
    public FriendshipCreation() {
        super();
        url = makeStaticURL(Global.getFriendshipCreation_path());
    }
}
