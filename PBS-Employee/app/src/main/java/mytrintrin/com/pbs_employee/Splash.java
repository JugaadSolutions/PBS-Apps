package mytrintrin.com.pbs_employee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Splash extends AppCompatActivity {


    public  static ArrayList<String> FleetIDArrayList = new ArrayList<String>();
    public  static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    public static  ArrayList<String> RVIDArrayList = new ArrayList<String>();
    public static  ArrayList<String> RVNameArrayList = new ArrayList<String>();
    public static  ArrayList<String> MCIDArrayList = new ArrayList<String>();
    public static  ArrayList<String> MCNameArrayList = new ArrayList<String>();
    public static  ArrayList<String> HANameArrayList = new ArrayList<String>();
    public static  ArrayList<String> HAIDArrayList = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getFleetdetails();
        getRestributiondetails();
        getMCdetails();
        getHAdetails();
        splashscreen();
        //startActivity(new Intent(this,MainActivity.class));

    }

    private void splashscreen() {
        Thread timer = new Thread(){
            public void run(){
                try
                {
                    sleep(3000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent=new Intent(Splash.this,LoginusingNFC.class);
                    startActivity(intent);

                }
            }
        };
        timer.start();
    }

    public  void getFleetdetails()
    {
        StringRequest fleetrequest = new StringRequest(Request.Method.GET, TransactionAPI.fleetidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject fleetresponsefronserver= new JSONObject(response);
                    JSONArray fleetdataarray= fleetresponsefronserver.getJSONArray("data");
                    for(int i =0 ;i<=fleetdataarray.length();i++) {
                        JSONObject getid = fleetdataarray.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("Name");
                        Log.d("ID",id);
                        FleetIDArrayList.add(id);
                        FleetNameArrayList.add(name);
                        Log.d("Array ID", String.valueOf(FleetIDArrayList));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Splash.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Splash.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Splash.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    //Toast.makeText(Splash.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Toast.makeText(Splash.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Splash.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Splash.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Splash.this, "Something went wrong", Toast.LENGTH_LONG).show();
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



        };
        Mysingleton.getInstance(this).addtorequestqueue(fleetrequest);
    }

    public  void getRestributiondetails()
    {
        StringRequest restributionrequest = new StringRequest(Request.Method.GET,TransactionAPI.RVidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject restributionfromserver = new JSONObject(response);
                    JSONArray restributiondataarray = restributionfromserver.getJSONArray("data");
                    for(int i=0;i<=restributiondataarray.length();i++) {
                        JSONObject portid = restributiondataarray.getJSONObject(i);
                        String id = portid.getString("_id");
                        String name = portid.getString("Name");
                        Log.d("RV", id);
                        RVIDArrayList.add(id);
                        Log.d("Array ID", String.valueOf(RVIDArrayList));
                        RVNameArrayList.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };
        Mysingleton.getInstance(this).addtorequestqueue(restributionrequest);
    }


    public void getMCdetails()
    {
        StringRequest maintenancerequest = new StringRequest(Request.Method.GET, TransactionAPI.MCidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject maintenancefromserver = new JSONObject(response);
                    JSONArray maintenancedataarray = maintenancefromserver.getJSONArray("data");
                    for(int i=0;i<maintenancedataarray.length();i++) {
                        JSONObject maintenanceid = maintenancedataarray.getJSONObject(i);
                        String id = maintenanceid.getString("_id");
                        String name = maintenanceid.getString("Name");
                        Log.d("MC ID", id);
                        MCIDArrayList.add(id);
                        MCNameArrayList.add(name);
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

        };
        Mysingleton.getInstance(this).addtorequestqueue(maintenancerequest);
    }


    public void  getHAdetails()

    {
        StringRequest holdingarearequest = new StringRequest(Request.Method.GET, TransactionAPI.HAidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject holdingareafromserver = new JSONObject(response);
                    JSONArray holdingareadataarray = holdingareafromserver.getJSONArray("data");
                    for(int i=0;i<holdingareadataarray.length();i++) {
                        JSONObject holdingareaid = holdingareadataarray.getJSONObject(i);
                        String id = holdingareaid.getString("_id");
                        String name = holdingareaid.getString("Name");
                        Log.d("HA ID", id);
                        HANameArrayList.add(name);
                        HAIDArrayList.add(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };
        Mysingleton.getInstance(this).addtorequestqueue(holdingarearequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
