package com.mytrintrin.www.pbs_trintrin;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZoneAlert extends AppCompatActivity {

    Toolbar Zonetoolbar;
    Spinner zonesspinner;
    ArrayList<JSONObject> Zone1ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone2ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone3ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone4ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone5ArrayList = new ArrayList<JSONObject>();
    LinearLayout Stationlayout;
    Animation startAnimation;
    SwipeRefreshLayout Zoneswipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_alert);
        Zonetoolbar = (Toolbar) findViewById(R.id.zonealerttoolbar);
        Zonetoolbar.setTitle("Zone Alert");
        setSupportActionBar(Zonetoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zonesspinner = (Spinner) findViewById(R.id.zonespinner);
        Stationlayout = (LinearLayout) findViewById(R.id.zonestationnamelayout);
        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking);
        getalldockingstation();
        zonesspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getzonedetails(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Zoneswipe = (SwipeRefreshLayout) findViewById(R.id.zonealertswipe);
        Zoneswipe.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        Zoneswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Zoneswipe.setRefreshing(false);
                        zonesspinner.setSelection(0);
                        getalldockingstation();
                    }
                },3000);
            }
        });
    }

    public void getalldockingstation() {
        StringRequest alldockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    Zone1ArrayList.clear();
                    Zone2ArrayList.clear();
                    Zone3ArrayList.clear();
                    Zone4ArrayList.clear();
                    Zone5ArrayList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject dockstationobject = data.getJSONObject(i);
                        String zoneid = dockstationobject.getString("zoneId");
                        if (zoneid.equals("1")) {
                            Zone1ArrayList.add(dockstationobject);
                        } else if (zoneid.equals("2")) {
                            Zone2ArrayList.add(dockstationobject);
                        } else if (zoneid.equals("3")) {
                            Zone3ArrayList.add(dockstationobject);
                        } else if (zoneid.equals("4")) {
                            Zone4ArrayList.add(dockstationobject);
                        } else if (zoneid.equals("5")) {
                            Zone5ArrayList.add(dockstationobject);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }

                if (error instanceof ServerError) {
                    Toast.makeText(ZoneAlert.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ZoneAlert.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ZoneAlert.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ZoneAlert.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ZoneAlert.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ZoneAlert.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ZoneAlert.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        alldockingstationrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(alldockingstationrequest);
    }

    public void getzonedetails(int zone) {
        if (zone == 1) {
            Stationlayout.removeAllViews();
            for (int i = 0; i < Zone1ArrayList.size(); i++) {
                try {
                    JSONObject zone1obj = Zone1ArrayList.get(i);
                    String sname = zone1obj.getString("name");
                    int capacity = zone1obj.getInt("bicycleCapacity");
                    int count = zone1obj.getInt("bicycleCount");
                    int maximum = zone1obj.getInt("maxAlert");
                    int minimum = zone1obj.getInt("minAlert");
                    Button stationname = new Button(ZoneAlert.this);
                    LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonparams.setMargins(0,0,0,5);
                    stationname.setLayoutParams(buttonparams);
                    stationname.setText(sname);
                    stationname.setTextColor(Color.WHITE);
                    int check = capacity-count;
                    if(count>=maximum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_red);
                        /*startAnimation.cancel();
                        startAnimation.reset();*/
                        startAnimation.setRepeatCount(Animation.INFINITE);
                        stationname.startAnimation(startAnimation);
                        startAnimation.setRepeatCount(0);

                    }
                    else if(count<=minimum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_orange);
                        /*startAnimation.cancel();
                        stationname.clearAnimation();
                        startAnimation.reset();*/
                        startAnimation.setRepeatCount(Animation.INFINITE);
                        stationname.startAnimation(startAnimation);
                        startAnimation.setRepeatCount(0);
                    }
                    else
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner);
                    }
                    Stationlayout.addView(stationname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        else if (zone == 2) {
            Stationlayout.removeAllViews();
            for (int i = 0; i < Zone2ArrayList.size(); i++) {
                try {
                    JSONObject zone2obj = Zone2ArrayList.get(i);
                    String sname = zone2obj.getString("name");
                    int capacity = zone2obj.getInt("bicycleCapacity");
                    int count = zone2obj.getInt("bicycleCount");
                    int maximum = zone2obj.getInt("maxAlert");
                    int minimum = zone2obj.getInt("minAlert");
                    Button stationname = new Button(ZoneAlert.this);
                    LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonparams.setMargins(0,0,0,5);
                    stationname.setLayoutParams(buttonparams);
                    stationname.setText(sname);
                    stationname.setTextColor(Color.WHITE);
                    if(count>=maximum)
                    {
                      stationname.setBackgroundResource(R.drawable.roundcorner_red);
                        /*startAnimation.cancel();
                        startAnimation.reset();*/
                        startAnimation.setRepeatCount(Animation.INFINITE);
                        stationname.startAnimation(startAnimation);
                        startAnimation.setRepeatCount(0);
                    }
                    else if(count<=minimum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_orange);
                        /*startAnimation.cancel();
                        stationname.clearAnimation();
                        startAnimation.reset();*/
                        startAnimation.setRepeatCount(Animation.INFINITE);
                        stationname.startAnimation(startAnimation);
                        startAnimation.setRepeatCount(0);
                    }
                    else
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner);
                    }
                    Stationlayout.addView(stationname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        else if (zone == 3) {
            Stationlayout.removeAllViews();
            for (int i = 0; i < Zone3ArrayList.size(); i++) {
                try {
                    JSONObject zone3obj = Zone3ArrayList.get(i);
                    String sname = zone3obj.getString("name");
                    int capacity = zone3obj.getInt("bicycleCapacity");
                    int count = zone3obj.getInt("bicycleCount");
                    int maximum = zone3obj.getInt("maxAlert");
                    int minimum = zone3obj.getInt("minAlert");
                    Button stationname = new Button(ZoneAlert.this);
                    LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonparams.setMargins(0,0,0,5);
                    stationname.setLayoutParams(buttonparams);
                    stationname.setText(sname);
                    stationname.setTextColor(Color.WHITE);
                    if(count>=maximum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_red);
                       /* Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking);
                        stationname.startAnimation(startAnimation);*/

                    }
                    else if(count<=minimum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_orange);
                    }
                    else
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner);
                    }
                    Stationlayout.addView(stationname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        else if (zone == 4) {
            Stationlayout.removeAllViews();
            for (int i = 0; i < Zone4ArrayList.size(); i++) {
                try {
                    JSONObject zone4obj = Zone4ArrayList.get(i);
                    String sname = zone4obj.getString("name");
                    int capacity = zone4obj.getInt("bicycleCapacity");
                    int count = zone4obj.getInt("bicycleCount");
                    int maximum = zone4obj.getInt("maxAlert");
                    int minimum = zone4obj.getInt("minAlert");
                    Button stationname = new Button(ZoneAlert.this);
                    LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonparams.setMargins(0,0,0,5);
                    stationname.setLayoutParams(buttonparams);
                    stationname.setText(sname);
                    stationname.setTextColor(Color.WHITE);
                    if(count>=maximum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_red);
                       /* Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking);
                        stationname.startAnimation(startAnimation);*/

                    }
                    else if(count<=minimum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_orange);
                    }
                    else
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner);
                    }
                    Stationlayout.addView(stationname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        else if (zone == 5) {
            Stationlayout.removeAllViews();
            for (int i = 0; i < Zone5ArrayList.size(); i++) {
                try {
                    JSONObject zone5obj = Zone5ArrayList.get(i);
                    String sname = zone5obj.getString("name");
                    int capacity = zone5obj.getInt("bicycleCapacity");
                    int count = zone5obj.getInt("bicycleCount");
                    int maximum = zone5obj.getInt("maxAlert");
                    int minimum = zone5obj.getInt("minAlert");
                    Button stationname = new Button(ZoneAlert.this);
                    LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonparams.setMargins(0,0,0,5);
                    stationname.setLayoutParams(buttonparams);
                    stationname.setText(sname);
                    stationname.setTextColor(Color.WHITE);
                    if(count>=maximum)
                    {

                        stationname.setBackgroundResource(R.drawable.roundcorner_red);
                        /*Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking);
                        stationname.startAnimation(startAnimation);*/

                    }
                    else if(count<=minimum)
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner_orange);
                    }
                    else
                    {
                        stationname.setBackgroundResource(R.drawable.roundcorner);
                    }
                    Stationlayout.addView(stationname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        else {
            Stationlayout.removeAllViews();
            return;
        }
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
