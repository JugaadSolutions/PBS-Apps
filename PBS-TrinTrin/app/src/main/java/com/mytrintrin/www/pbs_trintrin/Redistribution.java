package com.mytrintrin.www.pbs_trintrin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class Redistribution extends AppCompatActivity implements LocationListener,NavigationView.OnNavigationItemSelectedListener {

    Location location;
    LocationManager locationManager;

    double latitude,onstartlatitude;
    double longitude,onstartlongitude;

    SwitchCompat track;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, currentlat, currentlang, empoldpassword, empnewpassword, empconfirmpassword, fleetname,startkm,endkm,litres,fuelamount;

    Toolbar redistributiontoolbar;

    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    ArrayList<String> checkboxlist = new ArrayList<String>();
    LinearLayout Fleetnamelayout;

    SharedPreferences fleetpref,kmdetailspref;
    SharedPreferences.Editor fleeteditor,kmdetailseditor;

    ActionBarDrawerToggle mToogle;
    DrawerLayout RedistributionDrawer;
    Menu nav_menu;
    NavigationView Redistribution_navigation;

    EditText startrv,endrv,litresrv,amountrv;


    Spinner zonesspinner;
    ArrayList<JSONObject> Zone1ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone2ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone3ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone4ArrayList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Zone5ArrayList = new ArrayList<JSONObject>();
    LinearLayout Stationlayout;
    Animation startAnimation;
    SwipeRefreshLayout Zoneswipe;

    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redistribution);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();

        fleetpref = getApplicationContext().getSharedPreferences("fleetPref", MODE_PRIVATE);
        fleeteditor = fleetpref.edit();

        kmdetailspref = getApplicationContext().getSharedPreferences("kmdetailspref",MODE_PRIVATE);
        kmdetailseditor= kmdetailspref.edit();

        redistributiontoolbar = (Toolbar) findViewById(R.id.redistributiontoolbar);
        redistributiontoolbar.setTitle("Redistribution");
        setSupportActionBar(redistributiontoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getlocation();
        /*locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        }*/
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

        RedistributionDrawer = (DrawerLayout) findViewById(R.id.redistribution);
        mToogle = new ActionBarDrawerToggle(this, RedistributionDrawer, R.string.open, R.string.close);
        RedistributionDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        Redistribution_navigation = (NavigationView) findViewById(R.id.redistribution_navigationview);
        Redistribution_navigation.setNavigationItemSelectedListener(this);

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
        //fetchzonedetails();

    }

    private void getlocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            android.app.AlertDialog.Builder mAlertDialog = new android.app.AlertDialog.Builder(this);

            // Setting Dialog Title
            mAlertDialog.setTitle("Location not available, Open GPS?")
                    .setIcon(R.drawable.splashlogo)
                    .setMessage("Activate GPS to use location services?")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();

        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onstartlatitude = location.getLatitude();
            onstartlongitude = location.getLongitude();
            onLocationChanged(location);
        } else {
            GPSServices mGPSService = new GPSServices(this);
            if (mGPSService.isLocationAvailable == false) {
                Toast.makeText(this, "Your location is not available,please try again.", Toast.LENGTH_SHORT).show();
            }
        }
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
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
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
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
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
        new CountDownTimer(61000, 60000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Location previouslocation = new Location("");
                previouslocation.setLatitude(onstartlatitude);
                previouslocation.setLongitude(onstartlongitude);

                Location currentlocation = new Location("");
                currentlocation.setLatitude(latitude);
                currentlocation.setLongitude(longitude);

                float distance = previouslocation.distanceTo(currentlocation);
                if(distance>30)
                {
                    Toast.makeText(Redistribution.this, "Sending Location", Toast.LENGTH_SHORT).show();
                    sendtoserver();
                }

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
        Toast.makeText(this, " Sending Lat :"+currentlat+"Long :"+currentlang, Toast.LENGTH_SHORT).show();
        StringRequest sendlocation = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // Log.d("Response", response);
                Toast.makeText(Redistribution.this, "Location Reached to Server", Toast.LENGTH_SHORT).show();
                onstartlongitude = Double.parseDouble(currentlang);
                onstartlatitude = Double.parseDouble(currentlat);
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
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
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
               /* params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));*/
                params.put("latitude", currentlat);
                params.put("longitude", currentlang);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout_rv) {
            editor.putString("User-id", "");
            editor.putString("Role", "");
            editor.commit();
            startActivity(new Intent(this, Login.class));
            finish();
        }
        else if (item.getItemId()==R.id.checkin_rv)
        {
            Intent checkinrv = new Intent(this, Checkin_RV.class);
            checkinrv.putExtra("fleetname", fleetname);
            startActivity(checkinrv);
        }
        else if (item.getItemId()==R.id.checkout_rv)
        {
            Intent checkinrv = new Intent(this, Checkout_RV.class);
            checkinrv.putExtra("fleetname", fleetname);
            startActivity(checkinrv);
        }
        else if(item.getItemId()==R.id.vehiclepostion_rv)
        {
            startActivity(new Intent(this, Vehicle_rv.class));
        }
        else if(item.getItemId()==R.id.changefleet_rv)
        {
            getfleetnames();
        }
        else if(item.getItemId()==R.id.KMdetails_rv)
        {
            Toast.makeText(this, "KM Details", Toast.LENGTH_SHORT).show();
            showkmdetailsdialog();

        }
        else if (item.getItemId()==R.id.fueldetails_rv)
        {
            Toast.makeText(this, "Fuel Details", Toast.LENGTH_SHORT).show();
            showfueldetailsdialog();
        }
        else if(item.getItemId()==R.id.forcebicycle_rv)
        {
            startActivity(new Intent(this,ForceBicycle.class));
        }
        return false;
    }

    public void showfueldetailsdialog()
    {
        AlertDialog.Builder fueldialog = new AlertDialog.Builder(this);
        fueldialog.setTitle("Fuel Details");
        fueldialog.setIcon(R.drawable.splashlogo);
        LayoutInflater fuelinflate = LayoutInflater.from(this);
        View fuelview = fuelinflate.inflate(R.layout.fueldetails,null);
        litresrv = (EditText) fuelview.findViewById(R.id.litres_rv);
        amountrv = (EditText) fuelview.findViewById(R.id.amount_rv);
        fueldialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                litres = litresrv.getText().toString().trim();
                fuelamount = amountrv.getText().toString().trim();
                if(litres.equals("")||litres.equals(null))
                {
                    Toast.makeText(Redistribution.this, "Litres cannot be empty", Toast.LENGTH_SHORT).show();
                    showfueldetailsdialog();
                    return;
                }
                if(fuelamount.equals("")||fuelamount.equals(null))
                {
                    Toast.makeText(Redistribution.this, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
                    showfueldetailsdialog();
                    return;
                }
                if(fuelamount.length()<2)
                {
                    Toast.makeText(Redistribution.this, "Please enter correct amount", Toast.LENGTH_SHORT).show();
                    showfueldetailsdialog();
                    return;
                }
                else
                {
                    //sendfueldetailstoserver(litres,fuelamount);
                }

            }
        });
        fueldialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        fueldialog.setView(fuelview);
        fueldialog.setCancelable(false);
        fueldialog.show();

    }

    public void sendfueldetailstoserver(final String ltr, final String amt)
    {
        StringRequest fueldetailsrequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Redistribution.this, response, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Redistribution.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Redistribution.this);
                    builder.setIcon(R.drawable.splashlogo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
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
                params.put("fuellitres", ltr);
                params.put("fuelamount", amt);
                return params;
            }
        };
        fueldetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplication()).addtorequestqueue(fueldetailsrequest);
    }

    public void showkmdetailsdialog()
    {
        AlertDialog.Builder kmdialog = new AlertDialog.Builder(this);
        kmdialog.setTitle("KM Details");
        kmdialog.setIcon(R.drawable.splashlogo);
        LayoutInflater kminflate = LayoutInflater.from(this);
        View kmView = kminflate.inflate(R.layout.kmdetails,null);
        startrv = (EditText) kmView.findViewById(R.id.startingat_rv);
        endrv = (EditText) kmView.findViewById(R.id.endat_rv);
        if(kmdetailspref.contains("startkm"))
        {
            startkm = kmdetailspref.getString("startkm",null);
            startrv.setText(startkm);
        }
        kmdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startkm = startrv.getText().toString().trim();
                endkm = endrv.getText().toString().trim();
                if(startkm.equals("")||startkm.equals(null))
                {
                    Toast.makeText(Redistribution.this,"Start KM cannot be empty",Toast.LENGTH_LONG).show();
                    showkmdetailsdialog();
                    return;
                }
                if(startkm.length()<3)
                {
                    Toast.makeText(Redistribution.this,"Start KM cannot less than 3 digits",Toast.LENGTH_LONG).show();
                    showkmdetailsdialog();
                    return;
                }
                if(!startkm.equals("")&&!startkm.equals(null)&&(startkm.length()>3))
                {
                    kmdetailseditor.putString("startkm",startkm);
                    kmdetailseditor.commit();
                }
                if(!endkm.equals("")&&!endkm.equals(null)&&(endkm.length()>3))
                {
                    if((Integer.parseInt(endkm)-Integer.parseInt(startkm))>0) {
                        kmdetailseditor.putString("endkm", endkm);
                        kmdetailseditor.commit();
                        //sendkmdetailstoserver(startkm,endkm);
                    }
                    else
                    {
                        Toast.makeText(Redistribution.this, "End KM should be greater than Started KM", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        kmdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
            }
        });
        kmdialog.setView(kmView);
        kmdialog.setCancelable(false);
        kmdialog.show();
    }

    public void sendkmdetailstoserver(final String started,final  String ended)
    {
        Toast.makeText(this, started +"  "+ended, Toast.LENGTH_SHORT).show();
        StringRequest kmdetailsrequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Redistribution.this, response, Toast.LENGTH_SHORT).show();
                kmdetailseditor.putString("startkm","");
                kmdetailseditor.putString("endkm","");
                kmdetailseditor.commit();

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
                    Toast.makeText(Redistribution.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Redistribution.this);
                    builder.setIcon(R.drawable.splashlogo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
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
                params.put("startedat", started);
                params.put("endedat", ended);
                return params;
            }
        };
        kmdetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplication()).addtorequestqueue(kmdetailsrequest);

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
                    Toast.makeText(Redistribution.this, "Server Error", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Redistribution.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
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
                    Button stationname = new Button(Redistribution.this);
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
                    Button stationname = new Button(Redistribution.this);
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
                    Button stationname = new Button(Redistribution.this);
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
                    Button stationname = new Button(Redistribution.this);
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
                    Button stationname = new Button(Redistribution.this);
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

    public void fetchzonedetails() {
        new CountDownTimer(121000, 120000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(Redistribution.this, "Fetching zone details", Toast.LENGTH_SHORT).show();
                getalldockingstation();
            }

            @Override
            public void onFinish() {
                fetchzonedetails();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, 5, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

}
