package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwr on 12/9/16.
 */

public class extra {


/* to create table
    TableLayout tl = (TableLayout) findViewById(R.id.membertable);
    TableRow tr = new TableRow(this);
    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    Button b = new Button(this);
    b.setText("Dynamic Button");
    b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    tr.addView(b);
    tr.setBackgroundResource(R.drawable.sf_gradient_03);
    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));*/







/* to create table format programitically

    TableLayout tv=(TableLayout) findViewById(R.id.membertable);
    tv.removeAllViewsInLayout();
    TableRow tr=new TableRow(Members.this);

    tr.setLayoutParams(new ViewGroup.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.WRAP_CONTENT));

    if(flag==1){

        TextView b3=new TextView(Members.this);
        b3.setText("column heading 1");
        b3.setTextColor(Color.BLUE);
        b3.setTextSize(15);
        tr.addView(b3);

        TextView b4=new TextView(Members.this);
        b4.setPadding(10, 0, 0, 0);
        b4.setTextSize(15);
        b4.setText("column heading 2");
        b4.setTextColor(Color.BLUE);
        tr.addView(b4);

        TextView b5=new TextView(Members.this);
        b5.setPadding(10, 0, 0, 0);
        b5.setText("column heading 3");
        b5.setTextColor(Color.BLUE);
        b5.setTextSize(15);
        tr.addView(b5);
        tv.addView(tr);

        final View vline = new View(Members.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        vline.setBackgroundColor(Color.BLUE);
        tv.addView(vline);
        flag=0;
    }

    else
    {
        TextView b=new TextView(Members.this);
        b.setText(name);
        b.setTextColor(Color.RED);
        b.setTextSize(15);
        tr.addView(b);

    }*/

/*tracker*/



    /*public class Redistribution extends AppCompatActivity {

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
                    Toast.makeText(com.mytrintrin.www.pbs.Redistribution.this,"Location Testing",Toast.LENGTH_SHORT).show();
                    getlocation();
                    Toast.makeText(com.mytrintrin.www.pbs.Redistribution.this,"Location is "+currentlocation,Toast.LENGTH_SHORT).show();

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

        *//*To get Location*//*
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
    *//*ends*//*

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




    */



    /*ends*/



}
