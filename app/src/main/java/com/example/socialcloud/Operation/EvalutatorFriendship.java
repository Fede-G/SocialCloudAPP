package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class that asks the evalutation of an existing friendship
 */
public class EvalutatorFriendship extends FormatGenerator{

    /**
     * Constructor for URL and JSON
     */
    public EvalutatorFriendship(){
        super();
        url = makeStaticURL(Global.getEvalutationFriendship_path());
    }
}
