package com.example.socialcloud.Util;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.socialcloud.HttpThread.HttpPostHandler;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Operation.Logout;
import com.example.socialcloud.Task.Logout_task;

import org.json.JSONObject;

import java.util.HashMap;

//A Service that checks out if the App is open, background or is closing up
public class OnClearFromRecentService extends Service {

    /**
     * super()
     * @param intent Check Service.onBind(Intent intent)
     * @return Check Service.onBind(Intent intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * When the application is started
     * @param intent Which application does start the service
     * @param flags Explicit behaviors
     * @param startId Service starter
     * @return Type of Service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    /**
     * When application is already closed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    /**
     * When application is being closed
     * @param rootIntent intent that triggered the closure
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Start the Logout Asynchronous Task
        if (SessionUser.hasSession()){
            Logout_task L_t = new Logout_task();
            L_t.execute();
            stopSelf();
        }
    }

    //Asynchronous Task for the Logout operation
    public static class Logout_task extends AsyncTask<Void, Void, Void> {

        /**
         * Start the Logout Operation, by sending the User SessionID to the server to be deleted
         * @param voids no arbitrary params
         * @return null
         */
        @Override
        protected Void doInBackground(Void... voids) {
            Logout L = new Logout();
            String result = new HttpPostHandler().makeServiceCall(L.getURL(), SessionUser.getSessionId());
            return null;
        }

        /**
         * After the task is completed
         * @param v null
         */
        @Override
        protected void onPostExecute(Void v) {
        }
    }

}