package com.mytrintrin.www.pbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Members extends AppCompatActivity {

    Context context;
    LinearLayout linearLayout;
    ImageView refresh;


    CardView cardview;
    LinearLayout ParentLayout, DetailsLayout, ProfileLayout;
    LinearLayout.LayoutParams cardparams, parentparams, detailsparams, profileparams;
    TextView mname, mphone, memail, mplan,mcard,mbalance;
    ImageView ivprofilepic;

    String Name, Plan, Email, Phone, Balance, Smartcard, doccopy, docid;
    Toolbar Membertoolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        context = getApplicationContext();
        linearLayout = (LinearLayout) findViewById(R.id.memberslayout);
        refresh = (ImageView) findViewById(R.id.refreshmember);
        Membertoolbar = (Toolbar) findViewById(R.id.memberstoolbar);
        Membertoolbar.setTitle("Members");
        Membertoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(Membertoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewallmembers();
    }


    public void refreshmembers(View view)
    {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate);
        refresh.startAnimation(animation);
        Toast.makeText(Members.this, "Refreshing", Toast.LENGTH_SHORT).show();
        viewallmembers();
        refresh.clearAnimation();

    }

    public void viewallmembers() {
        StringRequest getmembers = new StringRequest(Request.Method.GET, API.getmembersapi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    Boolean error = responsefromserver.getBoolean("error");
                    String message = responsefromserver.getString("message");
                    JSONArray data = responsefromserver.getJSONArray("data");
                    Log.d("data", String.valueOf(data));
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject first = data.getJSONObject(i);
                        Name = first.getString("Name");
                        Phone = first.getString("phoneNumber");
                       // Email = first.getString("email");
                        Balance = first.getString("creditBalance");

                        if(first.has("email"))
                        {
                            Email = first.getString("email");
                            Log.d("cardno",Email);
                            if(Email == null)
                            {
                                Email = "Smart card has not assigned";
                            }

                        }
                        else
                            Email = "";

                        if(first.has("cardNum"))
                        {
                            Smartcard = first.getString("cardNum");
                            Log.d("cardno",Smartcard);
                            if(Smartcard == null)
                            {
                                Smartcard = "Smart card has not assigned";
                            }

                        }
                        else
                        Smartcard = "";

                        if (first.has("membershipId")) {

                            JSONObject membership = first.getJSONObject("membershipId");
                            Plan = membership.getString("subscriptionType");

                        } else {
                            Plan = "No plan selected";
                            Log.d("Membership Plan", Plan);
                        }


                        JSONArray documents = first.getJSONArray("documents");
                        if (documents.length() == 0) {

                        } else {
                            JSONObject firstdoc = documents.getJSONObject(0);
                            docid = first.getString("_id");
                            doccopy = firstdoc.getString("documentCopy");

                        }


                        Log.d("Name", Name);
                        Log.d("Documents", String.valueOf(documents));


                        cardview = new CardView(context);
                        cardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardparams.bottomMargin = 10;
                        cardview.setLayoutParams(cardparams);
                        cardview.setRadius(15);
                        cardview.setPadding(25, 25, 25, 25);
                        cardview.setCardBackgroundColor(Color.parseColor("#009746"));
                        cardview.setVisibility(View.VISIBLE);
                        cardview.setMaxCardElevation(30);
                        cardview.setMaxCardElevation(6);

                        ParentLayout = new LinearLayout(context);
                        parentparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ParentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        ParentLayout.setWeightSum(100);

                        DetailsLayout = new LinearLayout(context);
                        detailsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        DetailsLayout.setOrientation(LinearLayout.VERTICAL);
                        DetailsLayout.setGravity(Gravity.END);
                        DetailsLayout.setPadding(25, 25, 25, 25);
                        DetailsLayout.setLayoutParams(detailsparams);
                        DetailsLayout.setWeightSum(60);

                        mname = new TextView(context);
                        mname.setText(Name);
                        mname.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        mname.setTextColor(Color.WHITE);
                        mphone = new TextView(context);
                        mphone.setText("Phone:" + Phone);
                        memail = new TextView(context);
                        memail.setText("Email:" + Email);
                        mplan = new TextView(context);
                        mplan.setText("Plan:" + Plan);
                        mcard= new TextView(context);
                        mcard.setText("Smartcard No:"+Smartcard);
                        mbalance = new TextView(context);
                        mbalance.setText("Balance:"+Balance);


                        DetailsLayout.addView(mname);
                        DetailsLayout.addView(mphone);
                        DetailsLayout.addView(memail);
                        DetailsLayout.addView(mplan);
                        DetailsLayout.addView(mcard);
                        DetailsLayout.addView(mbalance);


                        ProfileLayout = new LinearLayout(context);
                        profileparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
                        ProfileLayout.setGravity(Gravity.END);
                        ProfileLayout.setOrientation(LinearLayout.VERTICAL);
                        ProfileLayout.setWeightSum(40);
                        ProfileLayout.setPadding(25, 25, 25, 25);
                        ProfileLayout.setLayoutParams(profileparams);

                        ivprofilepic = new ImageView(context);
                        ivprofilepic.setVisibility(View.VISIBLE);
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            if(documents.equals("[]"))
                            {
                                ivprofilepic.setImageResource(R.drawable.logo);
                            }
                            else {
                                URL url = new URL("http://www.mytrintrin.com/mytrintrin/Member/" + docid + "/" + doccopy + ".png");
                                ivprofilepic.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
                            }
                        } catch (IOException e) {
                            Log.e("TAG", e.getMessage());
                        }
                        ProfileLayout.addView(ivprofilepic);


                        cardview.addView(ParentLayout);
                        ParentLayout.addView(DetailsLayout);
                        ParentLayout.addView(ProfileLayout);
                        linearLayout.addView(cardview);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };

        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getmembers);

    }

}

