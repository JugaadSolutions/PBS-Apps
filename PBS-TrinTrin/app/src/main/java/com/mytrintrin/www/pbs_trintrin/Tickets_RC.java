package com.mytrintrin.www.pbs_trintrin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Tickets_RC extends AppCompatActivity {

    Toolbar TicketsRCToolbar;
    String Name,Loginid,Subject,Description;
    int Userid;
    EditText Name_ticketrc,Subject_ticketrc,Description_ticketrc;
    private ProgressDialog mProgressDialog;
    JSONObject Ticketobject;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets_rc);
        TicketsRCToolbar = (Toolbar) findViewById(R.id.ticketsrctoolbar);
        TicketsRCToolbar.setTitle("Ticket");
        setSupportActionBar(TicketsRCToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        Loginid = loginpref.getString("User-id", null);
        Name_ticketrc = (EditText) findViewById(R.id.name_ticketrc);
        Subject_ticketrc = (EditText) findViewById(R.id.subject_ticketrc);
        Description_ticketrc = (EditText) findViewById(R.id.description_ticketrc);

        Ticketobject = new JSONObject();
        if (getIntent().getExtras() != null) {
            Name = getIntent().getStringExtra("Name");
            Userid= getIntent().getIntExtra("userid",0);
            addtoticketobject();
            Toast.makeText(this, "Getting user details", Toast.LENGTH_SHORT).show();
            }
         else {
            Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
        }
    }

    private void addtoticketobject() {

        try {
            Ticketobject.put("user", Userid);
            Name_ticketrc.setText(Name);
            Name_ticketrc.setEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void raiseticket(View view)
    {
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
            Ticketobject.put("priority","2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest ticketsrcrequest = new JsonObjectRequest(Request.Method.POST, API.ticketsfromrc,Ticketobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                Subject_ticketrc.setError("Subject");
                Description_ticketrc.setError("Description");
                startActivity(new Intent(Tickets_RC.this,GetStarted.class));
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
                    Toast.makeText(Tickets_RC.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Tickets_RC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Tickets_RC.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Tickets_RC.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Tickets_RC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Tickets_RC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Tickets_RC.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        ticketsrcrequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(ticketsrcrequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}
