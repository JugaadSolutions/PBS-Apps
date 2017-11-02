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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
    TextView Stationname;

    LocationManager locationManager;
    String mprovider;
    Location currentlocation;

    double doclatitude;
    double docllongitude;

    ArrayList<Location> dockingstationlocation = new ArrayList<>();
    ArrayList<Ports> ChekinList = new ArrayList<>();

    JSONObject Stationobject;
    int noofports,portselection=0,k;;
    Button Next,Submit;
    JSONArray Forcecyclearray;
    JSONObject Forcecycleobject,FinalObject;
    EditText FPGAu3p1,FPGAu3p2,FPGAu3p3,FPGAu3p4, FPGAu4p1,FPGAu4p2,FPGAu4p3,FPGAu4p4,FPGAu5p1,FPGAu5p2,FPGAu5p3,FPGAu5p4,FPGAu6p1,FPGAu6p2,FPGAu6p3,FPGAu6p4;;
    LinearLayout FPGA6Layout;

    String PortID,VehicleNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.force_bicycle);
        Station = (Spinner) findViewById(R.id.stations_force);
        ForcebicylceToolbar = (Toolbar) findViewById(R.id.forcetoolbar);
        ForcebicylceToolbar.setTitle("Force Cycle");
        setSupportActionBar(ForcebicylceToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FPGA6Layout = (LinearLayout) findViewById(R.id.fpga6layout);
        Submit = (Button) findViewById(R.id.submit_unlock);

        FPGAu3p1 = (EditText) findViewById(R.id.fpgau3p1);
        FPGAu3p2 = (EditText) findViewById(R.id.fpgau3p2);
        FPGAu3p3 = (EditText) findViewById(R.id.fpgau3p3);
        FPGAu3p4 = (EditText) findViewById(R.id.fpgau3p4);

        FPGAu4p1 = (EditText) findViewById(R.id.fpgau4p1);
        FPGAu4p2 = (EditText) findViewById(R.id.fpgau4p2);
        FPGAu4p3 = (EditText) findViewById(R.id.fpgau4p3);
        FPGAu4p4 = (EditText) findViewById(R.id.fpgau4p4);

        FPGAu5p1 = (EditText) findViewById(R.id.fpgau5p1);
        FPGAu5p2 = (EditText) findViewById(R.id.fpgau5p2);
        FPGAu5p3 = (EditText) findViewById(R.id.fpgau5p3);
        FPGAu5p4 = (EditText) findViewById(R.id.fpgau5p4);

        FPGAu6p1 = (EditText) findViewById(R.id.fpgau6p1);
        FPGAu6p2 = (EditText) findViewById(R.id.fpgau6p2);
        FPGAu6p3 = (EditText) findViewById(R.id.fpgau6p3);
        FPGAu6p4 = (EditText) findViewById(R.id.fpgau6p4);

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
            locationManager.requestLocationUpdates(mprovider, 10000, 1, this);
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


                        /*float distance = currentlocation.distanceTo(dockinglocation);
                        if(distance<30) {
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
        if(StationNameArrayList.size()>0)
        {
            Stationadapter = new ArrayAdapter<String>(ForceBicycle.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
            Stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Station.setAdapter(Stationadapter);
            Station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    StationName = Station.getSelectedItem().toString();
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
                    Toast.makeText(ForceBicycle.this, StationId, Toast.LENGTH_SHORT).show();
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
        mProgressDialog.dismiss();
        try {
            JSONArray ports = Stationobject.getJSONArray("portIds");
            noofports= Stationobject.getInt("noofPorts");
            FPGA6Layout.setVisibility(View.VISIBLE);
            if(noofports==12)
            {
                FPGA6Layout.setVisibility(View.GONE);
            }
            PortIDArrayList.clear();
            PortNameArrayList.clear();
            for (int j = 0; j < ports.length(); j++) {
                JSONObject port = ports.getJSONObject(j);
                JSONObject dockports = port.getJSONObject("dockingPortId");
                int FPGA = dockports.getInt("FPGA");
                PortID = dockports.getString("PortID");
                PortIDArrayList.add(PortID);
                if(FPGA==3) {
                    int eportno = dockports.getInt("ePortNumber");
                    JSONArray vehicleId = dockports.getJSONArray("vehicleId");
                    if (vehicleId.length() > 0) {
                        JSONObject vehicleobject = vehicleId.getJSONObject(0);
                        JSONObject vehicle = vehicleobject.getJSONObject("vehicleid");
                         VehicleNumber = vehicle.getString("vehicleNumber");
                        CycleNumberList.add(VehicleNumber);
                        if (eportno == 1) {
                            FPGAu3p1.setText(VehicleNumber.substring(10));

                        }
                        else if(eportno == 2)
                        {
                            FPGAu3p2.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 3)
                        {
                            FPGAu3p3.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 4)
                        {
                            FPGAu3p4.setText(VehicleNumber.substring(10));
                        }
                        //Ports cycleandportobject = new Ports(PortID,VehicleNumber);
                    }
                    else
                    {
                       VehicleNumber = "";
                        //Ports ClearCheckinobject = new Ports(PortId,VehicleNumber);
                        CycleNumberList.add(VehicleNumber);
                    }

                }
                if(FPGA==4) {
                    int eportno = dockports.getInt("ePortNumber");
                    JSONArray vehicleId = dockports.getJSONArray("vehicleId");
                    if (vehicleId.length() > 0) {
                        JSONObject vehicleobject = vehicleId.getJSONObject(0);
                        JSONObject vehicle = vehicleobject.getJSONObject("vehicleid");
                        VehicleNumber = vehicle.getString("vehicleNumber");
                        if (eportno == 1) {
                            FPGAu4p1.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 2)
                        {
                            FPGAu4p2.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 3)
                        {
                            FPGAu4p3.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 4)
                        {
                            FPGAu4p4.setText(VehicleNumber.substring(10));
                        }
                        //Ports cycleandportobject = new Ports(PortId,VehicleNumber);
                    }
                   else
                    {
                        VehicleNumber = "";
                        CycleNumberList.add(VehicleNumber);
                        //Ports ClearCheckinobject = new Ports(PortId,VehicleNumber);
                    }
                }
                if(FPGA==5) {
                    int eportno = dockports.getInt("ePortNumber");
                    JSONArray vehicleId = dockports.getJSONArray("vehicleId");
                    if (vehicleId.length() > 0) {
                        JSONObject vehicleobject = vehicleId.getJSONObject(0);
                        JSONObject vehicle = vehicleobject.getJSONObject("vehicleid");
                        VehicleNumber = vehicle.getString("vehicleNumber");
                        if (eportno == 1) {
                            FPGAu5p1.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 2)
                        {
                            FPGAu5p2.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 3)
                        {
                            FPGAu5p3.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 4)
                        {
                            FPGAu5p4.setText(VehicleNumber.substring(10));
                        }
                    }
                   else {
                        VehicleNumber = "";
                        CycleNumberList.add(VehicleNumber);
                        //Ports ClearCheckinobject = new Ports(PortId,VehicleNumber);
                    }
                }

                if(FPGA==6) {
                    int eportno = dockports.getInt("ePortNumber");
                    JSONArray vehicleId = dockports.getJSONArray("vehicleId");
                    if (vehicleId.length() > 0) {
                        JSONObject vehicleobject = vehicleId.getJSONObject(0);
                        JSONObject vehicle = vehicleobject.getJSONObject("vehicleid");
                        VehicleNumber = vehicle.getString("vehicleNumber");
                        if (eportno == 1) {
                            FPGAu6p1.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 2)
                        {
                            FPGAu6p2.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 3)
                        {
                            FPGAu6p3.setText(VehicleNumber.substring(10));
                        }
                        else if(eportno == 4)
                        {
                            FPGAu6p4.setText(VehicleNumber.substring(10));
                        }
                    }
                    else{
                        VehicleNumber = "";
                        CycleNumberList.add(VehicleNumber);
                        //Ports ClearCheckinobject = new Ports(PortId,VehicleNumber);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            //setports();
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
        //forcecycle();
    }

    public void submitportdetailsandcheckin(View view)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("updating ports is in progress...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        CycleNumberList.clear();
        ChekinList.clear();
        String u3p1 =FPGAu3p1.getText().toString().trim();
        if(u3p1.equals("")) {
            CycleNumberList.add(u3p1);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u3p1);
        }
        String u3p2 =FPGAu3p2.getText().toString().trim();
        if(u3p2.equals("")) {
            CycleNumberList.add(u3p2);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u3p2);
        }
        String u3p3 =FPGAu3p3.getText().toString().trim();
        if(u3p3.equals("")) {
            CycleNumberList.add(u3p3);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u3p3);
        }
        String u3p4 =FPGAu3p4.getText().toString().trim();
        if(u3p4.equals("")) {
            CycleNumberList.add(u3p4);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u3p4);
        }
        String u4p1 =FPGAu4p1.getText().toString().trim();
        if(u4p1.equals("")) {
            CycleNumberList.add(u4p1);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u4p1);
        }
        String u4p2 =FPGAu4p2.getText().toString().trim();
        if(u4p2.equals("")) {
            CycleNumberList.add(u4p2);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u4p2);
        }
        String u4p3 =FPGAu4p3.getText().toString().trim();
        if(u4p3.equals("")) {
            CycleNumberList.add(u4p3);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u4p3);
        }
        String u4p4 =FPGAu4p4.getText().toString().trim();
        if(u4p4.equals("")) {
            CycleNumberList.add(u4p4);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u4p4);
        }
        String u5p1 =FPGAu5p1.getText().toString().trim();
        if(u5p1.equals("")) {
            CycleNumberList.add(u5p1);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u5p1);
        }
        String u5p2 =FPGAu5p2.getText().toString().trim();
        if(u5p2.equals("")) {
            CycleNumberList.add(u5p2);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u5p2);
        }
        String u5p3 =FPGAu5p3.getText().toString().trim();
        if(u5p3.equals("")) {
            CycleNumberList.add(u5p3);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u5p3);
        }
        String u5p4 =FPGAu5p4.getText().toString().trim();
        if(u5p4.equals("")) {
            CycleNumberList.add(u5p4);
        }
        else
        {
            CycleNumberList.add("MYS-Fleet-"+u5p4);
        }
        if(noofports==16) {
            String u6p1 = FPGAu6p1.getText().toString().trim();
            if(u6p1.equals("")) {
                CycleNumberList.add(u6p1);
            }
            else
            {
                CycleNumberList.add("MYS-Fleet-"+u6p1);
            }
            String u6p2 = FPGAu6p2.getText().toString().trim();
            if(u6p2.equals("")) {
                CycleNumberList.add(u6p2);
            }
            else
            {
                CycleNumberList.add("MYS-Fleet-"+u6p2);
            }
            String u6p3 = FPGAu6p3.getText().toString().trim();
            if(u6p3.equals("")) {
                CycleNumberList.add(u6p3);
            }
            else
            {
                CycleNumberList.add("MYS-Fleet-"+u6p3);
            }
            String u6p4 = FPGAu6p4.getText().toString().trim();
            if(u6p4.equals("")) {
                CycleNumberList.add(u6p4);
            }
            else
            {
                CycleNumberList.add("MYS-Fleet-"+u6p4);
            }
        }
        for( k=0;k<CycleNumberList.size();k++)
        {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String checkintime = dateFormatGmt.format(new Date()) + "";

            Ports portobject = new Ports(PortIDArrayList.get(k),CycleNumberList.get(k),checkintime);
            ChekinList.add(portobject);

            forcecycle(PortIDArrayList.get(k),CycleNumberList.get(k),checkintime);



        }
        /*try {
            FinalObject.put("forceList",ChekinList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            forcecycle();
        }*/


    }

    public void forcecycle(final String Pid,final  String cnum,final String cintime )
    {
        StringRequest forcebicyclerequest = new StringRequest(Request.Method.POST, API.forcebicycle, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /*try {
                    Port.setSelection(0);
                    portselection=0;
                    Next.setVisibility(View.VISIBLE);
                    Submit.setVisibility(View.GONE);
                    ForcePortIdList.clear();
                    CycleNumberList.clear();
                    JSONObject responsefromserver =new JSONObject( response);
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
                }*/
                if(k==CycleNumberList.size())
                {
                    Toast.makeText(ForceBicycle.this, "Completed", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }

                Log.d("force checkin",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                /*Port.setSelection(0);
                portselection=0;
                Next.setVisibility(View.GONE);
                Submit.setVisibility(View.VISIBLE);*/
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
                    Toast.makeText(ForceBicycle.this, "Please check your connection/Network Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vehicleId",cnum);
                params.put("toPort", Pid);
                params.put("checkInTime", cintime);
                return params;
            }
        };

        forcebicyclerequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        //calculatedistanceandsetstation();
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


    class Ports{
       private int FPGA;
        private String eport;
        private String portid;
        private String vehicleNumber;
        private String CinTime;



        public Ports(String Portid,String vehicleid)
        {
            portid = Portid;
            vehicleNumber = vehicleid;
        }

        public Ports(String Portid,String vehicleid,String cintime)
        {
            portid = Portid;
            vehicleNumber = vehicleid;
            CinTime =cintime;
        }

        public int getFPGA() {
            return FPGA;
        }

        public void setFPGA(int FPGA) {
            this.FPGA = FPGA;
        }

        public String getEport() {
            return eport;
        }

        public void setEport(String eport) {
            this.eport = eport;
        }

        public String getPortid() {
            return portid;
        }

        public void setPortid(String portid) {
            this.portid = portid;
        }

        public String getVehicleid() {
            return vehicleNumber;
        }

        public void setVehicleid(String vehicleid) {
            this.vehicleNumber = vehicleid;
        }
    }
}
