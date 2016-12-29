package com.mytrintrin.www.pbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button login;

    String email, pass;
    StringRequest loginrequest;
    Toolbar logintoolbar;


    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    CheckBox showpasslogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.etloginemail);
        password = (EditText) findViewById(R.id.etloginpassword);
        login = (Button) findViewById(R.id.blogin);
        logintoolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(logintoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        logintoolbar.setTitle("Login");
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        showpasslogin = (CheckBox) findViewById(R.id.loginshowpassword);
        checkinternet();

    }

    public void showpasswordlogin(View view)
    {
        if(showpasslogin.isChecked())
        {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Login.this);
            builder.setIcon(R.drawable.ic_wifi);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }

    /*ends*/

    public void verifylogin(View view) {
        checkinternet();
        email = username.getText().toString().trim();
        pass = password.getText().toString().trim();
        if (email.equals("") || pass.equals("") || email.length() < 9 || pass.length() < 4) {
            username.setError("Enter valid username/password");
            password.setError("Enter valid username/password");
        } else {
            loginrequest = new StringRequest(Request.Method.POST, API.loginapi, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject responsefromserver = new JSONObject(response);
                        Boolean error = responsefromserver.getBoolean("error");
                        String message = responsefromserver.getString("message");
                        JSONObject data = responsefromserver.getJSONObject("data");
                        String userid = data.getString("id");
                        String role = data.getString("role");
                        Log.d("role", role);
                        Log.d("User-id", userid);
                        editor.putString("User-id",userid);
                        editor.commit();
                        if (role.equals("registration-employee")) {
                            Toast.makeText(Login.this, role, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, Registration.class));
                        }
                        else if (role.equals("redistribution-employee"))
                        {
                            Toast.makeText(Login.this, role, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, Redistribution.class));

                        }

                        else {
                            AlertDialog.Builder LoginBuilder = new AlertDialog.Builder(Login.this);
                            LoginBuilder.setIcon(R.drawable.logo);
                            LoginBuilder.setTitle("Authorization Fail!");
                            LoginBuilder.setMessage("Invalid user??");
                            LoginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Login.this, "Try to login with valid credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                            LoginBuilder.show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        AlertDialog.Builder LoginBuilder = new AlertDialog.Builder(Login.this);
                        LoginBuilder.setIcon(R.drawable.logo);
                        LoginBuilder.setTitle("Invalid user");
                        LoginBuilder.setMessage("Authorization Fail!");
                        LoginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Login.this, "Try to login with valid credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                        LoginBuilder.show();
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(Login.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(Login.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(Login.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(Login.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(Login.this, "No Connection Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                    params.put("username", email);
                    params.put("password", pass);
                    return params;
                }
            };
            PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(loginrequest);
        }
    }


}
