package com.mytrintrin.www.mytrintrin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MyAccount extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    Toolbar MyaccountToolbar;
    ActionBarDrawerToggle mToogle;
    DrawerLayout MyaccountDrawer;
    private static int RESULT_LOAD_IMG = 1;
    String profilePicpath = "", loginuserid, username, usermail, userbalance;
    ImageView Profilepic;
    NavigationView NV_Myaccount;
    Location currentlocation;
    double currentlatitude, doclatitude, reglatitude, reglongitude;
    double currentlongitude, docllongitude;
    GoogleMap mmap;
    ArrayList<String> Stationnamearray = new ArrayList<>();
    int i;
    public static final int RequestPermissionCode = 1;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    TextView UserName, UserMail, UserBalance, MarqueeBalance, MarqueeValidity, LatestRides;
    Menu nav_Menu;

    private static final int ProfilePicRequest = 101;
    Bitmap profilepic;
    URL profilieurl;

    CardView Ridecardview;
    Context context;
    LinearLayout RideHistory, RideDetailsLayout;
    LinearLayout.LayoutParams ridecardparams, ridedetailsparams;
    TextView RideDate, RideFromStation, RideToStation, RideCheckinTime, RideFare, RideDuration;

    JSONObject memberdetailsobject;
    String updatename, updatelastname, updatemailid, updatephone, userid;
    ArrayList<Location> alldockinglocation = new ArrayList<>();
    ArrayList<String> allstationname = new ArrayList<>();
    ArrayList<Marker> allstationmarker = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        MyaccountToolbar = (Toolbar) findViewById(R.id.myaccount_action);
        MyaccountToolbar.setTitle("My Account");
        setSupportActionBar(MyaccountToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyaccountDrawer = (DrawerLayout) findViewById(R.id.myaccount_drawer);
        mToogle = new ActionBarDrawerToggle(this, MyaccountDrawer, R.string.open, R.string.close);
        MyaccountDrawer.addDrawerListener(mToogle);
        mToogle.syncState();

        NV_Myaccount = (NavigationView) findViewById(R.id.navigationview_myaccount);
        View profileview = NV_Myaccount.inflateHeaderView(R.layout.myaccount_header);
        View Balanceview = MyaccountToolbar.findViewById(R.id.myaccount_action);
        nav_Menu = NV_Myaccount.getMenu();
        Profilepic = (ImageView) profileview.findViewById(R.id.userprofilepic);
        UserName = (TextView) profileview.findViewById(R.id.username);
        UserMail = (TextView) profileview.findViewById(R.id.usermailid);
        // UserBalance = (TextView) Balanceview.findViewById(R.id.balance_myaccount_action);
        MarqueeBalance = (TextView) findViewById(R.id.MarqueeText);
        MarqueeValidity = (TextView) findViewById(R.id.validity);
        LatestRides = (TextView) findViewById(R.id.latestrideinmyaccount);

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyAccount.this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview_myaccount);
        navigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();
        RideHistory = (LinearLayout) findViewById(R.id.ridesinmyaacount);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        checkinternet();
        getlocation();
        getalldockingstations();
        getallregistrationcentre();
        getmemberdetails();
        getridedetails();
        onpermision();

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MyAccount.this);
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
    /*ends*/


    /*Requesting for permissions*/
    public void onpermision() {
        if (checkPermission()) {
            Log.d("permissions", "Granted");
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MyAccount.this, new String[]
                {
                        CAMERA,
                        ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WritePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && LocationPermission && WritePermission) {
                        Log.d("permissions", "Granted");
                    } else {
                        android.app.AlertDialog.Builder permissionbuilder = new android.app.AlertDialog.Builder(this);
                        permissionbuilder.setIcon(R.drawable.trintrinlogo);
                        permissionbuilder.setMessage("Request for permission?");
                        permissionbuilder.setTitle("Permissions");
                        permissionbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(MyAccount.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            }
                        });
                        permissionbuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        permissionbuilder.show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    /*Requesting permission ends*/

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraPosition currentloc = CameraPosition.builder()
                .target(new LatLng(currentlatitude, currentlongitude))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentloc), 2000, null);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mmap = googleMap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        } else {
            currentlatitude = mGPSService.getLatitude();
            currentlongitude = mGPSService.getLongitude();
            currentlocation = new Location("");
            currentlocation.setLatitude(currentlatitude);
            currentlocation.setLongitude(currentlongitude);
            Log.d("Current location", String.valueOf(currentlocation));
            //Toast.makeText(this, String.valueOf(currentlocation), Toast.LENGTH_SHORT).show();
        }
        mGPSService.closeGPS();
    }
    /*ends*/

    public void choosepicturefrom(View view) throws IOException {

        ImageView image = new ImageView(this);
        image.setPadding(16, 16, 16, 16);
        onpermision();
        if (profilieurl.equals("") || profilieurl.equals(null)) {
            image.setImageResource(R.drawable.trintrinlogo);
        } else {
            image.setImageBitmap(BitmapFactory.decodeStream((InputStream) profilieurl.getContent()));
        }

        AlertDialog.Builder Profilepicbuilder = new AlertDialog.Builder(this);
        Profilepicbuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                AlertDialog.Builder changepicbuilder = new AlertDialog.Builder(MyAccount.this);
                changepicbuilder.setIcon(R.drawable.trintrinlogo);
                changepicbuilder.setTitle("Change Profile Picture");
                final String[] items = new String[]{"From Camera", "From Gallery"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyAccount.this, android.R.layout.select_dialog_item, items);
                changepicbuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, ProfilePicRequest);
                        }
                        if (i == 1) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                            Toast.makeText(MyAccount.this, "Choose Pic", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                changepicbuilder.show();
            }
        });
        Profilepicbuilder.setView(image);
        Profilepicbuilder.show().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == ProfilePicRequest && resultCode == Activity.RESULT_OK) {
                profilepic = (Bitmap) data.getExtras().get("data");
                Profilepic.setImageBitmap(profilepic);
                profilePicpath = getStringImage(profilepic);
                updateprofilepicinserver(profilepic);
            }
            /*After picking from gallery*/
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
                if (null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    profilePicpath = cursor.getString(columnIndex);
                    cursor.close();
                    Profilepic.setImageBitmap(BitmapFactory.decodeFile(profilePicpath));
                    Bitmap bitmap = BitmapFactory.decodeFile(profilePicpath);
                    updateprofilepicinserver(bitmap);
                    getmemberdetails();
                } else {
                    Toast.makeText(this, "You haven't picked any pic",
                            Toast.LENGTH_LONG).show();
                }
            }
            /*Ends*/
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void updateprofilepicinserver(final Bitmap profilepath) {

        checkinternet();
        try {
            memberdetailsobject = new JSONObject();
            memberdetailsobject.put("Name", updatename);
            memberdetailsobject.put("lastName", updatelastname);
            memberdetailsobject.put("sex", "male");
            memberdetailsobject.put("email", updatemailid);
            memberdetailsobject.put("countryCode", "91");
            memberdetailsobject.put("_id", userid);
            JSONObject resultprofilepic = new JSONObject();
            resultprofilepic.put("result", getStringImage(profilepath));
            memberdetailsobject.put("profilePic", resultprofilepic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updatepicrequest = new JsonObjectRequest(Request.Method.PUT, API.updatepic + loginuserid, memberdetailsobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MyAccount.this, "Profile Pic updated successfully", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(MyAccount.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MyAccount.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MyAccount.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MyAccount.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MyAccount.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MyAccount.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MyAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(updatepicrequest);
    }


    /*to convert image to string format*/
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
/*ends*/

    public void getalldockingstations() {
        StringRequest alldockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    for (i = 0; i < data.length(); i++) {
                        JSONObject dscoordinates = data.getJSONObject(i);
                        JSONObject coordinates = dscoordinates.getJSONObject("gpsCoordinates");
                        String lat = coordinates.getString("latitude");
                        String lang = coordinates.getString("longitude");
                        final String Stationname = dscoordinates.getString("name");
                        Stationnamearray.add(i, Stationname);
                        final int Capacity = dscoordinates.getInt("bicycleCapacity");
                        final int bicyclecount = dscoordinates.getInt("bicycleCount");
                        final int emptyport =Capacity-bicyclecount;
                        doclatitude = Double.parseDouble(lat);
                        docllongitude = Double.parseDouble(lang);
                        Location dockinglocation = new Location("");
                        dockinglocation.setLatitude(doclatitude);
                        dockinglocation.setLongitude(docllongitude);
                        alldockinglocation.add(dockinglocation);
                        allstationname.add(Stationname);
                        mmap.addMarker(new MarkerOptions()
                                .position(new LatLng(doclatitude, docllongitude))
                                .title(Stationname).snippet("Cycle:" + bicyclecount+"\n"+"Empty Port:"+emptyport)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bike_black_24dp)));
                        mmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                LinearLayout info = new LinearLayout(MyAccount.this);
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(MyAccount.this);
                                title.setTextColor(Color.BLACK);
                                title.setGravity(Gravity.CENTER);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(MyAccount.this);
                                snippet.setTextColor(Color.GRAY);
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });
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
                    Toast.makeText(MyAccount.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MyAccount.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MyAccount.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MyAccount.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MyAccount.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MyAccount.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MyAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void getallregistrationcentre() {
        StringRequest allregistrationcentrerequest = new StringRequest(Request.Method.GET, API.allregistrationcentre, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject registrationdetails = data.getJSONObject(i);
                        String regname = registrationdetails.getString("name");
                        String reglocation = registrationdetails.getString("location");
                        JSONObject regcoordinates = registrationdetails.getJSONObject("gpsCoordinates");
                        String reglat = regcoordinates.getString("latitude");
                        String reglong = regcoordinates.getString("longitude");
                        reglatitude = Double.parseDouble(reglat);
                        reglongitude = Double.parseDouble(reglong);
                        mmap.addMarker(new MarkerOptions()
                                .position(new LatLng(reglatitude, reglongitude))
                                .title(regname).snippet("Registration Centre")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_store_mall_directory_black_24dp)));
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
                    Toast.makeText(MyAccount.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MyAccount.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MyAccount.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MyAccount.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MyAccount.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MyAccount.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MyAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        allregistrationcentrerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(allregistrationcentrerequest);
    }

    public void getmemberdetails() {
        StringRequest getmemberdetailsrequest = new StringRequest(Request.Method.GET, API.getmemberdetail + loginuserid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    userid = data.getString("UserID");
                    String name = data.getString("Name");
                    updatename = name;
                    String lastname = data.getString("lastName");
                    updatelastname = lastname;
                    String emailid = data.getString("email");
                    updatemailid = emailid;
                    String balance = data.getString("creditBalance");
                    userbalance = balance;
                    if ((data.has("membershipId"))) {
                        nav_Menu.findItem(R.id.topup_myaccount).setVisible(true);
                        nav_Menu.findItem(R.id.selectplan_myaccount).setVisible(false);
                        nav_Menu.findItem(R.id.tickets_myaccount).setVisible(true);
                        String validity = data.getString("validity");
                        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(inFormat.parse(validity));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        MarqueeValidity.setText("Validity : " + outFormat.format(c.getTime()));
                    } else {
                        nav_Menu.findItem(R.id.selectplan_myaccount).setVisible(true);
                        nav_Menu.findItem(R.id.topup_myaccount).setVisible(false);
                        nav_Menu.findItem(R.id.contact_myaccount).setVisible(true);
                        nav_Menu.findItem(R.id.logout_myaccount).setVisible(true);
                        nav_Menu.findItem(R.id.ride_myaccount).setVisible(false);
                        nav_Menu.findItem(R.id.payment_myaccount).setVisible(false);
                        nav_Menu.findItem(R.id.faq_myaccount).setVisible(false);
                        nav_Menu.findItem(R.id.changepassword_myaccount).setVisible(true);
                        nav_Menu.findItem(R.id.tickets_myaccount).setVisible(false);
                    }
                    if (Integer.parseInt(balance) > 0) {
                        nav_Menu.findItem(R.id.selectplan_myaccount).setVisible(false);
                    }
                    profilePicpath = data.getString("profilePic");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        if (profilePicpath.equals("")) {
                            Profilepic.setImageResource(R.drawable.trintrinlogo);
                        } else {
                            profilieurl = new URL("http://www.mytrintrin.com/mytrintrin/Member/" + userid + "/" + profilePicpath + ".png");
                            Profilepic.setImageBitmap(BitmapFactory.decodeStream((InputStream) profilieurl.getContent()));
                        }
                    } catch (IOException e) {
                        Log.e("TAG", e.getMessage());
                    }

                    username = name;
                    UserName.setText(name + " " + lastname);
                    UserMail.setText(emailid);
                   /* UserBalance.setText(balance+"/-");
                    userbalance = balance;*/
                    MarqueeBalance.setText("Balance : " + getResources().getString(R.string.Rs) + balance + "/-");

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
                    Toast.makeText(MyAccount.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MyAccount.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MyAccount.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MyAccount.this, "Please Check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MyAccount.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MyAccount.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MyAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        getmemberdetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(getmemberdetailsrequest);
    }

    public void getridedetails() {

        StringRequest getridedetailsrequest = new StringRequest(Request.Method.GET, API.ridehistory + loginuserid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    int datalength = data.length();
                    mProgressDialog.dismiss();
                    if (datalength > 0) {
                        if (datalength > 5) {
                            datalength = 5;
                        } else {
                            datalength = data.length();
                        }
                        for (int i = 0; i < datalength; i++) {
                            JSONObject ridedata = data.getJSONObject(i);
                            String Checkout = ridedata.getString("checkOutTime");
                            String FromStation = ridedata.getString("FromStation");
                            String ToStation = ridedata.getString("ToStation");
                            String CheckinTime = ridedata.getString("checkInTime");
                            String Fare = ridedata.getString("fare");
                            String Duration = ridedata.getString("duration");

                            Ridecardview = new CardView(context);
                            ridecardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            ridecardparams.bottomMargin = 10;
                            ridecardparams.rightMargin = 5;
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
                            RideDate.setText("Check out : " + Checkout);
                            RideDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideDate.setTextColor(Color.WHITE);

                            RideFromStation = new TextView(context);
                            RideFromStation.setText("From : " + FromStation);
                            RideFromStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideFromStation.setTextColor(Color.WHITE);

                            RideToStation = new TextView(context);
                            RideToStation.setText("To : " + ToStation);
                            RideToStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideToStation.setTextColor(Color.WHITE);

                            RideCheckinTime = new TextView(context);
                            RideCheckinTime.setText("Check in : " + CheckinTime);
                            RideCheckinTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideCheckinTime.setTextColor(Color.WHITE);

                            RideFare = new TextView(context);
                            RideFare.setText("Fare : " + Fare + "/-");
                            RideFare.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideFare.setTextColor(Color.WHITE);

                            RideDuration = new TextView(context);
                            RideDuration.setText("Duration : " + Duration.substring(0, 4) + "mins.");
                            RideDuration.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            RideDuration.setTextColor(Color.WHITE);

                            RideDetailsLayout.addView(RideDate);
                            RideDetailsLayout.addView(RideFromStation);
                            RideDetailsLayout.addView(RideCheckinTime);
                            RideDetailsLayout.addView(RideToStation);
                            RideDetailsLayout.addView(RideDuration);
                            RideDetailsLayout.addView(RideFare);

                            Ridecardview.addView(RideDetailsLayout);
                            RideHistory.addView(Ridecardview);
                        }
                    } else {
                        LatestRides.setVisibility(View.GONE);
                        Toast.makeText(MyAccount.this, "No Rides Found", Toast.LENGTH_LONG).show();
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
                    LatestRides.setVisibility(View.GONE);
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(MyAccount.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MyAccount.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MyAccount.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MyAccount.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MyAccount.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MyAccount.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MyAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.myaccount_myaccount) {

            startActivity(new Intent(MyAccount.this, MyAccount.class));

        } else if (id == R.id.changepassword_myaccount) {

            startActivity(new Intent(MyAccount.this, ChangePassword.class));

        } else if (id == R.id.payment_myaccount) {

            Intent payment = new Intent(this, Payment_History.class);
            payment.putExtra("balance", userbalance);
            startActivity(payment);

        } else if (id == R.id.topup_myaccount) {

            startActivity(new Intent(this, Topup.class));

        } else if (id == R.id.ride_myaccount) {

            startActivity(new Intent(this, Ride_History.class));

        } else if (id == R.id.faq_myaccount) {

            startActivity(new Intent(this, FAQS.class));

        } else if (id == R.id.contact_myaccount) {

            Intent faq = new Intent(MyAccount.this, Feedback.class);
            faq.putExtra("Name", username);
            startActivity(faq);

        } else if (id == R.id.logout_myaccount) {

            editor.putString("User-id", "");
            editor.commit();
            startActivity(new Intent(MyAccount.this, Login.class));
            finish();

        } else if (id == R.id.selectplan_myaccount) {

            startActivity(new Intent(MyAccount.this, SelectPlan.class));
        }

        else if (id == R.id.tickets_myaccount) {

            Intent tickets = new Intent(MyAccount.this, Tickets.class);
            tickets.putExtra("Name", username);
            startActivity(tickets);
        }

        return false;
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


