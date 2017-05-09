package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    private Toolbar ChangePasswordToolbar;
    EditText OldPassword,NewPassword,ConfirmPassword;
    String oldpassword,newpassword,confirmpassword,loginuserid;
    LinearLayout ChangepasswordLayout;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ChangePasswordToolbar = (Toolbar) findViewById(R.id.changepasswordtoolbar);
        ChangePasswordToolbar.setTitle("Change Password");
        setSupportActionBar(ChangePasswordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OldPassword = (EditText) findViewById(R.id.oldpassword_changepassword);
        NewPassword = (EditText) findViewById(R.id.newpassword_changepassword);
        ConfirmPassword = (EditText) findViewById(R.id.confirmpassword_changepassword);
        ChangepasswordLayout = (LinearLayout) findViewById(R.id.changepassword);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id",null);

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends


        checkinternet();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
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

    public void changepassword(View view)
    {
        checkinternet();
        oldpassword = OldPassword.getText().toString().trim();
        newpassword = NewPassword.getText().toString().trim();
        confirmpassword = ConfirmPassword.getText().toString().trim();

        if(!oldpassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$"))
        {
            OldPassword.setError("Invalid Password");
            Snackbar snackbar = Snackbar.make(ChangepasswordLayout, "Invalid Password", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            Toast.makeText(this, "Password must contain minimum 6 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character ", Toast.LENGTH_LONG).show();
            return;
        }

        if(!newpassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$"))
        {
            NewPassword.setError("Invalid Password");
            Snackbar snackbar = Snackbar.make(ChangepasswordLayout, "Invalid Password", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            Toast.makeText(this, "Password must contain minimum 6 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character ", Toast.LENGTH_LONG).show();
            return;
        }

        if(!confirmpassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$"))
        {
            ConfirmPassword.setError("Invalid Password");
            Snackbar snackbar = Snackbar.make(ChangepasswordLayout, "Invalid Password", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            Toast.makeText(this, "Password must contain minimum 6 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character ", Toast.LENGTH_LONG).show();
            return;
        }

        if(!newpassword.equals(confirmpassword))
        {
            NewPassword.setError("Password Didn't match");
            ConfirmPassword.setError("Password Didn't match");
            Snackbar snackbar = Snackbar.make(ChangepasswordLayout, "Password Didn't Match", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        /*oldpassword = md5(oldpassword);
        newpassword = md5(newpassword);*/
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Changing Password...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        StringRequest changepasswordrequest = new StringRequest(Request.Method.PUT, API.changepassword+loginuserid+"/password/change", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                OldPassword.setText("");
                NewPassword.setText("");
                ConfirmPassword.setText("");
                mProgressDialog.dismiss();
                startActivity(new Intent(ChangePassword.this,Login.class));
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
                    Toast.makeText(ChangePassword.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ChangePassword.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ChangePassword.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ChangePassword.this, "Please check you connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ChangePassword.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ChangePassword.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("newPassword", newpassword);
                params.put("currentPassword", oldpassword);
                params.put("cpassword", confirmpassword);
                return params;
            }
        };
        changepasswordrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getApplicationContext()).addtorequestqueue(changepasswordrequest);
    }

    public String md5(String s) {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            AlertDialog.Builder ErrorBuilder = new AlertDialog.Builder(ChangePassword.this);
            ErrorBuilder.setIcon(R.drawable.splashlogo);
            ErrorBuilder.setTitle("Change Password");
            ErrorBuilder.setMessage(message);
            ErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                }
            });
            ErrorBuilder.setCancelable(false);
            ErrorBuilder.show();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
