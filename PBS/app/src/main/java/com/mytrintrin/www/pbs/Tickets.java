package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Tickets extends AppCompatActivity {

    EditText Searchname;
    LinearLayout SearchResultslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets);
        Searchname = (EditText) findViewById(R.id.searchby_emailphone);
        SearchResultslayout = (LinearLayout) findViewById(R.id.searchresultslayout);
        getalldepartments();
    }

    public void getsearchresults(View view)
    {
        StringRequest searchrequest = new StringRequest(Request.Method.POST, API.searchmeber, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getalldepartments();
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if(data.length()>0)
                    {
                        SearchResultslayout.removeAllViews();
                        Toast.makeText(Tickets.this, "User Found with this phone number", Toast.LENGTH_SHORT).show();
                        for (int i =0;i<data.length();i++)
                        {
                            final JSONObject resultobject = data.getJSONObject(i);
                            String name = resultobject.getString("Name");
                            Button usernamebutton = new Button(Tickets.this);
                            LinearLayout.LayoutParams usernameparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            usernamebutton.setLayoutParams(usernameparams);
                            usernameparams.setMargins(0,0,0,5);
                            usernamebutton.setBackgroundResource(R.drawable.roundcorner);
                            usernamebutton.setTextColor(Color.WHITE);
                            usernamebutton.setText(name);
                            SearchResultslayout.addView(usernamebutton);
                            usernamebutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(Tickets.this, "No User Found with this phone number", Toast.LENGTH_SHORT).show();
                        SearchResultslayout.removeAllViews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("name",Searchname.getText().toString().trim());
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(searchrequest);
    }

   public void getalldepartments()
    {
        StringRequest getdepartmentrequest = new StringRequest(Request.Method.GET, API.getdepartment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Department",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getdepartmentrequest);
    }
}
