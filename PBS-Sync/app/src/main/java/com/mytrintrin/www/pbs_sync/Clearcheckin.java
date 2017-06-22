package com.mytrintrin.www.pbs_sync;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Clearcheckin extends AppCompatActivity {

    Toolbar clearcheckintoolbar;
    String cardnum_clearcheckout;
    private ProgressDialog mProgressDialog;

    CardView clearcheckincardview;
    LinearLayout Clearcheckinlayout, checkindetailslayout;
    LinearLayout.LayoutParams cardparams, detailsparams;
    TextView Name, VehicleNum, Port, Time;
    ArrayList<CardView> cardlist = new ArrayList<>();
    ArrayList<String> idlist = new ArrayList<>();
    String  checkinid,errormsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clearcheckin);
        clearcheckintoolbar = (Toolbar) findViewById(R.id.clearcheckintoolbar);
        clearcheckintoolbar.setTitle("Clear Checkin");
        setSupportActionBar(clearcheckintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Clearcheckinlayout = (LinearLayout) findViewById(R.id.clearcheckinlayout);
        checkinternet();
        getopentransaction();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Clearcheckin.this);
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


    public void getopentransaction()
    {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching checkin's...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest opentransactionrequest = new StringRequest(Request.Method.GET, API.getallopencheckins, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    cardlist.clear();
                    idlist.clear();
                    if (data.length() > 0)
                    {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject checkinobject = data.getJSONObject(i);
                            String id = checkinobject.getString("_id");
                            idlist.add(id);
                            JSONObject vehicle = checkinobject.getJSONObject("vehicleId");
                            String vehiclenum = vehicle.getString("vehicleNumber");
                            JSONObject fromport = checkinobject.getJSONObject("toPort");
                            String from = fromport.getString("Name");
                            String checkoutdate = checkinobject.getString("checkInTime");

                            clearcheckincardview = new CardView(Clearcheckin.this);
                            cardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            cardparams.bottomMargin = 10;
                            clearcheckincardview.setLayoutParams(cardparams);
                            clearcheckincardview.setRadius(15);
                            clearcheckincardview.setPadding(25, 25, 25, 25);
                            clearcheckincardview.setCardBackgroundColor(Color.parseColor("#009746"));
                            clearcheckincardview.setVisibility(View.VISIBLE);
                            clearcheckincardview.setMaxCardElevation(30);

                            checkindetailslayout = new LinearLayout(Clearcheckin.this);
                            detailsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            checkindetailslayout.setOrientation(LinearLayout.VERTICAL);
                            checkindetailslayout.setGravity(Gravity.END);
                            checkindetailslayout.setPadding(25, 25, 25, 25);
                            checkindetailslayout.setLayoutParams(detailsparams);

                            VehicleNum = new TextView(Clearcheckin.this);
                            VehicleNum.setText("Vehicle Number : " + vehiclenum);
                            VehicleNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            VehicleNum.setTextColor(Color.WHITE);

                            Port = new TextView(Clearcheckin.this);
                            Port.setText("Port : " + from);
                            Port.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            Port.setTextColor(Color.WHITE);

                            Time = new TextView(Clearcheckin.this);
                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(inFormat.parse(checkoutdate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            Time.setTextColor(Color.WHITE);
                            Time.setText("Time : " + outFormat.format(c.getTime()));

                            checkindetailslayout.addView(VehicleNum);
                            checkindetailslayout.addView(Port);
                            checkindetailslayout.addView(Time);

                            clearcheckincardview.addView(checkindetailslayout);
                            cardlist.add(clearcheckincardview);
                            Clearcheckinlayout.addView(clearcheckincardview);
                            clearcheckincardview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int position = Clearcheckinlayout.indexOfChild(view);
                                    checkinid = idlist.get(position);
                                    createcheckindialog(checkinid);
                                }
                            });
                        }
                }
                    else
                    {
                        errormsg = "Looks like there is no open transactions";
                        showalert(errormsg);
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
                    Toast.makeText(Clearcheckin.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckin.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckin.this);
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
                    Toast.makeText(Clearcheckin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckin.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckin.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        opentransactionrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(opentransactionrequest);
    }

    public  void createcheckindialog(final String id)
    {
        AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Clearcheckin.this);
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();
        SuccessBuilder.setIcon(R.drawable.splashlogo);
        SuccessBuilder.setTitle("Clear Checkin");
        SuccessBuilder.setMessage("Do you want to clear this checkin?");
        SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                clearcheckin(id);

            }
        });
        SuccessBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        SuccessBuilder.setCancelable(false);
        SuccessBuilder.show();
    }


    public void clearcheckin(String cid)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Clearing Checkin...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest clearcheckoutrequest = new StringRequest(Request.Method.DELETE, API.syncclearcheckin+cid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String data = responsefromserver.getString("data");
                    AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Clearcheckin.this);
                    SuccessBuilder.setIcon(R.drawable.splashlogo);
                    SuccessBuilder.setTitle("Clear Checkin");
                    SuccessBuilder.setMessage(data);
                    SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(Clearcheckin.this,Getstarted.class));
                            finish();
                        }
                    });
                    SuccessBuilder.setCancelable(false);
                    SuccessBuilder.show();
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
                    Toast.makeText(Clearcheckin.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckin.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckin.this);
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
                    Toast.makeText(Clearcheckin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckin.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckin.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        clearcheckoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(clearcheckoutrequest);
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

    public void showclearallcheckinsdialog(View view)
    {
        AlertDialog.Builder clearallcheckinsdialog = new AlertDialog.Builder(this);
        clearallcheckinsdialog.setIcon(R.mipmap.logo);
        clearallcheckinsdialog.setTitle("Clear Checkouts");
        clearallcheckinsdialog.setMessage("Do you really want to clear all checkins?");
        clearallcheckinsdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                clearallcheckins();

            }
        });
        clearallcheckinsdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        clearallcheckinsdialog.setCancelable(false);
        clearallcheckinsdialog.show();
    }


    public void clearallcheckins()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Clearing all checkin's...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest clearallcheckins = new StringRequest(Request.Method.DELETE, API.clearallcheckins, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String message = responsefromserver.getString("message");
                    AlertDialog.Builder clearallcheckoutbuilder = new AlertDialog.Builder(Clearcheckin.this);
                    clearallcheckoutbuilder.setIcon(R.mipmap.logo);
                    clearallcheckoutbuilder.setTitle("Clear all checkouts");
                    clearallcheckoutbuilder.setMessage(message);
                    clearallcheckoutbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(Clearcheckin.this,Getstarted.class));
                            finish();
                        }
                    });
                    clearallcheckoutbuilder.setCancelable(false);
                    clearallcheckoutbuilder.show();
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
                    Toast.makeText(Clearcheckin.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckin.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckin.this);
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
                    Toast.makeText(Clearcheckin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckin.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }){

        };
        clearallcheckins.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(clearallcheckins);

    }


    public void showalert(String message)
    {
        AlertDialog.Builder errorbuilder = new AlertDialog.Builder(Clearcheckin.this);
        errorbuilder.setIcon(R.mipmap.logo);
        errorbuilder.setTitle("Open Transactions");
        errorbuilder.setMessage(message);
        errorbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(Clearcheckin.this,Getstarted.class));
                finish();
            }
        });
        errorbuilder.setCancelable(false);
        errorbuilder.show();
    }


}
