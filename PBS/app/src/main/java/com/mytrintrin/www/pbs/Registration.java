package com.mytrintrin.www.pbs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout Basicdetails,Profilepic,Proofpic,Selectplan,Payment,Smartcard;

    EditText Firstname,Lastname,Email,Phone,Address,DocNo,Amount,TransactionNo,Comments,CardNum;

    CheckBox male,female,others;

    Spinner Country,State,City,DocType,Plans,Paymentmode;

    ImageView profilepic,proofpic;

    ImageButton Takeprofilepic;


    Animation rotate,fadein;

    FloatingActionButton Basicnext,Profilenext,Updateplan,Updatepayment,Updatecard;

    String Fname,Lname,email,phone,address,gender,country,state,city,doctype,docno;

    private static final int ProfilePicRequest = 101;
    private static final int ProofPicRequest = 102;
    private static final int docPicRequest = 103;

    Bitmap profilephoto,proofphoto;

    String newmemberid,Planname,Planid,LoginId;

    public static SharedPreferences loginnfcid;
    public static SharedPreferences.Editor loginnfceditor;

    public  static ArrayList<String> MembershipIDArrayList = new ArrayList<String>();
    public  static ArrayList<String> MembershipNameArrayList = new ArrayList<String>();
    public static  ArrayList<String> RVIDArrayList = new ArrayList<String>();
    public static  ArrayList<String> RVNameArrayList = new ArrayList<String>();
    public  ArrayAdapter<String> Membernameadapter,RVadapter,MCadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intialize();
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotatebutton);
        fadein = AnimationUtils.loadAnimation(this,R.anim.fadeout);

        loginnfcid = getSharedPreferences("LoginPref", MODE_PRIVATE);
        LoginId = loginnfcid.getString("User-id",null);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void intialize() {

        /*Basic details*/
        Basicdetails = (LinearLayout) findViewById(R.id.userbasicdetails);
        male= (CheckBox) findViewById(R.id.cbfemale);
        female=(CheckBox) findViewById(R.id.cbfemale);
        others= (CheckBox) findViewById(R.id.cbothers);
        Firstname= (EditText) findViewById(R.id.etfirstname);
        Lastname= (EditText) findViewById(R.id.etlastname);
        Email= (EditText) findViewById(R.id.etemail);
        Phone = (EditText) findViewById(R.id.etphone);
        Country = (Spinner) findViewById(R.id.country);
        State= (Spinner) findViewById(R.id.state);
        City = (Spinner) findViewById(R.id.city);
        Address = (EditText) findViewById(R.id.etaddress);
        Basicnext = (FloatingActionButton) findViewById(R.id.Basicfab);
        /*Ends*/

        /*Profile pic*/
        Profilepic = (LinearLayout) findViewById(R.id.profiledetails);
        Profilenext= (FloatingActionButton) findViewById(R.id.Profilefab);
        profilepic= (ImageView) findViewById(R.id.profilepic);
        Takeprofilepic = (ImageButton) findViewById(R.id.takepic);
        /*Ends*/

        /*Proof pic*/
        Proofpic = (LinearLayout) findViewById(R.id.proofdetails);
        proofpic= (ImageView) findViewById(R.id.proofpic);
        DocType = (Spinner) findViewById(R.id.prooftype);
        DocNo = (EditText) findViewById(R.id.etdocumentno);

        /*Ends*/

       /*Select Plans*/
        Selectplan = (LinearLayout) findViewById(R.id.selectplanlayout);
        Plans = (Spinner) findViewById(R.id.plans);
        Updateplan = (FloatingActionButton) findViewById(R.id.updateplanfab);
        /*Ends*/

        /*Payment*/
        Payment = (LinearLayout) findViewById(R.id.paymentlayout);
        Amount = (EditText) findViewById(R.id.etamount);
        TransactionNo = (EditText) findViewById(R.id.ettransactions);
        Comments = (EditText) findViewById(R.id.etcomments);
        Updatepayment = (FloatingActionButton) findViewById(R.id.updatepaymentfab);
        Paymentmode = (Spinner) findViewById(R.id.paymentmode);


        Smartcard = (LinearLayout) findViewById(R.id.smartcardlayout);
        CardNum = (EditText) findViewById(R.id.etsmartcardno);
        Updatecard = (FloatingActionButton) findViewById(R.id.updatecardfab);


    }

    public void Basicnext(View view)
    {
        Fname = Firstname.getText().toString().trim();
        if(Fname.equals("")||Fname.equals(null))
        {
            Firstname.setError("Please Enter First Name");
        }

        else {
            Lname = Lastname.getText().toString().trim();
            phone = Phone.getText().toString().trim();
            address = Address.getText().toString().trim();
            email = Email.getText().toString().trim();
            if (male.isChecked()) {
                gender = male.getText().toString();
            }
            if (female.isChecked())

            {
                gender = female.getText().toString();
            }

            if (others.isChecked()) {
                gender = others.getText().toString();
            }
            country = Country.getSelectedItem().toString();
            state = State.getSelectedItem().toString();
            city = City.getSelectedItem().toString();


            Basicnext.startAnimation(rotate);
            Basicdetails.startAnimation(fadein);
            Basicdetails.setVisibility(View.GONE);
            Profilepic.setVisibility(View.VISIBLE);
        }

    }

    /*To take profile pic*/

    public  void TakeProfilePic(View view)
    {
        Toast.makeText(Registration.this,"Profile Pic",Toast.LENGTH_SHORT).show();
        Intent pp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(pp,ProfilePicRequest);
    }

    /*take profile pic ends*/

    public void Profilenext(View view)
    {
        Profilenext.startAnimation(rotate);
        Profilepic.startAnimation(fadein);
        Profilepic.setVisibility(View.GONE);
        Proofpic.setVisibility(View.VISIBLE);
    }

    public  void  Takeproofpic(View view)
    {
        Intent proof = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(proof,ProofPicRequest);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ProfilePicRequest && resultCode == Activity.RESULT_OK) {
            profilephoto = (Bitmap) data.getExtras().get("data");
            profilepic.setImageBitmap(profilephoto);
        }
        if (requestCode == ProofPicRequest && resultCode == Activity.RESULT_OK) {
            proofphoto = (Bitmap) data.getExtras().get("data");
            proofpic.setImageBitmap(proofphoto);
        }

    }
