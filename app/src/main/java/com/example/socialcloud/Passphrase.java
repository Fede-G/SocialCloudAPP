package com.example.socialcloud;

import android.content.Intent;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialcloud.Model.User;
import com.example.socialcloud.Task.UnifiedRegistration_task;

import java.util.HashMap;

/**
 * Class that manages the Passphrase tab and the various components
 * Implements: the Navigation Drawer, the delegate for UnifiedRegistration_task
 */
public class Passphrase extends AppCompatActivity implements UnifiedRegistration_task.AsyncResponse{

    //UI variables
    private EditText passphraseV;
    private EditText passphrase_reV;
    private View ViewPP;
    private User new_user = new User();
    private Bundle bundle;

    UnifiedRegistration_task UR_t;

    /**
     * Method called when you create the activity_passphrase
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passphrase);
        // Set up the login form.
        passphraseV = (EditText) findViewById(R.id.pp_plaintext);
        passphrase_reV = (EditText) findViewById(R.id.ppre_plaintext);

        //Get the data that i receive from  the intent call
        if(getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            new_user = (User) getIntent().getSerializableExtra("userdata");
        }

        //setup the onclick button action
        Button send_button = (Button) findViewById(R.id.senddata_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

        ViewPP = findViewById(R.id.passphrase_form);
    }


    /**
     * At the end of the task go to the login screen, this is done by the delegator
     * @param output
     */
    @Override
    public void processFinish(HashMap<String, Object> output){
        //Here i COULD read the user and perform an automatic login, but i'll let the user do it

        final Intent mainIntent = new Intent(Passphrase.this, Login.class);
        Passphrase.this.startActivity(mainIntent);
        Passphrase.this.finish();
    }

    /**
     * Trying to test the passphrase to send after all data for making a new registration
     */
    private void sendData() {
        /*if (SDT != null) {
            return;
        }*/

        // Reset errors.
        passphraseV.setError(null);
        passphrase_reV.setError(null);

        // Store values at the time of the login attempt.
        String passphrase = passphraseV.getText().toString();
        String passphrase_re = passphrase_reV.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passphrase) && !isPasswordValid(passphrase_re)) {
            passphraseV.setError(getString(R.string.error_invalid_password));
            focusView = passphraseV;
            cancel = true;
        }

        //If everything is alright then register the new user
        if (cancel) {
            focusView.requestFocus();
        } else {

            //here i put the params needed for the task and start it (check UnifiedRegistration_task for params)
            HashMap<String, Object> params = new HashMap<>();
            params.put("user", new_user);
            params.put("passphrase", passphrase);

            UR_t = new UnifiedRegistration_task(this);
            UR_t.execute(params);
        }
    }

    /**
     * Method to check if the passphrase inserted is long enough
     * @param passphrase    passphrase inserted
     * @return              if it's long enough true, else false
     */
    private boolean isPasswordValid(String passphrase) {
        return passphrase.length() > 5;
    }
}

