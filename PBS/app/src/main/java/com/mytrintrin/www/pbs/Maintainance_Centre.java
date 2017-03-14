package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Maintainance_Centre extends AppCompatActivity {

    Toolbar Maintainencetoolbar;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    String loginuserid;

    Button AddCycle,MaintainedCycle,UnderMaintaince;

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintainance_centre);
        Maintainencetoolbar = (Toolbar) findViewById(R.id.maintainancetoolbar);
        Maintainencetoolbar.setTitle("Maintainance");
        Maintainencetoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(Maintainencetoolbar);

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id",null);

        AddCycle = (Button) findViewById(R.id.mcaddcycles);
        MaintainedCycle = (Button) findViewById(R.id.mcmaintanedcycles);
        UnderMaintaince = (Button) findViewById(R.id.mcundermaintainence);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            AddCycle.setVisibility(View.VISIBLE);
        } else {
            AddCycle.setVisibility(View.GONE);
        }


    }

    public void gotoadddcycle(View view)
    {
        startActivity(new Intent(Maintainance_Centre.this,Add_Cycle.class));
    }


    public void gotoundermaintainance(View view)
    {
        startActivity(new Intent(Maintainance_Centre.this,UnderMaintainance.class));
    }

    public void logoutfrommc(View view)
    {
        editor.putString("User-id","");
        editor.commit();
        finish();
        startActivity(new Intent(this,LoginNFC.class));
    }

}
