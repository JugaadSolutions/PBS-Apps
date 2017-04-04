package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class Repair extends AppCompatActivity {

    Toolbar RepairToolbar;
    Spinner DockingStationName_MC,Employeelist_MC;
    int i;
    public static ArrayList<String> StationnameArrayList = new ArrayList<String>();
    public static ArrayList<String> StationIdArrayList = new ArrayList<String>();
    public static ArrayAdapter<String> Stationnameadapter;

    public static ArrayList<String> McEmpnameArrayList = new ArrayList<String>();
    public static ArrayList<String> McEmpIDArrayList = new ArrayList<String>();
    public static ArrayAdapter<String> McEmpnameadapter;


    private ProgressDialog mProgressDialog;
    String stationname,stationid,CycleNumber,loginuserid,location,mcempname,empid;
    Context context;
    LinearLayout CycledetailsLayout,CheckboxLayout;
    LinearLayout.LayoutParams cyclenoparams;
    EditText cycleno;

    ArrayList<String> selectedStrings = new ArrayList<String>();
    ArrayList<String> checkboxlist = new ArrayList<String>();

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    JSONObject Repairobject,Chekcboxobject;
    JSONArray Checklistarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair);
        RepairToolbar = (Toolbar) findViewById(R.id.repairtoolbar);
        RepairToolbar.setTitle("Repair");
        setSupportActionBar(RepairToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DockingStationName_MC = (Spinner) findViewById(R.id.dockingstationspinner_mc);
        Employeelist_MC = (Spinner) findViewById(R.id.employeelist_mc);
        context=getApplicationContext();
        CycledetailsLayout = (LinearLayout) findViewById(R.id.cycledetailslayout);
        CheckboxLayout = (LinearLayout) findViewById(R.id.Checkboxlayout);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
        checkinternet();
        getalldockingstations();
        getmcemplist();
        createcycledetailslayout();
        getrepairchecklist();
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(Repair.this);
            builder.setIcon(R.mipmap.logo);
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

    private void getalldockingstations() {

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
                    for (i = 0; i < data.length(); i++) {
                        JSONObject stationnameobj = data.getJSONObject(i);
                        String id = stationnameobj.getString("StationID");
                        String sname = stationnameobj.getString("name");
                        StationIdArrayList.add(id);
                        StationnameArrayList.add(sname);
                        Stationnameadapter = new ArrayAdapter<String>(Repair.this, android.R.layout.simple_spinner_dropdown_item, StationnameArrayList);
                        Stationnameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        DockingStationName_MC.setAdapter(Stationnameadapter);
                        DockingStationName_MC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                stationname = DockingStationName_MC.getSelectedItem().toString();
                                stationid = StationIdArrayList.get(position);
                                Log.d("Sid",stationid);
                                mProgressDialog.dismiss();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Repair.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Repair.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Repair.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Repair.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Repair.this);
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
                    Toast.makeText(Repair.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Repair.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Repair.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void getmcemplist()
    {
        StringRequest mcemplistrequest = new StringRequest(Request.Method.GET, API.getmcemplist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    McEmpnameArrayList.clear();
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject mcempobj = data.getJSONObject(i);
                        String mcempid = mcempobj.getString("UserID");
                        String fname = mcempobj.getString("Name");
                        String lname = mcempobj.getString("lastName");
                        String Name = fname+" "+lname;
                        McEmpIDArrayList.add(mcempid);
                        McEmpnameArrayList.add(Name);

                        McEmpnameadapter = new ArrayAdapter<String>(Repair.this, android.R.layout.simple_spinner_dropdown_item, McEmpnameArrayList);
                        McEmpnameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Employeelist_MC.setAdapter(McEmpnameadapter);
                        Employeelist_MC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mcempname = Employeelist_MC.getSelectedItem().toString();
                                empid = McEmpIDArrayList.get(position);
                                Log.d("empid",empid);
                                mProgressDialog.dismiss();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });


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
                    Toast.makeText(Repair.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Repair.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Repair.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Repair.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Repair.this);
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
                    Toast.makeText(Repair.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Repair.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Repair.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        };
        mcemplistrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(mcemplistrequest);
    }

    private void createcycledetailslayout() {

        cycleno = new EditText(context);
        cyclenoparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cyclenoparams.bottomMargin = 10;
        cycleno.setLayoutParams(cyclenoparams);
        cycleno.setBackgroundResource(R.drawable.input_outline);
        cycleno.setVisibility(View.VISIBLE);
        cycleno.setHint("Cycle Number");
        cycleno.setInputType(InputType.TYPE_CLASS_NUMBER);
        cycleno.setHintTextColor(Color.parseColor("#009746"));
        cycleno.setTextColor(Color.parseColor("#009746"));
        cycleno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.bicycleno))});
        CycledetailsLayout.addView(cycleno);
    }

    /*need to change the repair checklist api*/
    public void getrepairchecklist()
    {
        StringRequest maintainencechecklistrequest = new StringRequest(Request.Method.GET, API.repairchecklist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    Chekcboxobject = responsefromserver;
                    createchecklist();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mProgressDialog.dismiss();

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Repair.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Repair.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Repair.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Repair.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    checkinternet();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Repair.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Repair.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Repair.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        };
        maintainencechecklistrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(maintainencechecklistrequest);
    }

    private CompoundButton.OnCheckedChangeListener handleCheck (final CheckBox chk)
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(!isChecked){
                    Toast.makeText(getApplicationContext(), "You unchecked " + chk.getTag(),Toast.LENGTH_SHORT).show();
                    selectedStrings.remove(chk.getTag().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You checked " + chk.getTag(),Toast.LENGTH_SHORT).show();
                    selectedStrings.add(chk.getTag().toString());
                }
            }
        };
    }

    public void createchecklist() {
        try {
            JSONObject data = Chekcboxobject.getJSONObject("data");
            JSONArray value = data.getJSONArray("value");
            CheckboxLayout.removeAllViews();
            for (int i=0 ;i<value.length();i++)
            {
                String checkboxvalue = value.getString(i);
                checkboxlist.add(i,checkboxvalue);
                AppCompatCheckBox air = new AppCompatCheckBox(Repair.this);
                air.setId(i);
                air.setText(checkboxlist.get(i));
                air.setTag(checkboxlist.get(i));
                air.setChecked(false);
                air.setOnCheckedChangeListener(handleCheck(air));
                CheckboxLayout.addView(air);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitdetails(View view)
    {
        checkinternet();
        CycleNumber = cycleno.getText().toString().trim();
        if(CycleNumber.isEmpty()||CycleNumber.equals("")||CycleNumber.equals(null))
        {
            cycleno.setError("Cycle Number");
            return;
        }
        Repairobject = new JSONObject();
        Checklistarray = new JSONArray();
        for(int l=0;l<selectedStrings.size();l++)
        {
            Checklistarray.put(selectedStrings.get(l));
        }
        try {
            Repairobject.put("employeeId", empid);
            Repairobject.put("vehicleId", CycleNumber);
            Repairobject.put("location", stationid);
            Repairobject.put("createdBy", loginuserid);
            Repairobject.put("checkList", Checklistarray);
            Repairobject.put("origin","app");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest maintenancerequest = new JsonObjectRequest(Request.Method.POST, API.repairedcycleinDS,Repairobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(Repair.this, "Details updated successfully", Toast.LENGTH_LONG).show();
                DockingStationName_MC.setSelection(0);
                cycleno.setText("");
                selectedStrings.clear();
                createchecklist();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Repair.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Repair.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Repair.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Repair.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Repair.this);
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
                    Toast.makeText(Repair.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Repair.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Repair.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }){
        };
        maintenancerequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(maintenancerequest);
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
