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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.socialcloud.Model.Album;
import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Operation.GetAlbum;
import com.example.socialcloud.Task.GetAlbum_task;
import com.example.socialcloud.Task.ShowAlbum_task;
import com.example.socialcloud.Util.ImageConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class that manages the PhotoGallery tab and the various components
 * Implements: the Navigation Drawer, the delegate for GetAlbum_task
 */
public class PhotoGallery extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetAlbum_task.AsyncResponse, ShowAlbum_task.AsyncResponse {

    //variables UI
    private ImageView nav_imageview;
    private TextView nav_nametext;
    private TextView nav_emailtext;

    private EditText passphrase_pg;
    private ImageView img_show;
    private TableLayout table;

    /**
     * Method called when you create the activity_photogallery
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
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

        View hView =  navigationView.getHeaderView(0);
        nav_nametext = (TextView)hView.findViewById(R.id.nameTextView);
        nav_emailtext = (TextView)hView.findViewById(R.id.EmailTextView);
        nav_imageview = (ImageView)hView.findViewById(R.id.avatarimageview);

        setNavigator();

        passphrase_pg = (EditText)findViewById(R.id.passphrase_photogallery);
        img_show = (ImageView)findViewById(R.id.image_gallery);
        table = (TableLayout)findViewById(R.id.tableLayout);

        recoverAlbum();
    }

    /**
     * Here the delegate passes the parameters back to the activity where i can elaborate them
     * @param output nothing
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        if(output.containsKey("album")){
            JSONArray album = (JSONArray)output.get("album");
            if(album==null){
                return;
            }
            TextView[] tag_array = new TextView[album.length()];
            TableRow row;
            for(int i=0; i<album.length(); i++){
                tag_array[i] = new TextView(this);
            }

            for(int i=0; i<album.length(); i++){
                try {
                    row = new TableRow(this);
                    JSONObject photo = album.getJSONObject(i);
                    Album album_image = new Album();
                    album_image.setFileName((String)photo.get("fileName"));
                    album_image.setId((Integer)photo.get("id"));
                    album_image.setMetaTag((String)photo.get("metaTag"));
                    JSONObject jsonUser = (JSONObject)photo.get("user");
                    User album_user = new User();
                    album_user.setId_user((Integer)jsonUser.get("id"));
                    album_image.setUser(album_user);
                    tag_array[i].setText((String) photo.get("metaTag"));
                    tag_array[i].setOnClickListener(setOnClickAction(album_image));
                    row.addView(tag_array[i]);
                    table.addView(row);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            table.requestLayout();
        }
        else if(output.containsKey("photo")){
            byte[] photo = (byte[])output.get("photo");
            if(photo!=null){
                Bitmap bitmap = ImageConverter.convertPhoto(photo);
                img_show.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Event assignation of dynamic events generation
     * @param image the image that i have to retrieve
     * @return  the object with attached the event itself
     */
    View.OnClickListener setOnClickAction(final Album image)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                passphrase_pg.setError(null);
                if(passphrase_pg.getText().toString().isEmpty()){
                    passphrase_pg.setError(getString(R.string.error_field_required));
                    passphrase_pg.requestFocus();
                    return;
                }

                getAlbumImage(image.getUser().getId_user(), passphrase_pg.getText().toString(), image.getFileName());
            }
        };
    }

    /**
     * Function to get the image to display
     */
    private void getAlbumImage(Integer id_ua, String passphrase, String filename){
        HashMap<String, Object> params = new HashMap<>();
        params.put("id_user_album", id_ua);
        params.put("passphrase", passphrase);
        params.put("filename", filename);
        ShowAlbum_task SA_t = new ShowAlbum_task(this);
        SA_t.execute(params);
    }

    /**
     * Function to recover the album on start
     */
    private void recoverAlbum(){
        GetAlbum_task GA_t = new GetAlbum_task(this);
        GA_t.execute();
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
                Intent user = new Intent(PhotoGallery.this,SocialNetwork.class);
                startActivity(user);
                break;
            case R.id.nav_friendship:
                Intent friendship = new Intent(PhotoGallery.this,Friendship.class);
                startActivity(friendship);
                break;
            case R.id.nav_search:
                Intent search = new Intent(PhotoGallery.this,Search.class);
                startActivity(search);
                break;
            case R.id.nav_upload_photo:
                Intent upload = new Intent(PhotoGallery.this,UploadPhoto.class);
                startActivity(upload);
                break;
            case R.id.nav_collection:
                Intent collection = new Intent(PhotoGallery.this,PhotoGallery.class);
                startActivity(collection);
                break;
            case R.id.nav_quit:
                Intent quit = new Intent(PhotoGallery.this,Quit.class);
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
