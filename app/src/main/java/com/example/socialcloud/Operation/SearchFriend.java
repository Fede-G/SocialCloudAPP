package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class for making requests to search a friend
 */
public class SearchFriend extends FormatGenerator {
    /**
     * Construtor for URL
     */
    public SearchFriend() {
        super();
        url = makeStaticURL(Global.getSearchFriend_path());
    }
}
