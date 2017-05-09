package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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

public class Ride_History extends AppCompatActivity {

    private Toolbar RideHistoryToolbar;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;

    CardView Ridecardview;
    Context context;
    LinearLayout RideHistory, RideDetailsLayout;
    LinearLayout.LayoutParams ridecardparams,ridedetailsparams;
    TextView RideDate,RideFromStation,RideToStation,RideCheckinTime,RideFare,RideDuration,RideBalance;
    private ProgressDialog mProgressDialog;

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
        loginuserid = loginpref.getString("User-id",null);
        context = getApplicationContext();
        RideHistory = (LinearLayout) findViewById(R.id.ridehistorylayout);
        checkinternet();

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        getridedetails();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(Ride_History.this);
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
    /*ends*/

    public void getridedetails() {

        checkinternet();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        StringRequest getridedetailsrequest = new StringRequest(Request.Method.GET, API.ridehistory+loginuserid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    int datalength = data.length();
                    mProgressDialog.dismiss();
                    RideHistory.removeAllViews();
                    for (int i=0; i< datalength ;i++)
                    {
                        JSONObject ridedata = data.getJSONObject(i);
                        String Checkout = ridedata.getString("checkOutTime");
                        String FromStation= ridedata.getString("FromStation");
                        String ToStation = ridedata.getString("ToStation");
                        String CheckinTime = ridedata.getString("checkInTime");
                        String Fare = ridedata.getString("fare");
                        String Duration = ridedata.getString("duration");
                        String Balance = ridedata.getString("balance");

                        Ridecardview = new CardView(context);
                        ridecardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ridecardparams.bottomMargin = 10;
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
                        RideDate.setText("Check out : "+Checkout);
                        RideDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideDate.setTextColor(Color.WHITE);

                        RideFromStation = new TextView(context);
                        RideFromStation.setText("From : "+FromStation);
                        RideFromStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideFromStation.setTextColor(Color.WHITE);

                        RideToStation = new TextView(context);
                        RideToStation.setText("To : "+ToStation);
                        RideToStation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideToStation.setTextColor(Color.WHITE);

                        RideCheckinTime = new TextView(context);
                        RideCheckinTime.setText("Check in : "+CheckinTime);
                        RideCheckinTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideCheckinTime.setTextColor(Color.WHITE);

                        RideFare = new TextView(context);
                        RideFare.setText("Fare : "+Fare+"/-");
                        RideFare.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideFare.setTextColor(Color.WHITE);

                        RideDuration = new TextView(context);
                        RideDuration.setText("Duration : "+Duration.substring(0,4));
                        RideDuration.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideDuration.setTextColor(Color.WHITE);

                        RideBalance = new TextView(context);
                        RideBalance.setText("Balance : "+Balance+"/-");
                        RideBalance.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        RideBalance.setTextColor(Color.WHITE);

                        RideDetailsLayout.addView(RideDate);
                        RideDetailsLayout.addView(RideFromStation);
                        RideDetailsLayout.addView(RideCheckinTime);
                        RideDetailsLayout.addView(RideToStation);
                        RideDetailsLayout.addView(RideDuration);
                        RideDetailsLayout.addView(RideFare);
                        RideDetailsLayout.addView(RideBalance);

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

                mProgressDialog.dismiss();

                if (error.networkResponse != null) {
                    Toast.makeText(Ride_History.this, "No Rides Found", Toast.LENGTH_LONG).show();
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
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Ride_History.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
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
        }){
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
                    startActivity(new Intent(Ride_History.this,MyAccount.class));
                    finish();
                }
            });
            RideBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }


}
