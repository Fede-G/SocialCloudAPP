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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Task.FriendshipManager_task;
import com.example.socialcloud.Task.GetFriendRequest_task;
import com.example.socialcloud.Util.ImageConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class that manages the Friendship tab and the various components
 * Implements: the Navigation Drawer, the delegate for GetFriendRequest_task and FriendshipManager_task
 */
public class Friendship extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetFriendRequest_task.AsyncResponse, FriendshipManager_task.AsyncResponse {

    //Graphical variables
    private ImageView nav_imageview;
    private TextView nav_nametext;
    private TextView nav_emailtext;

    //Class variables
    private User[] friends_array;
    private TableLayout table;
    private Boolean acceptedrequest;

    /**
     * Method called when you create the activity_friendship
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer setup
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //graphical object of the friendship content
        table = (TableLayout)findViewById(R.id.table_friendshipresult);

        View hView =  navigationView.getHeaderView(0);
        nav_nametext = (TextView)hView.findViewById(R.id.nameTextView);
        nav_emailtext = (TextView)hView.findViewById(R.id.EmailTextView);
        nav_imageview = (ImageView)hView.findViewById(R.id.avatarimageview);

        setNavigator();

        //On startup i get the friends request by starting a new task
        GetFriendRequest_task GFR_t = new GetFriendRequest_task(this);
        GFR_t.execute();
    }

    /**
     * Here the delegate passes the parameters back to the activity where i can elaborate them
     * @param output    a JSONArray of Users (check GetFriendRequest_task)
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        //Check if the output contains users requests to be shown
        if(output.containsKey("users")){
            //If there is then i create the table equivalent to show the results
            JSONArray friends = (JSONArray)output.get("users");
            TableRow row;
            TextView name_text;
            TextView surname_text;
            User friend = new User();
            friends_array = null;

            Button[] acceptButtons;
            Button[] refuseButtons;

            if(friends!=null) {
                //dynamic array generation for textview with integrated event
                acceptButtons = new Button[friends.length()];
                refuseButtons = new Button[friends.length()];
                for (int i = 0; i < friends.length(); i++) {
                    acceptButtons[i] = new Button(this);
                    refuseButtons[i] = new Button(this);
                }
                friends_array = new User[friends.length()];
                table.setVisibility(View.VISIBLE);

                //now i cycle every request and assign the values
                for (int i = 0; i < friends.length(); i++) {
                    row = new TableRow(this);
                    name_text = new TextView(this);
                    //format isn't right, unluckly doesn't work
                    //name_text.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                    surname_text = new TextView(this);
                    try {
                        JSONObject jsonfriend = friends.getJSONObject(i);
                        friend.setId_user((Integer) jsonfriend.get("id"));
                        friend.setFirstname((String) jsonfriend.get("firstName"));
                        friend.setLastname((String) jsonfriend.get("lastName"));
                        friends_array[i] = friend;

                        acceptButtons[i].setText("Accept");
                        refuseButtons[i].setText("Refuse");
                        acceptButtons[i].setOnClickListener(getOnClickDynEvent(acceptButtons[i], true, friends_array[i]));
                        refuseButtons[i].setOnClickListener(getOnClickDynEvent(refuseButtons[i], false, friends_array[i]));

                        name_text.setText(friend.getFirstname());
                        surname_text.setText(friend.getLastname());
                        row.addView(name_text);
                        row.addView(surname_text);
                        row.addView(acceptButtons[i]);
                        row.addView(refuseButtons[i]);
                        table.addView(row);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                table.requestLayout();
            }
        }else{
            //if i receive a request that is empty then i accepted/refused the request
            Intent user = new Intent(Friendship.this, Friendship.class);
            startActivity(user);
        }
    }

    /**
     * Event assignation of dynamic events generation
     * @param button The TextView that i have to assign the event to
     * @param accept Boolean value to decide if is accepted or not
     * @return  the object with attached the event itself
     */
    View.OnClickListener getOnClickDynEvent(final Button button, final Boolean accept, final User friend)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                acceptedrequest = accept;
                //assign the event manageFriendship
                manageFriendship(friend, acceptedrequest);
            }
        };
    }

    /**
     * Event for managing the accept/refuse of a friendship
     * @param friend    User friend that i want to accept or not
     * @param accept    if i accept or refuse said request
     */
    private void manageFriendship(User friend, boolean accept){
        FriendshipManager_task FM_t = new FriendshipManager_task(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("accept", accept);
        params.put("id_requestor", friend.getId_user());
        FM_t.execute(params);
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
                Intent user = new Intent(Friendship.this,SocialNetwork.class);
                startActivity(user);
                break;
            case R.id.nav_friendship:
                Intent friendship = new Intent(Friendship.this,Friendship.class);
                startActivity(friendship);
                break;
            case R.id.nav_search:
                Intent search = new Intent(Friendship.this,Search.class);
                startActivity(search);
                break;
            case R.id.nav_upload_photo:
                Intent upload = new Intent(Friendship.this,UploadPhoto.class);
                startActivity(upload);
                break;
            case R.id.nav_collection:
                Intent collection = new Intent(Friendship.this,PhotoGallery.class);
                startActivity(collection);
                break;
            case R.id.nav_quit:
                Intent quit = new Intent(Friendship.this,Quit.class);
                startActivity(quit);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
