package com.mytrintrin.www.pbs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Redistribution extends AppCompatActivity  implements LocationListener{

    Location location;
    LocationManager locationManager;

    double latitude;
    double longitude;


    SwitchCompat track;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid,currentlat,currentlang,empoldpassword,empnewpassword,empconfirmpassword;

    Toolbar redistributiontoolbar;

    NfcAdapter nfcAdapter;

    EditText oldpassword,newpassword,confirmpassword;

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
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.changepassword,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final LayoutInflater changepasswordinflate = LayoutInflater.from(this);
        final View changepasswordview = changepasswordinflate.inflate(R.layout.changepassword,null);
        AlertDialog.Builder changepasswordbuilder = new AlertDialog.Builder(this);
        changepasswordbuilder.setIcon(R.drawable.logo);
        changepasswordbuilder.setTitle("Change Password");
        changepasswordbuilder.setMessage("");
        changepasswordbuilder.setView(changepasswordview);
        changepasswordbuilder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                oldpassword = (EditText) changepasswordview.findViewById(R.id.etoldpassword);
                newpassword = (EditText) changepasswordview.findViewById(R.id.etnewpassword);
                confirmpassword = (EditText) changepasswordview.findViewById(R.id.etconfirmpassword);

                empoldpassword = oldpassword.getText().toString().trim();
                empnewpassword = newpassword.getText().toString().trim();
                empconfirmpassword = confirmpassword.getText().toString().trim();

                if(empoldpassword.equals("")||empnewpassword.equals("")||empconfirmpassword.equals("")) {

                    oldpassword.setError("Password cannot be empty");
                    newpassword.setError("Password cannot be empty");
                    confirmpassword.setError("Password cannot be empty");

                }
                else
                {
                    if (empnewpassword.equals("")||empconfirmpassword.equals(""))
                    {
                        newpassword.setError("Password cannot be empty");
                        confirmpassword.setError("Password cannot be empty");
                    }
                    if (empnewpassword.equals(empconfirmpassword)) {

                        StringRequest changepasswordrequest = new StringRequest(Request.Method.PUT, API.changepassword + loginuserid + "/password/change", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("changepasswordresponse",response);
                                try {
                                    JSONObject changepasswordresponse = new JSONObject(response);
                                    String errorflag = changepasswordresponse.getString("error");
                                    String message = changepasswordresponse.getString("message");
                                    if(errorflag.equals("false"))
                                    {
                                        Toast.makeText(Redistribution.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                        editor.putString("User-id","");
                                        editor.commit();
                                        finish();
                                        startActivity(new Intent(Redistribution.this,LoginNFC.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(Redistribution.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                                if (error instanceof ServerError) {
                                    Toast.makeText(Redistribution.this, "Please enter valid password", Toast.LENGTH_LONG).show();
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
                                params.put("newPassword",empconfirmpassword);
                                params.put("currentPassword",empoldpassword);
                                return params;
                            }
                        };
                        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(changepasswordrequest);

                    } else {

                        newpassword.setError("Password didn't match");
                        confirmpassword.setError("Password didn't match");
                        Toast.makeText(Redistribution.this, "Password didn't match", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

        changepasswordbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Redistribution.this, "Employee cancelled change password", Toast.LENGTH_SHORT).show();
            }
        });
        changepasswordbuilder.show();
        return true;
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
                sendtoserver();

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
                Log.d("lat & lang", "lat"+latitude+" long"+longitude);
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(sendlocation);
    }


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
}
