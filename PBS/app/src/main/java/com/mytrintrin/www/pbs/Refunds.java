package com.mytrintrin.www.pbs;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Refunds extends AppCompatActivity {

    Button Refundrequest;
    private static ArrayList<String> MemberIdarray = new ArrayList<String>();
    private static ArrayList<String> Membernamearray = new ArrayList<String>();
    private static ArrayList<String> Memberbalancearray = new ArrayList<String>();
    String Membername, MemberId, TransationId, Comments, Payment_mode, MemberBalance;
    ArrayAdapter Membernameadapter;
    Spinner Refund_Membernamespinner, Refund_Paymentmode,testsearch;
    EditText Refund_Transactionid, Refund_Comments;

    Toolbar Refundtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refunds);
        Refundrequest = (Button) findViewById(R.id.brefundrequest);
        Refund_Membernamespinner = (Spinner) findViewById(R.id.refundmemberid);
        Refund_Paymentmode = (Spinner) findViewById(R.id.refundpaymentmode);
        Refund_Transactionid = (EditText) findViewById(R.id.etrefundtransactionid);
        Refund_Comments = (EditText) findViewById(R.id.etrefundcomments);

        Refundtoolbar = (Toolbar) findViewById(R.id.refundToolbar);
        Refundtoolbar.setTitle("Refund");
        setSupportActionBar(Refundtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getmemberdetails();
    }

    private void getmemberdetails() {

        StringRequest memberdetails = new StringRequest(Request.Method.GET, API.getmembersapi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject memberdetailsfromserver = new JSONObject(response);
                    Boolean error = memberdetailsfromserver.getBoolean("error");
                    String message = memberdetailsfromserver.getString("message");
                    JSONArray data = memberdetailsfromserver.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject first = data.getJSONObject(i);
                        Membername = first.getString("Name");
                        Membernamearray.add(Membername);
                        MemberId = first.getString("_id");
                        MemberIdarray.add(MemberId);
                        MemberBalance = first.getString("creditBalance");
                        Memberbalancearray.add(MemberBalance);
                        Log.d("Membername", String.valueOf(Membernamearray));
                        Log.d("MemeberId", String.valueOf(MemberIdarray));
                        Membernameadapter = new ArrayAdapter<String>(Refunds.this, android.R.layout.simple_spinner_dropdown_item, Membernamearray);
                        Membernameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Refund_Membernamespinner.setAdapter(Membernameadapter);

                        Refund_Membernamespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Membername = Refund_Membernamespinner.getSelectedItem().toString();
                                MemberId = MemberIdarray.get(position);
                                MemberBalance = Memberbalancearray.get(position);
                                Log.d("Member id is", MemberId + "Bal" + MemberBalance);
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
                    Toast.makeText(Refunds.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Refunds.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Refunds.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Refunds.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Refunds.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Refunds.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Refunds.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(memberdetails);
    }

    public void Requestforrefund(View view) {
        AlertDialog.Builder RefundBuilder = new AlertDialog.Builder(this);
        RefundBuilder.setIcon(R.drawable.logo);
        RefundBuilder.setTitle("Refund Request");
        RefundBuilder.setMessage("Do you really want to cancel membership and request for refund?");
        RefundBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(Refunds.this, "Yes", Toast.LENGTH_SHORT).show();
               /* TransationId = Refund_Transactionid.getText().toString().trim();
                Comments = Refund_Comments.getText().toString().trim();
                Payment_mode = Refund_Paymentmode.getSelectedItem().toString();*/
                StringRequest refundrequest = new StringRequest(Request.Method.GET, API.cancelmemberrequest+MemberId+"/cancelmemberrequest", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject refundrequestresponse = new JSONObject(response);
                            JSONObject data = refundrequestresponse.getJSONObject("data");
                            String refundrequestmessage = data.getString("message");
                            AlertDialog.Builder RefundRequestBuilder = new AlertDialog.Builder(Refunds.this);
                            RefundRequestBuilder.setIcon(R.drawable.logo);
                            RefundRequestBuilder.setTitle("Refund Notice!!");
                            RefundRequestBuilder.setMessage(refundrequestmessage);
                            RefundRequestBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    TransationId = Refund_Transactionid.getText().toString().trim();
                                    Comments = Refund_Comments.getText().toString().trim();
                                    Payment_mode = Refund_Paymentmode.getSelectedItem().toString();
                                    StringRequest cancelmembership = new StringRequest(Request.Method.POST, API.cancelmembership+MemberId+"/cancelmembership", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(Refunds.this, "Refund success and Membership cancelled", Toast.LENGTH_SHORT).show();
                                            Refund_Transactionid.setText("");
                                            Refund_Comments.setText("");
                                            startActivity(new Intent(Refunds.this,Registration.class));


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

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

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("creditMode", Payment_mode);
                                            params.put("transactionNumber", TransationId);
                                            params.put("comments", Comments);
                                            return params;
                                        }

                                    };

                                    PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(cancelmembership);


                                }
                            });
                            RefundRequestBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(Refunds.this, "Cancelled the refund request", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RefundRequestBuilder.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(refundrequest);


            }
        });
        RefundBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Refunds.this, "Cancelled the refund request", Toast.LENGTH_SHORT).show();
            }
        });
        RefundBuilder.show();
    }
}
