package com.mytrintrin.www.pbs_kiosk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    ImageView SplashLogoView;
    private Toolbar SplashToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        SplashToolbar = (Toolbar) findViewById(R.id.splashtoolbar);
        SplashToolbar.setTitle("Trin Trin");
        setSupportActionBar(SplashToolbar);
        SplashLogoView = (ImageView) findViewById(R.id.splashlogoview);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.vibrates);
        SplashLogoView.setAnimation(animation);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    startActivity(new Intent(Splash.this, KioskDisplay.class));
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
