package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

//Format class that asks the server to delete the friendship request that is pending, because it will be accepted or refused after
public class DeletePendingFriendship extends FormatGenerator {

    /**
     * Constructor for URL and JSON
     * @param requestor ID of the requestor (most likely the user)
     * @param acceptor  ID of the requested user
     */
    public DeletePendingFriendship(Integer requestor, Integer acceptor) {
        super();
        url = makeStaticURL(Global.getDeletePendingFriendship_path());

        addIDsToJSON(requestor, acceptor);
    }

    /**
     * Function to add both params to the JSON following the format:
     * var messagePFS={"idSessionUser":sessionUser,"idSearchedUser":searchedUser};
     * @param requestor ID of requestor
     * @param acceptor  ID of requested
     */
    private void addIDsToJSON(int requestor, int acceptor){
        addObjectToJSON("id_requestor", requestor);
        addObjectToJSON("id_acceptor", acceptor);
    }
}
