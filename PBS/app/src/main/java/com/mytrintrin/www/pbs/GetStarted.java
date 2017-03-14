package com.mytrintrin.www.pbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

public class GetStarted extends AppCompatActivity {

    EditText Searchby_Phone;
    String searchby_phone;
    LinearLayout GeTstartedlayout,SearchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        Searchby_Phone = (EditText) findViewById(R.id.phone_searchmember);
        GeTstartedlayout = (LinearLayout) findViewById(R.id.getstartedlayout);
        SearchResults = (LinearLayout) findViewById(R.id.searchresults);
    }

    public void searchmemberbyphone(View view)
    {
        searchby_phone = Searchby_Phone.getText().toString().trim();
        if (searchby_phone.equals("")||(searchby_phone.equals(null)))
        {
            Searchby_Phone.setError("Phone Number");
            return;
        }
        StringRequest searchmemberbyphone = new StringRequest(Request.Method.POST, API.searchmeber, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if(data.length()>0)
                    {
                        SearchResults.removeAllViews();
                        Toast.makeText(GetStarted.this, "User Found with this phone number", Toast.LENGTH_SHORT).show();
                        for (int i =0;i<data.length();i++)
                        {
                            final JSONObject resultobject = data.getJSONObject(i);
                            String name = resultobject.getString("Name");
                            Button usernamebutton = new Button(GetStarted.this);
                            LinearLayout.LayoutParams usernameparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            usernamebutton.setLayoutParams(usernameparams);
                            usernameparams.setMargins(0,0,0,5);
                            usernamebutton.setBackgroundResource(R.drawable.roundcorner);
                            usernamebutton.setTextColor(Color.WHITE);
                            usernamebutton.setText(name);
                            SearchResults.addView(usernamebutton);
                            usernamebutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent registration = new Intent(GetStarted.this,Registration.class);
                                    registration.putExtra("Memberobject",resultobject.toString());
                                    startActivity(registration);
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(GetStarted.this, "No User Found with this phone number", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(GetStarted.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GetStarted.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GetStarted.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(GetStarted.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(GetStarted.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(GetStarted.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("name","91-"+searchby_phone);
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(searchmemberbyphone);
    }

    public void gotoregister(View view)
    {
        startActivity(new Intent(this,Registration.class));
    }
}
