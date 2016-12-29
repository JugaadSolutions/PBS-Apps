package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    ImageView logoview;
    MediaPlayer logosound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
                    startActivity(new Intent(Splash.this,LoginNFC.class));
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
