package com.mytrintrin.www.pbs_sync;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class multipleCheckout_clear extends AppCompatActivity {


    Toolbar Multiplecheckout;
    JSONObject Checkboxobject;
    ArrayList<String> checkboxlist = new ArrayList<String>();
    ArrayList<String> checkoutids = new ArrayList<String>();
    LinearLayout Multipletransactionslayout;
    EditText From,To;
    String from,to;
    Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_checkout_clear);
        Multiplecheckout = (Toolbar) findViewById(R.id.multipleclearcheckouttoolbar);
        Multiplecheckout.setTitle("Multiple Checkout");
        setSupportActionBar(Multiplecheckout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Multipletransactionslayout = (LinearLayout) findViewById(R.id.multipleclearcheckoutlayout);
        From = (EditText) findViewById(R.id.from_multiple);
        To = (EditText) findViewById(R.id.to_multiple);

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        From.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(multipleCheckout_clear.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        To.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(multipleCheckout_clear.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        from = From.getText().toString().trim();
        if(from.equals(null)||from.equals("")) {
            From.setText(sdf.format(myCalendar.getTime()));
        }
        else
        {
            To.setText(sdf.format(myCalendar.getTime()));
            to = To.getText().toString().trim();
        }

    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    multipleCheckout_clear.this);
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

    public void getmultipletransactions(View view)
    {
        StringRequest multiplecheckoutrequest = new StringRequest(Request.Method.POST, API.getallmutilpletransactions, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               Log.d("multiple",response);
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    Checkboxobject = responsefromserver;
                    createchecklist();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(multipleCheckout_clear.this, "Server is under maintenance", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                    return;
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(multipleCheckout_clear.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(multipleCheckout_clear.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(multipleCheckout_clear.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            multipleCheckout_clear.this);
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
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            checkinternet();
                        }
                    });
                    builder.show();
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(multipleCheckout_clear.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(multipleCheckout_clear.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(multipleCheckout_clear.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("fromdate", from);
                params.put("todate", to);
                return params;
            }

        };
        multiplecheckoutrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(multiplecheckoutrequest);
    }

    public void createchecklist() {
        try {
            JSONArray data = Checkboxobject.getJSONArray("data");
            for (int i=0;i<data.length();i++)
            {
                JSONObject particularcheckout = data.getJSONObject(i);
                String id = particularcheckout.getString("_id");
                checkoutids.add(id);
                JSONObject user = particularcheckout.getJSONObject("user");
                String cardnumber = user.getString("cardNum");
                JSONObject fromport = particularcheckout.getJSONObject("fromPort");
                String fromportname = fromport.getString("Name");
                JSONObject toport = particularcheckout.getJSONObject("toPort");
                String toportname = fromport.getString("Name");
                JSONObject Vehicle = particularcheckout.getJSONObject("vehicle");
                String vehiclenumber = Vehicle.getString("vehicleNumber");
                String checkouttime = particularcheckout.getString("checkOutTime");
                String checkintime = particularcheckout.getString("checkInTime");
                AppCompatCheckBox checkout = new AppCompatCheckBox(multipleCheckout_clear.this);
                checkout.setId(i);
                checkout.setText("Card Number :"+cardnumber+"\n"+"Vehicle Number : "+vehiclenumber+"\n"+"From Port : "+fromportname+"\n"+"Check-out Time : "+checkouttime+"\n"+"To Port : "+toportname+"Check-in Time :");
                //checkout.setTag(checkboxlist.get(i));
                checkout.setChecked(false);
                //checkout.setOnCheckedChangeListener(handleCheck(checkout));
                Multipletransactionslayout.addView(checkout);

            }
            /*JSONArray value = data.getJSONArray("value");
            Multipletransactionslayout.removeAllViews();
            for (int i=0 ;i<value.length();i++)
            {
                String checkboxvalue = value.getString(i);
                checkboxlist.add(i,checkboxvalue);
                AppCompatCheckBox checkout = new AppCompatCheckBox(multipleCheckout_clear.this);
                checkout.setId(i);
                checkout.setText(checkboxlist.get(i));
                checkout.setTag(checkboxlist.get(i));
                checkout.setChecked(false);
                checkout.setOnCheckedChangeListener(handleCheck(checkout));
                Multipletransactionslayout.addView(checkout);
            }*/
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private CompoundButton.OnCheckedChangeListener handleCheck (final CheckBox chk)
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(!isChecked){
                    Toast.makeText(getApplicationContext(), "You unchecked " + chk.getTag(),Toast.LENGTH_SHORT).show();
                    checkoutids.remove(chk.getTag().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You checked " + chk.getTag(),Toast.LENGTH_SHORT).show();
                    checkoutids.add(chk.getTag().toString());
                }
            }
        };
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}
