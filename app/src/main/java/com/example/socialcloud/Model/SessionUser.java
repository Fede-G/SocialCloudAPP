package com.example.socialcloud.Model;

//Static class needed to save the current user session during the navigation of the app
public class SessionUser {
    private static User user;
    private static String sessionId = null;

    /**
     * Getter of the Session User
     * @return User of the session
     */
    public static User getUser() {
        return user;
    }

    /**
     * Setter of the Session User
     * @param myuser User that logged in
     */
    public static void setUser(User myuser) {
        user = myuser;
    }

    /**
     * Getter of the Session ID
     * @return Session ID assigned by the OSN
     */
    public static String getSessionId() {
        return sessionId;
    }

    /**
     * Setter of the Session ID
     * @param mysessionId Session ID
     */
    public static void setSessionId(String mysessionId) {
        sessionId = mysessionId;
    }

    /**
     * Checks if my session is empty (not logged in) or if the user is already logged
     * @return false if the user isn't logged in (sessionID==null), true if he/she is
     */
    public static boolean hasSession(){
        if(sessionId == null){
            return false;
        }
        return true;
    }
}