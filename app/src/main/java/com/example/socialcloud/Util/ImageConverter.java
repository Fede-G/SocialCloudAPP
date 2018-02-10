package com.example.socialcloud.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;

//An Util class needed to convert image from various formats to bitmap, that Android can easily display
public class ImageConverter {

    /**
     * Converts a series of bytes to a Bitmap photo
     * @param photo bytes array of a photo
     * @return a bitmap image
     */
    public static Bitmap convertPhoto(byte[] photo){
        //Check that the image isn't null or empty
        if(photo!=null || photo.length!=0){
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        }
        return null;
    }

    /**
     * Converts a jsonarray to a series of bytes. Can be chained into convertPhoto to obtain a Bitmap
     * @param jsonarray a JSONArray that needs to be converted
     * @return          a byte array that represents an image
     */
    public static byte[] convertJSONArray(JSONArray jsonarray){
        //Check that the JSONArray isn't null or empty
        if(jsonarray==null || jsonarray.length()<=0){
            return new byte[0];
        }

        //Create an array of strings from the JSONArray
        String[] array_string = new String[jsonarray.length()];

        for(int i=0; i<jsonarray.length(); i++){
            try {
                array_string[i] = String.valueOf(jsonarray.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Convert to bytes
        byte[] bytes = new byte[array_string.length];

        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(array_string[i].trim());
        }

        return bytes;
    }

    // convert from bitmap to byte array
    public static String bytesToBase64(byte[] image) {
        return Base64.encodeToString(image, Base64.DEFAULT);
    }
}
