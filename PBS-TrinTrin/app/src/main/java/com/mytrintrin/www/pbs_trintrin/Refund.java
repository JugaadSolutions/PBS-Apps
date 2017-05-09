package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
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
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Refund extends AppCompatActivity {

    Button Refundrequest;
    String TransationId, Comments, Payment_mode, MemberBalance;
    String UserName;
    Spinner Refund_Paymentmode;
    EditText Refund_Transactionid, Refund_Comments,Refund_Username;
    Toolbar Refundtoolbar;
    private ProgressDialog mProgressDialog;
    int memberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refund);
        Refundrequest = (Button) findViewById(R.id.brefundrequest);
        Refund_Paymentmode = (Spinner) findViewById(R.id.refundpaymentmode);
        Refund_Transactionid = (EditText) findViewById(R.id.etrefundtransactionid);
        Refund_Comments = (EditText) findViewById(R.id.etrefundcomments);

        Refund_Username = (EditText) findViewById(R.id.etrefundname);
        Refundtoolbar = (Toolbar) findViewById(R.id.refundtoolbar);
        Refundtoolbar.setTitle("Refund");
        setSupportActionBar(Refundtoolbar);
       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        UserName=getIntent().getStringExtra("Name");
        memberid=getIntent().getIntExtra("userid",0);
        Refund_Username.setText(UserName);
        Log.d("Member ID", String.valueOf(memberid));

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        checkinternet();

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Refund.this);
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

    public void Requestforrefund(View view) {
        checkinternet();
        TransationId = Refund_Transactionid.getText().toString().trim();
        Comments = Refund_Comments.getText().toString().trim();
        Payment_mode = Refund_Paymentmode.getSelectedItem().toString();
        if(TransationId.isEmpty())
        {
            Refund_Transactionid.setError("Transaction Number");return;
        }
        if(Comments.isEmpty())
        {
            Refund_Comments.setError("Comments");return;
        }
        AlertDialog.Builder RefundBuilder = new AlertDialog.Builder(this);
        RefundBuilder.setIcon(R.mipmap.logo);
        RefundBuilder.setTitle("Refund Request");
        RefundBuilder.setMessage("Do you really want to cancel membership and request for refund?");
        RefundBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringRequest refundrequest = new StringRequest(Request.Method.GET, API.cancelmemberrequest + memberid + "/cancelmemberrequest", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject refundrequestresponse = new JSONObject(response);
                            JSONObject data = refundrequestresponse.getJSONObject("data");
                            String refundrequestmessage = data.getString("message");
                            AlertDialog.Builder RefundRequestBuilder = new AlertDialog.Builder(Refund.this);
                            RefundRequestBuilder.setIcon(R.mipmap.logo);
                            RefundRequestBuilder.setTitle("Refund Notice!!");
                            RefundRequestBuilder.setMessage(refundrequestmessage);
                            RefundRequestBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkinternet();
                                    StringRequest cancelmembership = new StringRequest(Request.Method.POST, API.cancelmembership + memberid + "/cancelmembership", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(Refund.this, "Refund success and Membership cancelled", Toast.LENGTH_SHORT).show();
                                            Refund_Transactionid.setText("");
                                            Refund_Comments.setText("");
                                            startActivity(new Intent(Refund.this, GetStarted.class));
                                            finish();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            if (error.networkResponse != null) {
                                                parseVolleyError(error);
                                                return;
                                            }

                                            if (error instanceof ServerError) {
                                                Toast.makeText(Refund.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                                                Log.d("Error", String.valueOf(error instanceof ServerError));
                                                error.printStackTrace();
                                            } else if (error instanceof AuthFailureError) {
                                                Toast.makeText(Refund.this, "Authentication Error", Toast.LENGTH_LONG).show();
                                                Log.d("Error", "Authentication Error");
                                                error.printStackTrace();
                                            } else if (error instanceof ParseError) {
                                                Toast.makeText(Refund.this, "Parse Error", Toast.LENGTH_LONG).show();
                                                Log.d("Error", "Parse Error");
                                                error.printStackTrace();
                                            } else if (error instanceof NetworkError) {
                                                Toast.makeText(Refund.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                                        Refund.this);
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
                                                Toast.makeText(Refund.this, "Timeout Error", Toast.LENGTH_LONG).show();
                                                Log.d("Error", "Timeout Error");
                                                error.printStackTrace();
                                            } else if (error instanceof NoConnectionError) {
                                                Toast.makeText(Refund.this, "No Connection Error", Toast.LENGTH_LONG).show();
                                                Log.d("Error", "No Connection Error");
                                                error.printStackTrace();
                                            } else {
                                                Toast.makeText(Refund.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                                            params.put("creditMode", Payment_mode);
                                            params.put("transactionNumber", TransationId);
                                            params.put("comments", Comments);
                                            return params;
                                        }
                                    };
                                    cancelmembership.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(cancelmembership);
                                }
                            });
                            RefundRequestBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(Refund.this, "Refund Request Cancelled.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RefundRequestBuilder.setCancelable(false);
                            RefundRequestBuilder.show();
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
                            Toast.makeText(Refund.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                            Log.d("Error", String.valueOf(error instanceof ServerError));
                            error.printStackTrace();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(Refund.this, "Authentication Error", Toast.LENGTH_LONG).show();
                            Log.d("Error", "Authentication Error");
                            error.printStackTrace();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(Refund.this, "Parse Error", Toast.LENGTH_LONG).show();
                            Log.d("Error", "Parse Error");
                            error.printStackTrace();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(Refund.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    Refund.this);
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
                            Toast.makeText(Refund.this, "Timeout Error", Toast.LENGTH_LONG).show();
                            Log.d("Error", "Timeout Error");
                            error.printStackTrace();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(Refund.this, "No Connection Error", Toast.LENGTH_LONG).show();
                            Log.d("Error", "No Connection Error");
                            error.printStackTrace();
                        } else {
                            Toast.makeText(Refund.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                refundrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(refundrequest);
            }
        });
        RefundBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Refund.this, "Refund Request Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        RefundBuilder.setCancelable(false);
        RefundBuilder.show();
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
