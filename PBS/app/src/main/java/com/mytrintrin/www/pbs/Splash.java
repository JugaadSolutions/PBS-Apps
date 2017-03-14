package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    ImageView logoview;
    MediaPlayer logosound;

    public static SharedPreferences loginnfcid;
    public static SharedPreferences.Editor loginnfceditor;
    String LoginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginnfcid = getSharedPreferences("LoginPref", MODE_PRIVATE);
        loginnfceditor = loginnfcid.edit();
        LoginId = loginnfcid.getString("User-id", null);

        logoview= (ImageView) findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotates);
        logoview.setAnimation(animation);
        logosound=MediaPlayer.create(this,R.raw.bell);
        logosound.start();
        Thread timer = new Thread()

        {
            public void run()
            {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(new Intent(Splash.this,Login.class));
                    /*if (loginnfcid.contains("User-id")) {
                        if (LoginId.equals("") || LoginId.equals(null)) {
                            startActivity(new Intent(Splash.this, LoginNFC.class));
                            finish();
                        } else {
                            startActivity(new Intent(Splash.this, GetStarted.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(Splash.this, LoginNFC.class));
                    }*/
                }
            }

        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logosound.release();
        finish();
    }
}
