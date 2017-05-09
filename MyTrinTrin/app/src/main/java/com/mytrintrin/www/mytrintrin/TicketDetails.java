package com.mytrintrin.www.mytrintrin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TicketDetails extends AppCompatActivity {

    String Ticketdetails,TicketId,Ticketstatus,Ticketdescription,Loginid;
    JSONObject Ticketobject;
    TextView TicketID,TicketStatus,TicketDescription,TicketSubject,Ticketcreated,TickeReplies;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    EditText ReplyforTicket;
    LinearLayout TicketConversations;
    private Toolbar Ticketsdetailstoolbar;
    CheckBox CloseTicket;
    Button CloseorReply;
    TextInputLayout CloseorReplyTI;
    ScrollView TicketsScrollLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_details);
        TicketID = (TextView) findViewById(R.id.id_ticketdetails);
        TicketStatus = (TextView) findViewById(R.id.status_ticketdetails);
        TicketDescription = (TextView) findViewById(R.id.description_ticketdetails);
        TicketSubject = (TextView) findViewById(R.id.subject_ticketdetails);
        Ticketcreated = (TextView) findViewById(R.id.createdat_ticketdetails);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        Loginid = loginpref.getString("User-id", null);
        ReplyforTicket = (EditText) findViewById(R.id.replyticket);
        TicketConversations = (LinearLayout) findViewById(R.id.ticketconverstationlayout);
        TicketsScrollLayout = (ScrollView) findViewById(R.id.ticketconverstationscroll);
        Ticketsdetailstoolbar = (Toolbar) findViewById(R.id.ticketdetailstoolbar);
        CloseTicket= (CheckBox) findViewById(R.id.cbcloseticket);
        TickeReplies = (TextView) findViewById(R.id.replies_ticketdetails);
        Ticketsdetailstoolbar.setTitle("Tickets");
        setSupportActionBar(Ticketsdetailstoolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CloseorReply = (Button) findViewById(R.id.btncloseorreply);
        CloseorReplyTI = (TextInputLayout) findViewById(R.id.ticketreply);

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            Ticketdetails = intent.getStringExtra("TicketObject");
            if (Ticketdetails != null) {
                setTicketdetails();
                Toast.makeText(this, "Getting ticket details", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "New Ticket", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTicketdetails()
    {
        try {
            Ticketobject = new JSONObject(Ticketdetails);
            TicketId = Ticketobject.getString("uuId");
            String Name = Ticketobject.getString("name");
            String Subject = Ticketobject.getString("subject");
            Ticketdescription = Ticketobject.getString("description");
            String Date = Ticketobject.getString("ticketdate");
            Ticketstatus = Ticketobject.getString("status");
            TicketID.setText("Ticket No : UT" + TicketId);
            SimpleDateFormat ticketinFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat ticketoutFormat = new SimpleDateFormat("MMM dd, yyyy");
            Calendar ticketcal = Calendar.getInstance();
            try {
                ticketcal.setTime(ticketinFormat.parse(Date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Ticketcreated.setText("Created at : " + ticketoutFormat.format(ticketcal.getTime()));
            TicketSubject.setText("Subject : " + Subject);
            TicketDescription.setText("Description : " + Ticketdescription);
            TicketStatus.setText("Status : " + Ticketstatus);
            if(Ticketstatus.equals("Close"))
            {
                CloseTicket.setVisibility(View.GONE);
                ReplyforTicket.setEnabled(false);
                CloseorReply.setEnabled(false);
            }
            else {
                CloseTicket.setVisibility(View.VISIBLE);
            }
            JSONArray transactions = Ticketobject.getJSONArray("transactions");
            if (transactions.length() > 0)
            {
                for (int i = 0; i < transactions.length(); i++) {
                    JSONObject transactionobject = transactions.getJSONObject(i);
                    String replydescription = transactionobject.getString("description");
                    String replyat = transactionobject.getString("replydate");
                    SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(inFormat.parse(replyat));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject replierobject = transactionobject.getJSONObject("replierId");
                    String repliername = replierobject.getString("Name");
                    String replierid = replierobject.getString("UserID");

                    TextView Description = new TextView(TicketDetails.this);
                    Description.setText(replydescription);
                    Description.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    Description.setTextSize(16);

                    TextView ReplierName = new TextView(TicketDetails.this);
                    if (replierid.equals(Loginid)) {
                        ReplierName.setText("By : You"+ " | " + "On :" + outFormat.format(c.getTime()));
                    } else {
                        ReplierName.setText("By : " + repliername + " | " + "On :" + outFormat.format(c.getTime()));
                    }
                    ReplierName.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    ReplierName.setTextSize(12);

                    View v = new View(this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    v.setPadding(5, 5, 5, 5);
                    v.setBackgroundColor(Color.parseColor("#009746"));

                    TicketConversations.addView(Description);
                    TicketConversations.addView(ReplierName);
                    TicketConversations.addView(v);
                }
        }
            else
            {
                TicketsScrollLayout.setVisibility(View.GONE);
                TickeReplies.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    TicketDetails.this);
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

    public void ticketreply(View view)
    {
        checkinternet();
        if(ReplyforTicket.getText().toString().equals("")||ReplyforTicket.getText().toString().equals(null))
        {
            ReplyforTicket.setError("Reply");
            return;
        }
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String replytime = dateFormatGmt.format(new Date()) + "";
        StringRequest ticketreplyrequest = new StringRequest(Request.Method.POST, API.replytotickets+TicketId+"/addreply", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(TicketDetails.this, "Reply sent successfully", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder replybuilder = new AlertDialog.Builder(TicketDetails.this);
                replybuilder.setIcon(R.drawable.trintrinlogo);
                replybuilder.setTitle("Reply");
                replybuilder.setMessage("Reply sent successfully.");
                replybuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(TicketDetails.this,MyAccount.class));
                        finish();
                    }
                });
                replybuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                        parseVolleyError(error);
                        return;
                    }
                    if (error instanceof ServerError) {
                        Toast.makeText(TicketDetails.this, "Server is under maintenance.please try later", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(TicketDetails.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(TicketDetails.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(TicketDetails.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                TicketDetails.this);
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
                        Toast.makeText(TicketDetails.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(TicketDetails.this, "No Connection Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(TicketDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("description",ReplyforTicket.getText().toString().trim() );
                params.put("status", Ticketstatus);
                params.put("replierId", Loginid);
                params.put("replydate", replytime);
                return params;
            }
        };
        ticketreplyrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(ticketreplyrequest);
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

    public void itemClicked(View v) {
        if(CloseTicket.isChecked()){
            CloseorReplyTI.setHint("Reason");
            CloseorReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeticket();
                }
            });
        }
        else
        {
            CloseorReplyTI.setHint("Reply");
            CloseorReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ticketreply(view);
                }
            });
        }
    }

    public void closeticket()
    {
        checkinternet();
        if(ReplyforTicket.getText().toString().equals("")||ReplyforTicket.getText().toString().equals(null))
        {
            ReplyforTicket.setError("Reason");
            return;
        }
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String replytime = dateFormatGmt.format(new Date()) + "";
        StringRequest Closeteicketrequest = new StringRequest(Request.Method.POST, API.replytotickets+TicketId+"/addreply", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AlertDialog.Builder closeticketbuilder = new AlertDialog.Builder(TicketDetails.this);
                closeticketbuilder.setIcon(R.drawable.splashlogo);
                closeticketbuilder.setTitle("Ticket");
                closeticketbuilder.setMessage("Ticket closed successfully");
                closeticketbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(TicketDetails.this,MyAccount.class));
                        finish();
                    }
                });
                closeticketbuilder.setCancelable(false);
                closeticketbuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(TicketDetails.this, "Server is under maintenance.please try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(TicketDetails.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(TicketDetails.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(TicketDetails.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            TicketDetails.this);
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
                    Toast.makeText(TicketDetails.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(TicketDetails.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(TicketDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("description",ReplyforTicket.getText().toString().trim() );
                params.put("status", "Close");
                params.put("replierId", Loginid);
                params.put("replydate", replytime);
                return params;
            }
        };
        Closeteicketrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(Closeteicketrequest);
    }
}
