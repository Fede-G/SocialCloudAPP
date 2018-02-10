package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the ask for pending requests
 */
public class PendingRequest extends FormatGenerator{

    /**
     * Constructor URL and JSON, that needs to follow the format:
     * //var messagePFS={"idSessionUser":sessionUser,"idSearchedUser":searchedUser};
     * @param id_user           ID of the user that wants to be friend
     * @param id_searched_user  ID of the user that is asked to be friend with
     */
    public PendingRequest(Integer id_user, Integer id_searched_user){
        super();
        url = makeStaticURL(Global.getPendingRequest_path());

        addObjectToJSON("idSessionUser", id_user);
        addObjectToJSON("idSearchedUser", id_searched_user);
    }
}
