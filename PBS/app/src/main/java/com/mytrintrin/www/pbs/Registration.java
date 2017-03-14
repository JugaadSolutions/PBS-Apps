package com.mytrintrin.www.pbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class Registration extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout Basicdetails, Profilepic, Proofpic, Selectplan, Payment, Smartcard;

    EditText Firstname, Lastname, Email, Phone, Address, DocNo, Amount, TransactionNo, Comments, CardNum;

    CheckBox male, female, others;

    Spinner Country, State, City, DocType, Plans, Paymentmode;

    ImageView profilepic, proofpic;

    ImageButton Takeprofilepic;

    Animation rotate, fadein;

    FloatingActionButton Basicnext, Profilenext,SaveMember, Updateplan, Updatepayment, Updatecard;

    String Fname, Lname, email, phone, address, gender, country, state, city, doctype, docno,uid;

    int usercreditbalance;

    private static final int ProfilePicRequest = 101;
    private static final int ProofPicRequest = 102;
    private static final int docPicRequest = 103;

    Bitmap profilephoto, proofphoto;

    String newmemberid, Planname, Planid, LoginId,userdetails;

    public static SharedPreferences loginnfcid;
    public static SharedPreferences.Editor loginnfceditor;

    public static ArrayList<String> MembershipIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipNameArrayList = new ArrayList<String>();
    public static ArrayList<Integer> Totalamountofmembership = new ArrayList<Integer>();
    public ArrayAdapter<String> Membernameadapter;

    public static final int RequestPermissionCode = 1;

    private ProgressDialog mProgressDialog;

    JSONObject Memberobject,UpdateMemberobject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intialize();
        onpermision();
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotatebutton);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        loginnfcid = getSharedPreferences("LoginPref", MODE_PRIVATE);
        loginnfceditor = loginnfcid.edit();
        LoginId = loginnfcid.getString("User-id", null);

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
        male = (CheckBox) findViewById(R.id.cbfemale);
        female = (CheckBox) findViewById(R.id.cbfemale);
        others = (CheckBox) findViewById(R.id.cbothers);
        Firstname = (EditText) findViewById(R.id.etfirstname);
        Lastname = (EditText) findViewById(R.id.etlastname);
        Email = (EditText) findViewById(R.id.etemail);
        Phone = (EditText) findViewById(R.id.etphone);
        Country = (Spinner) findViewById(R.id.country);
        State = (Spinner) findViewById(R.id.state);
        City = (Spinner) findViewById(R.id.city);
        Address = (EditText) findViewById(R.id.etaddress);
        Basicnext = (FloatingActionButton) findViewById(R.id.Basicfab);
        /*Ends*/

        /*Profile pic*/
        Profilepic = (LinearLayout) findViewById(R.id.profiledetails);
        Profilenext = (FloatingActionButton) findViewById(R.id.Profilefab);
        profilepic = (ImageView) findViewById(R.id.profilepic);
        Takeprofilepic = (ImageButton) findViewById(R.id.takepic);
        /*Ends*/

        /*Proof pic*/
        Proofpic = (LinearLayout) findViewById(R.id.proofdetails);
        proofpic = (ImageView) findViewById(R.id.proofpic);
        DocType = (Spinner) findViewById(R.id.prooftype);
        DocNo = (EditText) findViewById(R.id.etdocumentno);
        SaveMember= (FloatingActionButton) findViewById(R.id.Savememberfab);
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

        if( getIntent().getExtras() != null)
        {
            Intent intent = getIntent();
             userdetails = intent.getStringExtra("Memberobject");
            if(userdetails!=null)
            {
                setmemberdetails();
                SaveMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updatememberwithdocs();
                    }
                });
            }
        }
        else
        {
            Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
        }
    }

    /*Requesting for permissions*/
    public void onpermision() {
        if (checkPermission()) {
            // Toast.makeText(ChildActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(Registration.this, new String[]
                {
                        CAMERA,
                        ACCESS_FINE_LOCATION
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && LocationPermission) {

                        Toast.makeText(Registration.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registration.this, LoginNFC.class));
                    } else {
                        Toast.makeText(Registration.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        Toast.makeText(Registration.this, "App Need Permission to take photo", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder permissionbuilder = new AlertDialog.Builder(this);
                        permissionbuilder.setIcon(R.drawable.logo);
                        permissionbuilder.setMessage("Request for permission?");
                        permissionbuilder.setTitle("Permissions");
                        permissionbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Registration.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            }
                        });
                        permissionbuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        permissionbuilder.show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    /*Requesting permission ends*/


    public void Basicnext(View view) {
        Fname = Firstname.getText().toString().trim();
        if (Fname.equals("") || Fname.equals(null)) {
            Firstname.setError("Please Enter First Name");
        } else {
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
    public void TakeProfilePic(View view) {
        Toast.makeText(Registration.this, "Profile Picture", Toast.LENGTH_SHORT).show();
        onpermision();
        Intent pp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(pp, ProfilePicRequest);
    }
    /*take profile pic ends*/

    public void Profilenext(View view) {
        Profilenext.startAnimation(rotate);
        Profilepic.startAnimation(fadein);
        Profilepic.setVisibility(View.GONE);
        Proofpic.setVisibility(View.VISIBLE);
    }

    public void ProfileBack(View view) {
        Profilepic.setVisibility(View.GONE);
        Basicdetails.setVisibility(View.VISIBLE);
        Proofpic.setVisibility(View.GONE);
    }

    public void Takeproofpic(View view) {
        Toast.makeText(Registration.this, "Document Picture", Toast.LENGTH_SHORT).show();
        onpermision();
        Intent proof = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(proof, ProofPicRequest);
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
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
/*ends*/


    public void Savemember(View view) {

        getplans();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Registration is in progress...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final ArrayList<String> doclist = new ArrayList<String>();

        doctype = DocType.getSelectedItem().toString();
        docno = DocNo.getText().toString();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("description", "asdf");
        param.put("documentNumber", docno);
        param.put("documentType", doctype);
        param.put("documentCopy", getStringImage(proofphoto));
        doclist.add(String.valueOf(param));

        final JSONObject docs = new JSONObject();
        final JSONObject doccopy = new JSONObject();
        JSONObject resultprofilepic = new JSONObject();
        try {
            docs.put("documentType", doctype);
            docs.put("description", "asdf");
            docs.put("documentNumber", docno);
            doccopy.put("result", getStringImage(proofphoto));
            docs.put("documentCopy", doccopy);
            resultprofilepic.put("result", getStringImage(profilephoto));
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
            memberobject.put("phoneNumber", "91-" + phone);
            memberobject.put("country", country);
            memberobject.put("state", state);
            memberobject.put("city", city);
            memberobject.put("address", address);
            memberobject.put("countryCode", "91");
            memberobject.put("pinCode", "");
            memberobject.put("smartCardNumber", "");
            memberobject.put("cardNum", "");
            memberobject.put("profilePic", resultprofilepic);
            memberobject.put("memberprofilePic", "");
            memberobject.put("documents", docsarray);
            memberobject.put("createdBy", LoginId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Proofpic.setVisibility(View.GONE);

        JsonObjectRequest addmembers = new JsonObjectRequest(Request.Method.POST, API.addmember, memberobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject creatememberesponse = new JSONObject(String.valueOf(response));
                    JSONObject data = creatememberesponse.getJSONObject("data");
                    String createdmemberid = data.getString("_id");
                    newmemberid = createdmemberid;
                    mProgressDialog.dismiss();
                    Toast.makeText(Registration.this, "Member created successfully", Toast.LENGTH_SHORT).show();
                    String creditbalance = data.getString("creditBalance");
                    Boolean processingfee = data.getBoolean("processingFeesDeducted");
                    if(Integer.parseInt(creditbalance)>0&&processingfee.equals(false))
                    {
                        Toast.makeText(Registration.this, "Has already paid", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Selectplan.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
        };
        addmembers.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addmembers);
    }

    public void backtoprofile(View view) {
        Proofpic.setVisibility(View.GONE);
        Profilepic.setVisibility(View.VISIBLE);
    }


    public void Updateplan(View view) {
        Toast.makeText(Registration.this, "Plan updated successfully", Toast.LENGTH_SHORT).show();
        Selectplan.setVisibility(View.GONE);
        StringRequest updateplanrequest = new StringRequest(Request.Method.POST, API.assignmembeship + newmemberid + "/assignmembership", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
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
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getid = data.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("subscriptionType");
                        int usagefee = getid.getInt("userFees");
                        int processingfee = getid.getInt("securityDeposit");
                        int securitydeposit = getid.getInt("processingFees");
                        int smartcardfees = getid.getInt("smartCardFees");

                        MembershipIDArrayList.add(id);
                        MembershipNameArrayList.add(name);
                        Totalamountofmembership.add(usagefee+processingfee+securitydeposit+smartcardfees);

                        Membernameadapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_spinner_dropdown_item, MembershipNameArrayList);
                        Membernameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Plans.setAdapter(Membernameadapter);
                        Plans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Planname = Plans.getSelectedItem().toString();
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
                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
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

    public void Updatepayment(View view) {
        Toast.makeText(Registration.this, "Payment added successfully", Toast.LENGTH_SHORT).show();
        StringRequest updatepaymentrequest = new StringRequest(Request.Method.POST, API.addcredit + newmemberid + "/credit", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Payment.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
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
                params.put("creditMode", Paymentmode.getSelectedItem().toString());
                params.put("transactionNumber", TransactionNo.getText().toString().trim());
                params.put("comments", Comments.getText().toString().trim());
                params.put("createdBy", LoginId);
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updatepaymentrequest);
        Smartcard.setVisibility(View.VISIBLE);
    }

    public void Updatecard(View view) {
        StringRequest updatecardrequest = new StringRequest(Request.Method.POST, API.addcard + newmemberid + "/assigncard", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Registration.this, "Smart card added successfully", Toast.LENGTH_SHORT).show();
                Smartcard.setVisibility(View.GONE);
                startActivity(new Intent(Registration.this, Registration.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
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
                params.put("membershipId", Planid);
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

   public void setmemberdetails()
   {
       try {
           Memberobject = new JSONObject(userdetails);
           Fname = Memberobject.getString("Name");
           Firstname.setText(Fname);
           Lname = Memberobject.getString("lastName");
           Lastname.setText(Lname);
           email = Memberobject.getString("email");
           Email.setText(email);
           if(email.length()>1)
           {Email.setEnabled(false);}
           else
           {
               Email.setEnabled(true);
           }
           phone = Memberobject.getString("phoneNumber");
           Phone.setText(phone);
           if(phone.length()>1)
           {Phone.setEnabled(false);}
           else
           {
               Phone.setEnabled(true);
           }
           if(Memberobject.has("address"))
           {
               address = Memberobject.getString("address");
               Address.setText(address);
           }
           else{Address.setText("");}
           String profilepics = Memberobject.getString("profilePic");
           uid = Memberobject.getString("_id");
           if(profilepics.equals(""))
           {
               Toast.makeText(this, "No Profile Pic Found", Toast.LENGTH_SHORT).show();
           }
           else
           {
               StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
               StrictMode.setThreadPolicy(policy);
               try {
                   URL url =  new URL("http://www.mytrintrin.com/mytrintrin/Member/" + uid + "/" + profilepics + ".png");
                   profilephoto = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                   profilepic.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
               } catch (IOException e) {
                   Log.e("TAG", e.getMessage());
               }
           }
       } catch (JSONException e) {
                e.printStackTrace();
            }
   }


    public void updatememberwithdocs()
    {
        UpdateMemberobject = new JSONObject();
        newmemberid =uid;
        final JSONArray docsarray = new JSONArray();
        try {
            String profilepics = Memberobject.getString("profilePic");
            UpdateMemberobject.put("Name", Fname);
            UpdateMemberobject.put("lastName", Lname);
            UpdateMemberobject.put("sex", "male");
            UpdateMemberobject.put("email", email);
            UpdateMemberobject.put("countryCode", "91");
            UpdateMemberobject.put("_id", uid);
            doctype = DocType.getSelectedItem().toString();
            docno = DocNo.getText().toString();
            final JSONObject updatedocs = new JSONObject();
            final JSONObject updatedoccopy = new JSONObject();
            final JSONObject updateprofilepic = new JSONObject();
            try {
                updatedocs.put("documentType", doctype);
                updatedocs.put("description", "Documents of "+Fname);
                updatedocs.put("documentNumber", docno);
                updatedoccopy.put("result", getStringImage(proofphoto));
                updatedocs.put("documentCopy", updatedoccopy);
                updatedocs.put("createdBy", LoginId);
                docsarray.put(updatedocs);
                UpdateMemberobject.put("documents", docsarray);
                if(profilepics.equals("")) {
                    updateprofilepic.put("result", getStringImage(profilephoto));
                    UpdateMemberobject.put("profilePic", updateprofilepic);
                }
                else
                {
                    UpdateMemberobject.put("profilePic", profilepics);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updatememberwithdocsrequest = new JsonObjectRequest(Request.Method.PUT, API.updatemember + uid, UpdateMemberobject, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(Registration.this, "Documents updated successfully", Toast.LENGTH_LONG).show();
            getplans();
            try {
                usercreditbalance = Memberobject.getInt("creditBalance");
                Boolean processingfee = Memberobject.getBoolean("processingFeesDeducted");
                if(usercreditbalance>0&&processingfee.equals(false))
                {
                    Toast.makeText(Registration.this, "Has already paid", Toast.LENGTH_SHORT).show();
                   if( Totalamountofmembership.contains(usercreditbalance))
                    {
                       int index= Totalamountofmembership.indexOf(usercreditbalance);
                        String  planid = MembershipIDArrayList.get(index);
                        assingplan(planid);
                    }
                }
                else{
                    Proofpic.setVisibility(View.GONE);
                    Selectplan.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof ServerError) {
                Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                Log.d("Error", String.valueOf(error instanceof ServerError));
                error.printStackTrace();
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
    });
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updatememberwithdocsrequest);
    }

    public  void assingplan(final String planid)
    {
        Planid = planid;
        StringRequest updateplanrequest = new StringRequest(Request.Method.POST, API.assignmembeship + newmemberid + "/assignmembership", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Proofpic.setVisibility(View.GONE);
                Smartcard.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Registration.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
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
        }) {
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
                params.put("membershipId", planid);
                return params;
            }
        };
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(updateplanrequest);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_Register) {
            startActivity(new Intent(Registration.this, Registration.class));

        } else if (id == R.id.nav_Refunds) {

            startActivity(new Intent(Registration.this, Refunds.class));

        } else if (id == R.id.nav_slideshow) {

            startActivity(new Intent(Registration.this, Members.class));

        } else if (id == R.id.nav_logout) {

            loginnfceditor.putString("User-id", "");
            loginnfceditor.commit();
            startActivity(new Intent(Registration.this, Login.class));
            finish();

        } else if (id == R.id.nav_tickets) {
            startActivity(new Intent(Registration.this, Tickets.class));
        } /*else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
