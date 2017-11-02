package com.mytrintrin.www.mytrintrin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gwr on 9/17/17.
 */

public class TopupAdapter extends ArrayAdapter<TopupObject> {


    Context mcontext;
    String  loginuserid;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    public TopupAdapter(Context context, ArrayList<TopupObject> topupObjects) {
        super(context, 0, topupObjects);
        mcontext = context;
        loginpref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listviewItem = convertView;

        if (convertView == null)

        {
            listviewItem = LayoutInflater.from(getContext()).inflate(R.layout.topup_item, parent, false);
        }

        final TopupObject topupObject = getItem(position);

        TextView planname = (TextView) listviewItem.findViewById(R.id.plan_name);
        planname.setText(topupObject.getmPlanName());

        TextView validity = (TextView) listviewItem.findViewById(R.id.validity);
        validity.setText(topupObject.getmValidity() + " Days");

        TextView topupamount = (TextView) listviewItem.findViewById(R.id.topup_amount);
        topupamount.setText(topupObject.getmTopupamount());

        TextView usagefee = (TextView) listviewItem.findViewById(R.id.usagefee);
        usagefee.setText(topupObject.getmUsagefee() + " Rs");

        TextView rechargenow = (TextView) listviewItem.findViewById(R.id.rechargenow);
        rechargenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Topupuser(topupObject.getmPlanName(),topupObject.getmTopupamount());
            }
        });

        return listviewItem;
    }

    public void Topupuser(final String planname, final String topupamount) {
      final String CustomerId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        StringRequest Topuprequest = new StringRequest(Request.Method.POST, API.selectplan_longterm, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Intent paygovintent = new Intent(getContext(), Paygovwebview.class);
                    paygovintent.putExtra("paygovresponse", response);
                    mcontext.startActivity(paygovintent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(getContext(), "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getContext(), "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getContext(), "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getContext(), "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getContext(), "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("amount", topupamount);
                params.put("CustomerID", CustomerId);
                params.put("AdditionalInfo1", planname);
                params.put("AdditionalInfo2", "Topup");
                params.put("AdditionalInfo3", loginuserid);
                return params;
            }
        };
        Topuprequest.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getContext()).addtorequestqueue(Topuprequest);
    }
}