/*to convert image to string format*/
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
/*ends*/



    public void Savemember(View view)
    {

        getplans();


       final ArrayList<String> doclist = new ArrayList<String>();

        doctype = DocType.getSelectedItem().toString();
        docno = DocNo.getText().toString();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("description", "asdf");
        param.put("documentNumber",docno);
        param.put("documentType", doctype);
        param.put("documentCopy",getStringImage(proofphoto));
        doclist.add(String.valueOf(param));

        final JSONObject docs = new JSONObject();
        final JSONObject doccopy = new JSONObject();
        try {
            docs.put("documentType", doctype);
            docs.put("description", "asdf");
            docs.put("documentNumber",docno);
            //docs.put("documentCopy",getStringImage(proofphoto));
            doccopy.put("result",getStringImage(proofphoto));
            docs.put("documentCopy",doccopy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONArray docsarray = new JSONArray();
        docsarray.put(docs);

        final JSONObject memberobject = new JSONObject();
        try {
            memberobject.put("Name", Fname);
            memberobject.put("lastName", Lname);
            memberobject.put("sex", "male");
            memberobject.put("email", email);
            memberobject.put("phoneNumber", phone);
            memberobject.put("country", country);
            memberobject.put("state", state);
            memberobject.put("city", city);
            memberobject.put("address", address);
            memberobject.put("countryCode", "91");
            memberobject.put("pinCode", "");
            memberobject.put("smartCardNumber", "");
            memberobject.put("cardNum", "");
            memberobject.put("profilePic", getStringImage(profilephoto));
            memberobject.put("documents",docsarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Proofpic.setVisibility(View.GONE);


        JsonObjectRequest addmembers = new JsonObjectRequest(Request.Method.POST, API.addmember,memberobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject creatememberesponse = new JSONObject(String.valueOf(response));
                    JSONObject data = creatememberesponse.getJSONObject("data");
                    String createdmemberid = data.getString("_id");
                    newmemberid = createdmemberid;
                    Toast.makeText(Registration.this,"Member created successfully",Toast.LENGTH_SHORT).show();
                    uploadocuments();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Selectplan.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }*/

            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("Name", Fname);
                params.put("lastName", Lname);
                params.put("sex", "male");
                params.put("email", email);
                params.put("phoneNumber", phone);
                params.put("country", country);
                params.put("state", state);
                params.put("city", city);
                params.put("address", address);
                params.put("countryCode", "91");
                params.put("pinCode", "");
                params.put("smartCardNumber", "");
                params.put("cardNum", "");
                params.put("profilePic", getStringImage(profilephoto));
               // params.put("documents", docsarray.toString());
                return params;
            }

        };

        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addmembers);




        StringRequest addmember = new StringRequest(Request.Method.POST, API.addmember, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject creatememberesponse = new JSONObject(response);
                    JSONObject data = creatememberesponse.getJSONObject("data");
                    String createdmemberid = data.getString("_id");
                    newmemberid = createdmemberid;
                    Toast.makeText(Registration.this,"Member created successfully",Toast.LENGTH_SHORT).show();
                    uploadocuments();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Selectplan.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Server Error");
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Registration.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Registration.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Registration.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Registration.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Registration.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

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

            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Name",Fname );
                params.put("lastName", Lname);
                params.put("sex", "male");
                params.put("email", email);
                params.put("phoneNumber",phone);
                params.put("country",country);
                params.put("state",state);
                params.put("city",city);
                params.put("address",address);
                params.put("countryCode","91");
                params.put("pinCode","");
                params.put("smartCardNumber","");
                params.put("cardNum","");
                params.put("profilePic",getStringImage(profilephoto));
                params.put("documents", docsarray.toString());
                return params;
            }

        };

       // PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addmember);

    }

    public  void  uploadocuments()
    {
        StringRequest uploaddocsrequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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



           /* @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_id", newmemberid);
                params.put("membershipId", Planid);
                return params;
            }      */
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(uploaddocsrequest);
    }

    public  void Updateplan(View view)
    {
        Toast.makeText(Registration.this,"Plan updated successfully",Toast.LENGTH_SHORT).show();
        Selectplan.setVisibility(View.GONE);


        StringRequest updateplanrequest = new StringRequest(Request.Method.POST,API.assignmembeship+newmemberid+"/assignmembership", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Registration.this, response, Toast.LENGTH_SHORT).show();

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_id", newmemberid);
                params.put("membershipId", Planid);
                return params;
            }


        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updateplanrequest);
        Payment.setVisibility(View.VISIBLE);
    }

    private void getplans() {

        StringRequest getplanrequest = new StringRequest(Request.Method.GET, API.getplans, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject plansresponse = new JSONObject(response);
                    JSONArray data = plansresponse.getJSONArray("data");
                    for(int i =0 ;i < data.length();i++) {
                        JSONObject getid = data.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("subscriptionType");
                        Log.d("ID",id);
                        MembershipIDArrayList.add(id);
                        MembershipNameArrayList.add(name);
                        Log.d("Array ID", String.valueOf(MembershipIDArrayList));

                        Membernameadapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_spinner_dropdown_item, MembershipNameArrayList);
                        Membernameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Plans.setAdapter(Membernameadapter);
                        Plans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Planname=Plans.getSelectedItem().toString();
                                Planid = MembershipIDArrayList.get(position);
                                Log.d("Membership Id", Planid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

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
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getplanrequest);
    }

    public  void Updatepayment(View view)
    {
        Toast.makeText(Registration.this,"Payment added successfully",Toast.LENGTH_SHORT).show();
        Payment.setVisibility(View.GONE);

        StringRequest updatepaymentrequest = new StringRequest(Request.Method.POST, API.addcredit+newmemberid+"/credit", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("credit", Amount.getText().toString().trim());
                params.put("creditMode",Paymentmode.getSelectedItem().toString() );
                params.put("transactionNumber", TransactionNo.getText().toString().trim());
                params.put("comments",Comments.getText().toString().trim());
                params.put("createdBy", LoginId);
                return params;
            }

        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updatepaymentrequest);

        Smartcard.setVisibility(View.VISIBLE);

    }

    public  void Updatecard(View view)
    {

        StringRequest updatecardrequest = new StringRequest(Request.Method.POST, API.addcard+newmemberid+"/assigncard", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(Registration.this,"Smart card added successfully",Toast.LENGTH_SHORT).show();
                Smartcard.setVisibility(View.GONE);
                startActivity(new Intent(Registration.this,Registration.class));

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cardNumber", CardNum.getText().toString().trim());
                params.put("membershipId",Planid );
                return params;
            }


        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updatecardrequest);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Register) {
            startActivity(new Intent(Registration.this,Registration.class));
            // Handle the camera action
        } else if (id == R.id.nav_Refunds) {

            startActivity(new Intent(Registration.this,Refunds.class));

        } else if (id == R.id.nav_slideshow) {

            startActivity(new Intent(Registration.this,Members.class));

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
