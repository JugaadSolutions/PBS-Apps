package com.mytrintrin.www.pbs_sync;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Checkin extends AppCompatActivity {

    Toolbar Checkintoolbar;
    Spinner Station, Port, Vehicles;
    EditText CheckinDate, CheckinTime;

    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();

    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();

    public static ArrayList<String> VehicleIDArrayList = new ArrayList<String>();
    public static ArrayList<String> VehicleNameArrayList = new ArrayList<String>();

    public ArrayAdapter<String> Stationadapter, Vehicleadapter, Portadapter;
    String StationName, StationId, Vehiclename, VehicleId, PortName, PortId, checkintime, checkindate;
    JSONArray StationArray, PortArray;
    JSONObject StationObject;

    Calendar myCalendar;
    Button checkin;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);
        Checkintoolbar = (Toolbar) findViewById(R.id.checkintoolbar);
        Checkintoolbar.setTitle("Check in");
        setSupportActionBar(Checkintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Station = (Spinner) findViewById(R.id.stationspinner);
        Port = (Spinner) findViewById(R.id.portspinner);
        Vehicles = (Spinner) findViewById(R.id.cyclespinner);
        CheckinDate = (EditText) findViewById(R.id.checkin_date);
        CheckinTime = (EditText) findViewById(R.id.checkin_time);
        checkin = (Button) findViewById(R.id.createcheckin);
        getalldockingstation();
        getallvehicles();

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        CheckinDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Checkin.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        CheckinTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Checkin.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        CheckinTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        CheckinDate.setText(sdf.format(myCalendar.getTime()));
        checkindate = CheckinDate.getText().toString().trim();
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Checkin.this);
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
        StringRequest dockingstationrequest = new StringRequest(Request.Method.GET, API.alldockingstation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    StationNameArrayList.clear();
                    StationIDArrayList.clear();
                    StationArray = new JSONArray();
                    PortArray = new JSONArray();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject stationobject = data.getJSONObject(i);
                        String id = stationobject.getString("StationID");
                        String name = stationobject.getString("name");
                        JSONArray ports = stationobject.getJSONArray("portIds");
                        StationIDArrayList.add(i, id);
                        StationNameArrayList.add(i, name);
                        StationArray.put(i, stationobject);
                        PortArray.put(i, ports);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
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
                    Toast.makeText(Checkin.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Checkin.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin.this);
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
        }) {
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

    public void getstationdetails() {
        Stationadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
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
                Toast.makeText(Checkin.this, StationId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void getportdetails() {
        Portadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, PortNameArrayList);
        Portadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Port.setAdapter(Portadapter);
        Port.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PortName = Port.getSelectedItem().toString();
                PortId = PortIDArrayList.get(i);
                Toast.makeText(Checkin.this, PortId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void getallvehicles() {
        checkinternet();
        StringRequest vehiclesrequest = new StringRequest(Request.Method.GET, API.getallvehicles, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    VehicleIDArrayList.clear();
                    VehicleNameArrayList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject cycleobject = data.getJSONObject(i);
                        String vehicleid = cycleobject.getString("vehicleUid");
                        String vehiclenumber = cycleobject.getString("vehicleNumber");
                        VehicleNameArrayList.add(vehiclenumber);
                        VehicleIDArrayList.add(vehicleid);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    getvehicledetails();
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
                    Toast.makeText(Checkin.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Checkin.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin.this);
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        vehiclesrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(vehiclesrequest);
    }

    public void getvehicledetails() {

        Vehicleadapter = new ArrayAdapter<String>(Checkin.this, android.R.layout.simple_spinner_dropdown_item, VehicleNameArrayList);
        Vehicleadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Vehicles.setAdapter(Vehicleadapter);
        Vehicles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Vehiclename = Vehicles.getSelectedItem().toString();
                VehicleId = VehicleIDArrayList.get(i);
                Toast.makeText(Checkin.this, VehicleId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    public void createcheckin(View view) {
        checkinternet();
        checkin.setEnabled(false);
        if (CheckinTime.getText().toString().equals("") || CheckinTime.getText().toString().equals("")) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            checkin.setEnabled(true);
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Creating check in...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        SimpleDateFormat inputformat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat outputformat = new SimpleDateFormat("HH:mm:ss.SSS");
        outputformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = CheckinTime.getText().toString();
        try {
            Date date = inputformat.parse(time);
            checkintime = outputformat.format(date);
        } catch (ParseException e) {
        }
        StringRequest createcheckinrequest = new StringRequest(Request.Method.POST, API.createcheckin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(Checkin.this, response, Toast.LENGTH_SHORT).show();
                CheckinTime.setText("");
                CheckinDate.setText("");
                checkin.setEnabled(true);
                mProgressDialog.dismiss();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String data = responsefromserver.getString("message");
                    AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Checkin.this);
                    SuccessBuilder.setIcon(R.drawable.splashlogo);
                    SuccessBuilder.setTitle("Check In");
                    SuccessBuilder.setMessage(data);
                    SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           /* startActivity(new Intent(Checkin.this, Getstarted.class));
                            finish();*/
                            dialogInterface.dismiss();
                        }
                    });
                    SuccessBuilder.setCancelable(false);
                    SuccessBuilder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkin.setEnabled(true);
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Checkin.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Checkin.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkin.this);
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
                params.put("vehicleId", VehicleId);
                params.put("toPort", PortId);
                params.put("checkInTime", checkindate + "T" + checkintime + "Z");
                return params;
            }
        };
        createcheckinrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(createcheckinrequest);
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
