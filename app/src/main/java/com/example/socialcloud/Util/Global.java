package com.example.socialcloud.Util;

import android.app.Application;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

//This class is more like a repository of URLs than anything else, can be used in the whole project as references to be picked up and that don't change.
public class Global extends Application {

    private static final URL OSN_url = makeFinalURL("http://193.206.170.142/OSN");
    private static final URL RMS_url = makeFinalURL("http://193.206.170.143/RMS");
    private static final URL PFS_url = makeFinalURL("http://193.206.170.147/PathFinder");
    private static final URL KMS_url = makeFinalURL("http://193.206.170.148/KMS");

    private static final String register_dir = "/user/";

    private static final String loginget_dir = "/loginGet/";

    private static final String getpk_dir = "/getPK";

    private static final String clientkeys_dir = "/createSocialUser1/";

    private static final String savepkclient_dir = "/insertPKClient/";

    private static final String createsocialuser2_dir = "/createSocialUser2/";

    private static final String PFSinsertUser_dir = "/userInsertion/";

    private static final String login_dir = "/login/";

    private static final String searchfriend_dir = "/search";

    private static final String getselfprofile_dir = "/profile/";

    private static final String getgetalbum_dir = "/album/";

    private static final String getgetfriendrequest_dir = "/getFriendRequest/";

    private static final String getdeletependingfriendship_dir = "/deletePending/";

    private static final String getgetfriendshiprequestor_dir = "/getFriendshipRequestor/";

    private static final String getfriendshipcreation_dir = "/friendshipCreation/";

    private static final String getaskfriendshipcreation_dir = "/requestFriendship/";

    private static final String getsavealbum_dir = "/saveAlbum";

    private static final String getuploadreq1_dir = "/uploadReq1/";

    private static final String getgetpkclient_dir = "/getPKClient";

    private static final String getuploadreq2_dir = "/uploadReq2/";

    private static final String getgetuserviewphoto_dir = "/getPhoto/";

    private static final String getgetdownload_dir = "/downloadReq/";

    private static final String getpendingrequest_dir = "/pending/";

    private static final String getevalutationfriendship_dir = "/evaluationFriendship/";

    private static final String getchecksession_dir = "/checkSession";

    private static final String getlogout_dir = "/logout";

    /**
     * Method to create final URLs
     * @param url URL string
     * @return URL Object of that string
     */
    private static URL makeFinalURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Getter for the UserGenerator operation
     * @return an URL
     */
    public static String getRegisterUser_path(){ return OSN_url.toString()+register_dir; }

    /**
     * Getter for the LoginGet operation
     * @return an URL
     */
    public static String getLoginGet_path(){ return OSN_url.toString()+loginget_dir; }

    /**
     * Getter for the GetPK operation
     * @return an URL
     */
    public static String getGetPK_path() { return OSN_url.toString()+getpk_dir; }

    /**
     * Getter for the ClientKeys operation
     * @return an URL
     */
    public static String getClientKeys_path() { return RMS_url.toString()+clientkeys_dir; }

    /**
     * Getter for the SavePKClient operation
     * @return an URL
     */
    public static String getSavePKClient_path() { return OSN_url.toString()+savepkclient_dir; }

    /**
     * Getter for the CreateSocialUser2 operation
     * @return an URL
     */
    public static String getCreateSocialUser2_path() { return RMS_url.toString()+createsocialuser2_dir; }

    /**
     * Getter for the PFSInsertUser operation
     * @return an URL
     */
    public static String getPFSInsertUser_path() { return PFS_url.toString()+PFSinsertUser_dir; }

    /**
     * Getter for the Login operation
     * @return an URL
     */
    public static String getLogin_path() { return OSN_url.toString()+login_dir; }

    /**
     * Getter for the SearchFriend operation
     * @return an URL
     */
    public static String getSearchFriend_path() { return OSN_url.toString()+searchfriend_dir; }

    /**
     * Getter for the GetSelfProfile operation
     * @return an URL
     */
    public static String getGetSelf_path() { return OSN_url.toString()+getselfprofile_dir; }

    /**
     * Getter for the GetAlbum operation
     * @return an URL
     */
    public static String getGetAlbum_path() { return OSN_url.toString()+getgetalbum_dir; }

    /**
     * Getter for the GetFriendRequest operation
     * @return an URL
     */
    public static String getGetFriendRequest_path() { return OSN_url.toString()+getgetfriendrequest_dir; }

    /**
     * Getter for the DeletePendingFriendship operation
     * @return an URL
     */
    public static String getDeletePendingFriendship_path() { return OSN_url.toString()+getdeletependingfriendship_dir; }

    /**
     * Getter for the GetFriendshipRequestor operation
     * @return an URL
     */
    public static String getGetFriendshipRequestor_path() { return OSN_url.toString()+getgetfriendshiprequestor_dir; }

    /**
     * Getter for the FriendshipCreation operation
     * @return an URL
     */
    public static String getFriendshipCreation_path() { return PFS_url.toString()+getfriendshipcreation_dir; }

    /**
     * Getter for the AskFriendshipCreation operation
     * @return an URL
     */
    public static String getAskFriendshipCreation_path() { return OSN_url.toString()+getaskfriendshipcreation_dir; }

    /**
     * Getter for the SaveAlbum operation
     * @return an URL
     */
    public static String getSaveAlbum_path() { return OSN_url.toString()+getsavealbum_dir; }

    /**
     * Getter for the UploadReq1 operation
     * @return an URL
     */
    public static String getUploadReq1_path() { return RMS_url.toString()+getuploadreq1_dir; }

    /**
     * Getter for the GetPKClient operation
     * @return an URL
     */
    public static String getGetPKClient_path() { return OSN_url.toString()+getgetpkclient_dir; }

    /**
     * Getter for the UploadReq2 operation
     * @return an URL
     */
    public static String getUploadReq2_path() { return RMS_url.toString()+getuploadreq2_dir; }

    /**
     * Getter for the GetUserViewPhoto operation
     * @return an URL
     */
    public static String getGetUserViewPhoto_path() { return OSN_url.toString()+getgetuserviewphoto_dir; }

    /**
     * Getter for the GetDownload operation
     * @return an URL
     */
    public static String getGetDownload_path() { return RMS_url.toString()+getgetdownload_dir; }

    /**
     * Getter for the PendingRequest operation
     * @return an URL
     */
    public static String getPendingRequest_path() { return OSN_url.toString()+getpendingrequest_dir; }

    /**
     * Getter for the EvalutationFriendship operation
     * @return an URL
     */
    public static String getEvalutationFriendship_path() { return PFS_url.toString()+getevalutationfriendship_dir; }

    /**
     * Getter for the CheckSession operation
     * @return an URL
     */
    public static String getCheckSession_path() { return OSN_url.toString()+getchecksession_dir; }

    /**
     * Getter for the Logout operation
     * @return an URL
     */
    public static String getLogout_path() { return OSN_url.toString()+getlogout_dir; }
}