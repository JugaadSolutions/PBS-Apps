package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends AppCompatActivity {

    private Toolbar SplashToolbar;
    ImageView Logo;
    MediaPlayer Logosound;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        SplashToolbar = (Toolbar) findViewById(R.id.splashtoolbar);
        SplashToolbar.setTitle("Trin Trin");
        setSupportActionBar(SplashToolbar);
        Logo = (ImageView) findViewById(R.id.logoview);
        Logosound = MediaPlayer.create(this, R.raw.bell);
        Logosound.start();
        Animation logoanimation = AnimationUtils.loadAnimation(this, R.anim.vibrates);
        Logo.startAnimation(logoanimation);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);

        Thread timer = new Thread() {
            public void run() {
                try {
                    mProgressDialog.dismiss();
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //startActivity(new Intent(Splash.this,WelcomeActivity.class));
                    if (loginpref.contains("User-id")) {
                        if (loginuserid.equals("") || loginuserid.equals(null)) {
                            startActivity(new Intent(Splash.this, GetStarted.class));
                            finish();
                        } else {
                            startActivity(new Intent(Splash.this, MyAccount.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(Splash.this, WelcomeActivity.class));
                    }
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logosound.release();
        finish();
    }
}
