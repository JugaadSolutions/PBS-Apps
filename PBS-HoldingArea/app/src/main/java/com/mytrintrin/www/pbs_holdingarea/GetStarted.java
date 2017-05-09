package com.mytrintrin.www.pbs_holdingarea;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.TimeZone;

public class GetStarted extends AppCompatActivity {

    public static ArrayList<String> HANameArrayList = new ArrayList<String>();
    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    public static ArrayList<String> HAIDArrayList = new ArrayList<String>();
    public static ArrayList<String> FleetIDArrayList = new ArrayList<String>();
    public static ArrayList<String> VehicleNumberArrayList = new ArrayList<String>();

    public static Spinner HAspinner, Fleetspinner;
    public ArrayAdapter<String> HAadapter, FleetAdapter;
    JSONArray VehiclesinallHA, VehiclesinHA;

    private Toolbar GetStartedToolbar;
    String ha_StationName, ha_StationId, fleetname, cyclenum, cardnum;

    SharedPreferences fleetpref;
    SharedPreferences.Editor fleeteditor;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    EditText CycleNumber, CardNumber;
    Button CheckIn, Checkout;
    int numberOfDays;

    LinearLayout ErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        checkinternet();

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        HAadapter = new ArrayAdapter<String>(GetStarted.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
        VehiclesinallHA = new JSONArray();
        VehiclesinHA = new JSONArray();
        CycleNumber = (EditText) findViewById(R.id.cycleno_ha);
        CardNumber = (EditText) findViewById(R.id.cardno_ha);
        CycleNumber.addTextChangedListener(Cyclewatcher);
        CardNumber.addTextChangedListener(Cardwatcher);
        CheckIn = (Button) findViewById(R.id.btncheckin);
        Checkout = (Button) findViewById(R.id.btncheckout);
        ErrorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        getHAdetails();
        GetStartedToolbar = (Toolbar) findViewById(R.id.getstartedtoolbar);

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();

        fleetpref = getApplicationContext().getSharedPreferences("fleetPref", MODE_PRIVATE);
        fleeteditor = fleetpref.edit();
        if (fleetpref.contains("FleetName")) {
            fleetname = fleetpref.getString("FleetName", null);
            if (fleetname.equals("") || fleetname.equals(null)) {
                getfleetnames();
            } else {
                fleetname = fleetpref.getString("FleetName", null);
            }
        } else {
            getfleetnames();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_getstarted, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.changefleet_getstarted) {
            getfleetnames();
        } else if (item.getItemId() == R.id.changeha_getstarted) {
            getHAdetails();
        } else if (item.getItemId() == R.id.logout_ha) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            editor.putString("User-id", "");
            editor.putString("Role", "");
            editor.commit();
            startActivity(new Intent(this, Login.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    TextWatcher Cyclewatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 3 || editable.length() > 3)
                checkcycleinha();
        }
    };

