package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Payment_History extends AppCompatActivity {

    ListView paymentlistview;
    private Toolbar PaymentToolbar;
    String userbalance, loginuserid;
    TextView UserBalance;
    private ProgressDialog mProgressDialog;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    ArrayList<PaymentObject> paymentslist = new ArrayList<>();
    Date paymentDate;
    String creditordebit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history);
        PaymentToolbar = (Toolbar) findViewById(R.id.paymenthistory_toolbar);
        PaymentToolbar.setTitle("Payment History");
        setSupportActionBar(PaymentToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentlistview = (ListView) findViewById(R.id.payment_listview);
        PaymentToolbar = (Toolbar) findViewById(R.id.paymenthistory_toolbar);
        PaymentToolbar.setTitle("Payments");
        setSupportActionBar(PaymentToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userbalance = getIntent().getStringExtra("balance");
        UserBalance = (TextView) PaymentToolbar.findViewById(R.id.balance_payment_action);
        UserBalance.setText(userbalance + "/-");
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);

        checkinternet();
        getpaymentdetails();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Payment_History.this);
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

    public void getpaymentdetails() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        StringRequest paymentdetailsrequest = new StringRequest(Request.Method.GET, API.paymenthistory + loginuserid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    int datalength = data.length();
                    mProgressDialog.dismiss();
                    if (datalength > 0) {
                        paymentslist.clear();
                        for (int i = 0; i < datalength; i++) {
                            JSONObject paymentdata = data.getJSONObject(i);
                            String paymentdate = paymentdata.getString("createdAt");

                            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                            try {
                                paymentDate = readDate.parse(paymentdate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                            SimpleDateFormat writeDate = new SimpleDateFormat("dd MMMM yyyy");
                            writeDate.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                            paymentdate = writeDate.format(paymentDate);

                            String invoiceno = paymentdata.getString("invoiceNo");
                            String paymentmode = paymentdata.getString("paymentMode");
                            int credit = paymentdata.getInt("credit");

                            int debit = paymentdata.getInt("debit");
                            if(debit>0)
                            {
                                creditordebit = "Debit";
                            }
                            if (credit > 0) {
                                creditordebit="Credit";
                                PaymentObject paymentObject = new PaymentObject(paymentdate,invoiceno,paymentmode,credit,creditordebit);
                                paymentslist.add(paymentObject);
                            }
                        }
                        PaymentAdapter paymentAdapter = new PaymentAdapter(Payment_History.this,paymentslist);
                        paymentlistview.setAdapter(paymentAdapter);
                    } else {
                        Toast.makeText(Payment_History.this, "Looks there is no payment transaction.", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder PaymentBuilder = new AlertDialog.Builder(Payment_History.this);
                        PaymentBuilder.setIcon(R.drawable.splashlogo);
                        PaymentBuilder.setTitle("Payment");
                        PaymentBuilder.setMessage("Looks there is no payment transaction.");
                        PaymentBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivity(new Intent(Payment_History.this,MyAccount.class));
                                finish();
                            }
                        });
                        PaymentBuilder.setCancelable(false);
                        PaymentBuilder.show();
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
                    Toast.makeText(Payment_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Payment_History.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Payment_History.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(Payment_History.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                }
                else if (error instanceof NetworkError) {
                    Toast.makeText(Payment_History.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Payment_History.this);
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
                    Toast.makeText(Payment_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                }  else {
                    Toast.makeText(Payment_History.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        paymentdetailsrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(paymentdetailsrequest);
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
