package mytrintrin.com.pbs_employee;

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

public class checkinmanually extends AppCompatActivity {

    public static Spinner checkinspinner, HAcheckinspinner, RVcheckinspinner, MCcheckinspinner,Fleetcheckinspinner;
    public  ArrayAdapter<String> HAcheckinadapter,RVcheckinadapter,MCcheckinadapter;

    Button sendcheckin;

    String HAportid,RVportid,MCportid,Fleetportid;
    String Holdingarea,Restribution,Maintenance,Fleet;


    EditText VehicleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkinmanually);
        VehicleID = (EditText) findViewById(R.id.checkinvehicleid);
        sendcheckin = (Button) findViewById(R.id.bsendcheckin);
        checkinspinner = (Spinner) findViewById(R.id.checkinspinner);
        HAcheckinspinner = (Spinner) findViewById(R.id.HAcheckinspinner);
        RVcheckinspinner = (Spinner) findViewById(R.id.RVcheckinspinner);
        MCcheckinspinner = (Spinner) findViewById(R.id.MCcheckinspinner);
        Fleetcheckinspinner = (Spinner) findViewById(R.id.Fleetcheckinspinner);
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
                    checkinmanually.this);
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
        if (HAcheckinadapter==null||RVcheckinadapter==null||MCcheckinadapter==null)
        {
            HAcheckinadapter=RVcheckinadapter=MCcheckinadapter=null;
        }
        else {
            HAcheckinadapter.clear();
            RVcheckinadapter.clear();
            MCcheckinadapter.clear();
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
        checkinspinner.setAdapter(dockingadapter);
        checkinspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVcheckinspinner.setVisibility(View.GONE);
                        MCcheckinspinner.setVisibility(View.GONE);
                        HAcheckinspinner.setVisibility(View.GONE);
                        Fleetcheckinspinner.setVisibility(View.GONE);
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkinPortid=TransactionAPI.fleetid;
                        HAcheckinspinner.setVisibility(View.VISIBLE);
                        RVportid=MCportid=Fleetportid=Restribution=Maintenance=Fleet="";
                        HAcheckinadapter = new ArrayAdapter<String>(checkinmanually.this, android.R.layout.simple_spinner_dropdown_item, Splash.HANameArrayList);
                        HAcheckinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HAcheckinspinner.setAdapter(HAcheckinadapter);
                        HAcheckinspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea=HAcheckinspinner.getSelectedItem().toString();
                                HAportid = Splash.HAIDArrayList.get(position);
                                Log.d("Holding area port id", HAportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RVcheckinspinner.setVisibility(View.GONE);
                        MCcheckinspinner.setVisibility(View.GONE);
                        Fleetcheckinspinner.setVisibility(View.GONE);
                        break;

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkinPortid=TransactionAPI.holdingareaid;
                        RVcheckinspinner.setVisibility(View.VISIBLE);
                        HAportid=MCportid=Fleetportid=Holdingarea=Maintenance=Fleet="";
                        RVcheckinadapter = new ArrayAdapter<String>(checkinmanually.this, android.R.layout.simple_spinner_dropdown_item, Splash.RVNameArrayList);
                        RVcheckinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RVcheckinspinner.setAdapter(RVcheckinadapter);
                        RVcheckinspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution=RVcheckinspinner.getSelectedItem().toString();
                                RVportid = Splash.RVIDArrayList.get(position);
                                Log.d("Redistribution port id", RVportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        MCcheckinspinner.setVisibility(View.GONE);
                        HAcheckinspinner.setVisibility(View.GONE);
                        Fleetcheckinspinner.setVisibility(View.GONE);
                        break;

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        // checkinPortid=TransactionAPI.redistrubutionid;
                        MCcheckinspinner.setVisibility(View.VISIBLE);
                        HAportid=RVportid=Fleetportid=Holdingarea=Restribution=Fleet="";
                        MCcheckinadapter = new ArrayAdapter<String>(checkinmanually.this, android.R.layout.simple_spinner_dropdown_item, Splash.MCNameArrayList);
                        MCcheckinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // MCadapter.add("Select Maintenance Center");
                        MCcheckinspinner.setAdapter(MCcheckinadapter);
                        MCcheckinspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance=MCcheckinspinner.getSelectedItem().toString();
                                MCportid = Splash.MCIDArrayList.get(position);
                                Log.d("Maintenance port id", MCportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        HAcheckinspinner.setVisibility(View.GONE);
                        RVcheckinspinner.setVisibility(View.GONE);
                        Fleetcheckinspinner.setVisibility(View.GONE);
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
            Fleet = Fleetcheckinspinner.getSelectedItem().toString();
        }

        if (Holdingarea == "" || HAportid == null) {
            Holdingarea = "";
            HAportid = "";
        } else {
            Holdingarea = HAcheckinspinner.getSelectedItem().toString();
        }
        // Maintenance=MCspinner.getSelectedItem().toString();
        if (Maintenance == "" || MCportid == null) {
            Maintenance = "";
            MCportid = "";
        } else {
            Maintenance = MCcheckinspinner.getSelectedItem().toString();
        }
        // Restribution=RVspinner.getSelectedItem().toString();
        if (Restribution == "" || RVportid == null) {
            Restribution = "";
            RVportid = "";
        } else {
            Restribution = RVcheckinspinner.getSelectedItem().toString();
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
        String checkingstationvalue = checkinspinner.getSelectedItem().toString();
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
                Toast.makeText(checkinmanually.this,"Check in Successfully",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(checkinmanually.this, "can't check in  try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(checkinmanually.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(checkinmanually.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(checkinmanually.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(checkinmanually.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(checkinmanually.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(checkinmanually.this, "Something went wrong", Toast.LENGTH_LONG).show();
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


        Mysingleton.getInstance(this).addtorequestqueue(checkinrequest);


    }



}
