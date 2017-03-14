package com.mytrintrin.www.pbs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class Login extends AppCompatActivity {

    EditText username, password,forgotpassword;
    Button login;

    String email, pass;
    StringRequest loginrequest;
    Toolbar logintoolbar;


    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    CheckBox showpasslogin;
    public static final int RequestPermissionCode = 1;

    private ProgressDialog mProgressDialog;



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
        onpermision();

    }

    /*Requesting for permissions*/
    public void onpermision() {
        if (checkPermission()) {
            // Toast.makeText(ChildActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(Login.this, new String[]
                {
                        CAMERA,
                        ACCESS_FINE_LOCATION
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //  boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // boolean ReadRecordaudioPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;


                    if (CameraPermission&& LocationPermission) {

                        Toast.makeText(Login.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Login.this,LoginNFC.class));
                    } else {
                        Toast.makeText(Login.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        //int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;


    }

    /*Requesting permission ends*/

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
        pass= md5(pass);
        if (email.equals("") || pass.equals("") || email.length() < 2 || pass.length() < 2) {
            username.setError("Enter valid username/password");
            password.setError("Enter valid username/password");
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
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
                        mProgressDialog.dismiss();
                        if (role.equals("registration-employee")) {
                            Toast.makeText(Login.this, role, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, GetStarted.class));
                        }
                        else if (role.equals("redistribution-employee"))
                        {
                            Toast.makeText(Login.this, role, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, Redistribution.class));

                        }
                        else if (role.equals("maintenancecentre-employee"))
                        {
                            Toast.makeText(Login.this, role, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,Maintainance_Centre.class));
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
                        mProgressDialog.dismiss();
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

    @Override
    protected void onPause() {
        super.onPause();
        username.setText("");
        password.setText("");
    }

    public void gotoforgotpassword(View view)
    {
        LayoutInflater passwordinflate = LayoutInflater.from(this);
        View forgotpasswordView = passwordinflate.inflate(R.layout.forgotpasssword, null);
        final android.app.AlertDialog.Builder forgotpasswordbuilder = new android.app.AlertDialog.Builder(this);
        forgotpasswordbuilder.setView(forgotpasswordView);
        forgotpasswordbuilder.setTitle("Forgot Password");
        forgotpasswordbuilder.setIcon(R.drawable.logo);
        forgotpassword = (EditText) forgotpasswordView.findViewById(R.id.etloginforgotpasssword);
        forgotpasswordbuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String forgotpasswordvalue = forgotpassword.getText().toString().trim();
                if(forgotpasswordvalue.equals("")||forgotpasswordvalue.equals(null)||forgotpasswordvalue.length()<9)
                {
                    forgotpassword.setError("Enter email/phone");
                }
                else
                {
                    checkinternet();
                    StringRequest forgotpasswordrequest = new StringRequest(Request.Method.PUT, API.forgotpassword, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("Forgotpasswordresponse",response);
                            Toast.makeText(Login.this, "Password sent your mail", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder gmailbuilder = new AlertDialog.Builder(Login.this);
                            gmailbuilder.setIcon(R.drawable.logo);
                            gmailbuilder.setTitle("Password reset successfully");
                            gmailbuilder.setMessage("Please check your mail for new password");
                            gmailbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            gmailbuilder.show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (error instanceof ServerError) {
                                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                Log.d("Error", String.valueOf(error instanceof ServerError));
                                android.support.v7.app.AlertDialog.Builder LoginBuilder = new android.support.v7.app.AlertDialog.Builder(Login.this);
                                LoginBuilder.setIcon(R.drawable.logo);
                                LoginBuilder.setTitle("Invalid Employee");
                                LoginBuilder.setMessage("Sorry! we couldn't find any employee with this mail/phone.");
                                LoginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(Login.this, "Try to give valid credentials", Toast.LENGTH_SHORT).show();
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
                            params.put("email",forgotpasswordvalue);
                            return params;
                        }

                    };
                    PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(forgotpasswordrequest);
                }

            }
        });
        forgotpasswordbuilder.show();
    }

    public String md5(String s) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")), 0, s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
