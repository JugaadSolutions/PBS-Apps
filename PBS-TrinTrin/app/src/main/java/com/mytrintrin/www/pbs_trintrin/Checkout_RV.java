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

public class Checkout_RV extends AppCompatActivity {

    Toolbar CheckoutToolbar;

    public static ArrayList<String> FleetIDArrayList = new ArrayList<String>();
    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    public static ArrayList<String> RVIDArrayList = new ArrayList<String>();
    public static ArrayList<String> RVNameArrayList = new ArrayList<String>();
    public static ArrayList<String> MCIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MCNameArrayList = new ArrayList<String>();
    public static ArrayList<String> HANameArrayList = new ArrayList<String>();
    public static ArrayList<String> HAIDArrayList = new ArrayList<String>();

    public static Spinner checkoutstationspinner, HAspinner, RVspinner, MCspinner, Fleetspinner;
    public ArrayAdapter<String> HAadapter, RVadapter, MCadapter, Fleetadapter;

    String HAportid, RVportid, MCportid, Fleetportid;
    String Holdingarea, Restribution, Maintenance, Fleet,fleetname;

    EditText checkoutVehicleID, checkoutCardID;

    LinearLayout Bicyleidlayout, CheckoutErrorlayout, ll;

    ImageView imageView;

    int a = 100;

    ArrayList<EditText> Allcycleid;
    ArrayList<ImageView> Allcycleclear;
    ArrayList<String> Allcycleidlist = new ArrayList<>();

    int cycleidcount;

