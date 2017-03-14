package com.mytrintrin.www.portal;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {


    private Toolbar SplashToolbar;
    ImageView Logo;
    MediaPlayer Logosound;

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
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(Splash.this, WelcomeActivity.class));
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
