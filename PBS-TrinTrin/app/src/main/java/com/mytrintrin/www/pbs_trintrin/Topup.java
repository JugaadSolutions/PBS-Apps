package com.mytrintrin.www.pbs_trintrin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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

public class Topup extends AppCompatActivity {

    private Toolbar Topuptoolbar;
    EditText Transactionno_topup, Comments_topup, Username_topup;
    String amountfortopup, Usernamefortopup, transactionno_topup, comments_topup, Topupname, Topupid, TopupValidity;
    int Memberid_topup, Usagefee;
    Spinner Paymentmode_topup;
    public static ArrayList<String> TopupIDArrayList = new ArrayList<String>();
    public static ArrayList<String> TopupNameArrayList = new ArrayList<String>();
    public static ArrayList<String> TopupValidityArrayList = new ArrayList<String>();
    public static ArrayList<Integer> Totalamountoftopup = new ArrayList<Integer>();
    Spinner TopupPlans;
    public ArrayAdapter<String> Topupadapter;
    TextView planname_topup, planvalidity_topup, planuserfee_topup, plantotalfee_topup;
    JSONObject Topupobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup);
        Topuptoolbar = (Toolbar) findViewById(R.id.topuptoolbar);
        Topuptoolbar.setTitle("Top Up");
        setSupportActionBar(Topuptoolbar);
        Paymentmode_topup = (Spinner) findViewById(R.id.topuppaymentmode);
        Transactionno_topup = (EditText) findViewById(R.id.ettopuptransactionid);
        Comments_topup = (EditText) findViewById(R.id.ettopupcomments);
        Username_topup = (EditText) findViewById(R.id.username_topup);
        Usernamefortopup = getIntent().getStringExtra("Name");
        Memberid_topup = getIntent().getIntExtra("userid", 0);
        Username_topup.setText(Usernamefortopup);
        TopupPlans = (Spinner) findViewById(R.id.topupplans);
        planname_topup = (TextView) findViewById(R.id.planname_topup);
        planvalidity_topup = (TextView) findViewById(R.id.planvalidity_topup);
        planuserfee_topup = (TextView) findViewById(R.id.planusagefee_topup);
        plantotalfee_topup = (TextView) findViewById(R.id.plantotalfee_topup);
        checkinternet();
        gettopupplans();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(Topup.this);
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

    public void Topupforuser(View view) {
        checkinternet();
        transactionno_topup = Transactionno_topup.getText().toString().trim();
        comments_topup = Comments_topup.getText().toString().trim();
        if (transactionno_topup.isEmpty()) {
            Transactionno_topup.setError("Transaction Number");
            return;
        }
        if (comments_topup.isEmpty()) {
            Comments_topup.setError("Comments");
            return;
        }
        StringRequest topuprequest = new StringRequest(Request.Method.POST, API.addcredit + Memberid_topup + "/topup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Topup.this, "Topup successfull", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder TopuupBuilder = new AlertDialog.Builder(Topup.this);
                TopuupBuilder.setTitle("Top Up");
                TopuupBuilder.setMessage("Top up Successfull");
                TopuupBuilder.setIcon(R.mipmap.logo);
                TopuupBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Topup.this, GetStarted.class));
                        finish();
                    }
                });
                TopuupBuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(Topup.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Topup.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Topup.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Topup.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Topup.this);
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
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Topup.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Topup.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Topup.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("credit", Topupid);
                params.put("creditMode", Paymentmode_topup.getSelectedItem().toString());
                params.put("transactionNumber", transactionno_topup);
                params.put("comments", comments_topup);
                return params;
            }
        };
        topuprequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(topuprequest);
    }

    public void gettopupplans() {
        StringRequest getplanrequest = new StringRequest(Request.Method.GET, API.gettopupplans, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject plansresponse = new JSONObject(response);
                    Topupobject = plansresponse;
                    gettopupdetails();
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
                    Toast.makeText(Topup.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Topup.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Topup.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Topup.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    checkinternet();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Topup.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Topup.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Topup.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        getplanrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getplanrequest);
    }

    public void gettopupdetails() {
        try {
            JSONArray data = Topupobject.getJSONArray("data");
            TopupNameArrayList.clear();
            for (int i = 0; i < data.length(); i++) {
                JSONObject getid = data.getJSONObject(i);
                String id = getid.getString("topupId");
                String name = getid.getString("topupName");
                Usagefee = getid.getInt("userFees");
                TopupValidity = getid.getString("validity");

                TopupIDArrayList.add(id);
                TopupNameArrayList.add(name);
                Totalamountoftopup.add(Usagefee);
                TopupValidityArrayList.add(TopupValidity);

                Topupadapter = new ArrayAdapter<String>(Topup.this, android.R.layout.simple_spinner_dropdown_item, TopupNameArrayList);
                Topupadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                TopupPlans.setAdapter(Topupadapter);
                TopupPlans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Topupname = TopupPlans.getSelectedItem().toString();
                        Topupid = TopupIDArrayList.get(position);

                        planname_topup.setText("Plane Name : " + Topupname);
                        planvalidity_topup.setText("Validity : " + TopupValidityArrayList.get(position) + " days");
                        planuserfee_topup.setText("Usage Fee : " + Totalamountoftopup.get(position) + "/-");
                        plantotalfee_topup.setText("Total : " + Totalamountoftopup.get(position) + "/-");
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