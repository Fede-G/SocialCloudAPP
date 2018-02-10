package com.example.socialcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcloud.Model.SessionUser;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Task.UploadPhoto_task;
import com.example.socialcloud.Util.ImageConverter;

import java.util.HashMap;

/**
 * Class that manages the UploadPhoto tab and the various components
 * Implements: the Navigation Drawer, the delegate for UploadPhoto_task
 */
public class UploadPhoto extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadPhoto_task.AsyncResponse {

    private ImageView nav_imageview;
    private TextView nav_nametext;
    private TextView nav_emailtext;

    private EditText upload_metatag;
    private EditText upload_rule;
    private EditText upload_filename;
    private EditText upload_passphrase;

    private ImageView upload_preview;

    private byte[] image = null;

    /**
     * Method called when you create the activity_uploadphoto
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
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

        //Now to the image
        upload_metatag = (EditText) findViewById(R.id.metatag);
        upload_rule = (EditText) findViewById(R.id.access_rule);
        upload_filename = (EditText) findViewById(R.id.image_name);
        upload_passphrase = (EditText) findViewById(R.id.passphrase_pass);

        upload_preview = (ImageView) findViewById(R.id.preview_upload);

        //If i receive a message in this activity then i'm returning from the photoselector intent
        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();

            //Inform the user using an alertdialog
            AlertDialog alertDialog = new AlertDialog.Builder(UploadPhoto.this).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Image imported!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            image = bundle.getByteArray("bipmap_array");

            //setback all previous data that was insert
            if(image != null){
                upload_preview.setImageBitmap(ImageConverter.convertPhoto(image));
            }
        }

        //if i want to register, click the register button and i'll go forth to the passphrase
        Button rUploadButton = (Button) findViewById(R.id.upload_photo_button);
        rUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpload();
            }
        });

        //if i want to grab an image, i start the intent for photopicker
        Button uImagePickerButton = (Button) findViewById(R.id.gotopicker_button);
        uImagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAndSend();
            }
        });
    }

    /**
     * Here i make the intent for picking the photo expires
     */
    private void pickAndSend(){

        gotoPhotoselector(getCurrentFocus());
        //mPhotoTask = new Register.PhotoPickerTask(new_user);
        //mPhotoTask.execute((Void) null);
    }

    public void gotoPhotoselector(View view){
        Intent intent = new Intent(UploadPhoto.this, PhotoSelector.class);

        intent.putExtra("classcaller", UploadPhoto.class.toString());
        startActivity(intent);
    }

    /**
     * If i want to go on and register myself to the system then i have to send the new user to pick a new password
     */
    private void attemptUpload() {
        // Reset errors.
        upload_metatag.setError(null);
        upload_rule.setError(null);
        upload_filename.setError(null);
        upload_passphrase.setError(null);

        // Store values at the time of the attempt
        String metatag = upload_metatag.getText().toString();
        String str_rule = upload_rule.getText().toString();
        String filename = upload_filename.getText().toString();
        String passphrase = upload_passphrase.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(image==null){
            focusView = upload_preview;
            cancel = true;
        }

        //Check if the value is empty
        if (TextUtils.isEmpty(metatag)) {
            upload_metatag.setError(getString(R.string.error_field_required));
            focusView = upload_metatag;
            cancel = true;
        }

        //Check if the value is empty
        if (TextUtils.isEmpty(str_rule)) {
            upload_rule.setError(getString(R.string.error_field_required));
            focusView = upload_rule;
            cancel = true;
        }

        //Check if the value is empty
        if (TextUtils.isEmpty(filename)) {
            upload_filename.setError(getString(R.string.error_field_required));
            focusView = upload_filename;
            cancel = true;
        }

        //Check if the value is empty
        if(TextUtils.isEmpty(passphrase)){
            upload_passphrase.setError(getString((R.string.error_field_required)));
            focusView = upload_passphrase;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            HashMap<String, Object> params = new HashMap<>();
            params.put("tag", metatag);
            params.put("rule", Integer.parseInt(str_rule));
            params.put("image", image);
            params.put("image_name", filename.concat(".byte"));
            params.put("passphrase", passphrase);

            UploadPhoto_task UP_t = new UploadPhoto_task(this);
            UP_t.execute(params);
        }

    }

    /**
     * Here the delegate passes the parameters back to the activity where i can elaborate them
     * @param output nothing
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        final Intent mainIntent = new Intent(UploadPhoto.this, PhotoGallery.class);
        UploadPhoto.this.startActivity(mainIntent);
        UploadPhoto.this.finish();
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
                Intent user = new Intent(UploadPhoto.this,SocialNetwork.class);
                startActivity(user);
                break;
            case R.id.nav_friendship:
                Intent friendship = new Intent(UploadPhoto.this,Friendship.class);
                startActivity(friendship);
                break;
            case R.id.nav_search:
                Intent search = new Intent(UploadPhoto.this,Search.class);
                startActivity(search);
                break;
            case R.id.nav_upload_photo:
                Intent upload = new Intent(UploadPhoto.this,UploadPhoto.class);
                startActivity(upload);
                break;
            case R.id.nav_collection:
                Intent collection = new Intent(UploadPhoto.this,PhotoGallery.class);
                startActivity(collection);
                break;
            case R.id.nav_quit:
                Intent quit = new Intent(UploadPhoto.this,Quit.class);
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