    TextWatcher Cardwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 3 || editable.length() > 3)
                cardnum = CardNumber.getText().toString().trim();
            getmemberdetails();
        }
    };

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    GetStarted.this);
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
            builder.setNegativeButton("Check Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/


    public void getHAdetails() {
        StringRequest holdingarearequest = new StringRequest(Request.Method.GET, API.HAidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject holdingareafromserver = new JSONObject(response);
                    JSONArray holdingareadataarray = holdingareafromserver.getJSONArray("data");
                    HANameArrayList.clear();
                    HAIDArrayList.clear();
                    for (int i = 0; i < holdingareadataarray.length(); i++) {
                        JSONObject holdingareaid = holdingareadataarray.getJSONObject(i);
                        String id = holdingareaid.getString("_id");
                        String name = holdingareaid.getString("Name");
                        HANameArrayList.add(name);
                        HAIDArrayList.add(id);
                        JSONArray vehiclearray = holdingareaid.getJSONArray("vehicleId");
                        VehiclesinallHA.put(vehiclearray);
                    }
                    showholdingstation();
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
                    Toast.makeText(GetStarted.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void showholdingstation() {
        AlertDialog.Builder Stationdialog = new AlertDialog.Builder(this);
        Stationdialog.setTitle("Holding Area");
        Stationdialog.setMessage("Please select the station");
        Stationdialog.setIcon(R.drawable.splashlogo);
        LayoutInflater hainflate = LayoutInflater.from(this);
        View haView = hainflate.inflate(R.layout.holdingstation, null);
        HAadapter = new ArrayAdapter<String>(GetStarted.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
        HAspinner = (Spinner) haView.findViewById(R.id.holdingstationspinner);
        HAspinner.setAdapter(HAadapter);
        HAspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ha_StationId = HAIDArrayList.get(i);
                ha_StationName = HANameArrayList.get(i);
                try {
                    VehiclesinHA = VehiclesinallHA.getJSONArray(i);
                    getvehiclesinholdingarea();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(GetStarted.this, String.valueOf(ha_StationName), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Stationdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GetStartedToolbar.setTitle(ha_StationName);
                setSupportActionBar(GetStartedToolbar);
                dialogInterface.dismiss();
            }
        });
        Stationdialog.setView(haView);
        Stationdialog.setCancelable(false);
        Stationdialog.show();
    }


    private void getfleetnames() {
        checkinternet();
        StringRequest fleetrequest = new StringRequest(Request.Method.GET, API.fleetidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject fleetresponsefronserver = new JSONObject(response);
                    JSONArray fleetdataarray = fleetresponsefronserver.getJSONArray("data");
                    FleetNameArrayList.clear();
                    FleetIDArrayList.clear();
                    for (int i = 0; i < fleetdataarray.length(); i++) {
                        JSONObject getid = fleetdataarray.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("Name");
                        FleetIDArrayList.add(id);
                        FleetNameArrayList.add(name);
                    }
                    showfleetnames();
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
                    Toast.makeText(GetStarted.this, "Server is under maintainence", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
                    builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinternet();
                        }
                    });
                    builder.show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        fleetrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(fleetrequest);
    }

    public void showfleetnames() {
        AlertDialog.Builder Fleetdialog = new AlertDialog.Builder(this);
        Fleetdialog.setTitle("Fleet");
        Fleetdialog.setMessage("Please select the fleet");
        Fleetdialog.setIcon(R.drawable.splashlogo);
        LayoutInflater fleetinflate = LayoutInflater.from(this);
        View fleetView = fleetinflate.inflate(R.layout.fleets, null);
        FleetAdapter = new ArrayAdapter<String>(GetStarted.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
        Fleetspinner = (Spinner) fleetView.findViewById(R.id.fleetsspinner);
        Fleetspinner.setAdapter(FleetAdapter);
        Fleetspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fleetname = FleetNameArrayList.get(i);
                Toast.makeText(GetStarted.this, String.valueOf(fleetname), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Fleetdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fleeteditor.putString("FleetName", fleetname);
                fleeteditor.commit();
                Toast.makeText(GetStarted.this, fleetname, Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        Fleetdialog.setView(fleetView);
        Fleetdialog.setCancelable(false);
        Fleetdialog.show();
    }

    public void getvehiclesinholdingarea() {
        if (VehiclesinHA.length() > 0) {
            VehicleNumberArrayList.clear();
            for (int i = 0; i <= VehiclesinHA.length(); i++) {
                try {
                    JSONObject vehicle = VehiclesinHA.getJSONObject(i);
                    JSONObject vehicledetails = vehicle.getJSONObject("vehicleid");
                    String vehicleno = vehicledetails.getString("vehicleNumber");
                    VehicleNumberArrayList.add(vehicleno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(GetStarted.this, "No Vehicles found in holding area", Toast.LENGTH_SHORT).show();
        }
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

    public void checkcycleinha() {
        if (VehicleNumberArrayList.contains(CycleNumber.getText().toString())) {
            Toast.makeText(this, "Vehicle found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Vehicle found by this number", Toast.LENGTH_SHORT).show();
        }
    }

    public void getmemberdetails() {
        CheckIn.setVisibility(View.GONE);
        Checkout.setVisibility(View.GONE);
        StringRequest GetUserDetailsRequest = new StringRequest(Request.Method.POST, API.getmemberdetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if (data.length() == 0) {
                        Toast.makeText(GetStarted.this, "Invalid User", Toast.LENGTH_LONG).show();
                    } else if (data.length() > 0) {
                        JSONObject resultobject = data.getJSONObject(0);
                        JSONArray VehicleArray = resultobject.getJSONArray("vehicleId");
                        String Role = resultobject.getString("_type");
                        if(Role.equals("member"))
                        {
                            int status = resultobject.getInt("status");
                            if (status == 1) {
                                if (VehicleArray.length() > 0) {
                                    Toast.makeText(GetStarted.this, "You have already taken bicycle", Toast.LENGTH_SHORT).show();
                                    android.app.AlertDialog.Builder VehicleBuilder = new android.app.AlertDialog.Builder(GetStarted.this);
                                    VehicleBuilder.setIcon(R.drawable.splashlogo);
                                    VehicleBuilder.setTitle("Holding Area");
                                    VehicleBuilder.setMessage("User has already taken bicycle! User can only check-in the cycle.");
                                    VehicleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            CheckIn.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    VehicleBuilder.setCancelable(false);
                                    VehicleBuilder.show();
                                } else if (VehicleArray.length() == 0) {
                                    String Balance = resultobject.getString("creditBalance");
                                    String Validity = resultobject.getString("validity");
                                /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                String currentDateandTime = sdf.format(new Date());*/
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    String currentDateandTime = sdf.format(new Date());
                                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    try {
                                        Date uservalidity = myFormat.parse(Validity);
                                        Date currentdate = myFormat.parse(currentDateandTime);
                                        long diff = uservalidity.getTime() - currentdate.getTime();
                                        int numOfYear = (int) ((diff / (1000 * 60 * 60 * 24)) / 365);
                                        numberOfDays = (int) (diff / (1000 * 60 * 60 * 24));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (Integer.parseInt(Balance) > 0 && numberOfDays > 0) {

                                        AlertDialog.Builder UserBuilder = new AlertDialog.Builder(GetStarted.this);
                                        UserBuilder.setTitle("Holding Area");
                                        UserBuilder.setMessage("User Details");
                                        UserBuilder.setIcon(R.drawable.splashlogo);
                                        LayoutInflater hainflate = LayoutInflater.from(GetStarted.this);
                                        View userView = hainflate.inflate(R.layout.userdetails, null);
                                        TextView username = (TextView) userView.findViewById(R.id.username);
                                        TextView balance = (TextView) userView.findViewById(R.id.balance_user);
                                        ImageView userphoto = (ImageView) userView.findViewById(R.id.userpic);
                                        TextView validity = (TextView) userView.findViewById(R.id.validity_user);
                                        TextView plan = (TextView) userView.findViewById(R.id.plan_user);
                                        username.setText(resultobject.getString("Name"));
                                        balance.setText(resultobject.getString("creditBalance"));
                                        String userprofilepic = resultobject.getString("profilePic");
                                        String userid = resultobject.getString("UserID");
                                        Bitmap pic;
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        try {
                                            //URL url = new URL("https://www.mytrintrin.com/mytrintrin/Member/" + Userid + "/" + profilepics + ".png");
                                            URL url = new URL("http://43.251.80.79/mytrintrin/Member/" + userid + "/" + userprofilepic + ".png");
                                            pic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                            userphoto.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
                                        } catch (IOException e) {
                                            Log.e("TAG", e.getMessage());
                                        }
                                        UserBuilder.setView(userView);
                                        UserBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        UserBuilder.setCancelable(false);
                                        UserBuilder.show();
                                        Checkout.setVisibility(View.VISIBLE);
                                    } else {
                                        android.app.AlertDialog.Builder ValidityeBuilder = new android.app.AlertDialog.Builder(GetStarted.this);
                                        ValidityeBuilder.setIcon(R.drawable.splashlogo);
                                        ValidityeBuilder.setTitle("Holding Area");
                                        ValidityeBuilder.setMessage("User doesn't have sufficient balance or validity expired.Please Contact 0821-2333000.");
                                        ValidityeBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                CheckIn.setVisibility(View.VISIBLE);
                                            }
                                        });
                                        ValidityeBuilder.setCancelable(false);
                                        ValidityeBuilder.show();
                                    }
                                }
                            } else {
                                CheckIn.setVisibility(View.GONE);
                                Checkout.setVisibility(View.GONE);
                                android.app.AlertDialog.Builder userErrorBuilder = new android.app.AlertDialog.Builder(GetStarted.this);
                                userErrorBuilder.setIcon(R.drawable.splashlogo);
                                userErrorBuilder.setTitle("Holding Area");
                                userErrorBuilder.setMessage("User doesn't have authority to use PBS.Please Contact 0821-2333000");
                                userErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                userErrorBuilder.setCancelable(false);
                                userErrorBuilder.show();
                            }
                        }
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
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            GetStarted.this);
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
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("name", cardnum);
                return params;
            }
        };
        GetUserDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(GetUserDetailsRequest);
    }

    public void checkoutfromHA(View view)
    {
        if(CycleNumber.getText().toString().equals("")||CycleNumber.getText().toString().equals(null)||CycleNumber.getText().toString().length()<3)
        {
            CycleNumber.setError("Cycle Number");
            return;
        }
        if(CardNumber.getText().toString().equals("")||CardNumber.getText().toString().equals(null)||CardNumber.getText().toString().length()<3)
        {
            CardNumber.setError("Card Number");
            return;
        }
        ErrorLayout.setVisibility(View.INVISIBLE);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkouttime = dateFormatGmt.format(new Date()) + "";
        Checkout.setVisibility(View.GONE);
        CheckIn.setVisibility(View.GONE);

        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, API.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CardNumber.setText("");
                CheckIn.requestFocus();
                CycleNumber.setText("");
                CheckIn.setVisibility(View.GONE);
                Checkout.setVisibility(View.GONE);
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if(errorstatus.equals("1"))
                    {
                        String errormessage = data.getString("errorMsg");
                        ErrorLayout.setVisibility(View.VISIBLE);
                        TextView errormess = new TextView(GetStarted.this);
                        errormess.setText(errormessage);
                        ErrorLayout.addView(errormess);

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
                    Toast.makeText(GetStarted.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("vehicleId",CycleNumber.getText().toString().trim());
                params.put("cardId", CardNumber.getText().toString().trim());
                params.put("fromPort",ha_StationId);
                params.put("checkOutTime", checkouttime);
                return params;
            }
        };
        checkoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkoutrequest);
    }

    public void checkintoHA(View view)
    {
        if(CycleNumber.getText().toString().equals("")||CycleNumber.getText().toString().equals(null)||CycleNumber.getText().toString().length()<3)
        {
                CycleNumber.setError("Cycle Number");
            return;
        }
        if(CardNumber.getText().toString().equals("")||CardNumber.getText().toString().equals(null)||CardNumber.getText().toString().length()<3)
        {
            CardNumber.setError("Card Number");
            return;
        }
        ErrorLayout.setVisibility(View.INVISIBLE);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkintime = dateFormatGmt.format(new Date()) + "";
        Checkout.setVisibility(View.GONE);
        CheckIn.setVisibility(View.GONE);

        StringRequest checkinrequest = new StringRequest(Request.Method.POST, API.checkinurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check in Response", response);
                CardNumber.setText("");
                CheckIn.requestFocus();
                CycleNumber.setText("");
                CheckIn.setVisibility(View.GONE);
                Checkout.setVisibility(View.GONE);
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if(errorstatus.equals("1"))
                    {
                        String errormessage = data.getString("errormsg");
                        ErrorLayout.setVisibility(View.VISIBLE);
                        TextView errormess = new TextView(GetStarted.this);
                        errormess.setText(errormessage);
                        ErrorLayout.addView(errormess);

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
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("vehicleId",CycleNumber.getText().toString().trim());
                params.put("cardId", CardNumber.getText().toString().trim());
                params.put("toPort", ha_StationId);
                params.put("checkInTime", checkintime);
                return params;
            }
        };
        checkinrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(checkinrequest);
    }
}
