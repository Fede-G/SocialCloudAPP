package com.example.socialcloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Task.GetSelfProfile_task;
import com.example.socialcloud.Task.RequestFriendshipCreation_task;
import com.example.socialcloud.Task.ShowAndCheck_task;
import com.example.socialcloud.Util.ImageConverter;
import com.example.socialcloud.Util.OnClearFromRecentService;

import java.util.HashMap;

/**
 * Class that manages the SocialNetwork tab, the Homepage of the system, and the various components
 * Implements: the Navigation Drawer, the delegate for GetSelfProfile_task, ShowAndCheck_task and RequestFriendshipCreation_task
 */
public class SocialNetwork extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetSelfProfile_task.AsyncResponse, ShowAndCheck_task.AsyncResponse, RequestFriendshipCreation_task.AsyncResponse {

    //Variables graphical
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    private ImageView profilepic;
    private TextView namesurname_tv;
    private TextView email_tv;
    private TextView date_tv;
    private TextView city_tv;

    private ImageView nav_imageview;
    private TextView nav_nametext;
    private TextView nav_emailtext;

    private Button requestfriendshipbutton;

    User searched_user = new User();

    /**
     * Method called when you create the activity_social_network
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_network);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //drawer retrieving
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //activity retrieving UI + navigation UI
        profilepic = (ImageView) findViewById(R.id.ProfilePic);
        namesurname_tv = (TextView) findViewById(R.id.NameSurname);
        city_tv = (TextView) findViewById(R.id.City);
        email_tv = (TextView) findViewById(R.id.Email);
        date_tv = (TextView) findViewById(R.id.BirthDate);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        nav_nametext = (TextView)hView.findViewById(R.id.nameTextView);
        nav_emailtext = (TextView)hView.findViewById(R.id.EmailTextView);
        nav_imageview = (ImageView)hView.findViewById(R.id.avatarimageview);

        //Starting the service to stop the app and disconnect when you exit even if you don't press the back button
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        //If i have a session, then i'm already logged in
        if(SessionUser.getSessionId()!= null) {
            //If i'm logged in i have the data for the navigator
            setNavigator();
            //i check if i have other params, if i do then i have searched another user and i'm showing his profile
            if(getIntent().getExtras() != null){
                Bundle bundle = getIntent().getExtras();
                Integer id = (Integer) getIntent().getSerializableExtra("user_id");

                HashMap<String, Object> params = new HashMap<>();
                params.put("id_searched_user", id);
                ShowAndCheck_task SAC_t = new ShowAndCheck_task(this);
                SAC_t.execute(params);
            //else i retrieve my profile because i'm getting back to my home
            }else{
                User sessionuser = SessionUser.getUser();
                Bitmap photo = ImageConverter.convertPhoto(sessionuser.getPhoto());
                if(photo!=null) {
                    profilepic.setImageBitmap(photo);
                }
                namesurname_tv.setText(sessionuser.getLastname() + " " + sessionuser.getFirstname());
                if (sessionuser.getCity() != ""){
                    city_tv.setText(sessionuser.getCity());
                }
                email_tv.setText(sessionuser.getEmail());
                date_tv.setText(sessionuser.getBirth_day());
            }
        //if i'm not logged in then i have to retrieve my profile, where i have to pass my user id from the login
        } else if(getIntent().getExtras() != null) {
            Integer id = (Integer) getIntent().getSerializableExtra("user_id");

            HashMap<String, Object> params = new HashMap<>();
            params.put("user_id", id);
            GetSelfProfile_task GSP_t = new GetSelfProfile_task(this);
            GSP_t.execute(params);
        }

        //setter for the onclick event on the button
        requestfriendshipbutton = (Button) findViewById(R.id.requestfriendshipbutton);
        requestfriendshipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            requestFriendship();
            }
        });
    }

    /**
     * Method called to start the RequestFriendshipCreation_task to another user
     */
    private void requestFriendship(){
        RequestFriendshipCreation_task RFC_t = new RequestFriendshipCreation_task(this);
        HashMap<String, Object> params = new HashMap<>();
        requestfriendshipbutton.setVisibility(View.INVISIBLE);
        params.put("id_requested", searched_user.getId_user());
        RFC_t.execute(params);
    }

    /**
     * Manager for pressing the back button and close the drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Here the delegate passes the parameters back to the activity where i can elaborate them
     * @param output    a User object or none
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        //If i have data then is a user in return that is not me
        if(output.containsKey("user_data")){
            searched_user = (User)output.get("user_data");
            namesurname_tv.setText(searched_user.getLastname() + " " + searched_user.getFirstname());
            if (searched_user.getCity() != ""){
                city_tv.setText(searched_user.getCity());
            }
            email_tv.setText(searched_user.getEmail());
            date_tv.setText(searched_user.getBirth_day());
            Bitmap photo = ImageConverter.convertPhoto(searched_user.getPhoto());
            if(photo!=null) {
                profilepic.setImageBitmap(photo);
            }
            Boolean isInvisible = (Boolean)output.get("deactivate");
            if(!isInvisible)
                requestfriendshipbutton.setVisibility(View.VISIBLE);
        //else i get my data and put it back on screen
        }else{
            User sessionuser = SessionUser.getUser();
            namesurname_tv.setText(sessionuser.getLastname() + " " + sessionuser.getFirstname());
            if (sessionuser.getCity() != ""){
                city_tv.setText(sessionuser.getCity());
            }
            email_tv.setText(sessionuser.getEmail());
            date_tv.setText(sessionuser.getBirth_day());
            Bitmap photo = ImageConverter.convertPhoto(sessionuser.getPhoto());
            if(photo!=null) {
                profilepic.setImageBitmap(photo);
            }
            setNavigator();
        }
    }

    /**
     * Setter for the navigation drawer bar in the top left where i say what user is logged in
     */
    private void setNavigator(){
        User sessionuser = SessionUser.getUser();
        Bitmap photo = ImageConverter.convertPhoto(sessionuser.getPhoto());
        if(photo!=null) {
            nav_imageview.setImageBitmap(photo);
        }
        nav_nametext.setText(sessionuser.getLastname() + " " + sessionuser.getFirstname());
        nav_emailtext.setText(sessionuser.getEmail());
    }

    /**
     * Inflater for the options of the menu, by adding the item to said menu
     * @param menu  menu i have to inflate
     * @return      if the menu has been inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.social_network, menu);
        return true;
    }

    /**
     * handling of the option press
     * @param item  item that has been pressed
     * @return      if the item is been pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Management of the selection of an item from the navigation drawer and starting the intent that corresponds to the item clicked
     * @param item  item clicked
     * @return      a "fake" true, reality is that i start a new intent
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.nav_user:
                Intent user = new Intent(SocialNetwork.this,SocialNetwork.class);
                startActivity(user);
                break;
            case R.id.nav_friendship:
                Intent friendship = new Intent(SocialNetwork.this,Friendship.class);
                startActivity(friendship);
                break;
            case R.id.nav_search:
                Intent search = new Intent(SocialNetwork.this,Search.class);
                startActivity(search);
                break;
            case R.id.nav_upload_photo:
                Intent upload = new Intent(SocialNetwork.this,UploadPhoto.class);
                startActivity(upload);
                break;
            case R.id.nav_collection:
                Intent collection = new Intent(SocialNetwork.this,PhotoGallery.class);
                startActivity(collection);
                break;
            case R.id.nav_quit:
                Intent quit = new Intent(SocialNetwork.this,Quit.class);
                startActivity(quit);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
