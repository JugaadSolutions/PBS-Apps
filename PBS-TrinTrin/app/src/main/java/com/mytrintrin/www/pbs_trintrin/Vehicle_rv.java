package com.mytrintrin.www.pbs_trintrin;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Map;

public class Vehicle_rv extends AppCompatActivity {

    Toolbar VehicleRVToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;
    LinearLayout Cyclewithrvemp, Cyclewithrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_rv);
        VehicleRVToolbar = (Toolbar) findViewById(R.id.vehiclervtoolbar);
        VehicleRVToolbar.setTitle("Cycle");
        setSupportActionBar(VehicleRVToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
        Cyclewithrvemp = (LinearLayout) findViewById(R.id.cyclewithrvemplayout);
        Cyclewithrv = (LinearLayout) findViewById(R.id.cyclewithrvlayout);

        checkinternet();
        getcycleswithrvemp();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(Vehicle_rv.this);
            builder.setIcon(R.mipmap.logo);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/

    public void getcycleswithrvemp() {

        StringRequest cylewithrvemprequest = new StringRequest(Request.Method.GET, API.getrvempcycles + loginuserid + "/cyclestatus", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Vehicle_rv.this, response, Toast.LENGTH_SHORT).show();
                Log.d("Cycle response", response);
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    if(data.has("user")) {
                        JSONArray user = data.getJSONArray("user");
                        if (user.length() > 0) {
                            for (int i = 0; i < user.length(); i++) {
                                TextView cyclenouser = new TextView(Vehicle_rv.this);
                                cyclenouser.setText(user.get(i).toString());
                                cyclenouser.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                                Cyclewithrvemp.addView(cyclenouser);
                            }
                        } else {
                            TextView cyclenouser = new TextView(Vehicle_rv.this);
                            cyclenouser.setText("No Bicycle found with user");
                            cyclenouser.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                            Cyclewithrvemp.addView(cyclenouser);
                        }
                    }
                    if(data.has("rv")) {
                        JSONArray rv = data.getJSONArray("rv");
                        if (rv.length() > 0) {
                            for (int i = 0; i < rv.length(); i++) {
                                TextView cyclenorv = new TextView(Vehicle_rv.this);
                                cyclenorv.setText(rv.get(i).toString());
                                cyclenorv.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                                Cyclewithrv.addView(cyclenorv);
                            }
                        } else {
                            TextView cyclenorv = new TextView(Vehicle_rv.this);
                            cyclenorv.setText("No Bicycle found in rv");
                            cyclenorv.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                            Cyclewithrv.addView(cyclenorv);
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
                    Toast.makeText(Vehicle_rv.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Vehicle_rv.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Vehicle_rv.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Vehicle_rv.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Vehicle_rv.this);
                    builder.setIcon(R.mipmap.logo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinternet();
                        }
                    });
                    builder.show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Vehicle_rv.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Vehicle_rv.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Vehicle_rv.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        cylewithrvemprequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(cylewithrvemprequest);
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
