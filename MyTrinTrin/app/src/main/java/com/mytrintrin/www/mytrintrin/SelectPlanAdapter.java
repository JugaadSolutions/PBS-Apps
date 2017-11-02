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
 * Created by gwr on 9/18/17.
 */

public class SelectPlanAdapter extends ArrayAdapter<SelectplanObject> {

    Context mContext;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid;
    String Apicalforselectplan;


    public SelectPlanAdapter(Context context, ArrayList<SelectplanObject> selectplanObjects) {
        super(context, 0, selectplanObjects);
        mContext = context;
        loginpref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemview = convertView;

        if(listItemview==null)
        {
            listItemview = LayoutInflater.from(getContext()).inflate(R.layout.selectplan_item,parent,false);
        }

        final SelectplanObject selectplanObject = getItem(position);

        TextView planname = (TextView) listItemview.findViewById(R.id.plan_name);
        planname.setText(selectplanObject.getmPlanname());


        TextView validity = (TextView) listItemview.findViewById(R.id.validity);
        validity.setText(selectplanObject.getmValidity()+" Days");

        TextView securityfee = (TextView) listItemview.findViewById(R.id.securityfee);
        securityfee.setText(selectplanObject.getmSecurityfee()+" Rs");

        TextView processingfee = (TextView) listItemview.findViewById(R.id.processingfee);
        processingfee.setText(selectplanObject.getmProcessingfee()+" Rs");

        TextView usagefee = (TextView) listItemview.findViewById(R.id.usagefee);
        usagefee.setText(selectplanObject.getmUsageFee()+" Rs");

        TextView plantotal = (TextView) listItemview.findViewById(R.id.plan_amount);
        plantotal.setText(""+selectplanObject.getmPlanTotal());

        TextView paynow = (TextView) listItemview.findViewById(R.id.paynow);
        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, ""+selectplanObject.getmPlanname(), Toast.LENGTH_SHORT).show();
                makeplanpayment(selectplanObject.getmPlanname(),selectplanObject.getmPlanTotal());

            }
        });

        TextView plandescription = (TextView) listItemview.findViewById(R.id.description);
        plandescription.setText(selectplanObject.getmPlandescription());

        TextView plantype = (TextView) listItemview.findViewById(R.id.plan_type);
        plantype.setText(selectplanObject.getmPlantype());

        return listItemview;
    }

    public void makeplanpayment(final String plan, final int amount)
    {
        final String CustomerId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if(amount==350)
        {
            Apicalforselectplan = API.selectplan_longterm;
        }
        else
        {
            Apicalforselectplan = API.selectplan_shorterm;
        }

        StringRequest selectplanrequest = new StringRequest(Request.Method.POST, Apicalforselectplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(!response.equals(null))
                {
                    Intent paygovintent = new Intent(mContext,Paygovwebview.class);
                    paygovintent.putExtra("paygovresponse",response);
                    mContext.startActivity(paygovintent);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(mContext, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(mContext, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(mContext, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mContext, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(mContext, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                }  else {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf(amount));
                params.put("CustomerID", CustomerId);
                params.put("AdditionalInfo1",plan);
                params.put("AdditionalInfo2","NewMember");
                params.put("AdditionalInfo3",loginuserid);
                return params;
            }
        };
        selectplanrequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        TrinTrinSingleton.getInstance(getContext()).addtorequestqueue(selectplanrequest);
    }

}
