package com.mytrintrin.www.pbs_trintrin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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

public class Redistribution extends AppCompatActivity implements LocationListener {

    Location location;
    LocationManager locationManager;

    double latitude;
    double longitude;

    SwitchCompat track;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, currentlat, currentlang, empoldpassword, empnewpassword, empconfirmpassword, fleetname;

    Toolbar redistributiontoolbar;

    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    ArrayList<String> checkboxlist = new ArrayList<String>();
    LinearLayout Fleetnamelayout;

    SharedPreferences fleetpref;
    SharedPreferences.Editor fleeteditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redistribution);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();

        fleetpref = getApplicationContext().getSharedPreferences("fleetPref", MODE_PRIVATE);
        fleeteditor = fleetpref.edit();

        redistributiontoolbar = (Toolbar) findViewById(R.id.redistributiontoolbar);
        redistributiontoolbar.setTitle("Redistribution");
        setSupportActionBar(redistributiontoolbar);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        track = (SwitchCompat) findViewById(R.id.switchButton);
        track.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    ActionStartsHere();
                } else {
                    dontdoanything();
                }
            }
        });
        loginuserid = loginpref.getString("User-id", null);

       //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        Fleetnamelayout = new LinearLayout(Redistribution.this);
        if (fleetpref.contains("FleetName")) {
            fleetname= fleetpref.getString("FleetName", null);
            if (fleetname.equals("") || fleetname.equals(null)) {
                getfleetnames();
            }
            else
            {
                fleetname = fleetpref.getString("FleetName",null);
            }
        }
        else
        {
            getfleetnames();
        }
        checkinternet();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Redistribution.this);
            builder.setIcon(R.drawable.splashlogo);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.redistribution_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_rv) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            editor.putString("User-id", "");
            editor.putString("Role", "");
            editor.commit();
            startActivity(new Intent(this, Login.class));
            finish();
        }
        else if(item.getItemId()==R.id.changefleet_rv)
        {
            getfleetnames();
        }
        return super.onOptionsItemSelected(item);
        }


    private void getfleetnames() {
        checkinternet();
        StringRequest fleetrequest = new StringRequest(Request.Method.GET, API.fleetidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject fleetresponsefronserver = new JSONObject(response);
                    JSONArray fleetdataarray = fleetresponsefronserver.getJSONArray("data");
                    Fleetnamelayout.removeAllViews();
                    for (int i = 0; i < fleetdataarray.length(); i++) {
                        JSONObject getid = fleetdataarray.getJSONObject(i);
                        JSONObject stationid = getid.getJSONObject("StationId");
                        String name = stationid.getString("name");
                        checkboxlist.add(name);
                        AppCompatCheckBox fleetnamecb = new AppCompatCheckBox(Redistribution.this);
                        fleetnamecb.setText(checkboxlist.get(i));
                        fleetnamecb.setTag(checkboxlist.get(i));
                        fleetnamecb.setOnCheckedChangeListener(handleCheck(fleetnamecb));
                        Fleetnamelayout.addView(fleetnamecb);
                    }
                    AlertDialog.Builder cb = new AlertDialog.Builder(Redistribution.this);
                    cb.setIcon(R.mipmap.logo);
                    cb.setTitle("Fleet");
                    cb.setView(Fleetnamelayout);
                    cb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            fleeteditor.putString("FleetName", fleetname);
                            fleeteditor.commit();
                            Toast.makeText(Redistribution.this, fleetname, Toast.LENGTH_SHORT).show();
                        }
                    });
                    cb.show();
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
                    Toast.makeText(Redistribution.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Redistribution.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Redistribution.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Redistribution.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Redistribution.this);
                    builder.setIcon(R.drawable.splashlogo);
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
                    Toast.makeText(Redistribution.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Redistribution.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Redistribution.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        fleetrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(fleetrequest);
    }

    private void dontdoanything() {
        Toast.makeText(getApplicationContext(), "Tracker off", Toast.LENGTH_SHORT).show();
    }

    public void ActionStartsHere() {
        againStartGPSAndSendFile();

    }

    public void againStartGPSAndSendFile() {
        new CountDownTimer(31000, 30000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(Redistribution.this, "Sending Location", Toast.LENGTH_SHORT).show();
                sendtoserver();
            }

            @Override
            public void onFinish() {
                if (track.isChecked()) {
                    ActionStartsHere();
                } else {
                    dontdoanything();
                }
            }
        }.start();
    }

    private CompoundButton.OnCheckedChangeListener handleCheck(final CheckBox chk) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (!isChecked) {
                    Toast.makeText(getApplicationContext(), "You unchecked " + chk.getTag(), Toast.LENGTH_LONG).show();
                    fleetname = "";
                } else {
                    Toast.makeText(getApplicationContext(), "You checked " + chk.getTag(), Toast.LENGTH_LONG).show();
                    fleetname = chk.getTag().toString();
                }
            }
        };
    }

    private void sendtoserver() {

        String url = API.redistribution + loginuserid + "/location";
        Log.d("Location url", url);
        StringRequest sendlocation = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }

                if (error instanceof ServerError) {
                    Toast.makeText(Redistribution.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Redistribution.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Redistribution.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Redistribution.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Redistribution.this);
                    builder.setIcon(R.drawable.splashlogo);
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
                    Toast.makeText(Redistribution.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Redistribution.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Redistribution.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                Log.d("lat & lang", "lat" + latitude + " long" + longitude);
                return params;
            }
        };
        sendlocation.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(sendlocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        currentlat = String.valueOf(latitude);
        currentlang = String.valueOf(longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void logoutfromrv(View view) {
        editor.putString("User-id", "");
        editor.putString("Role", "");
        editor.commit();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void checkoutfromrv(View view) {
        Intent checkoutrv = new Intent(this, Checkout_RV.class);
        checkoutrv.putExtra("fleetname", fleetname);
        startActivity(checkoutrv);
    }

    public void checkintorv(View view) {
        Intent checkinrv = new Intent(this, Checkin_RV.class);
        checkinrv.putExtra("fleetname", fleetname);
        startActivity(checkinrv);
    }

    public void vehicleposition(View view) {
        startActivity(new Intent(this, Vehicle_rv.class));
    }

    public void gotozonealert(View view)
    {
        startActivity(new Intent(this,ZoneAlert.class));
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