    String[] strings;

    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_rv);

        CheckoutToolbar = (Toolbar) findViewById(R.id.checkouttoolbar);
        CheckoutToolbar.setTitle("Checkout");
        setSupportActionBar(CheckoutToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkoutstationspinner = (Spinner) findViewById(R.id.checkoutstationspinner);
        HAspinner = (Spinner) findViewById(R.id.HAspinner);
        RVspinner = (Spinner) findViewById(R.id.RVspinner);
        MCspinner = (Spinner) findViewById(R.id.MCspinner);
        Fleetspinner = (Spinner) findViewById(R.id.Fleetspinner);

        checkoutVehicleID = (EditText) findViewById(R.id.checkoutvehicleid);
        checkoutCardID = (EditText) findViewById(R.id.checkoutcardid);

        Bicyleidlayout = (LinearLayout) findViewById(R.id.checkoutbicylelayout);
        CheckoutErrorlayout = (LinearLayout) findViewById(R.id.checkouterrorlayout);

        Allcycleid = new ArrayList<EditText>();
        Allcycleclear = new ArrayList<ImageView>();

        fleetname = getIntent().getStringExtra("fleetname");

        getFleetdetails();
        getRestributiondetails();
        getMCdetails();
        getHAdetails();
        checkoutstations();
        checkinternet();

        Fleetadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
        HAadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
        RVadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, RVNameArrayList);
        MCadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Checkout_RV.this);
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
        if (HAadapter == null || RVadapter == null || MCadapter == null || Fleetadapter == null) {
            HAadapter = RVadapter = MCadapter = Fleetadapter = null;
        } else {
            HAadapter.clear();
            RVadapter.clear();
            MCadapter.clear();
            Fleetadapter.clear();
        }
    }

    public void getcheckoutdetails() {

        if (Fleet == "" || Fleetportid == null) {
            Fleet = "";
            Fleetportid = "";
        } else {
            Fleet = Fleetspinner.getSelectedItem().toString();
        }

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

    public void sendall(View view) {
        cycleidcount = Allcycleid.size();
        cycleidcount = cycleidcount + 1;
        Allcycleidlist.add(0, checkoutVehicleID.getText().toString().trim());
        for (int i = 1; i < cycleidcount; i++) {
            Allcycleidlist.add(i, Allcycleid.get(i - 1).getText().toString());
        }
        Toast.makeText(this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
        for (j = 0; j < cycleidcount; j++) {
            checkinternet();
            getcheckoutdetails();
            if (checkoutVehicleID.getText().toString().trim().equals("")) {
                checkoutVehicleID.setError("Please Enter Bicycle ID");
                return;
            }
            if (checkoutCardID.getText().toString().trim().equals("")) {
                checkoutCardID.setError("Please Enter Card ID");
                return;
            }
            String checkoutstationvalue = checkoutstationspinner.getSelectedItem().toString();
            if (checkoutstationvalue.equals("Select Station")) {
                Toast.makeText(this, "Please select the stations", Toast.LENGTH_LONG).show();
                cycleidcount = Allcycleid.size();
                return;
            }
            Bicyleidlayout.removeAllViews();
            sendToserver(Allcycleidlist.get(j));
        }
        /*checkoutVehicleID.setText("");
        checkoutCardID.setText("");*/
    }


    private void sendToserver(final String str) {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkouttime = dateFormatGmt.format(new Date()) + "";

        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, API.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response", response);
                checkoutstationspinner.setSelection(0);
                try {
                    cycleidcount = Allcycleid.size();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if (errorstatus.equals("0")) {
                        Toast.makeText(Checkout_RV.this, "Check out Successfully", Toast.LENGTH_SHORT).show();
                        Allcycleid.clear();
                        Allcycleidlist.clear();
                        Bicyleidlayout.removeAllViews();
                       /* checkoutVehicleID.setText("");
                        checkoutCardID.setText("");*/
                        //checkoutstationspinner.setSelection(0);
                        checkoutstations();
                    } else {
                        CheckoutErrorlayout.setVisibility(View.VISIBLE);
                        String errormessage = data.getString("errorMsg");
                        Toast.makeText(Checkout_RV.this, str + " Check out Unsuccessfull ", Toast.LENGTH_SHORT).show();
                        LinearLayout checkouterror = new LinearLayout(Checkout_RV.this);
                        LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        errorparams.weight = 1;
                        checkouterror.setOrientation(LinearLayout.HORIZONTAL);
                        TextView errorcycleid = new TextView(Checkout_RV.this);
                        errorcycleid.setLayoutParams(errorparams);
                        errorcycleid.setText(str);
                        errorcycleid.setTextSize(16);
                        errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        TextView errormess = new TextView(Checkout_RV.this);
                        errormess.setText(errormessage);
                        errormess.setTextSize(16);
                        errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        errormess.setLayoutParams(errorparams);
                        checkouterror.addView(errorcycleid);
                        checkouterror.addView(errormess);
                        CheckoutErrorlayout.addView(checkouterror);
                        Allcycleid.clear();
                        Allcycleidlist.clear();
                        Bicyleidlayout.removeAllViews();
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
                    Toast.makeText(Checkout_RV.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RV.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RV.this);
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
                    Toast.makeText(Checkout_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("vehicleId",fleetname+"-"+ str);
                params.put("cardId", checkoutCardID.getText().toString().trim());
                params.put("fromPort", HAportid + "" + RVportid + "" + MCportid + "" + Fleetportid);
                params.put("checkOutTime", checkouttime);
                return params;
            }
        };
        checkoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkoutrequest);
    }

    private void checkoutstations() {
        List<String> categories = new ArrayList<String>();
        categories.add("Select Station");
        categories.add("Fleet");
        categories.add("Holding Area");
        categories.add("Redistrubution Vehicle");
        categories.add("Maintainence Centre");
        ArrayAdapter<String> dockingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dockingadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkoutstationspinner.setAdapter(dockingadapter);
        checkoutstationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        Fleetspinner.setVisibility(View.VISIBLE);
                        HAportid = RVportid = MCportid = Holdingarea = Restribution = Maintenance = "";
                        Fleetadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
                        Fleetadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Fleetspinner.setAdapter(Fleetadapter);
                        Fleetspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Fleet = Fleetspinner.getSelectedItem().toString();
                                Fleetportid = FleetIDArrayList.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);
                        break;

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        HAspinner.setVisibility(View.VISIBLE);
                        RVportid = MCportid = Fleetportid = Restribution = Maintenance = Fleet = "";
                        HAadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
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

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVspinner.setVisibility(View.VISIBLE);
                        HAportid = MCportid = Fleetportid = Holdingarea = Maintenance = Fleet = "";
                        RVadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, RVNameArrayList);
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

                    case 4:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        MCspinner.setVisibility(View.VISIBLE);
                        HAportid = RVportid = Fleetportid = Holdingarea = Restribution = Fleet = "";
                        MCadapter = new ArrayAdapter<String>(Checkout_RV.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
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

    public void getFleetdetails() {
        StringRequest fleetrequest = new StringRequest(Request.Method.GET, API.fleetidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject fleetresponsefronserver = new JSONObject(response);
                    JSONArray fleetdataarray = fleetresponsefronserver.getJSONArray("data");
                    for (int i = 0; i < fleetdataarray.length(); i++) {
                        JSONObject getid = fleetdataarray.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("Name");
                        FleetIDArrayList.add(id);
                        FleetNameArrayList.add(name);
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
                    Toast.makeText(Checkout_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RV.this);
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
                    Toast.makeText(Checkout_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        fleetrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(fleetrequest);
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
                    Toast.makeText(Checkout_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RV.this);
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
                    Toast.makeText(Checkout_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        dynamicyclenum.setId(a + 1);
        a++;

        Allcycleid.add(dynamicyclenum);

        imageView = new ImageView(this);
        LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(ivparams);
        imageView.setImageResource(R.mipmap.ic_clear_black_24dp);
        ivparams.weight = 1;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toberemoved = Allcycleclear.indexOf(view);
                Toast.makeText(Checkout_RV.this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Checkout_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RV.this);
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
                    Toast.makeText(Checkout_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Checkout_RV.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout_RV.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout_RV.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout_RV.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout_RV.this);
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
                    Toast.makeText(Checkout_RV.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout_RV.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout_RV.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
