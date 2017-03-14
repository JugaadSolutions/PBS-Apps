package com.mytrintrin.www.pbs_trintrin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class GetStarted_MC extends AppCompatActivity {

    Toolbar GetstartedMCToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started_mc);
        GetstartedMCToolbar = (Toolbar) findViewById(R.id.getstartedmctoolbar);
        GetstartedMCToolbar.setTitle("Trin Trin");
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
    }

    public void gotomaintenance(View view)
    {
        startActivity(new Intent(this,Maintenance.class));
    }

    public void gotorepair(View view)
    {
        startActivity(new Intent(this,Repair.class));
    }

    public void gotoaddbicycle(View view)
    {
        startActivity(new Intent(this,Addcycle.class));
    }

    public void logoutfrommc(View view)
    {
        editor.putString("User-id", "");
        editor.putString("Role","");
        editor.commit();
        startActivity(new Intent(GetStarted_MC.this, Login.class));
        finish();
    }
}
