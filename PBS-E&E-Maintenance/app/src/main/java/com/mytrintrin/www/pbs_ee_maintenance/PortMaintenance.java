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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PortMaintenance extends AppCompatActivity {

    private Toolbar PortMaintenanceToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String stationobject, stationname, portname, portid, comments, LoginID;
    EditText StationName, Comments_Port;
    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();
    public ArrayAdapter<String> Portadapter;
    Spinner PortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.port_maintenance);
        PortMaintenanceToolbar = (Toolbar) findViewById(R.id.portmaintenancetoolbar);
        PortMaintenanceToolbar.setTitle("Port Status");
        setSupportActionBar(PortMaintenanceToolbar);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        StationName = (EditText) findViewById(R.id.stationname_port);
        Comments_Port = (EditText) findViewById(R.id.comments_port);
        PortSpinner = (Spinner) findViewById(R.id.PortSpinner);
        LoginID = loginpref.getString("User-id", null);
        getstationdetails();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    PortMaintenance.this);
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


    public void getstationdetails() {
        stationobject = getIntent().getStringExtra("stationobject");
        try {
            JSONObject Station = new JSONObject(stationobject);
            stationname = Station.getString("name");
            StationName.setText(stationname);
            StationName.setEnabled(false);
            JSONArray PortArray = Station.getJSONArray("portIds");
            PortNameArrayList.clear();
            PortIDArrayList.clear();
            for (int i = 0; i < PortArray.length(); i++) {
                JSONObject portobject = PortArray.getJSONObject(i);
                JSONObject dockingportobject = portobject.getJSONObject("dockingPortId");
                String PortName = dockingportobject.getString("Name");
                String PortID = dockingportobject.getString("PortID");
                PortNameArrayList.add(i, PortName);
                PortIDArrayList.add(i, PortID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            {
                getportdetails();
            }
        }

    }

    public void getportdetails() {
        Portadapter = new ArrayAdapter<String>(PortMaintenance.this, android.R.layout.simple_spinner_dropdown_item, PortNameArrayList);
        Portadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PortSpinner.setAdapter(Portadapter);
        PortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                portname = PortSpinner.getSelectedItem().toString();
                portid = PortIDArrayList.get(i);
                Toast.makeText(PortMaintenance.this, portid, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void sendportdetails(View view) {
        comments = Comments_Port.getText().toString().trim();
        if(comments.equals("")||comments.equals(null))
        {
            Comments_Port.setError("Comments");
            return;

        }
        StringRequest portdetailsrequest = new StringRequest(Request.Method.POST, API.portmaintenance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("portdetails response",response);
                AlertDialog.Builder PortBuilder = new AlertDialog.Builder(PortMaintenance.this);
                PortBuilder.setIcon(R.drawable.splashlogo);
                PortBuilder.setTitle("Port Status");
                PortBuilder.setMessage("Port Status Updated Successfully \n Do you want updated other ports?");
                PortBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getportdetails();
                    }
                });
                PortBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(PortMaintenance.this,ElectronicsMaintenance.class));
                        finish();
                    }
                });
                PortBuilder.setCancelable(false);
                PortBuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(PortMaintenance.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(PortMaintenance.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(PortMaintenance.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(PortMaintenance.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            PortMaintenance.this);
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
                    Toast.makeText(PortMaintenance.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(PortMaintenance.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(PortMaintenance.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("comments", comments);
                params.put("createdBy", LoginID);
                params.put("portId", portid);
                return params;
            }
        };
        portdetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(portdetailsrequest);
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
