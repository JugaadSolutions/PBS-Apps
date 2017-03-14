package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
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

public class SelectPlan extends AppCompatActivity {

    private Toolbar SelectPlanToolbar;
    private ProgressDialog mProgressDialog;
    public static ArrayList<String> MembershipIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipNameArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipValidityArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipUserFeeArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipSecurityFeeArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipSmartcardFeeArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipProcessingFeeArrayList = new ArrayList<String>();

    public static final String merchant_id ="96478";
    public static final String access_code ="AVUM66DI93AY80MUYA";
    public static final String working_key="5F8405032B54AF1400A79BB0B92D2ECC";
    public static final String currency="INR";
    public static final String redirect_url="http://www.mytrintrin.com/app/ccavResponseHandler.php";
    public static final String cancel_url="http://www.mytrintrin.com/app/ccavResponseHandler.php";
    public static final String rsa_url="http://www.mytrintrin.com/app/GetRSA.php";

    public static ArrayAdapter<String> Memberplanadapter;
    Spinner Plans;
    String Planname, Planid, Planvalidity, Planuserfee, Plansecurityfee, Plansmartcardfee, Planprocessingfee;
    TextView planname, planvalidity, planuserfee, plansecurityfee, plansmartcardfee, planprocessingfee, plantotalfee;
    int plantotal;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_plan);
        SelectPlanToolbar = (Toolbar) findViewById(R.id.selectplantoolbar);
        SelectPlanToolbar.setTitle("Select Plan");
        setSupportActionBar(SelectPlanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Plans = (Spinner) findViewById(R.id.planspinner);
        planname = (TextView) findViewById(R.id.planname);
        planvalidity = (TextView) findViewById(R.id.planvalidity);
        planuserfee = (TextView) findViewById(R.id.planusagefee);
        plansecurityfee = (TextView) findViewById(R.id.plansecurityfee);
        plansmartcardfee = (TextView) findViewById(R.id.plansmartcardfee);
        planprocessingfee = (TextView) findViewById(R.id.planprocessingfee);
        plantotalfee = (TextView) findViewById(R.id.plantotalfee);

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);

        checkinternet();
        GetallPlans();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectPlan.this);
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
    /*ends*/

    public void GetallPlans() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest getplanrequest = new StringRequest(Request.Method.GET, API.getplans, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject plansresponse = new JSONObject(response);
                    JSONArray data = plansresponse.getJSONArray("data");
                    MembershipNameArrayList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getid = data.getJSONObject(i);
                        String id = getid.getString("membershipId");
                        String name = getid.getString("subscriptionType");
                        String validity = getid.getString("validity");
                        String userfees = getid.getString("userFees");
                        String securitydeposit = getid.getString("securityDeposit");
                        String smartcardfees = getid.getString("smartCardFees");
                        String processingfees = getid.getString("processingFees");

                        MembershipIDArrayList.add(id);
                        MembershipNameArrayList.add(name);
                        MembershipValidityArrayList.add(validity);
                        MembershipUserFeeArrayList.add(userfees);
                        MembershipSecurityFeeArrayList.add(securitydeposit);
                        MembershipSmartcardFeeArrayList.add(smartcardfees);
                        MembershipProcessingFeeArrayList.add(processingfees);

                        Memberplanadapter = new ArrayAdapter<String>(SelectPlan.this, android.R.layout.simple_spinner_dropdown_item, MembershipNameArrayList);
                        Memberplanadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Plans.setAdapter(Memberplanadapter);
                        Plans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Planname = Plans.getSelectedItem().toString();
                                Planid = MembershipIDArrayList.get(position);
                                Planvalidity = MembershipValidityArrayList.get(position);
                                Planuserfee = MembershipUserFeeArrayList.get(position);
                                Plansecurityfee = MembershipSecurityFeeArrayList.get(position);
                                Plansmartcardfee = MembershipSmartcardFeeArrayList.get(position);
                                Planprocessingfee = MembershipProcessingFeeArrayList.get(position);
                                planname.setText("Plane Name : " + Planname);
                                planvalidity.setText("Validity : " + Planvalidity + " days");
                                plansecurityfee.setText("Security Fee(Refundable) : " + Plansecurityfee + "/-");
                                planprocessingfee.setText("Processing Fee : " + Planprocessingfee + "/-");
                                planuserfee.setText("Usage Fee : " + Planuserfee + "/-");
                                plansmartcardfee.setText("Smartcard Fee : " + Plansmartcardfee + "/-");
                                plantotal = Integer.parseInt(Planuserfee) + Integer.parseInt(Plansecurityfee) + Integer.parseInt(Plansmartcardfee) + Integer.parseInt(Planprocessingfee);
                                plantotalfee.setText("Total : " + plantotal + "/-");
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
                if (error instanceof ServerError) {
                    Toast.makeText(SelectPlan.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    mProgressDialog.dismiss();
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(SelectPlan.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(SelectPlan.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(SelectPlan.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(SelectPlan.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(SelectPlan.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(SelectPlan.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        getplanrequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(getplanrequest);
    }

    public void makeplanpayment(View view)
    {
        String orderid = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vAccessCode =ServiceUtility.chkNull(access_code).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchant_id).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency).toString().trim();
        String vAmount = ServiceUtility.chkNull(plantotal).toString().trim();
        String vLoginid = ServiceUtility.chkNull(loginuserid).toString().trim();
        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")&& !vLoginid.equals("")){
            Intent intent = new Intent(this,WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(access_code).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchant_id).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderid).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(plantotal).toString().trim());
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirect_url).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancel_url).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsa_url).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_PARAM_1, ServiceUtility.chkNull(loginuserid).toString().trim());

            startActivity(intent);
        }else{
           // showToast("All parameters are mandatory.");
        }
    }
}
