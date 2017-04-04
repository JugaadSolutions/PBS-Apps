package com.mytrintrin.www.mytrintrin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
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

public class Tickets extends AppCompatActivity {

    private Toolbar Ticketstoolbar;
    String Name,Loginid,Subject,Description,Department;
    int Userid;
    EditText Name_ticketrc,Subject_ticketrc,Description_ticketrc;
    private ProgressDialog mProgressDialog;
    JSONObject Ticketobject;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    CardView Ticketcardview;
    Context context;
    LinearLayout Ticketslayout, TicketDetailsLayout;
    LinearLayout.LayoutParams Ticketcardparams,Ticketlayoutparams;
    TextView TicketStatus,TicketId,TicketCreatedDate,TicketDescription,PreviousTickets;
    JSONArray TicketArray;
    int i=0,ticketposition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets);
        Ticketstoolbar= (Toolbar) findViewById(R.id.ticketstoolbar);
        Ticketstoolbar.setTitle("Tickets");
        setSupportActionBar(Ticketstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        Loginid = loginpref.getString("User-id", null);
        Name_ticketrc = (EditText) findViewById(R.id.name_ticketrc);
        Subject_ticketrc = (EditText) findViewById(R.id.subject_ticketrc);
        Description_ticketrc = (EditText) findViewById(R.id.description_ticketrc);
        PreviousTickets = (TextView) findViewById(R.id.previoustickets);
        checkinternet();
        context = getApplicationContext();
        TicketDetailsLayout = (LinearLayout) findViewById(R.id.getticketslayout);
        Ticketobject = new JSONObject();
        if (getIntent().getExtras() != null) {
            Name = getIntent().getStringExtra("Name");
            Name_ticketrc.setText(Name);
            Name_ticketrc.setEnabled(false);
        }
        getusertickets();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Tickets.this);
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

    public void raiseticket(View view)
    {
        checkinternet();
        Name = Name_ticketrc.getText().toString().trim();
        Subject = Subject_ticketrc.getText().toString().trim();
        Description = Description_ticketrc.getText().toString().trim();
        if(Name.equals("")||Name.equals(null))
        {
            Name_ticketrc.setError("Name");
            return;
        }
        if(Subject.equals("")||Subject.equals(null))
        {
            Subject_ticketrc.setError("Subject");
            return;
        }
        if(Description.equals("")||Description.equals(null))
        {
            Description_ticketrc.setError("Description");
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String ticketdate = dateFormatGmt.format(new Date()) + "";

        try {
            Ticketobject.put("name",Name);
            Ticketobject.put("createdBy", Loginid);
            Ticketobject.put("ticketdate", ticketdate);
            Ticketobject.put("subject",Subject);
            Ticketobject.put("channel", "4");
            Ticketobject.put("description",Description);
            Ticketobject.put("priority","3");
            Ticketobject.put("department",Department);
            Ticketobject.put("user",Loginid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest ticketsrcrequest = new JsonObjectRequest(Request.Method.POST, API.ticketsfromuser,Ticketobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                Subject_ticketrc.setError("Subject");
                Description_ticketrc.setError("Description");
                startActivity(new Intent(Tickets.this,MyAccount.class));
                finish();
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
                    Toast.makeText(Tickets.this, "Server is under maintenance.please try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Tickets.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Tickets.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Tickets.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Tickets.this);
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
                    Toast.makeText(Tickets.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Tickets.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Tickets.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        ticketsrcrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(ticketsrcrequest);
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

    public void getusertickets()
    {
        StringRequest getticketsrequest = new StringRequest(Request.Method.GET, API.getusertickets+Loginid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if(data.length()>0)
                    {
                        TicketArray = new JSONArray();
                        PreviousTickets.setVisibility(View.VISIBLE);
                        for(i=0;i<data.length();i++)
                        {
                            JSONObject ticketobject = data.getJSONObject(i);
                            TicketArray.put(ticketobject);
                            String TicketID = ticketobject.getString("uuId");
                            String date = ticketobject.getString("ticketdate");
                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(inFormat.parse(date));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String Subject = ticketobject.getString("subject");
                            String Ticketstatus = ticketobject.getString("status");

                            Ticketcardview = new CardView(context);
                            Ticketcardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            Ticketcardparams.bottomMargin = 10;
                            Ticketcardview.setLayoutParams(Ticketcardparams);
                            Ticketcardview.setRadius(15);
                            Ticketcardview.setPadding(25, 25, 25, 25);
                            Ticketcardview.setCardBackgroundColor(Color.parseColor("#009746"));
                            Ticketcardview.setVisibility(View.VISIBLE);
                            Ticketcardview.setMaxCardElevation(30);
                            Ticketcardview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                   ticketposition= TicketDetailsLayout.indexOfChild(view);
                                    ticketdetailsdialog();
                                }
                            });

                            Ticketslayout = new LinearLayout(context);
                            Ticketlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            Ticketslayout.setOrientation(LinearLayout.VERTICAL);
                            Ticketslayout.setGravity(Gravity.END);
                            Ticketslayout.setPadding(25, 25, 25, 25);
                            Ticketslayout.setLayoutParams(Ticketlayoutparams);

                            TicketId = new TextView(context);
                            TicketId.setText("Ticket Id: UT"+TicketID);
                            TicketId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            TicketId.setTextColor(Color.WHITE);

                            TicketDescription = new TextView(context);
                            TicketDescription.setText("Subject: "+Subject);
                            TicketDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            TicketDescription.setTextColor(Color.WHITE);

                            TicketStatus= new TextView(context);
                            TicketStatus.setText("Status : "+Ticketstatus);
                            TicketStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            TicketStatus.setTextColor(Color.WHITE);

                            Ticketslayout.addView(TicketId);
                            Ticketslayout.addView(TicketDescription);
                            Ticketslayout.addView(TicketStatus);

                            Ticketcardview.addView(Ticketslayout);
                            TicketDetailsLayout.addView(Ticketcardview);
                        }
                    }
                    else
                    {
                        AlertDialog.Builder TicketBuilder = new AlertDialog.Builder(Tickets.this);
                        TicketBuilder.setIcon(R.drawable.splashlogo);
                        TicketBuilder.setTitle("Tickets");
                        TicketBuilder.setMessage("No tickets to display");
                        TicketBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        TicketBuilder.show();
                        Toast.makeText(Tickets.this, "No tickets to display", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Tickets.this, "Server is under maintenance.please try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Tickets.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Tickets.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Tickets.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Tickets.this);
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
                    Toast.makeText(Tickets.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Tickets.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Tickets.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        getticketsrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(getticketsrequest);
    }

    public void ticketdetailsdialog()
    {
        try {
            JSONObject Ticketdetails = TicketArray.getJSONObject(ticketposition);
            String TicketID = Ticketdetails.getString("uuId");
            String Name = Ticketdetails.getString("name");
            String Subject=Ticketdetails.getString("subject");
            String Description = Ticketdetails.getString("description");
            String Date = Ticketdetails.getString("ticketdate");
            String Status = Ticketdetails.getString("status");
            final LinearLayout TicketLayout = new LinearLayout(Tickets.this);
            TicketLayout.setPadding(8,8,8,8);
            TicketLayout.setOrientation(LinearLayout.VERTICAL);

            TextView ID = new TextView(Tickets.this);
            ID.setText("Ticket No: UT"+TicketID);
            ID.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView name = new TextView(Tickets.this);
            name.setText("Name: "+Name);
            name.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView subject = new TextView(Tickets.this);
            subject.setText("Subject: "+Subject);
            subject.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView description = new TextView(Tickets.this);
            description.setText("Description: "+Description);
            description.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView date = new TextView(Tickets.this);
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(inFormat.parse(Date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date.setText("Date: "+outFormat.format(c.getTime()));
            date.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView status = new TextView(Tickets.this);
            status.setText("Status: "+Status);
            status.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView reply = new TextView(Tickets.this);
            reply.setText("Click here to reply");
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*EditText reply = new EditText(Tickets.this);
                    LinearLayout.LayoutParams replyparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    reply.setLayoutParams(replyparams);
                    reply.setHint("Reply");
                    reply.setBackgroundResource(R.drawable.input_outline);
                    reply.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    reply.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    reply.setTypeface(null, Typeface.BOLD);
                    reply.setPadding(15, 15, 15, 15);
                    replyparams.setMargins(0, 0, 0, 15);
                    TicketLayout.addView(reply);*/
                    LayoutInflater replyinflate = LayoutInflater.from(Tickets.this);
                    View replyview = replyinflate.inflate(R.layout.replytickets, null);
                    final EditText replyet = (EditText) replyview.findViewById(R.id.etreplytickets);
                    replyet.requestFocus();
                    replyet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        }
                    });
                    TicketLayout.addView(replyview);
                }
            });

            TicketLayout.addView(ID);
            TicketLayout.addView(name);
            TicketLayout.addView(subject);
            TicketLayout.addView(description);
            TicketLayout.addView(date);
            TicketLayout.addView(status);
            TicketLayout.addView(reply);

            Toast.makeText(Tickets.this, TicketID, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder TicketBuilder = new AlertDialog.Builder(Tickets.this);
            AlertDialog dialog = TicketBuilder.create();
            TicketBuilder.setTitle("Ticket Details");
            TicketBuilder.setIcon(R.drawable.splashlogo);
            TicketBuilder.setView(TicketLayout);
            TicketBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            TicketBuilder.setNegativeButton("Reply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            //TicketBuilder.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            TicketBuilder.show();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
