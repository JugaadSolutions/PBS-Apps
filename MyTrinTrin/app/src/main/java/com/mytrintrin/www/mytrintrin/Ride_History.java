package com.mytrintrin.www.mytrintrin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Ride_History extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private Toolbar RideHistoryToolbar;
    Location currentlocation;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, StationName, StationId, PortNumber;

    CardView Ridecardview;
    Context context;
    LinearLayout RideHistory, RideDetailsLayout;
    LinearLayout.LayoutParams ridecardparams, ridedetailsparams;
    TextView RideDate, RideFromStation, RideToStation, RideCheckinTime, RideFare, RideDuration, RideBalance;
    private ProgressDialog mProgressDialog;

    private LocationManager locationManager;
    private String provider;
    SharedPreferences dockingstationpref;
    SharedPreferences.Editor dockingstationeditor;
    JSONArray stationdata;
    EditText Cyclenumber;
    Button startride;
    LinearLayout Currentrideinfolayout;
    private static int MY_REQUEST_CODE = 2;
    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();
    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();
    public static ArrayList<String> NearestStationNameArrayList = new ArrayList<String>();
    public static ArrayList<Location> dockingstationlocation = new ArrayList<>();
    JSONArray StationArray, PortArray;
    JSONObject Stationobject;
    Spinner Stations;
    double doclatitude, docllongitude;
    public ArrayAdapter<String> Stationadapter;
    GoogleMap mmap;
    final JSONObject loc = new JSONObject();
    String prime = "",opentransactionflag;
    String serverresponselocation, message,Cyclenum;
    TextView latitudelongitude, fromcurrentride;
    JSONObject Checkoutobject = new JSONObject();
    JSONObject Checkinobject = new JSONObject();
    SharedPreferences checkoutpref;
    SharedPreferences.Editor checkouteditor;
    TextView checkouttime_tv,cyclenum_tv;

    LocationRequest locationRequest;

    Date Checkoutdate,Checkindate,Ridecheckout,Ridecheckin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_history);
        RideHistoryToolbar = (Toolbar) findViewById(R.id.ridehistorytoolbar);
        RideHistoryToolbar.setTitle("Ride History");
        setSupportActionBar(RideHistoryToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
        context = getApplicationContext();
        RideHistory = (LinearLayout) findViewById(R.id.ridehistorylayout);
        currentlocation = new Location("");
        dockingstationpref = getApplicationContext().getSharedPreferences("dockingpref", MODE_PRIVATE);
        dockingstationeditor = dockingstationpref.edit();
        stationdata = new JSONArray();
        currentlocation = new Location("");
        startride = (Button) findViewById(R.id.startbicycleride);
        Currentrideinfolayout = (LinearLayout) findViewById(R.id.currentrideinfolayout);
        latitudelongitude = (TextView) findViewById(R.id.latnlng);
        fromcurrentride = (TextView) findViewById(R.id.from_currentride);

        checkoutpref = getApplicationContext().getSharedPreferences("Checkoutpref", MODE_PRIVATE);
        checkouteditor = checkoutpref.edit();

        checkouttime_tv = (TextView) findViewById(R.id.at_currentride);
        cyclenum_tv = (TextView) findViewById(R.id.cyclenumber_currentride);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.dockingstationmap);
        mapFragment.getMapAsync(Ride_History.this);

        checkinternet();
        checkpreviouscheckout();
        getalldockingstation();
        getridedetails();
        getlocation();
    }

    private void getlocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        /*GPSTracker gps = new GPSTracker(this);
        if(!gps.canGetLocation()){
            gps.showSettingsAlert();
        }*/

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Location location = locationManager.getLastKnownLocation(provider);
        Location location =locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


        if (location != null) {
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
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Ride_History.this);
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


    public void checkpreviouscheckout()
    {
        if(checkoutpref.contains("checkoutlocation"))
        {
            String previouslocation = checkoutpref.getString("checkoutlocation","");
            if(previouslocation.equals(""))
            {
                startride.setVisibility(View.VISIBLE);
            }
            else
            {
                startride.setVisibility(View.GONE);
                Currentrideinfolayout.setVisibility(View.VISIBLE);
                fromcurrentride.setText(previouslocation);
                fromcurrentride.setText("From :"+checkoutpref.getString("checkoutlocation",""));
                cyclenum_tv.setText("Cycle :"+checkoutpref.getString("cyclenumber","").substring(10));
                checkouttime_tv.setText("At :"+checkoutpref.getString("checkouttime",""));

            }
        }
        else
        {
            startride.setVisibility(View.VISIBLE);
        }
    }

    public void getridedetails() {

        checkinternet();
        mProgressDialog = new ProgressDialog(Ride_History.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest getridedetailsrequest = new StringRequest(Request.Method.GET, API.ridehistory + loginuserid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mProgressDialog.dismiss();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    int datalength = data.length();
                    if (datalength > 10) {
                        datalength = 10;
                    }
                    RideHistory.removeAllViews();
                    for (int i = 0; i < datalength; i++) {
                        JSONObject ridedata = data.getJSONObject(i);
                        String Checkout = ridedata.getString("checkOutTime");
                        String FromStation = ridedata.getString("FromStation");
                        FromStation = FromStation.split("-")[0];
                        String ToStation = ridedata.getString("ToStation");
                        ToStation = ToStation.split("-")[0];
                        String CheckinTime = ridedata.getString("checkInTime");
                        String Fare = ridedata.getString("fare");
                        String Duration = ridedata.getString("duration");
                        if(Duration.length()>3)
                        {
                            Duration = Duration.substring(0, 3);
                        }
                        String Balance = ridedata.getString("balance");

                        Ridecardview = new CardView(context);
                        ridecardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ridecardparams.setMargins(0, 0, 2, 0);
                        ridecardparams.bottomMargin = 5;
                        Ridecardview.setLayoutParams(ridecardparams);
                        Ridecardview.setRadius(15);
                        Ridecardview.setPadding(25, 25, 25, 25);
                        Ridecardview.setCardBackgroundColor(Color.parseColor("#009746"));
                        Ridecardview.setVisibility(View.VISIBLE);
                        Ridecardview.setMaxCardElevation(30);

                        RideDetailsLayout = new LinearLayout(context);
                        ridedetailsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        RideDetailsLayout.setOrientation(LinearLayout.VERTICAL);
                        RideDetailsLayout.setGravity(Gravity.END);
                        RideDetailsLayout.setPadding(25, 25, 25, 25);
                        RideDetailsLayout.setLayoutParams(ridedetailsparams);

                        RideDate = new TextView(context);

                        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            Ridecheckout = readDate.parse(Checkout);
                            Ridecheckin = readDate.parse(CheckinTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                        Checkout = writeDate.format(Ridecheckout);
                        CheckinTime = writeDate.format(Ridecheckin);

                        RideDate.setText("Check out : " + Checkout);
                        RideDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideDate.setTextColor(Color.WHITE);

                        RideFromStation = new TextView(context);
                        RideFromStation.setText("From : " + FromStation + "," + Checkout);
                        RideFromStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideFromStation.setTextColor(Color.WHITE);

                        RideCheckinTime = new TextView(context);
                        RideCheckinTime.setText("Check in : " + CheckinTime);
                        RideCheckinTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideCheckinTime.setTextColor(Color.WHITE);

                        RideToStation = new TextView(context);
                        RideToStation.setText("To : " + ToStation + "," + CheckinTime);
                        RideToStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideToStation.setTextColor(Color.WHITE);

                        RideFare = new TextView(context);
                        RideFare.setText("Fare : " + Fare + "/-" + "," + "Duration : " + Duration + "mins," + "Balance : " + Balance + "/-");
                        RideFare.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideFare.setTextColor(Color.WHITE);

                        RideDuration = new TextView(context);
                        RideDuration.setText("Duration : " + Duration);
                        RideDuration.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideDuration.setTextColor(Color.WHITE);

                        RideBalance = new TextView(context);
                        RideBalance.setText("Balance : " + Balance + "/-");
                        RideBalance.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideBalance.setTextColor(Color.WHITE);

                        RideDetailsLayout.addView(RideFromStation);
                        RideDetailsLayout.addView(RideToStation);
                        RideDetailsLayout.addView(RideFare);
                        // RideDetailsLayout.addView(RideBalance);
                        //RideDetailsLayout.addView(RideDate);
                        // RideDetailsLayout.addView(RideDuration);
                        //RideDetailsLayout.addView(RideCheckinTime);

                        Ridecardview.addView(RideDetailsLayout);
                        RideHistory.addView(Ridecardview);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Ride_History.this);
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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                }  else {
                    Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        getridedetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(getridedetailsrequest);
    }

    public void getalldockingstation() {
        StringRequest alldockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mProgressDialog.dismiss();
                    JSONObject responsefromserver = new JSONObject(response);
                    stationdata = responsefromserver.getJSONArray("data");
                    StationNameArrayList.clear();
                    StationIDArrayList.clear();
                    NearestStationNameArrayList.clear();
                    StationArray = new JSONArray();
                    PortArray = new JSONArray();
                    dockingstationlocation.clear();
                    for (int i = 0; i < stationdata.length(); i++) {
                        JSONObject dscoordinates = stationdata.getJSONObject(i);
                        JSONObject coordinates = dscoordinates.getJSONObject("gpsCoordinates");
                        String lat = coordinates.getString("latitude");
                        String lang = coordinates.getString("longitude");
                        doclatitude = Double.parseDouble(lat);
                        docllongitude = Double.parseDouble(lang);
                        Location dockinglocation = new Location("");
                        dockinglocation.setLatitude(doclatitude);
                        dockinglocation.setLongitude(docllongitude);
                        dockingstationlocation.add(dockinglocation);
                        float distance = currentlocation.distanceTo(dockinglocation);
                        if (distance < 40) {
                            String id = dscoordinates.getString("StationID");
                            final String Stationname = dscoordinates.getString("name");
                            JSONArray ports = dscoordinates.getJSONArray("portIds");
                            StationIDArrayList.add(id);
                            StationNameArrayList.add(Stationname);
                            StationArray.put(dscoordinates);
                            PortArray.put(ports);
                        } else if (distance < 80) {
                            String Neareststationname = dscoordinates.getString("name");
                            NearestStationNameArrayList.add(Neareststationname);
                        }

                        final String Stationname = dscoordinates.getString("name");
                        final int Capacity = dscoordinates.getInt("bicycleCapacity");
                        final int bicyclecount = dscoordinates.getInt("bicycleCount");
                        final int emptyport = Capacity - bicyclecount;
                        mmap.addMarker(new MarkerOptions()
                                .position(new LatLng(doclatitude, docllongitude))
                                .title(Stationname).snippet("Cycle:" + bicyclecount + "\n" + "Empty Port:" + emptyport)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bike_black_24dp)));
                        mmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                LinearLayout info = new LinearLayout(Ride_History.this);
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(Ride_History.this);
                                title.setTextColor(Color.BLACK);
                                title.setGravity(Gravity.LEFT);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(Ride_History.this);
                                snippet.setTextColor(Color.GRAY);
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });
                    }
                    //setnearestsation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                }  else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Ride_History.this);

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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(alldockingstationrequest);
    }

    public void calculatedistance() {
        try {
            StationNameArrayList.clear();
            StationIDArrayList.clear();
            NearestStationNameArrayList.clear();
            StationArray = new JSONArray();
            PortArray = new JSONArray();
            dockingstationlocation.clear();
            for (int i = 0; i < stationdata.length(); i++) {
                JSONObject dscoordinates = stationdata.getJSONObject(i);
                JSONObject coordinates = dscoordinates.getJSONObject("gpsCoordinates");
                String lat = coordinates.getString("latitude");
                String lang = coordinates.getString("longitude");
                doclatitude = Double.parseDouble(lat);
                docllongitude = Double.parseDouble(lang);
                Location dockinglocation = new Location("");
                dockinglocation.setLatitude(doclatitude);
                dockinglocation.setLongitude(docllongitude);
                dockingstationlocation.add(dockinglocation);
                float distance = currentlocation.distanceTo(dockinglocation);
                if (distance < 40) {
                    String id = dscoordinates.getString("StationID");
                    final String Stationname = dscoordinates.getString("name");
                    JSONArray ports = dscoordinates.getJSONArray("portIds");
                    StationIDArrayList.add(id);
                    StationNameArrayList.add(Stationname);
                    StationArray.put(dscoordinates);
                    PortArray.put(ports);
                } else if (distance < 80) {
                    String Neareststationname = dscoordinates.getString("name");
                    NearestStationNameArrayList.add(Neareststationname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkprimeandstation() {
        calculatedistance();
        if (StationIDArrayList.size() > 0) {
            try {
                loc.put("latitude", String.valueOf(currentlocation.getLatitude()));
                loc.put("longitude", String.valueOf(currentlocation.getLongitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest checkprimerequest = new JsonObjectRequest(Request.Method.POST, "http://43.251.80.79:13095/api/users/check/prime/" + loginuserid, loc, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject responsefromserver = response;
                        JSONObject data = responsefromserver.getJSONObject("data");
                        if (data.length() == 0) {
                            message = responsefromserver.getString("message");
                            showalertdialog(message);
                            if(message.equals("Please return cycle to unlock"))
                            {
                                if(!checkoutpref.getString("checkoutlocation","").equals(""))
                                {
                                    fromcurrentride.setText("From :"+checkoutpref.getString("checkoutlocation",""));
                                    cyclenum_tv.setText("Cycle :"+checkoutpref.getString("cyclenumber","").substring(10));
                                    checkouttime_tv.setText("At :"+checkoutpref.getString("checkouttime",""));
                                }
                                else {
                                    startride.setVisibility(View.GONE);
                                    Currentrideinfolayout.setVisibility(View.VISIBLE);
                                }
                            }

                            return;
                        }
                        else if(data.has("vehicle"))
                        {
                            String station = data.getString("station");
                            String checkouttime = data.getString("checkOutTime");
                            String vehicleId = data.getString("vehicleId");


                            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                            try {
                                Checkoutdate = readDate.parse(checkouttime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                            writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                            checkouttime = writeDate.format(Checkoutdate);

                            String bicyclename = data.getString("vehicle");
                            if(bicyclename.length()>7)
                            {
                                bicyclename= bicyclename.substring(10);
                            }
                            fromcurrentride.setText("From :"+ station);
                            //  checkouttime_tv.setText("At:"+outFormat.format(c.getTime()));
                            checkouttime_tv.setText("At:"+checkouttime);
                            cyclenum_tv.setText("Cycle:"+bicyclename);
                            Currentrideinfolayout.setVisibility(View.VISIBLE);
                            checkouteditor.putString("checkoutlocation",station);
                            checkouteditor.putString("cyclenumber","MYS-Fleet-"+bicyclename);
                            checkouteditor.putString("vehicleId",vehicleId);
                            checkouteditor.putString("checkouttime",checkouttime);
                            checkouteditor.commit();
                            startride.setVisibility(View.GONE);
                            prime = "true";
                            opentransactionflag = "true";
                        }
                        else {
                            prime = data.getString("prime");
                            serverresponselocation = data.getString("location");
                            opentransactionflag = "false";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setnearestsation();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        parseVolleyError(error);
                        return;
                    }
                    if (error instanceof ServerError) {
                        Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    }else if (error instanceof NoConnectionError) {
                        Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    }
                    else if (error instanceof NetworkError) {
                        Toast.makeText(Ride_History.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                                Ride_History.this);

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
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    }  else {
                        Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            })
            {
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    String json;
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        try {
                            json = new String(volleyError.networkResponse.data,
                                    HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                        } catch (UnsupportedEncodingException e) {
                            return new VolleyError(e.getMessage());
                        }
                        return new VolleyError(json);
                    }
                    return volleyError;
                }

                @Override
                public void deliverError(VolleyError error) {
                    super.deliverError(error);
                }
            };

            checkprimerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkprimerequest);
        } else {
            message = "Sorry you are not near to any Trin Trin Hubs.";
            showalertdialog(message);
            return;
        }
    }

    public void setnearestsation() {
        if (prime.equals("true")&&(opentransactionflag.equals("false"))) {
            if (StationNameArrayList.size() > 0) {
                AlertDialog.Builder checkoutdialog = new AlertDialog.Builder(this);
                checkoutdialog.setIcon(R.drawable.splashlogo);
                checkoutdialog.setTitle("Start Ride");
                LayoutInflater rangeinflate = LayoutInflater.from(this);
                View rangeView = rangeinflate.inflate(R.layout.startride, null);
                Cyclenumber = (EditText) rangeView.findViewById(R.id.cyclenumber_unlock);
                Stations = (Spinner) rangeView.findViewById(R.id.stationscheckout);
                checkoutdialog.setView(rangeView);
                Stationadapter = new ArrayAdapter<String>(Ride_History.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
                Stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Stations.setAdapter(Stationadapter);
                if ((Stations.getSelectedItem().toString().equals(serverresponselocation))) {
                    Stations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            StationName = Stations.getSelectedItem().toString();
                            StationId = StationIDArrayList.get(i);
                            try {
                                Stationobject = StationArray.getJSONObject(i);
                                JSONArray ports = PortArray.getJSONArray(i);
                                PortIDArrayList.clear();
                                PortNameArrayList.clear();
                                int length = ports.length();
                                for (int j = 0; j < ports.length(); j++) {
                                    JSONObject port = ports.getJSONObject(j);
                                    JSONObject dockports = port.getJSONObject("dockingPortId");
                                    String portid = dockports.getString("PortID");
                                    String portname = dockports.getString("Name");
                                    PortIDArrayList.add(portid);
                                    PortNameArrayList.add(portname);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else {
                    android.support.v7.app.AlertDialog.Builder Nostation = new android.support.v7.app.AlertDialog.Builder(this);
                    Nostation.setIcon(R.drawable.splashlogo);
                    Nostation.setTitle("Nearest Hubs");
                    Nostation.setMessage("Sorry,You not near to any of the TrinTrin Hubs");
                    Nostation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    Nostation.setCancelable(false);
                    Nostation.show();
                }
                checkoutdialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Cyclenum = Cyclenumber.getText().toString().trim();
                        if (Cyclenum.equals("") || Cyclenum.equals(null) || (Cyclenum.length()<2)) {
                            Toast.makeText(Ride_History.this, "Invalid Cycle Number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        IntentIntegrator integrator = new IntentIntegrator(Ride_History.this);
                        integrator.setPrompt("Scan Port QRCode");
                        integrator.setOrientationLocked(true);
                        integrator.initiateScan();
                    }
                });
                checkoutdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Ride_History.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                checkoutdialog.setCancelable(false);
                checkoutdialog.show();
            } else {
                android.support.v7.app.AlertDialog.Builder Nostation = new android.support.v7.app.AlertDialog.Builder(this);
                Nostation.setIcon(R.drawable.splashlogo);
                Nostation.setTitle("Prime");
                Nostation.setMessage(message);
                Nostation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                Nostation.setCancelable(false);
                Nostation.show();
            }
        }
        else  if (prime.equals("true")&&(opentransactionflag.equals("true")))
        {
            AlertDialog.Builder returncycledialog = new AlertDialog.Builder(this);
            returncycledialog.setIcon(R.drawable.splashlogo);
            returncycledialog.setTitle("Open Transaction");
            returncycledialog.setMessage("Please ret" +
                    "urn the cycle.");
            returncycledialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            returncycledialog.setCancelable(false);
            returncycledialog.show();
        }

        else {
            android.support.v7.app.AlertDialog.Builder NoPrime = new android.support.v7.app.AlertDialog.Builder(this);
            NoPrime.setIcon(R.drawable.splashlogo);
            NoPrime.setTitle("Trin Trin");
            NoPrime.setMessage("Sorry,You're not a prime member.");
            NoPrime.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            NoPrime.setCancelable(false);
            NoPrime.show();
        }
    }

    public void showcheckoutdialog(View view) {
        checkprimeandstation();
    }

    public void showreturnqr(View view) {
        calculatedistance();
        if (StationIDArrayList.size() > 0) {
            IntentIntegrator integrator = new IntentIntegrator(Ride_History.this);
            integrator.setPrompt("Scan Port QRCode");
            integrator.setOrientationLocked(true);
            Intent intent = integrator.createScanIntent();
            startActivityForResult(intent, MY_REQUEST_CODE);
        } else {
            message = "Sorry you are not near to any Trin Trin Hubs.";
            showalertdialog(message);
            return;
        }
    }


    public void returnwithoutqr(View view)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest returncyclerequest = new StringRequest(Request.Method.POST, "http://43.251.80.79:13095/api/transactions/return/app", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String message = responsefromserver.getString("message");
                    if(message.equals("No open checkouts found"))
                    {
                        checkouteditor.putString("checkoutlocation","");
                        checkouteditor.putString("cyclenumber","");
                        checkouteditor.putString("checkouttime","");
                        checkouteditor.commit();
                        startride.setVisibility(View.VISIBLE);
                        Currentrideinfolayout.setVisibility(View.GONE);
                        //showalertdialog("Transaction Completed.");
                        AlertDialog.Builder RideDetailsBuilder = new AlertDialog.Builder(Ride_History.this);
                        RideDetailsBuilder.setIcon(R.drawable.splashlogo);
                        RideDetailsBuilder.setTitle("Ride Info");
                        RideDetailsBuilder.setMessage("Transaction Completed."+"\n"+"Thanks for riding.Happy Trin Trining.:)");
                        RideDetailsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        RideDetailsBuilder.setCancelable(true);
                        RideDetailsBuilder.show();
                    }
                    if(message.equals("Please try after some time."))
                    {
                        showalertdialog(message);
                    }
                    if(message.equals("Return Initiated"))
                    {
                        String checkoutfrom = responsefromserver.getString("fromPort");
                        String checkouttime = responsefromserver.getString("checkOutTime");
                        String checkinto = responsefromserver.getString("toPort");
                        String checkintime = responsefromserver.getString("checkInTime");

                        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            Checkoutdate = readDate.parse(checkouttime);
                            Checkindate = readDate.parse(checkintime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                        checkouttime = writeDate.format(Checkoutdate);
                        checkintime = writeDate.format(Checkindate);



                        String duration = responsefromserver.getString("duration");
                        if(duration.length()>3)
                        {
                            duration = duration.substring(0,3);
                        }
                        String fare = responsefromserver.getString("creditsUsed");
                        String balance = responsefromserver.getString("creditBalance");

                        checkouteditor.putString("checkoutlocation","");
                        checkouteditor.putString("cyclenumber","");
                        checkouteditor.putString("checkouttime","");
                        checkouteditor.commit();
                        startride.setVisibility(View.VISIBLE);
                        Currentrideinfolayout.setVisibility(View.GONE);
                        AlertDialog.Builder RideDetailsBuilder = new AlertDialog.Builder(Ride_History.this);
                        RideDetailsBuilder.setIcon(R.drawable.splashlogo);
                        RideDetailsBuilder.setTitle("Ride Info");
                        RideDetailsBuilder.setMessage("From :" + checkoutfrom + "\n" + "At :"+checkouttime + "\n" + "To :"+checkinto + "\n" + "At :"+checkintime + "\n" +"Duration :"+duration+"mins"+"\n"+"Fare :"+fare+"\n"+"Balance :"+balance+"\n"+" Thanks for riding.Happy Trin Trining.:)");
                        RideDetailsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        RideDetailsBuilder.setCancelable(true);
                        RideDetailsBuilder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                }
                else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Ride_History.this);

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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                }  else {
                    Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("user", loginuserid);
                params.put("vehicleId", checkoutpref.getString("vehicleId",""));
                return params;
            }

        };
        returncyclerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(returncyclerequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
        if (result != null && requestCode == 49374) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                PortNumber = result.getContents();
                checkoutcycle();
            }
        } else if (result != null && requestCode == 2) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                PortNumber = result.getContents();
                checkincycle();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            AlertDialog.Builder RideBuilder = new AlertDialog.Builder(Ride_History.this);
            RideBuilder.setIcon(R.drawable.splashlogo);
            RideBuilder.setTitle("Rides");
            RideBuilder.setMessage("No Rides Found");
            RideBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            RideBuilder.setCancelable(false);
            RideBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }


    public void checkoutcycle() {
        try {
            loc.put("latitude", String.valueOf(currentlocation.getLatitude()));
            loc.put("longitude", String.valueOf(currentlocation.getLongitude()));
            Checkoutobject.put("portId", PortNumber);
            Checkoutobject.put("dsCoordinates", loc);
            Checkoutobject.put("vehicleId","MYS-Fleet-"+Cyclenum);
            Checkoutobject.put("user",loginuserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest checkoutrequest = new JsonObjectRequest(Request.Method.POST, "http://43.251.80.79:13095/api/transactions/unlock/app", Checkoutobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject responsefromserver = response;
                try {
                    JSONObject data = responsefromserver.getJSONObject("data");
                    if (data.length() == 0) {
                        String message = responsefromserver.getString("message");
                        showalertdialog(message);
                        startride.setVisibility(View.VISIBLE);
                    } else {
                        startride.setVisibility(View.GONE);
                        String locationfromserver = data.getString("location");
                        String checkouttime = data.getString("checkOutTime");
                        String vehicleId = data.getString("vehicleId");


                        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            Checkoutdate = readDate.parse(checkouttime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                        checkouttime = writeDate.format(Checkoutdate);

                        String bicyclename = data.getString("vehicleNumber");
                        bicyclename = bicyclename.substring(10);
                        fromcurrentride.setText("From :"+ locationfromserver);
                        //  checkouttime_tv.setText("At:"+outFormat.format(c.getTime()));
                        checkouttime_tv.setText("At:"+checkouttime);
                        cyclenum_tv.setText("Cycle:"+bicyclename);
                        Currentrideinfolayout.setVisibility(View.VISIBLE);
                        checkouteditor.putString("checkoutlocation",locationfromserver);
                        checkouteditor.putString("cyclenumber","MYS-Fleet-"+bicyclename);
                        checkouteditor.putString("vehicleId",vehicleId);
                        checkouteditor.putString("checkouttime",checkouttime);
                        checkouteditor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                }else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Ride_History.this);
                    builder.setIcon(R.mipmap.logo);
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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                }  else {
                    Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        });
        checkoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkoutrequest);

    }

    public void checkincycle() {
        try {
            loc.put("latitude", String.valueOf(currentlocation.getLatitude()));
            loc.put("longitude", String.valueOf(currentlocation.getLongitude()));
            Checkinobject.put("portId", PortNumber);
            Checkinobject.put("dsCoordinates", loc);
            Checkinobject.put("user", loginuserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest checkinrequest = new JsonObjectRequest(Request.Method.POST, "http://43.251.80.79:13095/api/transactions/return/app", Checkinobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject responsefromserver = response;
                try {
                    JSONObject data = responsefromserver.getJSONObject("data");
                    if (data.length() == 0) {
                        String message = responsefromserver.getString("message");
                        showalertdialog(message);
                        Currentrideinfolayout.setVisibility(View.VISIBLE);
                    } else {
                        String checkoutfrom = data.getString("fromPort");
                        String checkouttime = data.getString("checkOutTime");
                        String checkinto = data.getString("toPort");
                        String checkintime = data.getString("checkInTime");

                        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            Checkoutdate = readDate.parse(checkouttime);
                            Checkindate = readDate.parse(checkintime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                        checkouttime = writeDate.format(Checkoutdate);
                        checkintime = writeDate.format(Checkindate);



                        String duration = data.getString("duration");
                        if(duration.length()>3)
                        {
                            duration = duration.substring(0,3);
                        }
                        String fare = data.getString("creditsUsed");
                        String balance = data.getString("creditBalance");

                        checkouteditor.putString("checkoutlocation","");
                        checkouteditor.putString("cyclenumber","");
                        checkouteditor.putString("checkouttime","");
                        checkouteditor.commit();
                        startride.setVisibility(View.VISIBLE);
                        Currentrideinfolayout.setVisibility(View.GONE);
                        AlertDialog.Builder RideDetailsBuilder = new AlertDialog.Builder(Ride_History.this);
                        RideDetailsBuilder.setIcon(R.drawable.splashlogo);
                        RideDetailsBuilder.setTitle("Ride Info");
                        RideDetailsBuilder.setMessage("From :" + checkoutfrom + "\n" + "At :"+checkouttime + "\n" + "To :"+checkinto + "\n" + "At :"+checkintime + "\n" +"Duration :"+duration+"mins"+"\n"+"Fare :"+fare+"\n"+"Balance :"+balance+"\n"+" Thanks for riding.Happy Trin Trining.:)");
                        RideDetailsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        RideDetailsBuilder.setCancelable(true);
                        RideDetailsBuilder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Ride_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Ride_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Ride_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Ride_History.this);
                    builder.setIcon(R.mipmap.logo);
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
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Ride_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Ride_History.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Ride_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        });
        checkinrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkinrequest);

    }

    public void showalertdialog(String msg) {
        AlertDialog.Builder errorbuilder = new AlertDialog.Builder(
                Ride_History.this);
        errorbuilder.setIcon(R.mipmap.logo);
        errorbuilder.setTitle("Prime");
        errorbuilder.setMessage(msg);
        errorbuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        errorbuilder.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        currentlocation.setLatitude(location.getLatitude());
        currentlocation.setLongitude(location.getLongitude());
        latitudelongitude.setText("Lat : " + location.getLatitude() + "Long : " + location.getLongitude());
        calculatedistance();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraPosition currentloc = CameraPosition.builder()
                .target(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentloc), 2000, null);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mmap = googleMap;
    }

}
