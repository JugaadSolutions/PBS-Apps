package com.mytrintrin.www.mytrintrin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GetStarted extends AppCompatActivity {

    Toolbar getStartedToolbar;
    Button Login,Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        getStartedToolbar = (Toolbar) findViewById(R.id.Getstartedtoolbar);
        getStartedToolbar.setTitle("Trin Trin");
        setSupportActionBar(getStartedToolbar);
        checkinternet();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(GetStarted.this);
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
    /*ends*/

    //to go to login
    public void gotologin(View view)
    {
        checkinternet();
        startActivity(new Intent(GetStarted.this,Login.class));
    }

    //to go to register
    public  void gotoregister(View view)
    {
        checkinternet();
        startActivity(new Intent(GetStarted.this,Register.class));
    }

    //to go to slider
    public void gotoslider(View view)
    {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        prefManager.setFirstTimeLaunch(true);
        startActivity(new Intent(GetStarted.this, WelcomeActivity.class));
        finish();
    }
}
