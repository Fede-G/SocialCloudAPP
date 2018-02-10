package com.example.socialcloud;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.socialcloud.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Class that manages the request for photos to the camera or the gallery
 */
public class PhotoSelector extends AppCompatActivity {

    //This class has been heavily based on two stackoverflow tutorials instead of android ones.
    //Camera - courtesy of : https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
    //and from https://developer.android.com/training/camera/photobasics.html
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private User new_user = null;
    private Bundle bundle = null;
    private byte[] byteArray = null;
    private Bitmap photo = null;

    /**
     * The new activity that wwill pick up the image and display it before passing it to the caller
     * @param savedInstanceState    previous saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //default + UI component
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selector);
        this.imageView = (ImageView)this.findViewById(R.id.imageViewer);

        bundle = getIntent().getExtras();

        //i check who requests the camera: it can be done by the registration or the
        if(bundle.getString("classcaller").equals(Register.class.toString())){
            new_user = (User) bundle.getSerializable("userdata");
        }

        //Button for picking an image from the gallery and his listener onClick
        Button galleryButton = (Button) this.findViewById((R.id.viewgallery));
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE);
            }
        });

        //Button for making a new image from the camera and his listener onClick
        Button photoButton = (Button) this.findViewById(R.id.takephoto);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //Set the button to take back the results to the activity caller
        Button sendButton = (Button) this.findViewById(R.id.confirm_image_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bundle.getString("classcaller").equals(Register.class.toString())){
                    gotoRegister(getCurrentFocus());
                }else if(bundle.getString("classcaller").equals(UploadPhoto.class.toString())){
                    gotoUpload(getCurrentFocus());
                }
            }
        });
    }

    /**
     *
     * @param requestCode   Request code of the action, can be camera or pick from gallery
     * @param resultCode    result code to check if the application has been closed without selecting anything
     * @param data          data of the return operation (used on the camera request)
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If i choosen for the camera, then i get what i snapped
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
             photo = (Bitmap) data.getExtras().get("data");
             imageView.setImageBitmap(photo);
        }
        //else i pick the image from the image URI that i have chosen
        else if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                photo = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(photo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * this starts an intent to go to back the registration page by passing my photo in PNG
     * @param view view of input that i make the callback to if needed
     */
    public void gotoRegister(View view){
        Intent intent = new Intent(PhotoSelector.this, Register.class);

        //if i selected a photo i pass the data, else it will be null
        if(photo != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }

        intent.putExtra("userdata", new_user);
        intent.putExtra("bipmap_array", byteArray);
        startActivity(intent);
    }

    /**
     * this starts an intent to go to back the upload photo page by passing my photo in PNG
     * @param view view of input that i make the callback to if needed
     */
    public void gotoUpload(View view){
        Intent intent = new Intent(PhotoSelector.this, UploadPhoto.class);

        //if i selected a photo i pass the data, else it will be null
        if(photo != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }

        intent.putExtra("bipmap_array", byteArray);
        startActivity(intent);
    }
}
