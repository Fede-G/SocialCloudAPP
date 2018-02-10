package com.example.socialcloud;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.socialcloud.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Class that manages the Registration of a new user, but without calling any type of Task that will communicate with the server
 */
public class Register extends AppCompatActivity {

    //UI variables
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private EditText rDateView;

    private EditText rEmailView;
    private EditText rPassView;
    private EditText rPassReView;
    private EditText rNameView;
    private EditText rSurnameView;
    private EditText rCityView;

    private Bundle bundle;
    private User new_user = new User();

    /**
     * Method called when you create the activity_register
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addDatePicker();

        rEmailView = (EditText) findViewById(R.id.reg_email);
        rPassView = (EditText) findViewById(R.id.r_password);
        rNameView = (EditText) findViewById(R.id.r_name);
        rSurnameView = (EditText) findViewById(R.id.r_surname);
        rCityView = (EditText) findViewById(R.id.r_city);

        rPassReView = (EditText) findViewById(R.id.r_passwordre);

        //If i receive a message in this activity then i'm returning from the photoselector intent
        if(getIntent().getExtras() != null){
            bundle = getIntent().getExtras();

            //Inform the user using an alertdialog
            AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Image imported!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            byte[] image = bundle.getByteArray("bipmap_array");
            User old_user = (User) bundle.getSerializable("userdata");

            //setback all previous data that was insert
            if(image != null){
                new_user.setPhoto(image);
            }
            rEmailView.setText(old_user.getEmail());
            rPassView.setText(old_user.getPw());
            rNameView.setText(old_user.getFirstname());
            rSurnameView.setText(old_user.getLastname());
            rCityView.setText(old_user.getCity());
            rDateView.setText(old_user.getBirth_day());
        }

        //if i want to register, click the register button and i'll go forth to the passphrase
        Button rRegisterButton = (Button) findViewById(R.id.email_register_button);
        rRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        //if i want to grab an image, i start the intent for photopicker
        Button rImagePickerButton = (Button) findViewById(R.id.photoselector_register_button);
        rImagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAndSend();
            }
        });
    }

    /**
     * Here i pick the date from the viewtext, that calls a datesetlisternet
     */
    private void addDatePicker() {
        myCalendar = Calendar.getInstance();

        rDateView = (EditText) findViewById(R.id.reg_date);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        rDateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        Register.this,
                        date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    /**
     * update the label with the date that is been picked
     */
    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        rDateView.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * Here i pick the data and save it so i can get it back after the intent for picking the photo expires
     */
    private void pickAndSend(){
        /*if (mPhotoTask != null){
            return;
        }*/

        // Store values at the time of the attempt.
        String email = rEmailView.getText().toString();
        String password = rPassView.getText().toString();
        String name = rNameView.getText().toString();
        String surname = rSurnameView.getText().toString();
        String city = rCityView.getText().toString();
        String date = rDateView.getText().toString();

        new_user = new User(name, surname, email, date, city, password,null);
        gotoPhotoselector(getCurrentFocus());
        //mPhotoTask = new Register.PhotoPickerTask(new_user);
        //mPhotoTask.execute((Void) null);
    }

    /**
     * If i want to go on and register myself to the system then i have to send the new user to pick a new password
     */
    private void attemptRegister() {

        /*
        if (mAuthTask != null){
            return;
        }
        */

        // Reset errors.
        rEmailView.setError(null);
        rPassView.setError(null);
        rPassReView.setError(null);
        rNameView.setError(null);
        rSurnameView.setError(null);
        rCityView.setError(null);
        rDateView.setError(null);

        // Store values at the time of the attempt
        String email = rEmailView.getText().toString();
        String password = rPassView.getText().toString();
        String repassword = rPassReView.getText().toString();
        String name = rNameView.getText().toString();
        String surname = rSurnameView.getText().toString();
        String city = rCityView.getText().toString();
        String date = rDateView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check if the password is empty
        if (TextUtils.isEmpty(password) && !isPasswordValid(password, repassword)) {
            rPassReView.setError(getString(R.string.error_invalid_password));
            focusView = rPassReView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            rEmailView.setError(getString(R.string.error_field_required));
            focusView = rEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            rEmailView.setError(getString(R.string.error_invalid_email));
            focusView = rEmailView;
            cancel = true;
        }

        //Check if name and surname are empty, plus dates and city are valid
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || !isPersonalDataValid(name, surname, date, city)) {
            rNameView.setError(getString(R.string.utilsnotright));
            focusView = rPassReView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //here i create a new user ans save it
            new_user.setFirstname(name);
            new_user.setLastname(surname);
            new_user.setEmail(email);
            new_user.setBirth_day(date);
            new_user.setCity(city);
            new_user.setPw(password);
            gotoPassphrase(getCurrentFocus());
        }
    }

    /**
     * Checker for the password if it's equal to is reconfirm and it' long enough
     * @param password      password from the input
     * @param repassword    repassword from the input
     * @return              true if it's >4 and both are equals, else false
     */
    private boolean isPasswordValid(String password, String repassword) {
        if(!password.equals(repassword))
            return false;
        else
            return password.length() > 4;
    }

    /**
     * Check if the email is a valid address
     * @param email email string
     * @return      true if it's a valid email, else false
     */
    private boolean isEmailValid(String email){
        InternetAddress emailAddr = null;
        try {
            emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Checker if the data is in the right format (e.g. no numbers in name) using regular expressions
     * @param name      name of input
     * @param surname   surname of input
     * @param date      date of input
     * @param city      city of input
     * @return          if RegEx is respected true, else false, with an option to check for city if it's empty
     */
    private boolean isPersonalDataValid(String name, String surname, String date, String city) {
        if (city.equals("")) {
            return !(name.matches(".*\\d+.*") || surname.matches(".*\\d+.*"));
        }
        return !(name.matches(".*\\d+.*") || surname.matches(".*\\d+.*") || city.matches(".*\\d+.*"));
    }

    /**
     * Starting the intent to go to the passphrase input insertion
     * @param view  caller of the view
     */
    public void gotoPassphrase(View view){
        Intent intent = new Intent(Register.this, Passphrase.class);
        //Solo se è completo
        intent.putExtra("classcaller", Register.class.toString());
        intent.putExtra("userdata", new_user);
        startActivity(intent);
    }

    /**
     * Starting the intent to go to the photo selector to pick/snap an image
     * @param view  caller of the view
     */
    public void gotoPhotoselector(View view){
        Intent intent = new Intent(Register.this, PhotoSelector.class);

        intent.putExtra("classcaller", Register.class.toString());
        intent.putExtra("userdata", new_user);
        startActivity(intent);
    }
}

