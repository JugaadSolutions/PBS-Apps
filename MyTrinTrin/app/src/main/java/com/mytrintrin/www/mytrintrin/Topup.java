package com.mytrintrin.www.mytrintrin;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import utility.AvenuesParams;
import utility.ServiceUtility;

public class Topup extends AppCompatActivity {

    private Toolbar TopupToolbar;
    EditText Topupamount;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, topupamount,Topupname, Topupid, TopupValidity;
    int  Usagefee,TopupAmount,TopupPGcharges,TotalAmount;

    public static ArrayList<String> TopupIDArrayList = new ArrayList<String>();
    public static ArrayList<String> TopupNameArrayList = new ArrayList<String>();
    public static ArrayList<String> TopupValidityArrayList = new ArrayList<String>();
    public static ArrayList<Integer> Totalamountoftopup = new ArrayList<Integer>();
    public static ArrayList<Integer> TopupPgchargesList = new ArrayList<Integer>();
    Spinner TopupPlans;
    public ArrayAdapter<String> Topupadapter;
    TextView planname_topup, planvalidity_topup, planuserfee_topup, plantotalfee_topup,planservicefee_topup;
    JSONObject Topupobject;

    public static final String merchant_id = "96478";
    public static final String access_code = "AVZS70EE81BJ45SZJB";
    public static final String working_key = "E2B38B98BE6D31F70F4320A8F1A784B0";
    public static final String currency = "INR";

    public static final String redirect_url = "https://www.mytrintrin.com/app/ccavResponseHandler.php";
    public static final String cancel_url = "https://www.mytrintrin.com/app/ccavResponseHandler.php";
    public static final String rsa_url = "https://www.mytrintrin.com/app/GetRSA.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup);
        TopupToolbar = (Toolbar) findViewById(R.id.topuptoolbar);
        TopupToolbar.setTitle("Top Up");
        setSupportActionBar(TopupToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Topupamount = (EditText) findViewById(R.id.topupamount);*/
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
        checkinternet();
        TopupPlans = (Spinner) findViewById(R.id.topupplans);
        planname_topup = (TextView) findViewById(R.id.planname_topup);
        planvalidity_topup = (TextView) findViewById(R.id.planvalidity_topup);
        planuserfee_topup = (TextView) findViewById(R.id.planusagefee_topup);
        plantotalfee_topup = (TextView) findViewById(R.id.plantotalfee_topup);
        planservicefee_topup = (TextView) findViewById(R.id.planservicefee_topup);
        gettopupplans();

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
           // Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Topup.this);
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

   /* public void Topupuser(View view) {

        TotalAmount = TopupAmount+TopupPGcharges;
        String orderid = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vAccessCode = ServiceUtility.chkNull(access_code).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchant_id).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency).toString().trim();
        String vAmount = ServiceUtility.chkNull(TotalAmount).toString().trim();
        String vLoginid = ServiceUtility.chkNull(loginuserid).toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("") && !vLoginid.equals("")) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(access_code).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchant_id).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderid).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(TotalAmount).toString().trim());
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirect_url).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancel_url).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsa_url).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_PARAM_1, ServiceUtility.chkNull(loginuserid).toString().trim());
            startActivity(intent);
        } else {
            // showToast("All parameters are mandatory.");
        }
    }*/

    public void Topupuser(View view)

    {

        final String CustomerId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        StringRequest Topuprequest = new StringRequest(Request.Method.POST, API.selectplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Topup response ",response);
                if(!response.equals(null))
                {
                    Intent paygovintent = new Intent(Topup.this,Paygovwebview.class);
                    paygovintent.putExtra("paygovresponse",response);
                    startActivity(paygovintent);
                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(response));
                    startActivity(intent);*/
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                    Toast.makeText(Topup.this, "Please check your connection.", Toast.LENGTH_LONG).show();
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
        }){
           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }*/

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf(TopupAmount));
                params.put("CustomerID", CustomerId);
                params.put("AdditionalInfo1",Topupname);
                params.put("AdditionalInfo2","Topup");
                params.put("AdditionalInfo3",loginuserid);
                return params;
            }
        };
        Topuprequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(Topuprequest);

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
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Topup.this);
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
        getplanrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(getplanrequest);
    }


    public void gettopupdetails() {
        try {
            JSONArray data = Topupobject.getJSONArray("data");
            TopupIDArrayList.clear();
            TopupNameArrayList.clear();
            Totalamountoftopup.clear();
            TopupValidityArrayList.clear();
            TopupPgchargesList.clear();
            for (int i = 0; i < data.length(); i++) {
                JSONObject getid = data.getJSONObject(i);
                String id = getid.getString("topupId");
                String name = getid.getString("topupName");
                Usagefee = getid.getInt("userFees");
                TopupValidity = getid.getString("validity");
                int pgcharges = getid.getInt("ccserviceCharge");

                TopupIDArrayList.add(id);
                TopupNameArrayList.add(name);
                Totalamountoftopup.add(Usagefee);
                TopupValidityArrayList.add(TopupValidity);
                TopupPgchargesList.add(pgcharges);

                Topupadapter = new ArrayAdapter<String>(Topup.this, android.R.layout.simple_spinner_dropdown_item, TopupNameArrayList);
                Topupadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                TopupPlans.setAdapter(Topupadapter);
                TopupPlans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Topupname = TopupPlans.getSelectedItem().toString();
                        Topupid = TopupIDArrayList.get(position);

                        planname_topup.setText("Plan Name : " + Topupname);
                        planvalidity_topup.setText("Validity : " + TopupValidityArrayList.get(position) + " days");
                        planuserfee_topup.setText("Usage Fee : " + Totalamountoftopup.get(position) + "/-");
                        planservicefee_topup.setText("Service Fee : " + TopupPgchargesList.get(position) + "/-");
                        TopupAmount = Totalamountoftopup.get(position);
                        TopupPGcharges = TopupPgchargesList.get(position);
                       // int total = TopupAmount + TopupPGcharges;
                        int total = TopupAmount ;
                        plantotalfee_topup.setText("Total : "+total+"/-");
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
}


