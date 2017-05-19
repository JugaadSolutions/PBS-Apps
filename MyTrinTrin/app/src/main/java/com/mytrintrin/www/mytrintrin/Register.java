package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    Toolbar RegisterToolbar;
    EditText FirstName, LastName, Phone, Email, Password, ConfirmPassword;
    String firstname, lastname, phone, email, password, confirmpassword,passworderrormessage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        RegisterToolbar = (Toolbar) findViewById(R.id.registertoolbar);
        RegisterToolbar.setTitle("Sign Up");
        setSupportActionBar(RegisterToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intialize();
    }

    private void Intialize() {

        FirstName = (EditText) findViewById(R.id.firstname_register);
        LastName = (EditText) findViewById(R.id.lastname_register);
        Phone = (EditText) findViewById(R.id.phonenumber_register);
        Email = (EditText) findViewById(R.id.email_register);
        Password = (EditText) findViewById(R.id.newpassword_register);
        ConfirmPassword = (EditText) findViewById(R.id.confirmpassword_register);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Register.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/

    public void Registeruser(View view) {

        checkinternet();
        firstname = FirstName.getText().toString().trim();
        lastname = LastName.getText().toString().trim();
        phone = Phone.getText().toString().trim();
        email = Email.getText().toString().trim();
        password = Password.getText().toString().trim();
        confirmpassword = ConfirmPassword.getText().toString().trim();

        if (firstname.equals("") || firstname.equals(null)) {
            FirstName.setError("First Name");
            return;
        }
        if (phone.equals("") || phone.equals(null)) {
            Phone.setError("Phone Number");
            return;
        }
        if (email.equals("") || email.equals(null)) {
            Email.setError("Email");
            return;
        }
        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            Email.setError("Invalid Email Address");
            return;
        }
        if (!password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$")) {
            Password.setError("Invalid Password");
            passworderrormessage = "Password must contain minimum 6 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character";
            showpassworderror(passworderrormessage);
            return;
        }
        if (!confirmpassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$")) {
            ConfirmPassword.setError("Invalid Password");
            passworderrormessage = "Password must contain minimum 6 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character";
            showpassworderror(passworderrormessage);
            return;
        }
        if (!password.equals(confirmpassword)) {
            Password.setError("Password didn't match");
            ConfirmPassword.setError("Password didn't match");
            passworderrormessage = "Password Didn't Match";
            showpassworderror(passworderrormessage);
            return;
        }

        //password = md5(password);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        StringRequest registerrequest = new StringRequest(Request.Method.POST, API.register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                FirstName.setText("");
                LastName.setText("");
                Phone.setText("");
                Email.setText("");
                Password.setText("");
                ConfirmPassword.setText("");
                Toast.makeText(Register.this, "Sign Up Successfull", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                android.app.AlertDialog.Builder SignupBuilder = new android.app.AlertDialog.Builder(Register.this);
                SignupBuilder.setIcon(R.drawable.splashlogo);
                SignupBuilder.setTitle("Sign Up");
                SignupBuilder.setMessage("Sign Up Successfull!!!");
                SignupBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }
                });
                SignupBuilder.setCancelable(false);
                SignupBuilder.show();
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
                    Toast.makeText(Register.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Register.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Register.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Register.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Register.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Register.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("Name", firstname);
                params.put("lastName", lastname);
                params.put("email", email);
                params.put("phoneNumber","91-"+phone);
                params.put("password", password);
                params.put("cpassword",confirmpassword);
                return params;
            }
        };
        registerrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(registerrequest);
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            showpassworderror(message);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    public  void showpassworderror(String errormsg)
    {
        AlertDialog.Builder ErrorBuilder = new AlertDialog.Builder(Register.this);
        ErrorBuilder.setIcon(R.drawable.splashlogo);
        ErrorBuilder.setTitle("Change Password");
        ErrorBuilder.setMessage(errormsg);
        ErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ErrorBuilder.setCancelable(false);
        ErrorBuilder.show();
    }

}






