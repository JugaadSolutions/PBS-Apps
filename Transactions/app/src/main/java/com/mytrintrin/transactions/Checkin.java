package com.mytrintrin.transactions;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkin extends AppCompatActivity {

    EditText VehicleID,Toport;
    Button sendcheckin;

    Spinner checkinstationspinner;
    String checkinPortid;


    public static Spinner  HAspinner, RVspinner, MCspinner,Fleetspinner;
    public  ArrayAdapter<String> HAadapter,RVadapter,MCadapter,Fleetadapter;

    String HAportid,RVportid,MCportid,Fleetportid;
    String Holdingarea,Restribution,Maintenance,Fleet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        VehicleID = (EditText) findViewById(R.id.checkinvehicleid);
        //Toport = (EditText) findViewById(R.id.checkintoportid);
        sendcheckin = (Button) findViewById(R.id.bsendcheckin);
        checkinstationspinner = (Spinner) findViewById(R.id.checkinspinner);
        HAspinner = (Spinner) findViewById(R.id.HAspinner);
        RVspinner = (Spinner) findViewById(R.id.RVspinner);
        MCspinner = (Spinner) findViewById(R.id.MCspinner);
        Fleetspinner = (Spinner) findViewById(R.id.Fleetspinner);
        checkindockingstations();
        checkinternet();

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Checkin.this);
            builder.setIcon(R.drawable.ic_wifi);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }

    /*ends*/


    @Override
    protected void onPause() {
        super.onPause();
        if (HAadapter==null||RVadapter==null||MCadapter==null)
        {
            HAadapter=RVadapter=MCadapter=null;
        }
        else {
            HAadapter.clear();
            RVadapter.clear();
            MCadapter.clear();
        }
    }

    private void checkindockingstations() {
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
                        //checkinPortid=TransactionAPI.fleetid;
                        HAspinner.setVisibility(View.VISIBLE);
                        RVportid=MCportid=Fleetportid=Restribution=Maintenance=Fleet="";
                        HAadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, Splash.HANameArrayList);
                        HAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HAspinner.setAdapter(HAadapter);
                        HAspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea=HAspinner.getSelectedItem().toString();
                                HAportid = Splash.HAIDArrayList.get(position);
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

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkinPortid=TransactionAPI.holdingareaid;
                        RVspinner.setVisibility(View.VISIBLE);
                        HAportid=MCportid=Fleetportid=Holdingarea=Maintenance=Fleet="";
                        RVadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, Splash.RVNameArrayList);
                        RVadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RVspinner.setAdapter(RVadapter);
                        RVspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution=RVspinner.getSelectedItem().toString();
                                RVportid = Splash.RVIDArrayList.get(position);
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

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                       // checkinPortid=TransactionAPI.redistrubutionid;
                        MCspinner.setVisibility(View.VISIBLE);
                        HAportid=RVportid=Fleetportid=Holdingarea=Restribution=Fleet="";
                        MCadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, Splash.MCNameArrayList);
                        MCadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // MCadapter.add("Select Maintenance Center");
                        MCspinner.setAdapter(MCadapter);
                        MCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance=MCspinner.getSelectedItem().toString();
                                MCportid = Splash.MCIDArrayList.get(position);
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

                   /* case 4:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkinPortid=TransactionAPI.maintainenceid;
                        break;*/

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void getcheckindetails() {
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

    public void sendcheckindetails(View view)
    {
        checkinternet();
        getcheckindetails();
        if(VehicleID.getText().toString().trim().equals(""))
        {
            VehicleID.setError("Please Enter Bicycle ID");
            return;
        }
        String checkingstationvalue = checkinstationspinner.getSelectedItem().toString();
        if (checkingstationvalue.equals("Select Station")) {
            Toast.makeText(this, "Please select the stations", Toast.LENGTH_LONG).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        final String checkintime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        StringRequest checkinrequest = new StringRequest(Request.Method.POST,TransactionAPI.checkinurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Check in response",response);
                Holdingarea = "";
                HAportid="";
                Maintenance = "";
                MCportid="";
                Restribution = "";
                RVportid="";
                Fleet = "";
                Fleetportid="";
                checkindockingstations();
                VehicleID.setText("");
                Toast.makeText(Checkin.this,"Check in Successfully",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(Checkin.this, "can't check in  try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkin.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkin.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("vehicleId",VehicleID.getText().toString().trim());
                params.put("toPort",Fleetportid+""+MCportid+""+RVportid+""+HAportid);
                params.put("checkInTime",checkintime);
                return params;
            }
        };


        Singleton.getInstance(this).addtorequest(checkinrequest);


    }
    /*Holdingarea = "";
        HAportid="";
        Maintenance = "";
        MCportid="";
        Restribution = "";
        RVportid="";
        Fleet = "";
        Fleetportid="";
        checkindockingstations();*/

}
