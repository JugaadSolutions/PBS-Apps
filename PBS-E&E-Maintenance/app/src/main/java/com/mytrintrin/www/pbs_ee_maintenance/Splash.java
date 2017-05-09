package com.mytrintrin.www.pbs_ee_maintenance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {


    ImageView SplashLogoView;
    private Toolbar SplashToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid,role;
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
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(loginpref.contains("User-id"))
                    {
                        loginuserid = loginpref.getString("User-id", null);
                        role = loginpref.getString("Role", null);
                        if(!loginuserid.equals(null)&&role.equals("maintenancecentre-employee"))
                        {
                            startActivity(new Intent(Splash.this, ElectronicsMaintenance.class));
                        }
                        else
                        {
                            startActivity(new Intent(Splash.this, Login.class));
                        }
                    }
                    else
                    {
                        startActivity(new Intent(Splash.this, Login.class));
                    }
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
