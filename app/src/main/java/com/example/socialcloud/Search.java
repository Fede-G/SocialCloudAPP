package com.example.socialcloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Task.SearchUser_task;
import com.example.socialcloud.Util.ImageConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Search extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchUser_task.AsyncResponse {

    private ImageView nav_imageview;
    private TextView nav_nametext;
    private TextView nav_emailtext;

    private TextView search_tv;
    private Button search_button;
    TableLayout table;

    User[] friends_array = null;

    /**
     * Method called when you create the activity_social_network
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //drawer init
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //other stuff getID
        search_tv = (TextView)findViewById(R.id.search_textview);
        search_button = (Button)findViewById(R.id.search_button);
        table = (TableLayout)findViewById(R.id.table_searchresult);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                igniteSearch();
            }
        });

        View hView =  navigationView.getHeaderView(0);
        nav_nametext = (TextView)hView.findViewById(R.id.nameTextView);
        nav_emailtext = (TextView)hView.findViewById(R.id.EmailTextView);
        nav_imageview = (ImageView)hView.findViewById(R.id.avatarimageview);

        setNavigator();
    }

    /**
     * Here the delegate passes the parameters back to the activity where i can elaborate them
     * @param output    a User object or none
     */
    public void processFinish(HashMap<String, Object> output){
        //if i'm here i have searched and those are the results.
        if(output.containsKey("friends_array")){
            JSONArray friends = (JSONArray)output.get("friends_array");
            TableRow row;
            TextView[] ai_text = null;
            TextView name_text;
            TextView surname_text;
            TextView city_text;
            User friend;
            friends_array = null;
            //if i have no friends with those params.. then i display nothing.
            if(friends != null) {
                ai_text = new TextView[friends.length()];
                //create the dynamic textviews and his event
                for (int i = 0; i < friends.length(); i++) {
                    ai_text[i] = new TextView(this);
                    ai_text[i].setOnClickListener(getOnClickDoSomething(ai_text[i]));
                }
                friends_array = new User[friends.length()];
                table.setVisibility(View.VISIBLE);

                //foreach friend
                for (int i = 0; i < friends.length(); i++) {
                    //new friend
                    friend = new User();
                    row = new TableRow(this);
                    ai_text[i].setClickable(true);
                    name_text = new TextView(this);
                    //name_text.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                    surname_text = new TextView(this);
                    city_text = new TextView(this);
                    try {
                        //get friend params
                        JSONObject jsonfriend = friends.getJSONObject(i);
                        friend.setId_user((Integer) jsonfriend.get("id"));
                        friend.setCity((String) jsonfriend.get("city"));
                        friend.setFirstname((String) jsonfriend.get("firstName"));
                        friend.setLastname((String) jsonfriend.get("lastName"));
                        friends_array[i] = friend;

                        //set them
                        ai_text[i].setText(Integer.toString(i));
                        name_text.setText(friend.getFirstname());
                        surname_text.setText(friend.getLastname());
                        city_text.setText(friend.getCity());
                        row.addView(ai_text[i]);
                        row.addView(name_text);
                        row.addView(surname_text);
                        row.addView(city_text);
                        table.addView(row);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                table.requestLayout();     // Not sure if this is needed.
            }
        }
    }

    /**
     * Start a new search using the params i have
     */
    private void igniteSearch(){
        if(TextUtils.isEmpty(search_tv.getText().toString())){
            search_tv.setError(getString(R.string.error_field_required));
            search_tv.requestFocus();
        }else{
            HashMap<String, Object> params = new HashMap<>();
            params.put("friend_data", search_tv.getText().toString());
            SearchUser_task SU_t = new SearchUser_task(this);
            SU_t.execute(params);
        }
    }

    /**
     * Evento to do something on click to the textview
     * @param TV
     * @return
     */
    View.OnClickListener getOnClickDoSomething(final TextView TV)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int i = Integer.parseInt(TV.getText().toString());
                Intent user = new Intent(Search.this,SocialNetwork.class);
                user.putExtra("user_id", friends_array[i].getId_user());
                startActivity(user);
            }
        };
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
                Intent user = new Intent(Search.this,SocialNetwork.class);
                startActivity(user);
                break;
            case R.id.nav_friendship:
                Intent friendship = new Intent(Search.this,Search.class);
                startActivity(friendship);
                break;
            case R.id.nav_search:
                Intent search = new Intent(Search.this,Search.class);
                startActivity(search);
                break;
            case R.id.nav_upload_photo:
                Intent upload = new Intent(Search.this,UploadPhoto.class);
                startActivity(upload);
                break;
            case R.id.nav_collection:
                Intent collection = new Intent(Search.this,PhotoGallery.class);
                startActivity(collection);
                break;
            case R.id.nav_quit:
                Intent quit = new Intent(Search.this,Quit.class);
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
