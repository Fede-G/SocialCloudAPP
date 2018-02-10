package com.example.socialcloud;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.socialcloud.Task.Logout_task;

import java.util.HashMap;

//Quit class that on the menu choice quit stops the application and closes it, after deleting the SessionID from the server
public class Quit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Logout_task.AsyncResponse{

    private Logout_task L_t = null;

    /**
     * Method called when you create the activity_quit
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit);

        endApp();
    }

    /**
     * Function called to end the app, starting by creating a new Logout Task to delete my session
     */
    private void endApp() {
        L_t = new Logout_task(this);
        L_t.execute();
    }

    /**
     * After it completes the task i delegate the result to the Quit class
     * @param output an empty Hashmap, could be used in the future
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        //I check if the application is running and i stop it, i go to sleep (allowing it to finish) and then wake up
        L_t.cancel(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Bruteforce kill, via pid AND intent
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     * Drawer unused option
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Drawer unused option
     * @param menu menu option
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Drawer unused option
     * @param item item menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * Drawer unused option
     * @param item item menu
     * @return false
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
