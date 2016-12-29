package com.mytrintrin.www.pbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Redistribution extends AppCompatActivity {

    Location currentlocation;
    double latitude;
    double longitude;


    SwitchCompat track;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;

    Toolbar redistributiontoolbar;

    NfcAdapter nfcAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redistribution);

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id",null);

        redistributiontoolbar = (Toolbar) findViewById(R.id.redistributionToolbar);
        redistributiontoolbar.setTitle("Redistribution");
        setSupportActionBar(redistributiontoolbar);

        track = (SwitchCompat) findViewById(R.id.switchButton);
        track.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if(isChecked)
                {
                    ActionStartsHere();
                }
                else {
                    dontdoanything();
                }
            }
        });


    }






    private void dontdoanything() {
        Toast.makeText(getApplicationContext(),"Tracker off",Toast.LENGTH_SHORT).show();
    }

    public void ActionStartsHere()
    {
        againStartGPSAndSendFile();

    }

    public void againStartGPSAndSendFile()
    {
        new CountDownTimer(31000,30000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                Toast.makeText(Redistribution.this,"Location Testing",Toast.LENGTH_SHORT).show();
                getlocation();
                Toast.makeText(Redistribution.this,"Location is "+currentlocation,Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFinish()
            {
                if(track.isChecked()) {
                    ActionStartsHere();
                }
                else
                {
                    dontdoanything();
                }
            }

        }.start();
    }

    private void sendtoserver() {

        String url = API.redistribution+loginuserid+"/location";
        Log.d("Location url",url);

        StringRequest sendlocation = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(sendlocation);
    }

    /*To get Location*/
    private void getlocation() {

        GPSServices mGPSService = new GPSServices(this);
        mGPSService.getLocation();

        if (mGPSService.isLocationAvailable == false) {
            Toast.makeText(this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            currentlocation = new Location("");
            currentlocation.setLatitude(0);
            currentlocation.setLongitude(0);
            //return;
        } else {
            latitude = mGPSService.getLatitude();
            longitude = mGPSService.getLongitude();
            currentlocation = new Location("");
            currentlocation.setLatitude(latitude);
            currentlocation.setLongitude(longitude);
            Log.d("Current location", String.valueOf(currentlocation));


        }

        mGPSService.closeGPS();
        sendtoserver();

    }
    /*ends*/

    public void logoutfromrv(View view)
    {
        editor.putString("User-id","");
        editor.commit();
        finish();
        startActivity(new Intent(this,LoginNFC.class));
    }



    public void checkoutfromrv(View view)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            startActivity(new Intent(this,Checkoutmanually.class));

        } else {
            startActivity(new Intent(this,Checkoutnfc.class));
        }

    }
}
