package mytrintrin.com.pbs_employee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dockingstations extends AppCompatActivity {
    Location currentlocation;
    String dockingstationurl=" http://43.251.80.79:3001/api/dockingstation/";
    TextView locateddockingstation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dockingstations);
        locateddockingstation= (TextView) findViewById(R.id.tvlocateddockingstation);


             /*To get Location*/


        GPSServices mGPSService = new GPSServices(this);
        mGPSService.getLocation();

        if (mGPSService.isLocationAvailable == false) {
            Toast.makeText(this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            double latitude = mGPSService.getLatitude();
            double longitude = mGPSService.getLongitude();
            currentlocation = new Location("");
            currentlocation.setLatitude(latitude);
            currentlocation.setLongitude(longitude);
            Toast.makeText(this,""+currentlocation, Toast.LENGTH_SHORT).show();

        }

        mGPSService.closeGPS();
        displaydockingstation();

        /*ends*/


    }

    private void displaydockingstation() {
        StringRequest dockingstations= new StringRequest(Request.Method.GET, dockingstationurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver= new JSONObject(response);
                    JSONArray alldata = responsefromserver.getJSONArray("data");
                    JSONObject data = alldata.getJSONObject(0);
                    JSONObject gpscordinates = data.getJSONObject("gpsCoordinates");

                    String lat= gpscordinates.getString("latitude");
                    String lon= gpscordinates.getString("longitude");

                    Location dockinglocation = new Location("");
                    dockinglocation.setLatitude(Double.parseDouble(lat));
                    dockinglocation.setLongitude(Double.parseDouble(lon));
                    Log.d("docking station", String.valueOf(dockinglocation));
                    float distance = currentlocation.distanceTo(dockinglocation);
                    Log.d("distance", String.valueOf(distance));
                    if(distance<=100)
                    {
                        String stationname = data.getString("name");
                        locateddockingstation.setText("Your in "+stationname);

                    }
                    else
                    {
                        locateddockingstation.setText("You are far from any docking station");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Mysingleton.getInstance(this).addtorequestqueue(dockingstations);
    }



    public  void lockandrelease(View view)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dockingstations.this);
         alertDialog.setTitle("Release");
         alertDialog.setMessage("Do you want to release?");
         alertDialog.setIcon(R.drawable.ic_lock_open);
         alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
        Toast.makeText(getApplicationContext(), "You clicked on yes", Toast.LENGTH_SHORT).show();
            }
        });


        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "You clicked on no", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "You clicked on Cancel",
                        Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }
}
