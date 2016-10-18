package mytrintrin.com.pbs_employee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class checkoutmanually extends AppCompatActivity {

    Spinner stationspinner;
    Spinner holdingareaspinner;
    Spinner redistubutionspinner;
    Spinner maintenancespinner;

    EditText cycleid,cardid;
    EditText portid;

    String holdingarea;
    String restributionvehicle;
    String maintenance;

    String ManualCheckoutURL=API.checkoutmanualurl;
    String Stationnamesurl = API.stationnameurl;

    String fleetportid;
    String StationId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutmanually);
        stationspinner = (Spinner) findViewById(R.id.dockingstationmanualspinner);
        holdingareaspinner = (Spinner) findViewById(R.id.holdingareaspinner);
        redistubutionspinner = (Spinner) findViewById(R.id.redistubutionspinner);
        maintenancespinner = (Spinner) findViewById(R.id.maintenancespinner);
        cycleid = (EditText) findViewById(R.id.etcheckoutmanualcycleid);
        portid = (EditText) findViewById(R.id.etcheckoutmanualportid);
        cardid= (EditText) findViewById(R.id.etcheckoutmanualcardid);
        dockingstation();
        dockingstationnames();
        //getallportdid();

    }

    private void getallportdid() {
        StringRequest allportdetailsrequest = new StringRequest(Request.Method.GET, API.allportsurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject allportdetailfromserver = new JSONObject(response);
                    JSONArray allportdata= allportdetailfromserver.getJSONArray("data");
                    String portstest[]=new String[allportdata.length()];;
                    for(int i=0;i<=allportdata.length();i++) {
                        JSONObject allportdetailfromdata = allportdata.getJSONObject(i);
                        StationId = allportdetailfromdata.getString("StationId");
                        Log.d("StationId", StationId);
                        if(fleetportid==StationId) {
                            //Log.d("StationId", StationId);
                            portstest[i] = StationId;
                        }
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
            /*Adding headers to request(manditory)*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };
        Mysingleton.getInstance(this).addtorequestqueue(allportdetailsrequest);
    }

    private  void dockingstationnames()
    {
        StringRequest stationnamerequest = new StringRequest(Request.Method.GET, Stationnamesurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject stationnamefromserver = new JSONObject(response);
                    JSONArray data = stationnamefromserver.getJSONArray("data");
                    for(int i=0;i<=data.length();i++) {
                        JSONObject stationfromdata = data.getJSONObject(i);
                        String stationname = stationfromdata.getString("name");
                        Log.d("Station Name", stationname);
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
            /*Adding headers to request(manditory)*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

                /*headers ends*/

        };
        Mysingleton.getInstance(this).addtorequestqueue(stationnamerequest);
    }





    private void dockingstation() {
        List<String> categories = new ArrayList<String>();
        categories.add("Select Stations");
        categories.add("Fleet");
        categories.add("Holding Area");
        categories.add("Redistrubution Vehicle");
        categories.add("Maintainence Centre");
        ArrayAdapter<String> dockingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dockingadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationspinner.setAdapter(dockingadapter);
        stationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        holdingareaspinner.setVisibility(View.GONE);
                        redistubutionspinner.setVisibility(View.GONE);
                        maintenancespinner.setVisibility(View.GONE);
                        holdingarea = "";
                        maintenance = "";
                        restributionvehicle = "";
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        StringRequest fleetportsrequest = new StringRequest(Request.Method.GET, API.fleetareaurl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject fleetportfromserver = new JSONObject(response);
                                    JSONArray fleetportdata= fleetportfromserver.getJSONArray("data");
                                    for(int i=0;i<=fleetportdata.length();i++) {
                                        JSONObject portfromdata = fleetportdata.getJSONObject(i);
                                        fleetportid = portfromdata.getString("_id");
                                        Log.d("portid",fleetportid);
                                        getallportdid();

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
                            //*Adding headers to request(manditory)*//*
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Content-Type", "application/x-www-form-urlencoded");
                                return headers;
                            }

                            //*headers ends*//*

                        };
                        Mysingleton.getInstance(getApplicationContext()).addtorequestqueue(fleetportsrequest);

                        holdingareaspinner.setVisibility(View.GONE);
                        redistubutionspinner.setVisibility(View.GONE);
                        maintenancespinner.setVisibility(View.GONE);
                        holdingarea = "";
                        maintenance = "";
                        restributionvehicle = "";
                        break;
                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        holdingarea = "";
                        holdingareaspinner.setVisibility(View.VISIBLE);
                        redistubutionspinner.setVisibility(View.GONE);
                        List<String> areas = new ArrayList<String>();
                        areas.add("Select Holding Area");
                        areas.add("Holding Area 1");
                        areas.add("Holding Area 2");
                        areas.add("Holding Area 3");
                        areas.add("Holding Area 4");
                        ArrayAdapter<String> HAadapter = new ArrayAdapter<String>(checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, areas);
                        HAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        holdingareaspinner.setAdapter(HAadapter);
                        holdingarea = holdingareaspinner.getSelectedItem().toString();
                        maintenance = "";
                        restributionvehicle = "";
                        break;
                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        holdingareaspinner.setVisibility(View.GONE);
                        maintenancespinner.setVisibility(View.GONE);
                        redistubutionspinner.setVisibility(View.VISIBLE);
                        List<String> rv = new ArrayList<String>();
                        rv.add("Select Restribution Vehicle");
                        rv.add("Restribution Vehicle 1");
                        rv.add("Restribution Vehicle 2");
                        rv.add("Restribution Vehicle 3");
                        rv.add("Restribution Vehicle 4");
                        ArrayAdapter<String> RVAdapter = new ArrayAdapter<String>(checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, rv);
                        RVAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        redistubutionspinner.setAdapter(RVAdapter);
                        restributionvehicle = redistubutionspinner.getSelectedItem().toString();
                        holdingarea = "";
                        maintenance = "";
                        break;
                    case 4:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        holdingareaspinner.setVisibility(View.GONE);
                        maintenancespinner.setVisibility(View.VISIBLE);
                        List<String> mc = new ArrayList<String>();
                        mc.add("Select Maintenance Centre");
                        mc.add("Maintenance Centre 1");
                        mc.add("Maintenance Centre 2");
                        mc.add("Maintenance Centre 3");
                        mc.add("Maintenance Centre 4");
                        ArrayAdapter<String> MCAdapter = new ArrayAdapter<String>(checkoutmanually.this, android.R.layout.simple_spinner_dropdown_item, mc);
                        MCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        maintenancespinner.setAdapter(MCAdapter);
                        maintenance = maintenancespinner.getSelectedItem().toString();
                        redistubutionspinner.setVisibility(View.GONE);
                        holdingarea = "";
                        restributionvehicle = "";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void getalldetails() {
        if (holdingarea == "") {
            holdingarea = "";
        } else {
            holdingarea = holdingareaspinner.getSelectedItem().toString();
        }
        if (maintenance == "") {
            maintenance = "";
        } else {
            maintenance = maintenancespinner.getSelectedItem().toString();
        }

        if (restributionvehicle == "") {
            restributionvehicle = "";
        } else {
            restributionvehicle = redistubutionspinner.getSelectedItem().toString();
        }

    }

    public void getdetailsofcheckout(View view) {
      /*  getalldetails();

        String dockingstationname = stationspinner.getSelectedItem().toString();
        String holdingareaname = holdingarea;
        if (holdingareaname == null) {
            holdingareaname = "";
        }
        String restributionname = restributionvehicle;
        if (restributionname == null) {
            restributionname = "";
        }
        String maintenancename = maintenance;
        if (maintenancename == null) {
            maintenancename = "";
        }*/
        Calendar calendar = Calendar.getInstance();
        final String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, ManualCheckoutURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject checkoutresponsefromserver= new JSONObject(response);
                    String message = checkoutresponsefromserver.getString("message");
                    Log.d("message",message);
                    Toast.makeText(checkoutmanually.this,message,Toast.LENGTH_LONG).show();
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
                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("cardId","2DR35A6C00000000");
                params.put("vehicleNumber","1113");
                params.put("fromPort","580343e4d98322278b949f31");
                params.put("checkOutTime",date);
                return  params;
            }
        };

        Mysingleton.getInstance(this).addtorequestqueue(checkoutrequest);




       // String Cycleid = cycleid.getText().toString().trim();
       // String Portid = portid.getText().toString().trim();
        //Toast.makeText(this, dockingstationname + "" + Cycleid + "" + Portid + "" + holdingareaname + "" + "" + restributionname + "" + maintenancename + "" + date, Toast.LENGTH_LONG).show();
        holdingarea = "";
        restributionvehicle = "";
        maintenance = "";

    }


}
