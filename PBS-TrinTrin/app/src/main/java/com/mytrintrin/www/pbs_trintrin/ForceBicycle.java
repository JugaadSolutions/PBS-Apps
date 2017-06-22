package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForceBicycle extends AppCompatActivity implements LocationListener {

    Spinner Station, Port;

    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();

    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();

    public ArrayAdapter<String> Stationadapter, Portadapter;
    String StationName, StationId, PortName, PortId;
    JSONArray StationArray, PortArray;
    JSONObject StationObject;

    public static ArrayList<String> CycleNumberList = new ArrayList<>();
    public static ArrayList<String> ForcePortIdList = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    Toolbar ForcebicylceToolbar;
    EditText Bicyclenumber,Stationname;

    LocationManager locationManager;
    String mprovider;
    Location currentlocation;

    double doclatitude;
    double docllongitude;

    ArrayList<Location> dockingstationlocation = new ArrayList<>();

    JSONObject Stationobject;
    int noofports,portselection=0;;
    Button Next,Submit;
    JSONArray Forcecyclearray;
    JSONObject Forcecycleobject,FinalObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.force_bicycle);
        //Station = (Spinner) findViewById(R.id.stationspinner);
        Stationname = (EditText) findViewById(R.id.stationame_force);
        Port = (Spinner) findViewById(R.id.portspinner);
        ForcebicylceToolbar = (Toolbar) findViewById(R.id.forcetoolbar);
        ForcebicylceToolbar.setTitle("Force Cycle");
        setSupportActionBar(ForcebicylceToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bicyclenumber = (EditText) findViewById(R.id.cyclenumber_force);
        Next= (Button) findViewById(R.id.next_unlock);
        Submit = (Button) findViewById(R.id.submit_unlock);
        FinalObject = new JSONObject();

         /* to get location*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mprovider = locationManager.getBestProvider(criteria, false);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);
            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }
                /*ends*/
        getalldockingstation();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    ForceBicycle.this);
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

    public void getalldockingstation() {
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
                    dockingstationlocation.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject dscoordinates = data.getJSONObject(i);
                        JSONObject coordinates = dscoordinates.getJSONObject("gpsCoordinates");
                        String lat = coordinates.getString("latitude");
                        String lang = coordinates.getString("longitude");
                        doclatitude = Double.parseDouble(lat);
                        docllongitude = Double.parseDouble(lang);
                        Location dockinglocation = new Location("");
                        dockinglocation.setLatitude(doclatitude);
                        dockinglocation.setLongitude(docllongitude);
                        dockingstationlocation.add(dockinglocation);
                        String id = dscoordinates.getString("StationID");
                        final String Stationname = dscoordinates.getString("name");
                        JSONArray ports = dscoordinates.getJSONArray("portIds");
                        StationIDArrayList.add(id);
                        StationNameArrayList.add(Stationname);
                        StationArray.put(dscoordinates);
                        PortArray.put(ports);
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
                    Toast.makeText(ForceBicycle.this, "Server is under maintenance,Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ForceBicycle.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ForceBicycle.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ForceBicycle.this, "Please Check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            ForceBicycle.this);
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
                    Toast.makeText(ForceBicycle.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ForceBicycle.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ForceBicycle.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        for (int i = 0; i < dockingstationlocation.size(); i++) {
            float distance = currentlocation.distanceTo(dockingstationlocation.get(i));
            if (distance < 100) {
                String Station = StationNameArrayList.get(i);
                Toast.makeText(this, Station, Toast.LENGTH_SHORT).show();
                Stationname.setText(Station);
                Stationname.setEnabled(false);
                StationId = StationIDArrayList.get(i);
                try {
                    Stationobject = StationArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getportdetails();
                break;
            }
        }
    }


    public void getstationdetails() {
        Stationadapter = new ArrayAdapter<String>(ForceBicycle.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
        Stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Station.setAdapter(Stationadapter);
        Station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StationName = Station.getSelectedItem().toString();
                StationId = StationIDArrayList.get(i);
                try {
                    StationObject = StationArray.getJSONObject(i);
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
                Toast.makeText(ForceBicycle.this, StationId, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void getportdetails() {
        try {
            JSONArray ports = Stationobject.getJSONArray("portIds");
            noofports= Stationobject.getInt("noofPorts");
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
        Portadapter = new ArrayAdapter<String>(ForceBicycle.this, android.R.layout.simple_spinner_dropdown_item, PortNameArrayList);
        Portadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Port.setAdapter(Portadapter);
        Port.setEnabled(false);
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

    public void takenextportdetails(View view)
    {

        String cyclenum = Bicyclenumber.getText().toString().trim();
        if(cyclenum.equals(""))
        {
            cyclenum = "-";
            CycleNumberList.add(cyclenum);
            ForcePortIdList.add(PortId);
            Bicyclenumber.setText("");
        }
        else
        {
            cyclenum="MYS-Fleet-"+cyclenum;
            CycleNumberList.add(cyclenum);
            ForcePortIdList.add(PortId);
            Bicyclenumber.setText("");
        }
        portselection++;
        Toast.makeText(this, "Cycle :"+cyclenum+" Port No :"+PortId, Toast.LENGTH_SHORT).show();
        if(portselection==noofports)
        {
            Next.setVisibility(View.GONE);
            Submit.setVisibility(View.VISIBLE);
        }
        else
        {
            Port.setSelection(portselection);
        }

    }

    public void submitunlockdetails(View view)
    {
        Forcecyclearray = new JSONArray();
        for(int j =0 ; j<ForcePortIdList.size();j++)
        {
            try {
                Forcecycleobject= new JSONObject();
                Forcecycleobject.put("portId",ForcePortIdList.get(j));
                Forcecycleobject.put("vehicleId",CycleNumberList.get(j));
                Forcecyclearray.put(j,Forcecycleobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            FinalObject.put("forceList",Forcecyclearray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        forcecycle();
    }

    public void forcecycle()
    {
        JsonObjectRequest forcebicyclerequest = new JsonObjectRequest(Request.Method.POST, API.forcebicycle,FinalObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Port.setSelection(0);
                    portselection=0;
                    Next.setVisibility(View.VISIBLE);
                    Submit.setVisibility(View.GONE);
                    ForcePortIdList.clear();
                    CycleNumberList.clear();
                    JSONObject responsefromserver = response;
                    String message = responsefromserver.getString("message");
                    AlertDialog.Builder Successbuilder = new AlertDialog.Builder(
                            ForceBicycle.this);
                    Successbuilder.setIcon(R.drawable.splashlogo);
                    Successbuilder.setTitle("Force Cycle");
                    Successbuilder.setMessage(message);
                    Successbuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    Successbuilder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Port.setSelection(0);
                portselection=0;
                Next.setVisibility(View.GONE);
                Submit.setVisibility(View.VISIBLE);
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(ForceBicycle.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ForceBicycle.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ForceBicycle.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ForceBicycle.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ForceBicycle.this);
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
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ForceBicycle.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ForceBicycle.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ForceBicycle.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }){

        };

        forcebicyclerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(forcebicyclerequest);
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

    @Override
    public void onLocationChanged(Location location) {
        currentlocation = new Location("");
        currentlocation.setLatitude(location.getLatitude());
        currentlocation.setLongitude(location.getLongitude());
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
