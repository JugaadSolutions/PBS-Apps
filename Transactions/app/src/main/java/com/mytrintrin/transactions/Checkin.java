package com.mytrintrin.transactions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Checkin extends AppCompatActivity {

    EditText VehicleID,Toport,CardID;
    Button sendcheckin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        VehicleID = (EditText) findViewById(R.id.checkinvehicleid);
        Toport = (EditText) findViewById(R.id.checkintoportid);
        CardID = (EditText) findViewById(R.id.checkincardid);
        sendcheckin = (Button) findViewById(R.id.bsendcheckin);
    }

    public void sendcheckindetails(View view)
    {
        Calendar calendar = Calendar.getInstance();
        final String checkintime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        StringRequest checkinrequest = new StringRequest(Request.Method.POST, TransactionAPI.checkinurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Check in response",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("vehicleNumber",VehicleID.getText().toString().trim());
                params.put("cardId",CardID.getText().toString().trim());
                params.put("toPort",Toport.getText().toString().trim());
                params.put("checkInTime",checkintime);
                return params;
            }
        };

        Singleton.getInstance(this).addtorequest(checkinrequest);

    }

}
