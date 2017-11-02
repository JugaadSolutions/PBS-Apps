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
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private Toolbar ForgotPasswordToolbar;
    EditText EmailForgot;
    String emailforgot;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        ForgotPasswordToolbar = (Toolbar) findViewById(R.id.forgotPasswordtoolbar);
        ForgotPasswordToolbar.setTitle("Forgot Password");
        setSupportActionBar(ForgotPasswordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EmailForgot = (EditText) findViewById(R.id.email_forgotpassword);
        checkinternet();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    ForgotPassword.this);
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

    public void resetpassword(View view) {
        checkinternet();
        emailforgot = EmailForgot.getText().toString().trim();
        if (!emailforgot.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            EmailForgot.setError("Invalid Email Address");
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        StringRequest forgotpasswordrequest = new StringRequest(Request.Method.PUT, API.forgotpassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(ForgotPassword.this, "Check your mail for password reset link", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                AlertDialog.Builder ForgotBuilder = new AlertDialog.Builder(ForgotPassword.this);
                ForgotBuilder.setTitle("Forgot Password");
                ForgotBuilder.setIcon(R.drawable.trintrinlogo);
                ForgotBuilder.setMessage("Check your mail for password reset link");
                ForgotBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EmailForgot.setText("");
                        dialogInterface.dismiss();
                        startActivity(new Intent(ForgotPassword.this, Login.class));
                        finish();
                    }
                });
                ForgotBuilder.setCancelable(false);
                ForgotBuilder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                } else if (error.networkResponse.data != null) {
                    Log.d("check network error", String.valueOf(error));
                } else {
                    if (error instanceof ServerError) {
                        Toast.makeText(ForgotPassword.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(ForgotPassword.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(ForgotPassword.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    }else if (error instanceof NoConnectionError) {
                        Toast.makeText(ForgotPassword.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(ForgotPassword.this, "Please check your connection", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                                ForgotPassword.this);
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
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(ForgotPassword.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
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
                params.put("email", emailforgot);
                params.put("origin", "app");
                return params;
            }
        };
        forgotpasswordrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(forgotpasswordrequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder ErrorBuilder = new AlertDialog.Builder(ForgotPassword.this);
            ErrorBuilder.setTitle("Forgot Password");
            ErrorBuilder.setIcon(R.drawable.splashlogo);
            ErrorBuilder.setMessage(message);
            ErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }
            });
            ErrorBuilder.setCancelable(false);
            ErrorBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
