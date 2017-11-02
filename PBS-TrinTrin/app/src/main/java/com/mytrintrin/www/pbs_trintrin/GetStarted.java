package com.mytrintrin.www.pbs_trintrin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GetStarted extends AppCompatActivity {

    EditText Searchby_Phone;
    String searchby_phone,loginuserid,centername;
    LinearLayout GeTstartedlayout, SearchResults;
    private Toolbar GetStartedToolbar;
    SharedPreferences loginpref,assignedpref;
    SharedPreferences.Editor editor,assignededitor;
    TextView Centername,Versionname;
    private long mRequestStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        GetStartedToolbar = (Toolbar) findViewById(R.id.getstartedtoolbar);
        GetStartedToolbar.setTitle("Trin Trin");
        setSupportActionBar(GetStartedToolbar);
        Searchby_Phone = (EditText) findViewById(R.id.phone_searchmember);
        GeTstartedlayout = (LinearLayout) findViewById(R.id.getstartedlayout);
        SearchResults = (LinearLayout) findViewById(R.id.searchresults);
        Centername = (TextView) findViewById(R.id.centername);
        Versionname = (TextView) findViewById(R.id.versionname);
        Versionname.setText("Version : "+BuildConfig.VERSION_NAME.toString());
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);

        assignedpref = getApplicationContext().getSharedPreferences("assignedPref", MODE_PRIVATE);
        assignededitor = assignedpref.edit();
        if(assignedpref.contains("assignedcenter"))
        {
            centername = assignedpref.getString("assignedcenter",null);
            if(centername.equals(null)||centername.equals("")) {
                getassignedcenter();
            }
            else
            {
                Centername.setText("You have been assigned to : "+centername);
            }
        }
        else
        {
            getassignedcenter();
        }

        checkinternet();

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    GetStarted.this);
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

    public void searchmemberbyphone(View view) {
        SearchResults.removeAllViews();
        searchby_phone = Searchby_Phone.getText().toString().trim();
        if (searchby_phone.equals("") || (searchby_phone.equals(null))) {
            Searchby_Phone.setError("Enter search credential");
            return;
        }
        if(TextUtils.isDigitsOnly(Searchby_Phone.getText())&&searchby_phone.length()>4)
        {
            searchby_phone="91-"+searchby_phone;
        }
        mRequestStartTime = System.currentTimeMillis();
       StringRequest searchmemberbyphone = new StringRequest(Request.Method.POST, API.searchmember, new Response.Listener<String>() {
       // StringRequest searchmemberbyphone = new StringRequest(Request.Method.GET, API.searchmember+searchby_phone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    /*byte[] compressed = compress(response);
                    String decompressed = decompress(compressed);
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                    Toast.makeText(GetStarted.this, ""+totalRequestTime, Toast.LENGTH_SHORT).show();
                    Log.d("Response Time",totalRequestTime+"");*/
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if (data.length() > 0) {
                        SearchResults.removeAllViews();
                        Toast.makeText(GetStarted.this, "User Found", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                GetStarted.this);
                        builder.setIcon(R.drawable.splashlogo);
                        builder.setTitle("Search Results");
                        builder.setMessage("User Found");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                        for (int i = 0; i < data.length(); i++) {
                            final JSONObject resultobject = data.getJSONObject(i);
                            String name = resultobject.getString("Name");
                            String lname = resultobject.getString("lastName");
                            Button usernamebutton = new Button(GetStarted.this);
                            LinearLayout.LayoutParams usernameparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            usernamebutton.setLayoutParams(usernameparams);
                            usernameparams.setMargins(0, 0, 0, 5);
                            int status = resultobject.getInt("status");
                            if(status==1) {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner);
                                usernamebutton.setTextColor(Color.WHITE);
                            }
                            else if(status==0)
                            {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner_yellow);
                                usernamebutton.setTextColor(Color.BLACK);
                            }
                            else if(status==2)
                            {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner);
                                usernamebutton.setTextColor(Color.WHITE);
                            }
                            else if(status==-1)
                            {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner_red);
                                usernamebutton.setTextColor(Color.WHITE);
                            }
                            else if(status==-2)
                            {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner_black);
                                usernamebutton.setTextColor(Color.WHITE);
                            }
                            else if(status==-3)
                            {
                                usernamebutton.setBackgroundResource(R.drawable.roundcorner_blue);
                                usernamebutton.setTextColor(Color.WHITE);
                            }
                            usernamebutton.setText(name+" "+lname);
                            SearchResults.addView(usernamebutton);
                            usernamebutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent registration = new Intent(GetStarted.this, Registration.class);
                                    registration.putExtra("Memberobject", resultobject.toString());
                                    startActivity(registration);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(GetStarted.this, "No User Found", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                GetStarted.this);
                        builder.setIcon(R.drawable.splashlogo);
                        builder.setTitle("Search Results");
                        builder.setMessage("No User Found");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Searchby_Phone.setText("");
                                        dialog.dismiss();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                Log.d("Response Time",totalRequestTime+"");
                Toast.makeText(GetStarted.this, ""+totalRequestTime, Toast.LENGTH_SHORT).show();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(GetStarted.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
                /*headers.put("Accept-Encoding", "gzip");
                headers.put("Content-Encoding", "gzip");*/
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", searchby_phone);
                return params;
            }
        };
        searchmemberbyphone.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(searchmemberbyphone);
    }

    public void gotoregister(View view)
    {
        startActivity(new Intent(this,Registration.class));
    }

    public void gotosignup(View view)
    {
        startActivity(new Intent(this,Signup.class));
    }

    public void gototickets(View view)
    {
        startActivity(new Intent(this,Tickets_RC.class));
    }

    public void Logoutoutfromregistration(View view)
    {
        editor.putString("User-id", "");
        editor.putString("Role","");
        editor.commit();

        assignededitor.putString("assignedcenter", "");
        assignededitor.commit();

        startActivity(new Intent(this,Login.class));
        finish();
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


    public void getassignedcenter()
    {
        StringRequest getassignedcenter = new StringRequest(Request.Method.GET, API.getassignedregistrationcenter, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if(data.length()>0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject rcenterobject = data.getJSONObject(i);
                            String name = rcenterobject.getString("name");
                            JSONObject assignedto = rcenterobject.getJSONObject("assignedTo");
                            String userid = assignedto.getString("UserID");
                            if(userid.equals(loginuserid))
                            {
                                assignededitor.putString("assignedcenter",name);
                                assignededitor.commit();
                                Centername.setText("You have been assigned to :"+name);
                                AlertDialog.Builder assignedbuilder = new AlertDialog.Builder(GetStarted.this);
                                assignedbuilder.setIcon(R.drawable.splashlogo);
                                assignedbuilder.setTitle("Assigned Registartion Center");
                                assignedbuilder.setMessage("You have been assigned to : "+name);
                                assignedbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                             dialogInterface.dismiss();
                                    }
                                });
                                assignedbuilder.setCancelable(false);
                                assignedbuilder.show();
                            }
                        }
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
                    Toast.makeText(GetStarted.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(GetStarted.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GetStarted.this);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        getassignedcenter.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getassignedcenter);
    }


    public static byte[] compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }



}
