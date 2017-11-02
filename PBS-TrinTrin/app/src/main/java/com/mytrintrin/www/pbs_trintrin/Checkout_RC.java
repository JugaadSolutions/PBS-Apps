package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class Checkout_RC extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;

    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();
    public static ArrayList<String> Nostationarraylist = new ArrayList<String>();

    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();

    JSONArray StationArray, PortArray;

    EditText CardNumber, CycleNumber;

    JSONObject Stationobject = new JSONObject();

    Spinner Port,Stations;

    ArrayAdapter Portadapter;
    public ArrayAdapter<String> Stationadapter;

    String PortName, PortId, StationId,Station,StationName,CardNum,CycleNum,message;
    int memberId;

    private ProgressDialog mProgressDialog;
    Toolbar Checkout_RCToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_rc);
        Checkout_RCToolbar = (Toolbar) findViewById(R.id.checkout_rctoolbar);
        Checkout_RCToolbar.setTitle("Checkout");
        setSupportActionBar(Checkout_RCToolbar);
        CardNumber = (EditText) findViewById(R.id.cardnumber_unlock);
        CycleNumber = (EditText) findViewById(R.id.cyclenum_unlock);
        Port = (Spinner) findViewById(R.id.ports);
        Stations = (Spinner) findViewById(R.id.stations);
        CardNum = getIntent().getStringExtra("cardnum");
        memberId = getIntent().getIntExtra("memberid",0);
        CardNumber.setText(CardNum);
        checkopentransactions();
        //getalldockingstations();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            // Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Checkout_RC.this);
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
        }
    }
    /*ends*/

    public void checkopentransactions()
    {
        checkinternet();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Checking open transactions");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest checkopentransactionrequest = new StringRequest(Request.Method.GET, API.opencheckout+memberId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    mProgressDialog.dismiss();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if(data.length()>0)
                    {
                        message = "User Already Taken Vehicle";
                        showalertdialog(message);
                    }
                    else
                    {
                        getalldockingstations();
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
                    Toast.makeText(Checkout_RC.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RC.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RC.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Checkout_RC.this);
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
                    Toast.makeText(Checkout_RC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RC.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        };
        checkopentransactionrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkopentransactionrequest);

    }


    public void getalldockingstations() {
        checkinternet();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest alldockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    StationNameArrayList.clear();
                    StationIDArrayList.clear();
                    StationArray = new JSONArray();
                    PortArray = new JSONArray();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject dscoordinates = data.getJSONObject(i);
                        String id = dscoordinates.getString("StationID");
                        final String Stationname = dscoordinates.getString("name");
                        JSONArray ports = dscoordinates.getJSONArray("portIds");
                        StationIDArrayList.add(id);
                        StationNameArrayList.add(Stationname);
                        StationArray.put(dscoordinates);
                        PortArray.put(ports);

                       /* float distance = currentlocation.distanceTo(dockinglocation);
                        if(distance<100) {
                            String id = dscoordinates.getString("StationID");
                            final String Stationname = dscoordinates.getString("name");
                            JSONArray ports = dscoordinates.getJSONArray("portIds");
                            StationIDArrayList.add(id);
                            StationNameArrayList.add(Stationname);
                            StationArray.put(dscoordinates);
                            PortArray.put(ports);
                        }*/

                    }
                    calculatedistanceandsetstation();
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
                    Toast.makeText(Checkout_RC.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RC.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RC.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Checkout_RC.this);
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
                    Toast.makeText(Checkout_RC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RC.this, "Something went wrong", Toast.LENGTH_LONG).show();
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


    public void calculatedistanceandsetstation() {
        if(StationNameArrayList.size()>0)
        {
            Stationadapter = new ArrayAdapter<String>(Checkout_RC.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
            Stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Stations.setAdapter(Stationadapter);
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
                    } finally {
                        getportdetails();
                    }
                    Toast.makeText(Checkout_RC.this, StationId, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

        }
        else
        {
            mProgressDialog.dismiss();
            android.support.v7.app.AlertDialog.Builder Nostation = new android.support.v7.app.AlertDialog.Builder(this);
            Nostation.setIcon(R.drawable.splashlogo);
            Nostation.setTitle("Nearest Hubs");
            Nostation.setMessage("Sorry,You are not near to any of the TrinTrin Hubs");
            Nostation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            Nostation.setCancelable(false);
            Nostation.show();
        }
    }

    public void getportdetails() {
        try {
            JSONArray ports = Stationobject.getJSONArray("portIds");
            PortIDArrayList.clear();
            PortNameArrayList.clear();
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
        } finally {
            setports();
        }
    }


    public void setports() {
        mProgressDialog.dismiss();
        Portadapter = new ArrayAdapter<String>(Checkout_RC.this, android.R.layout.simple_spinner_dropdown_item, PortNameArrayList);
        Portadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Port.setAdapter(Portadapter);
        Port.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PortName = Port.getSelectedItem().toString();
                PortId = PortIDArrayList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void releasecycle(View view) {

        CardNum = CardNumber.getText().toString().trim();
        CycleNum = CycleNumber.getText().toString().trim();

        if (CycleNum.equals("") || CycleNum.equals(null)) {
            CycleNumber.setError("Cycle Number");
            Toast.makeText(Checkout_RC.this, "Cycle Nummber Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (CycleNum.length()<3) {
            CycleNumber.setError("Cycle Number");
            Toast.makeText(Checkout_RC.this, "Cycle Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (CardNum.equals("") || CardNum.equals(null)) {
            CardNumber.setError("Card Number");
            Toast.makeText(Checkout_RC.this, "Cardnumber Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (CardNum.length()<2) {
            CardNumber.setError("Card Number");
            Toast.makeText(Checkout_RC.this, "Card Number", Toast.LENGTH_SHORT).show();
            return;
        }


        if (StationName.equals("") || StationName.equals(null)) {
            Toast.makeText(Checkout_RC.this, "Station Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        StringRequest releasecylerequest = new StringRequest(Request.Method.POST, API.releasecycle, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String error = responsefromserver.getString("error");
                    if(error.equals("false"))
                    {
                        Toast.makeText(Checkout_RC.this, "Unlock Successfull", Toast.LENGTH_SHORT).show();
                        String data = responsefromserver.getString("data");
                        AlertDialog.Builder CheckoutBuilder = new AlertDialog.Builder(Checkout_RC.this);
                        CheckoutBuilder.setIcon(R.mipmap.logo);
                        CheckoutBuilder.setTitle("Checkout");
                        CheckoutBuilder.setMessage(data +". "+"Do you want to create checkout?");
                        CheckoutBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                createcheckout();
                            }
                        });
                        CheckoutBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        CheckoutBuilder.setCancelable(false);
                        CheckoutBuilder.show();
                    }
                    else if(error.equals("true"))
                    {
                        message =responsefromserver.getString("message");
                        showalertdialog(message);
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
                    Toast.makeText(Checkout_RC.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RC.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RC.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RC.this);
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
                    Toast.makeText(Checkout_RC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RC.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("stationId", StationId);
                params.put("portId", PortId);
                return params;
            }
        };
        releasecylerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(releasecylerequest);
    }


    public void createcheckout()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Creating Checkout...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        StringRequest createcheckoutrequest = new StringRequest(Request.Method.POST, API.createcheckout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    CycleNumber.getText().clear();
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if(errorstatus.equals("1"))
                    {
                        message= "Duplicate Entry";
                        showalertdialog(message);
                    }
                    else {
                        message ="Checkout entry created";
                        showalertdialog(message);
                    }
                    Toast.makeText(Checkout_RC.this, "Checkout Entry Created Successfully", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error instanceof ServerError) {
                    Toast.makeText(Checkout_RC.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Checkout_RC Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RC.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RC.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RC.this);
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
                    Toast.makeText(Checkout_RC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RC.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("stationId", StationId);
                params.put("fromPort", PortId);
                params.put("cardId",CardNum);
                params.put("vehicleId","MYS-Fleet-"+CycleNum);
                return params;
            }
        };
        createcheckoutrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(createcheckoutrequest);
    }


    public void showalertdialog(String msg) {
        AlertDialog.Builder errorbuilder = new AlertDialog.Builder(
                Checkout_RC.this);
        errorbuilder.setIcon(R.mipmap.logo);
        errorbuilder.setTitle("Error");
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
