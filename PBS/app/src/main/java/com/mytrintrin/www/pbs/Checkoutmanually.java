package com.mytrintrin.www.pbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Checkoutmanually extends AppCompatActivity {

    Toolbar checkoutmanual;

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
    String Holdingarea, Restribution, Maintenance, Fleet;

    EditText checkoutVehicleID, checkoutCardID;

    LinearLayout Bicyleidlayout, CheckoutErrorlayout, ll;

    ImageView imageView;

    public static int a = 100;

    List<EditText> Allcycleid;

    int cycleidcount;

    String[] strings;

    int j = 0;

    String cycleid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutmanually);

        checkoutmanual = (Toolbar) findViewById(R.id.checkoutmanaultoolbar);
        checkoutmanual.setTitle("Checkout");
        checkoutmanual.setTitleTextColor(Color.WHITE);
        setSupportActionBar(checkoutmanual);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFleetdetails();
        getRestributiondetails();
        getMCdetails();
        getHAdetails();

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

        checkoutdockingstations();
        checkinternet();

    }

    public void addbicyleidtextbox(View view) {

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);


        EditText editText = new EditText(this);
        LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etparams.setMargins(0, 0, 0, 15);
        editText.setLayoutParams(etparams);
        editText.setHint("Bicycle ID");
        editText.setBackgroundResource(R.drawable.input_outline);
        editText.setPadding(30, 30, 30, 30);
        editText.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
        editText.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
        editText.setTypeface(null, Typeface.BOLD);
        etparams.weight = 1;
        editText.setId(a + 1);
        a++;
        Log.d("et id", String.valueOf(a));

        Allcycleid.add(editText);
        cycleidcount = Allcycleid.size();

        Log.d("total", String.valueOf(Allcycleid.size()));

        imageView = new ImageView(this);
        LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
        imageView.setLayoutParams(ivparams);
        imageView.setImageResource(R.drawable.clearbutton);
        ivparams.weight = 1;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) ll.getParent()).removeView(ll);
                cycleidcount--;
                Toast.makeText(Checkoutmanually.this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
            }
        });
        ll.addView(editText);
        ll.addView(imageView);

        Bicyleidlayout.addView(ll);

    }

    public void getallvalues(View view) {
        if (cycleidcount == 0) {
            strings = new String[1];
            strings[0] = checkoutVehicleID.getText().toString().trim();
            Log.d("string value", String.valueOf(strings));

        } else {
            cycleidcount = cycleidcount + 1;
            strings = new String[cycleidcount];
            strings[0] = checkoutVehicleID.getText().toString().trim();
            for (int i = 1; i < cycleidcount; i++) {
                strings[i] = Allcycleid.get(i - 1).getText().toString();
                Log.d("string value", String.valueOf(i));
            }

        }

    }

    public void sendall(View view)

    {

        cycleidcount = cycleidcount + 1;
        strings = new String[cycleidcount];
        strings[0] = checkoutVehicleID.getText().toString().trim();
        for (int i = 1; i < cycleidcount; i++) {
            strings[i] = Allcycleid.get(i - 1).getText().toString();
            Log.d("string value", String.valueOf(strings));
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
            Calendar calendar = Calendar.getInstance();
            // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());

            /*GMT +5:30*/
            // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        /*Ends*/

        /*GMT 0*/
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final String checkouttime = dateFormatGmt.format(new Date()) + "";
        /*Ends*/


            sendToserver(strings[j]);


        }
        /*cycleidcount = Allcycleid.size();
        Holdingarea = "";
        HAportid = "";
        Maintenance = "";
        MCportid = "";
        Restribution = "";
        RVportid = "";
        Fleet = "";
        Fleetportid = "";
        checkoutdockingstations();
        checkoutVehicleID.setText("");
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
                // ((ViewGroup) ll.getParent()).removeView(ll);
                try {
                    cycleidcount = Allcycleid.size();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if (errorstatus.equals("0")) {
                        Toast.makeText(Checkoutmanually.this, "Check out Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        CheckoutErrorlayout.setVisibility(View.VISIBLE);
                        String errormessage = data.getString("errorMsg");
                        Toast.makeText(Checkoutmanually.this, str + " Check out Unsuccessfull ", Toast.LENGTH_SHORT).show();
                        LinearLayout checkouterror = new LinearLayout(Checkoutmanually.this);
                        LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        errorparams.weight = 1;
                        checkouterror.setOrientation(LinearLayout.HORIZONTAL);
                        TextView errorcycleid = new TextView(Checkoutmanually.this);
                        errorcycleid.setLayoutParams(errorparams);
                        errorcycleid.setText(str);
                        errorcycleid.setTextSize(16);
                        errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        TextView errormess = new TextView(Checkoutmanually.this);
                        errormess.setText(errormessage);
                        errormess.setTextSize(16);
                        errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        errormess.setLayoutParams(errorparams);
                        checkouterror.addView(errorcycleid);
                        checkouterror.addView(errormess);
                        CheckoutErrorlayout.addView(checkouterror);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutmanually.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

                params.put("vehicleId", str);
                params.put("cardId", checkoutCardID.getText().toString().trim());
                params.put("fromPort", HAportid + "" + RVportid + "" + MCportid + "" + Fleetportid);
                params.put("checkOutTime", checkouttime);
                return params;
            }
        };

        PBSSingleton.getInstance(this).addtorequestqueue(checkoutrequest);
        //Toast.makeText(this,HAportid+""+RVportid+""+MCportid+""+Fleetportid, Toast.LENGTH_LONG).show();


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
                        Log.d("ID", id);
                        FleetIDArrayList.add(id);
                        FleetNameArrayList.add(name);
                        Log.d("Array ID", String.valueOf(FleetIDArrayList));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    //Toast.makeText(Splash.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Toast.makeText(Checkoutmanually.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                        Log.d("RV", id);
                        RVIDArrayList.add(id);
                        Log.d("Array ID", String.valueOf(RVIDArrayList));
                        RVNameArrayList.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutmanually.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                        Log.d("MC ID", id);
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

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutmanually.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(maintenancerequest);
    }

    public void getHAdetails()

    {
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
                        Log.d("HA ID", id);
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

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutmanually.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(holdingarearequest);
    }


    private void checkoutdockingstations() {

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
                        //checkoutPortid=TransactionAPI.fleetid;
                        Fleetspinner.setVisibility(View.VISIBLE);
                        // HAportid=RVportid=MCportid=Fleetportid=Holdingarea=Restribution=Maintenance=Fleet="";
                        HAportid = RVportid = MCportid = Holdingarea = Restribution = Maintenance = "";
                        Fleetadapter = new ArrayAdapter<String>(Checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
                        Fleetadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Fleetspinner.setAdapter(Fleetadapter);
                        Fleetspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Fleet = Fleetspinner.getSelectedItem().toString();
                                Fleetportid = FleetIDArrayList.get(position);
                                Log.d("Holding area port id", Fleetportid);
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
                        //checkoutPortid=TransactionAPI.holdingareaid;
                        HAspinner.setVisibility(View.VISIBLE);
                        RVportid = MCportid = Fleetportid = Restribution = Maintenance = Fleet = "";
                        HAadapter = new ArrayAdapter<String>(Checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
                        HAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HAspinner.setAdapter(HAadapter);
                        HAspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea = HAspinner.getSelectedItem().toString();
                                HAportid = HAIDArrayList.get(position);
                                Log.d("Holding area port id", HAportid);
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
                        // checkoutPortid=TransactionAPI.redistrubutionid;
                        RVspinner.setVisibility(View.VISIBLE);
                        HAportid = MCportid = Fleetportid = Holdingarea = Maintenance = Fleet = "";
                        RVadapter = new ArrayAdapter<String>(Checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, RVNameArrayList);
                        RVadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RVspinner.setAdapter(RVadapter);
                        RVspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution = RVspinner.getSelectedItem().toString();
                                RVportid = RVIDArrayList.get(position);
                                Log.d("Redistribution port id", RVportid);
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
                        //checkoutPortid=TransactionAPI.maintainenceid;
                        MCspinner.setVisibility(View.VISIBLE);
                        HAportid = RVportid = Fleetportid = Holdingarea = Restribution = Fleet = "";
                        MCadapter = new ArrayAdapter<String>(Checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
                        MCadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // MCadapter.add("Select Maintenance Center");
                        MCspinner.setAdapter(MCadapter);
                        MCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance = MCspinner.getSelectedItem().toString();
                                MCportid = MCIDArrayList.get(position);
                                Log.d("Maintenance port id", MCportid);
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


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Checkoutmanually.this);
            builder.setIcon(R.drawable.ic_wifi);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                            //startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
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


    public void getcheckoutdetails() {
        //Holdingarea=HAspinner.getSelectedItem().toString();
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
        // Maintenance=MCspinner.getSelectedItem().toString();
        if (Maintenance == "" || MCportid == null) {
            Maintenance = "";
            MCportid = "";
        } else {
            Maintenance = MCspinner.getSelectedItem().toString();
        }
        // Restribution=RVspinner.getSelectedItem().toString();
        if (Restribution == "" || RVportid == null) {
            Restribution = "";
            RVportid = "";
        } else {
            Restribution = RVspinner.getSelectedItem().toString();
        }

    }


    public void sendcheckoutdetails(View view) {
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
            return;
        }
        Calendar calendar = Calendar.getInstance();
        // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());

            /*GMT +5:30*/
        // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        /*Ends*/

        /*GMT 0*/
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkouttime = dateFormatGmt.format(new Date()) + "";
        /*Ends*/

        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, API.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response", response);
                Holdingarea = "";
                HAportid = "";
                Maintenance = "";
                MCportid = "";
                Restribution = "";
                RVportid = "";
                Fleet = "";
                Fleetportid = "";
                checkoutdockingstations();
                checkoutVehicleID.setText("");
                checkoutCardID.setText("");
                Toast.makeText(Checkoutmanually.this, "Check out Successfully", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutmanually.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutmanually.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("vehicleId", checkoutVehicleID.getText().toString().trim());
                params.put("cardId", checkoutCardID.getText().toString().trim());
                //params.put("fromPort",coFromport.getText().toString().trim());
                // params.put("fromPort",checkoutPortid);
                params.put("fromPort", HAportid + "" + RVportid + "" + MCportid + "" + Fleetportid);
                params.put("checkOutTime", checkouttime);
                return params;
            }
        };

        PBSSingleton.getInstance(this).addtorequestqueue(checkoutrequest);
        //Toast.makeText(this,HAportid+""+RVportid+""+MCportid+""+Fleetportid, Toast.LENGTH_LONG).show();


    }


}
