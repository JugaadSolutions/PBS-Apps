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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Checkout extends AppCompatActivity {

    Toolbar Checkouttoolbar;
    Spinner Station,Port,Vehicles,Member;
    EditText CheckoutDate,CheckoutTime;

    public static ArrayList<String> StationIDArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();

    public static ArrayList<String> PortIDArrayList = new ArrayList<String>();
    public static ArrayList<String> PortNameArrayList = new ArrayList<String>();

    public static ArrayList<String> UserIDArrayList = new ArrayList<String>();
    public static ArrayList<String> UserNameArrayList = new ArrayList<String>();

    public static ArrayList<String> VehicleIDArrayList = new ArrayList<String>();
    public static ArrayList<String> VehicleNameArrayList = new ArrayList<String>();

    public ArrayAdapter<String> Stationadapter,Vehicleadapter,Portadapter,Memberadapter;
    String StationName,StationId,Vehiclename,VehicleId,PortName,PortId,MemberName,MemberId,checkouttime,checkoutdate;
    JSONArray StationArray,PortArray;
    JSONObject StationObject;

    Calendar myCalendar;
    private ProgressDialog mProgressDialog;
    Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        Checkouttoolbar = (Toolbar) findViewById(R.id.checkouttoolbar);
        Checkouttoolbar.setTitle("Check out");
        setSupportActionBar(Checkouttoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Station = (Spinner) findViewById(R.id.stationspinner);
        Port = (Spinner) findViewById(R.id.portspinner);
        Vehicles = (Spinner) findViewById(R.id.cyclespinner);
        Member = (Spinner) findViewById(R.id.memberspinner);
        CheckoutDate = (EditText) findViewById(R.id.checkout_date);
        CheckoutTime = (EditText) findViewById(R.id.checkout_time);
        checkout = (Button) findViewById(R.id.createcheckout);
        getalldockingstation();
        getallvehicles();
        getallusers();

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

        CheckoutDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Checkout.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        CheckoutTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Checkout.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        CheckoutTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }


    private void updateLabel() {

        // String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        CheckoutDate.setText(sdf.format(myCalendar.getTime()));
        checkoutdate= CheckoutDate.getText().toString();
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Checkout.this);
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


    public void getallusers()
    {
        StringRequest membersrequest = new StringRequest(Request.Method.GET, API.getalluser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    UserIDArrayList.clear();
                    UserNameArrayList.clear();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject memberobject = data.getJSONObject(i);
                        if(memberobject.has("cardNum")) {
                            String memberid = memberobject.getString("UserID");
                            String membername = memberobject.getString("cardNum");
                            UserIDArrayList.add(memberid);
                            UserNameArrayList.add(membername);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    {
                       getuserdetails();
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
                    Toast.makeText(Checkout.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout.this);
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
                    Toast.makeText(Checkout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        membersrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(membersrequest);
    }



    public void getuserdetails()
    {
        Memberadapter = new ArrayAdapter<String>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, UserNameArrayList);
        Memberadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Member.setAdapter(Memberadapter);
        Member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MemberName = Member.getSelectedItem().toString();
                MemberId = UserIDArrayList.get(i);
                Toast.makeText(Checkout.this, MemberId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void getalldockingstation()
    {
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
                        StationIDArrayList.add(i,id);
                        StationNameArrayList.add(i,name);
                        StationArray.put(i,stationobject);
                        PortArray.put(i,ports);
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
                    Toast.makeText(Checkout.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout.this);
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
                    Toast.makeText(Checkout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void getstationdetails()
    {
        Stationadapter = new ArrayAdapter<String>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, StationNameArrayList);
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
                    for(int j =0 ;j<ports.length();j++)
                    {
                        JSONObject port = ports.getJSONObject(j);
                        JSONObject dockports = port.getJSONObject("dockingPortId");
                        String portid = dockports.getString("PortID");
                        String portname = dockports.getString("Name");
                        PortIDArrayList.add(portid);
                        PortNameArrayList.add(portname);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    getportdetails();
                }
                Toast.makeText(Checkout.this, StationId, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void getportdetails()
    {
        Portadapter = new ArrayAdapter<String>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, PortNameArrayList);
        Portadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Port.setAdapter(Portadapter);
        Port.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PortName = Port.getSelectedItem().toString();
                PortId = PortIDArrayList.get(i);
                Toast.makeText(Checkout.this, PortId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getallvehicles()
    {
        checkinternet();
        StringRequest vehiclesrequest = new StringRequest(Request.Method.GET, API.getallvehicles, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responsefromserver=new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    VehicleIDArrayList.clear();
                    VehicleNameArrayList.clear();
                    for (int i =0 ; i< data.length();i++)
                    {
                        JSONObject cycleobject = data.getJSONObject(i);
                        String vehicleid = cycleobject.getString("vehicleUid");
                        String vehiclenumber = cycleobject.getString("vehicleNumber");
                        VehicleNameArrayList.add(vehiclenumber);
                        VehicleIDArrayList.add(vehicleid);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
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
                    Toast.makeText(Checkout.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout.this);
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
                    Toast.makeText(Checkout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        vehiclesrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(vehiclesrequest);
    }

    public void getvehicledetails()
    {

        Vehicleadapter = new ArrayAdapter<String>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, VehicleNameArrayList);
        Vehicleadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Vehicles.setAdapter(Vehicleadapter);
        Vehicles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Vehiclename = Vehicles.getSelectedItem().toString();
                VehicleId = VehicleIDArrayList.get(i);
                Toast.makeText(Checkout.this, VehicleId, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    public void createcheckout(View view)
    {
        checkinternet();
        checkout.setEnabled(false);
        if(CheckoutTime.getText().toString().equals("")||CheckoutDate.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            checkout.setEnabled(true);
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Creating check out...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        SimpleDateFormat inputformat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat outputformat = new SimpleDateFormat("HH:mm:ss.SSS");
        outputformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = CheckoutTime.getText().toString();
        try {
            Date date = inputformat.parse(time);
            checkouttime = outputformat.format(date);
        } catch (ParseException e) {
        }
        StringRequest createcheckoutrequest = new StringRequest(Request.Method.POST, API.createcheckout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(Checkout.this, response, Toast.LENGTH_SHORT).show();
                CheckoutTime.setText("");
                CheckoutDate.setText("");
                mProgressDialog.dismiss();
                checkout.setEnabled(true);
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    String data = responsefromserver.getString("message");
                    AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Checkout.this);
                    SuccessBuilder.setIcon(R.drawable.splashlogo);
                    SuccessBuilder.setTitle("Check Out");
                    SuccessBuilder.setMessage(data);
                    SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*startActivity(new Intent(Checkout.this, Getstarted.class));
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
                mProgressDialog.dismiss();
                checkout.setEnabled(true);
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Checkout.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Checkout.this);
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
                    Toast.makeText(Checkout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }){

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
                params.put("fromPort", PortId);
                params.put("checkOutTime",checkoutdate + "T" + checkouttime + "Z");
                params.put("cardId",MemberId);
                params.put("checkOutInitiatedTime",checkoutdate + "T" + checkouttime + "Z");
                params.put("checkOutCompletionTime",checkoutdate + "T" + checkouttime + "Z");
                return params;
            }
        };
        createcheckoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(createcheckoutrequest);
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
