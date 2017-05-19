package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Payment_History extends AppCompatActivity {

    private Toolbar PaymentToolbar;
    private ProgressDialog mProgressDialog;
    CardView cardview;
    Context context;
    LinearLayout PaymentHistory, PaymentDetailsLayout;
    LinearLayout.LayoutParams cardparams, detailsparams;
    TextView PaymentDate, PaymentInvoiceno, PaymentMode, PaymentCredit,PaymentDescription;
    int MemberId;
    SwipeRefreshLayout Paymentswipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history);
        PaymentToolbar = (Toolbar) findViewById(R.id.paymenthistory_toolbar);
        PaymentToolbar.setTitle("Payment History");
        setSupportActionBar(PaymentToolbar);
        context = getApplicationContext();
        PaymentHistory = (LinearLayout) findViewById(R.id.paymenthistorylayout);
        MemberId = getIntent().getIntExtra("userid",0);

        checkinternet();
        getpaymentdetails();
        Paymentswipe = (SwipeRefreshLayout) findViewById(R.id.paymentswipelayout);
        Paymentswipe.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        Paymentswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Paymentswipe.setRefreshing(false);
                        getpaymentdetails();
                    }
                },3000);
            }
        });
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Payment_History.this);
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

    public void getpaymentdetails() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        StringRequest paymentdetailsrequest = new StringRequest(Request.Method.GET, API.paymenthistory + MemberId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    int datalength = data.length();
                    mProgressDialog.dismiss();
                    if (datalength > 0) {
                        PaymentHistory.removeAllViews();
                        for (int i = 0; i < datalength; i++) {
                            JSONObject paymentdata = data.getJSONObject(i);
                            String paymentdate = paymentdata.getString("createdAt");

                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(inFormat.parse(paymentdate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String invoiceno = paymentdata.getString("invoiceNo");
                            String paymentmode = paymentdata.getString("paymentMode");
                            String credit = paymentdata.getString("credit");
                            String paymentdescription = paymentdata.getString("paymentDescription");

                            cardview = new CardView(context);
                            cardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            cardparams.bottomMargin = 10;
                            cardview.setLayoutParams(cardparams);
                            cardview.setRadius(15);
                            cardview.setPadding(25, 25, 25, 25);
                            cardview.setCardBackgroundColor(Color.parseColor("#009746"));
                            cardview.setVisibility(View.VISIBLE);
                            cardview.setMaxCardElevation(30);
                            cardview.setMaxCardElevation(6);

                            PaymentDetailsLayout = new LinearLayout(context);
                            detailsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            PaymentDetailsLayout.setOrientation(LinearLayout.VERTICAL);
                            PaymentDetailsLayout.setGravity(Gravity.END);
                            PaymentDetailsLayout.setPadding(25, 25, 25, 25);
                            PaymentDetailsLayout.setLayoutParams(detailsparams);

                            PaymentDate = new TextView(context);
                            PaymentDate.setText("Date : " + outFormat.format(c.getTime()));
                            PaymentDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            PaymentDate.setTextColor(Color.WHITE);

                            PaymentInvoiceno = new TextView(context);
                            PaymentInvoiceno.setText("Invoice No : " + invoiceno);
                            PaymentInvoiceno.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            PaymentInvoiceno.setTextColor(Color.WHITE);

                            PaymentMode = new TextView(context);
                            PaymentMode.setText("Mode of Payment : " + paymentmode);
                            PaymentMode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            PaymentMode.setTextColor(Color.WHITE);

                            PaymentCredit = new TextView(context);
                            PaymentCredit.setText("Amount : " + credit);
                            PaymentCredit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            PaymentCredit.setTextColor(Color.WHITE);

                            PaymentDescription = new TextView(context);
                            PaymentDescription.setText("Towards : " + paymentdescription);
                            PaymentDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            PaymentDescription.setTextColor(Color.WHITE);

                            PaymentDetailsLayout.addView(PaymentDate);
                            PaymentDetailsLayout.addView(PaymentInvoiceno);
                            PaymentDetailsLayout.addView(PaymentMode);
                            PaymentDetailsLayout.addView(PaymentCredit);
                            PaymentDetailsLayout.addView(PaymentDescription);

                            cardview.addView(PaymentDetailsLayout);
                            PaymentHistory.addView(cardview);
                        }
                    } else {
                        Toast.makeText(Payment_History.this, "Looks there is no payment transaction.", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder PaymentBuilder = new AlertDialog.Builder(Payment_History.this);
                        PaymentBuilder.setIcon(R.mipmap.logo);
                        PaymentBuilder.setTitle("Payment");
                        PaymentBuilder.setMessage("No Payment Transaction Found!!!");
                        PaymentBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        PaymentBuilder.setNegativeButton("Refresh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getpaymentdetails();
                            }
                        });
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
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Payment_History.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Payment_History.this);
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
                    Toast.makeText(Payment_History.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Payment_History.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
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
        paymentdetailsrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(paymentdetailsrequest);
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
