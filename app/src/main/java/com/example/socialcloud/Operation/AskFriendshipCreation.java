package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

//Format class for asking to create a friendship
public class AskFriendshipCreation extends FormatGenerator{

    /**
     * Constructor for URL and JSON
     * @param requestor id of the requesting user
     * @param searched          id of the searched user
     */
    public AskFriendshipCreation(Integer requestor, Integer searched) {
        super();
        url = makeStaticURL(Global.getAskFriendshipCreation_path());

        addObjectToJSON("id_requestor", requestor);
        addObjectToJSON("id_acceptor", searched);
    }
}
