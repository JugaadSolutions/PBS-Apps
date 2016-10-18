package com.mytrintrin.transactions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    EditText coVehicleID,coFromport,coCardID;
    Button sendcheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        coVehicleID = (EditText) findViewById(R.id.checkoutvehicleid);
        coFromport = (EditText) findViewById(R.id.checkoutfromportid);
        coCardID = (EditText) findViewById(R.id.checkoutcardid);
        sendcheckout = (Button) findViewById(R.id.bsendcheckout);
    }

    public void sendcheckoutdetails(View view)
    {
        Calendar calendar = Calendar.getInstance();
        final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());

        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, TransactionAPI.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkout.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkout.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkout.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkout.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkout.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkout.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("vehicleNumber",coVehicleID.getText().toString().trim());
                params.put("cardId",coCardID.getText().toString().trim());
                params.put("fromPort",coFromport.getText().toString().trim());
                params.put("checkOutTime",checkouttime);
                return params;
            }
        };

        Singleton.getInstance(this).addtorequest(checkoutrequest);

    }
}
