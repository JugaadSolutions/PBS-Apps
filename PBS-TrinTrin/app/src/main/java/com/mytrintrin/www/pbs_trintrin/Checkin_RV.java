package com.mytrintrin.www.pbs_trintrin;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Checkin_RV extends AppCompatActivity {

    Toolbar CheckinToolbar;

    public static ArrayList<String> RVIDArrayList = new ArrayList<String>();
    public static ArrayList<String> RVNameArrayList = new ArrayList<String>();
    public static ArrayList<String> MCIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MCNameArrayList = new ArrayList<String>();
    public static ArrayList<String> HANameArrayList = new ArrayList<String>();
    public static ArrayList<String> HAIDArrayList = new ArrayList<String>();

    public static Spinner checkinstationspinner, HAspinner, RVspinner, MCspinner, Fleetspinner;
    public ArrayAdapter<String> HAadapter, RVadapter, MCadapter;

    String HAportid, RVportid, MCportid;
    String Holdingarea, Restribution, Maintenance,fleetname;

    EditText checkinVehicleID, checkinCardID;

    LinearLayout Bicyleidlayout, CheckinErrorlayout, ll;

    ImageView imageView;

    int a = 100;

    ArrayList<EditText> Allcycleid;
    ArrayList<ImageView> Allcycleclear;
    ArrayList<String> Allcycleidlist=new ArrayList<>();

    int cycleidcount;

    String[] strings;

    int j = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_rv);

        CheckinToolbar = (Toolbar) findViewById(R.id.checkintoolbar);
        CheckinToolbar.setTitle("Checkin");
        setSupportActionBar(CheckinToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkinstationspinner = (Spinner) findViewById(R.id.checkinstationspinner);
        HAspinner = (Spinner) findViewById(R.id.HAspinner);
        RVspinner = (Spinner) findViewById(R.id.RVspinner);
        MCspinner = (Spinner) findViewById(R.id.MCspinner);
        Fleetspinner = (Spinner) findViewById(R.id.Fleetspinner);

        checkinVehicleID = (EditText) findViewById(R.id.checkinvehicleid);
        checkinCardID = (EditText) findViewById(R.id.checkincardid);

        Bicyleidlayout = (LinearLayout) findViewById(R.id.checkinbicylelayout);
        CheckinErrorlayout = (LinearLayout) findViewById(R.id.checkinerrorlayout);

        Allcycleid = new ArrayList<EditText>();
        Allcycleclear=new ArrayList<ImageView>();

        fleetname = getIntent().getStringExtra("fleetname");

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        getRestributiondetails();
        getMCdetails();
        getHAdetails();
        checkinstations();
        checkinternet();

        HAadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
        RVadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, RVNameArrayList);
        MCadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Checkin_RV.this);
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
    protected void onPause() {
        super.onPause();
        if (HAadapter == null || RVadapter == null || MCadapter == null) {
            HAadapter = RVadapter = MCadapter = null;
        } else {
            HAadapter.clear();
            RVadapter.clear();
            MCadapter.clear();
        }
    }

    public void getcheckindetails() {


        if (Holdingarea == "" || HAportid == null) {
            Holdingarea = "";
            HAportid = "";
        } else {
            Holdingarea = HAspinner.getSelectedItem().toString();
        }

        if (Maintenance == "" || MCportid == null) {
            Maintenance = "";
            MCportid = "";
        } else {
            Maintenance = MCspinner.getSelectedItem().toString();
        }

        if (Restribution == "" || RVportid == null) {
            Restribution = "";
            RVportid = "";
        } else {
            Restribution = RVspinner.getSelectedItem().toString();
        }
    }

    public void addbicyleidtextbox(View view) {

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        EditText dynamicyclenum = new EditText(this);
        LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etparams.setMargins(0, 0, 0, 15);
        dynamicyclenum.setLayoutParams(etparams);
        dynamicyclenum.setHint("Bicycle Number");
        dynamicyclenum.setBackgroundResource(R.drawable.input_outline);
        //editText.setPadding(30, 30, 30, 30);
        dynamicyclenum.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
        dynamicyclenum.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
        dynamicyclenum.setTypeface(null, Typeface.BOLD);
        dynamicyclenum.setInputType(InputType.TYPE_CLASS_NUMBER);
        dynamicyclenum.setFilters(new InputFilter[] {new InputFilter.LengthFilter(getResources().getInteger(R.integer.bicycleno))});
        etparams.weight = 1;
        dynamicyclenum.setId(a+1);
        a++;

        Allcycleid.add(dynamicyclenum);

        imageView = new ImageView(this);
        LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(70,70);
        imageView.setLayoutParams(ivparams);
        imageView.setImageResource(R.mipmap.ic_clear_black_24dp);
        ivparams.weight = 1;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toberemoved =Allcycleclear.indexOf(view);
                Toast.makeText(Checkin_RV.this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
                Allcycleclear.remove(toberemoved);
                Allcycleid.remove(toberemoved);
                View removingview = Bicyleidlayout.getChildAt(toberemoved);
                Bicyleidlayout.removeView(removingview);
            }
        });
        Allcycleclear.add(imageView);
        ll.addView(dynamicyclenum);
        ll.addView(imageView);

        Bicyleidlayout.addView(ll);
    }

    public void sendall(View view) {
        cycleidcount = Allcycleid.size();
        cycleidcount = cycleidcount+1;
        Allcycleidlist.add(0,checkinVehicleID.getText().toString().trim());
        for (int i = 1; i < cycleidcount; i++) {
            Allcycleidlist.add(i,Allcycleid.get(i - 1).getText().toString());
        }
        Toast.makeText(this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
        for (j = 0; j < cycleidcount; j++) {
            checkinternet();
            getcheckindetails();
            if (checkinVehicleID.getText().toString().trim().equals("")) {
                checkinVehicleID.setError("Please Enter Bicycle ID");
                return;
            }
            if (checkinCardID.getText().toString().trim().equals("")) {
                checkinCardID.setError("Please Enter Card ID");
                return;
            }
            String checkinstationvalue = checkinstationspinner.getSelectedItem().toString();
            if (checkinstationvalue.equals("Select Station")) {
                Toast.makeText(this, "Please select the stations", Toast.LENGTH_LONG).show();
                cycleidcount = Allcycleid.size();
                return;
            }
            sendToserver(Allcycleidlist.get(j));
        }
    }

    private void sendToserver(final String str) {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkintime = dateFormatGmt.format(new Date()) + "";

        StringRequest checkinrequest = new StringRequest(Request.Method.POST, API.checkinurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response", response);
                try {
                    cycleidcount = Allcycleid.size();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if (errorstatus.equals("0")) {
                        Toast.makeText(Checkin_RV.this, "Check in Successfully", Toast.LENGTH_SHORT).show();
                        Allcycleid.clear();
                        Allcycleidlist.clear();
                        Bicyleidlayout.removeAllViews();
                        checkinVehicleID.setText("");
                        checkinCardID.setText("");
                        //checkinstationspinner.setSelection(0);
                        checkinstations();
                    } else {
                        CheckinErrorlayout.setVisibility(View.VISIBLE);
                        String errormessage = data.getString("errorMsg");
                        Toast.makeText(Checkin_RV.this, str + " Check in Unsuccessfull ", Toast.LENGTH_SHORT).show();
                        LinearLayout checkinerror = new LinearLayout(Checkin_RV.this);
                        LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        errorparams.weight = 1;
                        checkinerror.setOrientation(LinearLayout.HORIZONTAL);
                        TextView errorcycleid = new TextView(Checkin_RV.this);
                        errorcycleid.setLayoutParams(errorparams);
                        errorcycleid.setText(str);
                        errorcycleid.setTextSize(16);
                        errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        TextView errormess = new TextView(Checkin_RV.this);
                        errormess.setText(errormessage);
                        errormess.setTextSize(16);
                        errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        errormess.setLayoutParams(errorparams);
                        checkinerror.addView(errorcycleid);
                        checkinerror.addView(errormess);
                        CheckinErrorlayout.addView(checkinerror);
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
                    Toast.makeText(Checkin_RV.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkin_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkin_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkin_RV.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin_RV.this);
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
                    Toast.makeText(Checkin_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkin_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkin_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("vehicleId",fleetname+"-"+str);
                params.put("cardId", checkinCardID.getText().toString().trim());
                params.put("toPort", HAportid + "" + RVportid + "" + MCportid + "");
                params.put("checkInTime", checkintime);
                return params;
            }
        };
        checkinrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkinrequest);
    }


    public void getRestributiondetails() {
        StringRequest restributionrequest = new StringRequest(Request.Method.GET, API.RVidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject restributionfromserver = new JSONObject(response);
                    JSONArray restributiondataarray = restributionfromserver.getJSONArray("data");
                    for (int i = 0; i < restributiondataarray.length(); i++) {
                        JSONObject portid = restributiondataarray.getJSONObject(i);
                        String id = portid.getString("_id");
                        String name = portid.getString("Name");
                        RVIDArrayList.add(id);
                        RVNameArrayList.add(name);
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
                    Toast.makeText(Checkin_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkin_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkin_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkin_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin_RV.this);
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
                    Toast.makeText(Checkin_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkin_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkin_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        restributionrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(restributionrequest);
    }

    public void getMCdetails() {
        StringRequest maintenancerequest = new StringRequest(Request.Method.GET, API.MCidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject maintenancefromserver = new JSONObject(response);
                    JSONArray maintenancedataarray = maintenancefromserver.getJSONArray("data");
                    for (int i = 0; i < maintenancedataarray.length(); i++) {
                        JSONObject maintenanceid = maintenancedataarray.getJSONObject(i);
                        String id = maintenanceid.getString("_id");
                        String name = maintenanceid.getString("Name");
                        MCIDArrayList.add(id);
                        MCNameArrayList.add(name);
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
                    Toast.makeText(Checkin_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkin_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkin_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkin_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin_RV.this);
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
                    Toast.makeText(Checkin_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkin_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkin_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        maintenancerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(maintenancerequest);
    }

    public void getHAdetails() {
        StringRequest holdingarearequest = new StringRequest(Request.Method.GET, API.HAidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject holdingareafromserver = new JSONObject(response);
                    JSONArray holdingareadataarray = holdingareafromserver.getJSONArray("data");
                    for (int i = 0; i < holdingareadataarray.length(); i++) {
                        JSONObject holdingareaid = holdingareadataarray.getJSONObject(i);
                        String id = holdingareaid.getString("_id");
                        String name = holdingareaid.getString("Name");
                        HANameArrayList.add(name);
                        HAIDArrayList.add(id);
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
                    Toast.makeText(Checkin_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkin_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkin_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkin_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin_RV.this);
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
                    Toast.makeText(Checkin_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkin_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkin_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        holdingarearequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(holdingarearequest);
    }


    private void checkinstations() {
        List<String> categories = new ArrayList<String>();
        categories.add("Select Station");
        categories.add("Holding Area");
        categories.add("Redistrubution Vehicle");
        categories.add("Maintainence Centre");
        ArrayAdapter<String> dockingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dockingadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkinstationspinner.setAdapter(dockingadapter);
        checkinstationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        HAspinner.setVisibility(View.VISIBLE);
                        RVportid = MCportid  = Restribution = Maintenance  = "";
                        HAadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
                        HAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HAspinner.setAdapter(HAadapter);
                        HAspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea = HAspinner.getSelectedItem().toString();
                                HAportid = HAIDArrayList.get(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVspinner.setVisibility(View.VISIBLE);
                        HAportid = MCportid  = Holdingarea = Maintenance  = "";
                        RVadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, RVNameArrayList);
                        RVadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        RVspinner.setAdapter(RVadapter);
                        RVspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution = RVspinner.getSelectedItem().toString();
                                RVportid = RVIDArrayList.get(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        MCspinner.setVisibility(View.VISIBLE);
                        HAportid = RVportid  = Holdingarea = Restribution  = "";
                        MCadapter = new ArrayAdapter<String>(Checkin_RV.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
                        MCadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        MCspinner.setAdapter(MCadapter);
                        MCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance = MCspinner.getSelectedItem().toString();
                                MCportid = MCIDArrayList.get(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        HAspinner.setVisibility(View.GONE);
                        RVspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
