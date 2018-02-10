package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;

import org.json.JSONException;

/**
 * Format class to save the album (image) to the system
 */
public class SaveAlbum extends FormatGenerator{

    /**
     * Constructor of URL
     */
    public SaveAlbum(String tag, Integer id, String image_name) {
        super();
        url = makeStaticURL(Global.getSaveAlbum_path());

        //var album={"tag":tag,"id":Lockr.get("id"),"fileName":Lockr.get("fileName")};
        addObjectToJSON("tag", tag);
        addObjectToJSON("id", id);
        addObjectToJSON("fileName", image_name);
    }
}
