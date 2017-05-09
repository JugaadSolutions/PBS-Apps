package com.mytrintrin.www.pbs_ee_maintenance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ElectronicsMaintenance extends AppCompatActivity {

    private Toolbar EMaintenanceToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    EditText MeterReading,BatteryVoltage,CCTVStatus,CPUStatus,KIOSKStatus;
    String LoginID,meterreading,batteryvoltage,cctv,cpustatus,kioskstatus;
    Spinner Stationspinner;
    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();
    public ArrayAdapter<String> Stationadapter;
    String StationName,StationId;
    JSONArray StationArray;
    JSONObject StationObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electronics_maintenance);
        EMaintenanceToolbar = (Toolbar) findViewById(R.id.emaintenancetoolbar);
        EMaintenanceToolbar.setTitle("Maintenance");
        setSupportActionBar(EMaintenanceToolbar);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        MeterReading = (EditText) findViewById(R.id.meterreading);
        BatteryVoltage = (EditText) findViewById(R.id.batteryvoltage);
        CCTVStatus = (EditText) findViewById(R.id.batteryvoltage);
        CPUStatus = (EditText) findViewById(R.id.cpustatus);
        KIOSKStatus = (EditText) findViewById(R.id.kioskstatus);
        LoginID = loginpref.getString("User-id",null);
        Stationspinner = (Spinner) findViewById(R.id.stationspinner);
        StationArray = new JSONArray();
        checkinternet();
        getalldockingstation();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    ElectronicsMaintenance.this);
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

    public void getalldockingstation()
    {
        StringRequest dockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    StationNameArrayList.clear();
                    StationIDArrayList.clear();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject stationobject = data.getJSONObject(i);
                        String id = stationobject.getString("StationID");
                        String name = stationobject.getString("name");
                        StationIDArrayList.add(i,id);
                        StationNameArrayList.add(i,name);
                        StationArray.put(i,stationobject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    {
                        getstationdetails();
                    }
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
                    Toast.makeText(ElectronicsMaintenance.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ElectronicsMaintenance.this);
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
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ElectronicsMaintenance.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ElectronicsMaintenance.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };
        dockingstationrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(dockingstationrequest);
    }

    public void submitdetailsofemaintenance(View view)
    {
        meterreading = MeterReading.getText().toString().trim();
        if(meterreading.equals("")||meterreading.equals(null))
        {
            MeterReading.setError("Meter Reading");
            return;
        }
        batteryvoltage = BatteryVoltage.getText().toString().trim();
        if(batteryvoltage.equals("")||batteryvoltage.equals(null))
        {
            BatteryVoltage.setError("Battery Voltage");
            return;
        }
        cctv =CCTVStatus.getText().toString().trim();
        if(cctv.equals("")||cctv.equals(null))
        {
            CCTVStatus.setError("CCTV Status");
            return;
        }
        cpustatus = CPUStatus.getText().toString().trim();
        if(cpustatus.equals("")||cpustatus.equals(null))
        {
            CPUStatus.setError("CPU Status");
            return;
        }
        kioskstatus = KIOSKStatus.getText().toString().trim();
        if(kioskstatus.equals("")||kioskstatus.equals(null))
        {
            KIOSKStatus.setError("KIOSK");
            return;
        }
        StringRequest emaintenancerequest = new StringRequest(Request.Method.POST, API.emaintenance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("maintenance response",response);
                Toast.makeText(ElectronicsMaintenance.this, response, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder MaintenanceBuilder = new AlertDialog.Builder(ElectronicsMaintenance.this);
                MaintenanceBuilder.setIcon(R.drawable.splashlogo);
                MaintenanceBuilder.setTitle("Maintenance");
                MaintenanceBuilder.setMessage("Maintenance Report Sent Successfully.");
                MaintenanceBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(ElectronicsMaintenance.this,ElectronicsMaintenance.class));
                        finish();
                    }
                });
                MaintenanceBuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ElectronicsMaintenance.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ElectronicsMaintenance.this);
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
                    Toast.makeText(ElectronicsMaintenance.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ElectronicsMaintenance.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ElectronicsMaintenance.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        })
        {
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
                params.put("stationId",StationId);
                params.put("createdBy", LoginID);
                params.put("meterReading", meterreading);
                params.put("batteryVoltage", batteryvoltage);
                params.put("cctvStatus", cctv);
                params.put("kioskdisplayStatus", kioskstatus);
                params.put("cpuStatus", cpustatus);

                return params;
            }
        };
        emaintenancerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(emaintenancerequest);

    }

    public void logoutfromemaintenance(View view)
    {
        editor.putString("User-id", "");
        editor.putString("Role", "");
        editor.commit();
        startActivity(new Intent(this, Login.class));
        finish();
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

    public void getstationdetails()
    {
        Stationadapter = new ArrayAdapter<String>(ElectronicsMaintenance.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
        Stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Stationspinner.setAdapter(Stationadapter);
        Stationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StationName = Stationspinner.getSelectedItem().toString();
                StationId = StationIDArrayList.get(i);
                try {
                   StationObject = StationArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ElectronicsMaintenance.this, StationId, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stationadapter.clear();
    }


    public void gotoportmaintenance(View view)
    {
        if(StationObject.equals("")||StationObject.equals(null))
        {
            Toast.makeText(ElectronicsMaintenance.this, "Please select station", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent portmaintenanceintent = new Intent(ElectronicsMaintenance.this,PortMaintenance.class);
        portmaintenanceintent.putExtra("stationobject",StationObject.toString());
        startActivity(portmaintenanceintent);

    }


}
