package com.mytrintrin.www.pbs_sync;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
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

public class Clearcheckout extends AppCompatActivity {

    Toolbar clearcheckouttoolbar;
    String cardnum_clearcheckout;
    private ProgressDialog mProgressDialog;

    CardView clearcheckoutcardview;
    LinearLayout Clearcheckoutlayout, checkoutdetailslayout;
    LinearLayout.LayoutParams cardparams, detailsparams;
    TextView Name, VehicleNum, Port, Time;
    ArrayList<CardView> cardlist = new ArrayList<>();
    ArrayList<String> idlist = new ArrayList<>();
    String  checkoutid,errormsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clearcheckout);
        clearcheckouttoolbar = (Toolbar) findViewById(R.id.clearcheckout_toolbar);
        clearcheckouttoolbar.setTitle("Clear Checkout");
        setSupportActionBar(clearcheckouttoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Clearcheckoutlayout = (LinearLayout) findViewById(R.id.clearcheckoutlayout);
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
                    Clearcheckout.this);
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
        mProgressDialog.setMessage("Fetching open checkouts...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest opentransactionrequest = new StringRequest(Request.Method.GET, API.getopentransactions, new Response.Listener<String>() {
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
                            JSONObject checkoutobject = data.getJSONObject(i);
                            String id = checkoutobject.getString("_id");
                            idlist.add(id);
                            JSONObject user = checkoutobject.getJSONObject("user");
                            String username = user.getString("Name");
                            String lastname = user.getString("lastName");
                            JSONObject vehicle = checkoutobject.getJSONObject("vehicle");
                            String vehiclenum = vehicle.getString("vehicleNumber");
                            JSONObject fromport = checkoutobject.getJSONObject("fromPort");
                            String from = fromport.getString("Name");
                            String checkoutdate = checkoutobject.getString("checkOutTime");

                            clearcheckoutcardview = new CardView(Clearcheckout.this);
                            cardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            cardparams.bottomMargin = 10;
                            clearcheckoutcardview.setLayoutParams(cardparams);
                            clearcheckoutcardview.setRadius(15);
                            clearcheckoutcardview.setPadding(25, 25, 25, 25);
                            clearcheckoutcardview.setCardBackgroundColor(Color.parseColor("#009746"));
                            clearcheckoutcardview.setVisibility(View.VISIBLE);
                            clearcheckoutcardview.setMaxCardElevation(30);

                            checkoutdetailslayout = new LinearLayout(Clearcheckout.this);
                            detailsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            checkoutdetailslayout.setOrientation(LinearLayout.VERTICAL);
                            checkoutdetailslayout.setGravity(Gravity.END);
                            checkoutdetailslayout.setPadding(25, 25, 25, 25);
                            checkoutdetailslayout.setLayoutParams(detailsparams);

                            Name = new TextView(Clearcheckout.this);
                            Name.setText("Name : " + username + " " + lastname);
                            Name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            Name.setTextColor(Color.WHITE);

                            VehicleNum = new TextView(Clearcheckout.this);
                            VehicleNum.setText("Vehicle Number : " + vehiclenum);
                            VehicleNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            VehicleNum.setTextColor(Color.WHITE);

                            Port = new TextView(Clearcheckout.this);
                            Port.setText("Port : " + from);
                            Port.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            Port.setTextColor(Color.WHITE);

                            Time = new TextView(Clearcheckout.this);
                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(inFormat.parse(checkoutdate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Time.setText("Time : " + outFormat.format(c.getTime()));
                            Time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            Time.setTextColor(Color.WHITE);

                            checkoutdetailslayout.addView(Name);
                            checkoutdetailslayout.addView(VehicleNum);
                            checkoutdetailslayout.addView(Port);
                            checkoutdetailslayout.addView(Time);

                            clearcheckoutcardview.addView(checkoutdetailslayout);
                            cardlist.add(clearcheckoutcardview);
                            Clearcheckoutlayout.addView(clearcheckoutcardview);
                            clearcheckoutcardview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int position = Clearcheckoutlayout.indexOfChild(view);
                                    checkoutid = idlist.get(position);
                                    createcheckoutdialog(checkoutid);
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
                    Toast.makeText(Clearcheckout.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckout.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckout.this);
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
                    Toast.makeText(Clearcheckout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckout.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void showclearallcheckoutsdialog(View view)
    {
        AlertDialog.Builder clearallcheckoutdialog = new AlertDialog.Builder(this);
        clearallcheckoutdialog.setIcon(R.mipmap.logo);
        clearallcheckoutdialog.setTitle("Clear Checkouts");
        clearallcheckoutdialog.setMessage("Do you really want to clear all checkouts?");
        clearallcheckoutdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                clearallcheckouts();

            }
        });
        clearallcheckoutdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        clearallcheckoutdialog.setCancelable(false);
        clearallcheckoutdialog.show();
    }


    public void clearallcheckouts()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Clearing all checkouts...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest clearallcheckouts = new StringRequest(Request.Method.DELETE, API.clearallcheckouts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String message = responsefromserver.getString("message");
                    AlertDialog.Builder clearallcheckoutbuilder = new AlertDialog.Builder(Clearcheckout.this);
                    clearallcheckoutbuilder.setIcon(R.mipmap.logo);
                    clearallcheckoutbuilder.setTitle("Clear all checkouts");
                    clearallcheckoutbuilder.setMessage(message);
                    clearallcheckoutbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(Clearcheckout.this,Getstarted.class));
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
                    Toast.makeText(Clearcheckout.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckout.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckout.this);
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
                    Toast.makeText(Clearcheckout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckout.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }){

        };
        clearallcheckouts.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(clearallcheckouts);

    }

    public  void createcheckoutdialog(final String id)
    {
        AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Clearcheckout.this);
        SuccessBuilder.setIcon(R.drawable.splashlogo);
        SuccessBuilder.setTitle("Clear Checkout");
        SuccessBuilder.setMessage("Do you want to clear this checkout?");
        SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                clearcheckout(id);

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


    public void clearcheckout(String cid)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Clearing Checkout...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest clearcheckoutrequest = new StringRequest(Request.Method.DELETE, API.syncclearcheckout+cid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String data = responsefromserver.getString("data");
                    AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Clearcheckout.this);
                    SuccessBuilder.setIcon(R.drawable.splashlogo);
                    SuccessBuilder.setTitle("Clear Checkout");
                    SuccessBuilder.setMessage(data);
                    SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(Clearcheckout.this, Getstarted.class));
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
                    Toast.makeText(Clearcheckout.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Clearcheckout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Clearcheckout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Clearcheckout.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Clearcheckout.this);
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
                    Toast.makeText(Clearcheckout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Clearcheckout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Clearcheckout.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void showalert(String message)
    {
        AlertDialog.Builder errorbuilder = new AlertDialog.Builder(Clearcheckout.this);
        errorbuilder.setIcon(R.mipmap.logo);
        errorbuilder.setTitle("Open Transactions");
        errorbuilder.setMessage(message);
        errorbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(Clearcheckout.this,Getstarted.class));
                finish();
            }
        });
        errorbuilder.setCancelable(false);
        errorbuilder.show();
    }

}
