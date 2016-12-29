package mytrintrin.com.pbs_employee;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText loginmail;
    EditText loginpassword;
    CheckBox loginshowpassword;

    /*Login URL*/
    // String loginurl="http://43.251.80.79:3001/api/auth/login";
    String loginurl = "http://43.251.80.79:13056/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginmail = (EditText) findViewById(R.id.etloginemail);
        loginpassword = (EditText) findViewById(R.id.etloginpassword);
        loginshowpassword= (CheckBox) findViewById(R.id.cbshowpassword);
        checkinternet();

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

    public void showpassword(View view)
    {
        if(loginshowpassword.isChecked())
        {
            loginpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {

            loginpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    //Login verify using email and password

    public void Loginverify(View view) {

        checkinternet();

        String email = loginmail.getText().toString().trim();
        String password = loginpassword.getText().toString().trim();
        if (email.equals("") && password.equals("")) {
            loginmail.setError("Enter mail");
            loginpassword.setError("Enter password");
        } else {
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject responsefromserver = new JSONObject(response);
                        Boolean error = responsefromserver.getBoolean("error");
                        String message = responsefromserver.getString("message");
                        JSONObject data = responsefromserver.getJSONObject("data");
                        String userid = data.getString("id");
                        Log.d("User-id", userid);

                        if (!error) {
                            Intent operationsintent = new Intent(Login.this, operations.class);
                            startActivity(operationsintent);

                        } else {
                            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
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
                /*Adding headers to request(manditory)*/
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }

                /*headers ends*/
                /*Sending login credentials to api*/
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", loginmail.getText().toString().trim());
                    params.put("password", loginpassword.getText().toString().trim());
                    return params;
                }
                /*ends*/
            };
            Mysingleton.getInstance(getApplicationContext()).addtorequestqueue(stringRequest);
        }
    }

    /*To clear edit text values*/
    @Override
    protected void onPause() {
        super.onPause();
        loginmail.setText("");
        loginpassword.setText("");

    }
}
