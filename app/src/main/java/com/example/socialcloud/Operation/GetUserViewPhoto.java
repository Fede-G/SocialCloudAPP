package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

/**
 * Format class to get the photo from an user
 */
public class GetUserViewPhoto extends FormatGenerator {
    /**
     * Constructor for URL and JSON following the sequent format:
     * //var message={"id":id,"filename":filename};
     * @param id        ID of the user i want to get the resource of
     * @param filename  name of the file i want to get
     */
    public GetUserViewPhoto(Integer id, String filename) {
        super();
        url = makeStaticURL(Global.getGetUserViewPhoto_path());

        addObjectToJSON("id", id);
        addObjectToJSON("filename", filename);
    }
}