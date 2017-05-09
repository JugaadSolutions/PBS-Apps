package com.mytrintrin.www.pbs_trintrin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Registration extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar RegistrationToolbar;
    ActionBarDrawerToggle mToogle;
    DrawerLayout RegistrationDrawer;

    LinearLayout Basicdetails, Profilepic, Proofpic, Selectplan, Payment, Smartcard;
    EditText Firstname, Lastname, Email, Phone, Address, DocNo, Amount, TransactionNo, Comments, CardNum, OtpNum,Age,EmergencyContact;
    CheckBox male, female, others;
    Spinner Country, State, City, DocType, Plans, Paymentmode, CountryCode;
    TextView statetv, citytv;
    ImageView profilepic, proofpic, proofpic_2;
    ImageButton Takeprofilepic, Takeproofpic, Takeproofpic_2;
    String Fname, Lname, email, phone, address, gender, country, state, city, doctype, docno, uid, Planname, Planid, LoginId, userdetails, cardno,age,emergencycontact;
    Bitmap profilephoto = null, proofphoto = null, proofphoto_2 = null;
    int Userid, CreditBalance, Usagefee, OTP;
    Menu nav_menu;
    NavigationView Registration_navigation;
    Button AddMembers;

    private static final int ProfilePicRequest = 101;
    private static final int ProofPicRequest = 102;
    private static final int ProofPicRequest_2 = 103;
    public static final int RequestPermissionCode = 1;

    private ProgressDialog mProgressDialog;

    public static ArrayList<String> MembershipIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MembershipNameArrayList = new ArrayList<String>();
    public static ArrayList<Integer> MembershipUsageArrayList = new ArrayList<Integer>();
    public static ArrayList<Integer> Totalamountofmembership = new ArrayList<Integer>();
    public ArrayAdapter<String> Membernameadapter;

    JSONObject Memberobject, Documentobject, Docresultobject, Profileobject, Prospectiveobject,Emergencyobject;
    JSONArray Documentarray;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        RegistrationToolbar = (Toolbar) findViewById(R.id.registration_action);
        RegistrationToolbar.setTitle("Registration");
        setSupportActionBar(RegistrationToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RegistrationDrawer = (DrawerLayout) findViewById(R.id.registration_drawer);
        mToogle = new ActionBarDrawerToggle(this, RegistrationDrawer, R.string.open, R.string.close);
        RegistrationDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        Registration_navigation = (NavigationView) findViewById(R.id.registration_navigationview);
        nav_menu = Registration_navigation.getMenu();

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        onpermision();
        getplans();
        Memberobject = new JSONObject();
        Documentobject = new JSONObject();
        Docresultobject = new JSONObject();
        Profileobject = new JSONObject();
        Documentarray = new JSONArray();
        Emergencyobject = new JSONObject();

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        LoginId = loginpref.getString("User-id", null);

        if (savedInstanceState != null) {
            profilephoto = savedInstanceState.getParcelable("profile");
            proofphoto = savedInstanceState.getParcelable("proof");
            profilepic = (ImageView) findViewById(R.id.profilepic);
            proofpic = (ImageView) findViewById(R.id.proofpic);
            profilepic.setImageBitmap(profilephoto);
            proofpic.setImageBitmap(proofphoto);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.registration_navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        intialize();
        checkinternet();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Registration.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/

    private void intialize() {

        /*Basic details*/
        Basicdetails = (LinearLayout) findViewById(R.id.userbasicdetails);
        male = (CheckBox) findViewById(R.id.cbmale);
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
        statetv = (TextView) findViewById(R.id.tvstate);
        citytv = (TextView) findViewById(R.id.tvcity);
        CountryCode = (Spinner) findViewById(R.id.countrycodespinner);
        Age = (EditText) findViewById(R.id.etage);
        EmergencyContact = (EditText) findViewById(R.id.etemergencycontact);
        /*Ends*/

        /*Profile pic*/
        Profilepic = (LinearLayout) findViewById(R.id.profiledetails);
        profilepic = (ImageView) findViewById(R.id.profilepic);
        Takeprofilepic = (ImageButton) findViewById(R.id.takepic);
        /*Ends*/

        /*Proof pic*/
        Proofpic = (LinearLayout) findViewById(R.id.proofdetails);
        proofpic = (ImageView) findViewById(R.id.proofpic);
        proofpic_2 = (ImageView) findViewById(R.id.proofpic_2);
        DocType = (Spinner) findViewById(R.id.prooftype);
        DocNo = (EditText) findViewById(R.id.etdocumentno);
        Takeproofpic = (ImageButton) findViewById(R.id.takeproofpic);
        Takeproofpic_2 = (ImageButton) findViewById(R.id.takeproofpic_2);
        /*Ends*/

       /*Select Plans*/
        Selectplan = (LinearLayout) findViewById(R.id.selectplanlayout);
        Plans = (Spinner) findViewById(R.id.plans);
        /*Ends*/

        /*Payment*/
        Payment = (LinearLayout) findViewById(R.id.paymentlayout);
        Amount = (EditText) findViewById(R.id.etamount);
        TransactionNo = (EditText) findViewById(R.id.ettransactions);
        Comments = (EditText) findViewById(R.id.etcomments);
        Paymentmode = (Spinner) findViewById(R.id.paymentmode);
        Amount.setEnabled(false);
        /*Ends*/

        /*Smartcard*/
        Smartcard = (LinearLayout) findViewById(R.id.smartcardlayout);
        CardNum = (EditText) findViewById(R.id.etsmartcardno);
        /*Ends*/

        AddMembers = (Button) findViewById(R.id.addmember_registration);

        countrycodecheck();
        documentcheck();

        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            userdetails = intent.getStringExtra("Memberobject");
            if (userdetails != null) {
                setmemberdetails();
                Toast.makeText(this, "Getting user details", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "New User", Toast.LENGTH_SHORT).show();
        }

        CountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (CountryCode.getSelectedItem().toString().equals("India (+91)")) {
                    Selectplan.setVisibility(View.GONE);
                    Payment.setVisibility(View.GONE);
                    Smartcard.setVisibility(View.GONE);
                    AddMembers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkdetailsandgenerateotp();
                        }
                    });
                } else {
                    Selectplan.setVisibility(View.VISIBLE);
                    Payment.setVisibility(View.VISIBLE);
                    Smartcard.setVisibility(View.VISIBLE);
                    AddMembers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Addmember(view);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!Country.getSelectedItem().toString().equals("India")) {
                    State.setVisibility(View.GONE);
                    City.setVisibility(View.GONE);
                    statetv.setVisibility(View.GONE);
                    citytv.setVisibility(View.GONE);
                    String proofs[] = {"Passport"};
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_list_item_1, proofs);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    DocType.setAdapter(spinnerArrayAdapter);
                } else {
                    State.setVisibility(View.VISIBLE);
                    City.setVisibility(View.VISIBLE);
                    statetv.setVisibility(View.VISIBLE);
                    citytv.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.proof_arrays));
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    DocType.setAdapter(spinnerArrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        DocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (DocType.getSelectedItem().toString().equals("Aadhar")) {
                    Selectplan.setVisibility(View.VISIBLE);
                    Payment.setVisibility(View.VISIBLE);
                    Smartcard.setVisibility(View.VISIBLE);
                    DocNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});
                    DocNo.setInputType(InputType.TYPE_CLASS_NUMBER);
                    AddMembers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Addmember(view);
                        }
                    });
                } else if (!DocType.getSelectedItem().toString().equals("Aadhar") && (!CountryCode.getSelectedItem().toString().equals("India (+91)"))) {
                    Selectplan.setVisibility(View.VISIBLE);
                    Payment.setVisibility(View.VISIBLE);
                    Smartcard.setVisibility(View.VISIBLE);
                    DocNo.setInputType(InputType.TYPE_CLASS_TEXT);
                    DocNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
                    AddMembers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Addmember(view);
                        }
                    });
                } else {
                    Selectplan.setVisibility(View.GONE);
                    Payment.setVisibility(View.GONE);
                    Smartcard.setVisibility(View.GONE);
                    DocNo.setInputType(InputType.TYPE_CLASS_TEXT);
                    DocNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
                    AddMembers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkdetailsandgenerateotp();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                        ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    boolean WritePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && LocationPermission && WritePermission) {
                        Toast.makeText(Registration.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registration.this, Registration.class));
                    } else {
                        Toast.makeText(Registration.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
    /*Requesting permission ends*/

    /*To take profile and proof pic*/
    public void TakeProfilePic(View view) {
        Toast.makeText(Registration.this, "Profile Picture", Toast.LENGTH_SHORT).show();
        onpermision();
        Intent pp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(pp, ProfilePicRequest);
    }

    public void Takeproofpic(View view) {
        Toast.makeText(Registration.this, "Document Picture", Toast.LENGTH_SHORT).show();
        onpermision();
        Intent proof = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(proof, ProofPicRequest);
        Takeproofpic.setVisibility(View.GONE);
        Takeproofpic_2.setVisibility(View.VISIBLE);
    }

    public void Takeproofpic_2(View view) {
        Toast.makeText(Registration.this, "Document Picture", Toast.LENGTH_SHORT).show();
        onpermision();
        Intent proof = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(proof, ProofPicRequest_2);
        Takeproofpic_2.setVisibility(View.VISIBLE);
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
        if (requestCode == ProofPicRequest_2 && resultCode == Activity.RESULT_OK) {
            proofphoto_2 = (Bitmap) data.getExtras().get("data");
            proofpic_2.setImageBitmap(proofphoto_2);

        }
    }

    /*take profile and proof pic ends*/

    public void checkdetailsandgenerateotp() {
        Toast.makeText(this, "OTP", Toast.LENGTH_SHORT).show();
        Fname = Firstname.getText().toString().trim();
        if (Fname.equals("") || Fname.equals(null)) {
            Firstname.setError("Please Enter First Name");
            Firstname.requestFocus();
            return;
        }
        docno = DocNo.getText().toString();

        if (Phone.getText().toString().trim().equals("") || Phone.getText().toString().trim().equals(null)) {
            Phone.setError("Phone Number");
            return;
        }

        if (Phone.getText().toString().trim().length() < 10) {
            Phone.setError("Phone Number");
            return;
        }

        if(Age.getText().toString().trim().equals("")||Age.getText().toString().trim().equals(null))
        {
            Age.setError("Age");
            Age.requestFocus();
            return;
        }

        if(EmergencyContact.getText().toString().trim().equals("")||EmergencyContact.getText().toString().trim().equals(null))
        {
            EmergencyContact.setError("Emergency Contact");
            EmergencyContact.requestFocus();
            return;
        }

        if(EmergencyContact.getText().toString().trim().length()<10)
        {
            EmergencyContact.setError("Emergency Contact");
            EmergencyContact.requestFocus();
            return;
        }

        if(Age.getText().toString().trim().length()<2)
        {
            Age.setError("Age");
            Age.requestFocus();
            return;
        }


        boolean hasproof = (proofpic.getDrawable() != null);
        if (hasproof) {
            // imageView has image in it
        } else {
            Toast.makeText(this, "Document cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean hasprofilepic = (profilepic.getDrawable() != null);
        if (hasprofilepic) {
            // imageView has image in it
        } else {
            Toast.makeText(this, "profile cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (docno.equals("") || docno.equals(null)) {
            DocNo.setError("Document Number");
            Toast.makeText(this, "Document number cannot be empty", Toast.LENGTH_SHORT).show();
            DocNo.requestFocus();
            return;
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Registration is in progress...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            Lname = Lastname.getText().toString().trim();
            phone = Phone.getText().toString().trim();
            if (phone.equals("")) {
                phone = "";
            } else {
                phone = "91-" + phone;
            }
            address = Address.getText().toString().trim();
            email = Email.getText().toString().trim();
            if (male.isSelected()) {
                gender = male.getTag().toString();
                female.toggle();
                others.toggle();
                Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
            }
            if (female.isChecked()) {
                gender = female.getTag().toString();
                male.toggle();
                others.toggle();
                Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
            }
            if (others.isChecked()) {
                gender = others.getTag().toString();
                female.toggle();
                male.toggle();
                Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
            }
            country = Country.getSelectedItem().toString();
            state = State.getSelectedItem().toString();
            city = City.getSelectedItem().toString();
            doctype = DocType.getSelectedItem().toString();
            docno = DocNo.getText().toString();
            try {
                Documentobject.put("documentType", doctype);
                Documentobject.put("description", "Documents of " + Fname);
                Documentobject.put("documentNumber", docno);
                Docresultobject.put("result", getStringImage(proofphoto));

                Emergencyobject.put("countryCode","91");
                Emergencyobject.put("contactName","");
                Emergencyobject.put("contactNumber",EmergencyContact.getText().toString().trim());

                boolean hasproofpic_2 = (proofpic_2.getDrawable() != null);
                if (hasproofpic_2) {
                    // imageView has image in it
                    JSONObject Documentobj_2 = new JSONObject();
                    JSONObject Documentobjresult_2 = new JSONObject();

                    Documentobj_2.put("documentType", doctype);
                    Documentobj_2.put("description", "Documents of " + Fname);
                    Documentobj_2.put("documentNumber", docno);
                    Documentobjresult_2.put("result", getStringImage(proofphoto_2));
                    Documentobj_2.put("documentCopy", Documentobjresult_2);
                    Documentarray.put(Documentobj_2);
                } else {

                }

                if (proofphoto.equals("")) {
                    Toast.makeText(this, "Document cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Documentobject.put("documentCopy", Docresultobject);
                Profileobject.put("result", getStringImage(profilephoto));
                if (profilephoto.equals(null)) {
                    Toast.makeText(this, "picture cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Documentarray.put(Documentobject);

                Memberobject.put("Name", Fname);
                Memberobject.put("lastName", Lname);
                Memberobject.put("sex", "Male");
                Memberobject.put("email", email);
                Memberobject.put("phoneNumber", phone);
                Memberobject.put("country", country);
                Memberobject.put("state", state);
                Memberobject.put("city", city);
                Memberobject.put("address", address);
                Memberobject.put("countryCode", "91");
                Memberobject.put("pinCode", "");
                Memberobject.put("profilePic", Profileobject);
                Memberobject.put("memberprofilePic", "");
                Memberobject.put("documents", Documentarray);
                Memberobject.put("UserID", Userid);
                Memberobject.put("createdBy", LoginId);
                Memberobject.put("age",Age.getText().toString().trim());
                Memberobject.put("emergencyContact",Emergencyobject);
                sendprospectivedetailstoserver();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void Addmember(View view) {
        Fname = Firstname.getText().toString().trim();
        if (Fname.equals("") || Fname.equals(null)) {
            Firstname.setError("Please Enter First Name");
            Firstname.requestFocus();
            return;
        }

        if(Age.getText().toString().trim().equals("")||Age.getText().toString().trim().equals(null))
        {
            Age.setError("Age");
            Age.requestFocus();
            return;
        }

        if(EmergencyContact.getText().toString().trim().equals("")||EmergencyContact.getText().toString().trim().equals(null))
        {
            EmergencyContact.setError("Emergency Contact");
            EmergencyContact.requestFocus();
            return;
        }

        if(EmergencyContact.getText().toString().trim().length()<10)
        {
            EmergencyContact.setError("Emergency Contact");
            EmergencyContact.requestFocus();
            return;
        }

        if(Age.getText().toString().trim().length()<2)
        {
            Age.setError("Age");
            Age.requestFocus();
            return;
        }

        docno = DocNo.getText().toString();
        if (docno.equals("") || docno.equals(null)) {
            DocNo.setError("Document Number");
            Toast.makeText(this, "Document number cannot be empty", Toast.LENGTH_SHORT).show();
            DocNo.requestFocus();
            return;
        }
        boolean hasproof = (proofpic.getDrawable() != null);
        if (hasproof) {
            // imageView has image in it
        } else {
            Toast.makeText(this, "Document cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean hasprofilepic = (profilepic.getDrawable() != null);
        if (hasprofilepic) {
            // imageView has image in it
        } else {
            Toast.makeText(this, "profile cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CardNum.getText().toString().trim().equals("") || CardNum.getText().toString().trim().equals("")) {
            CardNum.setError("Card Number");
            return;
        } else {
            Lname = Lastname.getText().toString().trim();
            phone = Phone.getText().toString().trim();
            if (phone.equals("")) {
                phone = "";
            } else {
                phone = "91-" + phone;
            }
            doctype = DocType.getSelectedItem().toString();
            docno = DocNo.getText().toString();
            if(doctype.equals("Aadhar")&&docno.length()<12)
            {
                DocNo.setError("Aadhar Card");
                DocNo.requestFocus();
                return;
            }
            address = Address.getText().toString().trim();
            email = Email.getText().toString().trim();
            country = Country.getSelectedItem().toString();
            state = State.getSelectedItem().toString();
            city = City.getSelectedItem().toString();
            doctype = DocType.getSelectedItem().toString();
            docno = DocNo.getText().toString();
            try {
                Documentobject.put("documentType", doctype);
                Documentobject.put("description", "Documents of " + Fname);
                Documentobject.put("documentNumber", docno);
                Docresultobject.put("result", getStringImage(proofphoto));

                Emergencyobject.put("countryCode","91");
                Emergencyobject.put("contactName","");
                Emergencyobject.put("contactNumber",EmergencyContact.getText().toString().trim());

                boolean hasproofpic_2 = (proofpic_2.getDrawable() != null);
                if (hasproofpic_2) {
                    // imageView has image in it
                    JSONObject Documentobj_2 = new JSONObject();
                    JSONObject Documentobjresult_2 = new JSONObject();

                    Documentobj_2.put("documentType", doctype);
                    Documentobj_2.put("description", "Documents of " + Fname);
                    Documentobj_2.put("documentNumber", docno);
                    Documentobjresult_2.put("result", getStringImage(proofphoto_2));
                    Documentobj_2.put("documentCopy", Documentobjresult_2);
                    Documentarray.put(Documentobj_2);
                } else {

                }

                if (proofphoto.equals("")) {
                    Toast.makeText(this, "Document cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Documentobject.put("documentCopy", Docresultobject);
                Profileobject.put("result", getStringImage(profilephoto));
                if (profilephoto.equals(null)) {
                    Toast.makeText(this, "picture cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Documentarray.put(Documentobject);

                if (TransactionNo.getText().toString().trim().equals("") || TransactionNo.getText().toString().trim().equals(null)) {
                    TransactionNo.setError("Transaction Number");
                    return;
                }

                //Documentobject.put("documentCopy", Docresultobject);

                Memberobject.put("Name", Fname);
                Memberobject.put("lastName", Lname);
                Memberobject.put("sex", "Male");
                Memberobject.put("email", email);
                Memberobject.put("phoneNumber", phone);
                Memberobject.put("country", country);
                Memberobject.put("state", state);
                Memberobject.put("city", city);
                Memberobject.put("address", address);
                Memberobject.put("countryCode", "91");
                Memberobject.put("pinCode", "");
                Memberobject.put("profilePic", Profileobject);
                Memberobject.put("memberprofilePic", "");
                Memberobject.put("documents", Documentarray);
                Memberobject.put("membershipId", Planid);
                //Memberobject.put("credit", Amount.getText().toString().trim());

                Memberobject.put("creditMode", Paymentmode.getSelectedItem().toString());
                Memberobject.put("transactionNumber", TransactionNo.getText().toString().trim());
                Memberobject.put("comments", Comments.getText().toString().trim());
                Memberobject.put("cardNumber", CardNum.getText().toString().trim());
                Memberobject.put("UserID", Userid);
                Memberobject.put("createdBy", LoginId);

                Memberobject.put("age",Age.getText().toString().trim());
                Memberobject.put("emergencyContact",Emergencyobject);

                int getpostion = Plans.getSelectedItemPosition();
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Registration is in progress...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                AlertDialog.Builder Summary = new AlertDialog.Builder(Registration.this);
                Summary.setIcon(R.mipmap.logo);
                Summary.setTitle("Member summary");
                Summary.setMessage("Name: " + Fname + "\n" + "Email: " + email + "\n" + "Phone: " + phone + "\n" + "Plan: " + Planname + "\n" + "Card Number: " + CardNum.getText().toString().trim() + "\n" + "Balance: " + Totalamountofmembership.get(getpostion));
                Summary.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        sendmemberdetailstoserver();
                    }
                });
                Summary.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Firstname.requestFocus();
                        mProgressDialog.dismiss();
                    }
                });
                Summary.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*to convert image to string format*/
    public String getStringImage(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } else {
            return "";
        }
    }
/*ends*/

    private void getplans() {
        checkinternet();
        StringRequest getplanrequest = new StringRequest(Request.Method.GET, API.getplans, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject plansresponse = new JSONObject(response);
                    JSONArray data = plansresponse.getJSONArray("data");
                    MembershipNameArrayList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getid = data.getJSONObject(i);
                        String id = getid.getString("membershipId");
                        String name = getid.getString("subscriptionType");
                        Usagefee = getid.getInt("userFees");
                        int processingfee = getid.getInt("securityDeposit");
                        int securitydeposit = getid.getInt("processingFees");
                        int smartcardfees = getid.getInt("smartCardFees");

                        MembershipIDArrayList.add(id);
                        MembershipNameArrayList.add(name);
                        MembershipUsageArrayList.add(Usagefee);
                        Totalamountofmembership.add(Usagefee + processingfee + securitydeposit + smartcardfees);

                        Membernameadapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_spinner_dropdown_item, MembershipNameArrayList);
                        Membernameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Plans = (Spinner) findViewById(R.id.plans);
                        Plans.setAdapter(Membernameadapter);
                        Plans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Planname = Plans.getSelectedItem().toString();
                                Planid = MembershipIDArrayList.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                    intialize();
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
                    Toast.makeText(Registration.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Registration.this);
                    builder.setIcon(R.drawable.splashlogo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinternet();
                        }
                    });
                    builder.show();
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
        getplanrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getplanrequest);
    }

    public void sendmemberdetailstoserver() {

        JsonObjectRequest addmembers = new JsonObjectRequest(Request.Method.POST, API.addmember, Memberobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject creatememberesponse = new JSONObject(String.valueOf(response));
                    JSONObject data = creatememberesponse.getJSONObject("data");
                    mProgressDialog.dismiss();
                    Toast.makeText(Registration.this, "Member regisetered successfully", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder SuccessBuilder = new AlertDialog.Builder(Registration.this);
                    SuccessBuilder.setIcon(R.drawable.splashlogo);
                    SuccessBuilder.setTitle("Registration");
                    SuccessBuilder.setMessage("Member registered successfully");
                    SuccessBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Registration.this, Registration.class));
                            finish();
                        }
                    });
                    SuccessBuilder.setCancelable(false);
                    SuccessBuilder.show();
                    String creditbalance = data.getString("creditBalance");
                    Boolean processingfee = data.getBoolean("processingFeesDeducted");
                    if (Integer.parseInt(creditbalance) > 0 && processingfee.equals(false)) {
                        Toast.makeText(Registration.this, "Has already paid", Toast.LENGTH_SHORT).show();
                    } else {
                        // Selectplan.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
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
                    Toast.makeText(Registration.this, "Please check your connection/Server is under maintenance.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Registration.this);
                    builder.setIcon(R.drawable.splashlogo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinternet();
                        }
                    });
                    builder.show();
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
        addmembers.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addmembers);
    }

    public void sendprospectivedetailstoserver() {
        checkinternet();
        JsonObjectRequest addmembers = new JsonObjectRequest(Request.Method.POST, API.addmember, Memberobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject creatememberesponse = response;
                    JSONObject data = creatememberesponse.getJSONObject("data");
                    Prospectiveobject = data;
                    Userid = Prospectiveobject.getInt("UserID");
                    mProgressDialog.dismiss();
                    Toast.makeText(Registration.this, "Prospective Member", Toast.LENGTH_SHORT).show();
                    showotpdialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
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
                    Toast.makeText(Registration.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Registration.this);
                    builder.setIcon(R.drawable.splashlogo);
                    builder.setTitle("NO INTERNET CONNECTION!!!");
                    builder.setMessage("Your offline !!! Please check your connection and come back later.");
                    builder.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkinternet();
                        }
                    });
                    builder.show();
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
        addmembers.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addmembers);
    }


    public void setmemberdetails() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        try {
            if (!userdetails.equals(null) || !userdetails.equals("")) {
                Memberobject = new JSONObject(userdetails);
            } else {
                Memberobject = Prospectiveobject;
            }
            Fname = Memberobject.getString("Name");
            Firstname.setText(Fname);
            Firstname.setEnabled(false);
            Lname = Memberobject.getString("lastName");
            Lastname.setText(Lname);
            if(Memberobject.has("age")) {
                age = Memberobject.getString("age");
                Age.setText(age);
                Age.setEnabled(false);
            }

            if (Memberobject.has("email")) {
                email = Memberobject.getString("email");
                Email.setText(email);
                if (email.length() > 1) {
                    Email.setEnabled(false);
                } else {
                    Email.setEnabled(true);
                }
            }
            if (Memberobject.has("phoneNumber")) {
                phone = Memberobject.getString("phoneNumber");
                if (phone.equals("") || phone.equals(null)) {
                    Phone.setEnabled(true);
                } else {
                    phone = phone.substring(3);
                    Phone.setText(phone);
                    if (phone.length() > 5) {
                        Phone.setEnabled(false);
                    } else {
                        Phone.setEnabled(true);
                    }
                }
            }
            if (Memberobject.has("address")) {
                address = Memberobject.getString("address");
                Address.setText(address);
                Address.setEnabled(false);
            } else {
                Address.setText("");
            }
            String profilepics = Memberobject.getString("profilePic");
            if (Memberobject.has("UserID")) {
                Userid = Memberobject.getInt("UserID");
            } else {
                Userid = 0;
            }
            if (Memberobject.has("creditBalance")) {
                CreditBalance = Memberobject.getInt("creditBalance");
                Boolean processingfee = Memberobject.getBoolean("processingFeesDeducted");
                if (CreditBalance > 0 && processingfee.equals(false)) {
                    if (Totalamountofmembership.contains(CreditBalance)) {
                        int getindex = Totalamountofmembership.indexOf(CreditBalance);
                        Toast.makeText(this, String.valueOf(Totalamountofmembership.indexOf(CreditBalance)), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, MembershipNameArrayList.get(getindex), Toast.LENGTH_SHORT).show();
                        Planid = MembershipIDArrayList.get(getindex);
                        Plans.setSelection(getindex);
                        Plans.setEnabled(false);
                        Amount.setText(String.valueOf(CreditBalance));
                        Amount.setEnabled(false);
                    }
                } else if (CreditBalance > 0 && processingfee.equals(true)) {
                    if (MembershipUsageArrayList.contains(CreditBalance)) {
                        int getindex = MembershipUsageArrayList.indexOf(CreditBalance);
                        Toast.makeText(this, String.valueOf(MembershipUsageArrayList.indexOf(CreditBalance)), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, MembershipNameArrayList.get(getindex), Toast.LENGTH_SHORT).show();
                        Planid = MembershipIDArrayList.get(getindex);
                        Plans.setSelection(getindex);
                        Plans.setEnabled(false);
                        Amount.setText(String.valueOf(CreditBalance));
                        Amount.setEnabled(false);
                    }
                }
            }
            if (Memberobject.has("documents")) {
                JSONArray docsarray = Memberobject.getJSONArray("documents");
                JSONObject docobj = docsarray.getJSONObject(0);
                String docnos = docobj.getString("documentNumber");
                String doccopy = docobj.getString("documentCopy");
                DocNo.setText(docnos);
                DocNo.setEnabled(false);
                Takeproofpic.setEnabled(false);
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    URL url = new URL("https://www.mytrintrin.com/mytrintrin/Member/" + Userid + "/" + doccopy + ".png");
                    //URL url = new URL("http://43.251.80.79/mytrintrin/Member/" + Userid + "/" + doccopy + ".png");
                    proofphoto = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    proofpic.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
                    Takeprofilepic.setEnabled(false);
                } catch (IOException e) {
                    Log.e("TAG", e.getMessage());
                }
                if (docsarray.length() > 1) {
                    JSONObject docobj_2 = docsarray.getJSONObject(1);
                    String docnos_2 = docobj_2.getString("documentNumber");
                    String doccopy_2 = docobj_2.getString("documentCopy");
                    DocNo.setText(docnos_2);
                    DocNo.setEnabled(false);
                    Takeproofpic_2.setEnabled(false);
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                       URL url = new URL("https://www.mytrintrin.com/mytrintrin/Member/" + Userid + "/" + doccopy_2 + ".png");
                        //URL url = new URL("http://43.251.80.79/mytrintrin/Member/" + Userid + "/" + doccopy_2 + ".png");
                        proofphoto_2 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        proofpic_2.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
                        Takeproofpic_2.setEnabled(false);
                    } catch (IOException e) {
                        Log.e("TAG", e.getMessage());
                    }
                }
            }
            if (Memberobject.has("cardNum")) {
                cardno = Memberobject.getString("cardNum");
                CardNum.setText(cardno);
                CardNum.setEnabled(false);
            }
            if (Memberobject.has("membershipId")) {
                nav_menu.findItem(R.id.nav_refund).setVisible(true);
                nav_menu.findItem(R.id.nav_topup).setVisible(true);
            } else {
                nav_menu.findItem(R.id.nav_refund).setVisible(false);
                nav_menu.findItem(R.id.nav_topup).setVisible(false);
            }
            if (profilepics.equals("")) {
                Toast.makeText(this, "No Profile Pic Found", Toast.LENGTH_SHORT).show();
            } else {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    URL url = new URL("https://www.mytrintrin.com/mytrintrin/Member/" + Userid + "/" + profilepics + ".png");
                   // URL url = new URL("http://43.251.80.79/mytrintrin/Member/" + Userid + "/" + profilepics + ".png");
                    profilephoto = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    profilepic.setImageBitmap(BitmapFactory.decodeStream((InputStream) url.getContent()));
                    Takeprofilepic.setEnabled(false);
                } catch (IOException e) {
                    Log.e("TAG", e.getMessage());
                }
            }
            if (Memberobject.has("sex")) {
                String gender = Memberobject.getString("sex");
                if (gender.equals("Male")) {
                    male.setChecked(true);
                    female.setEnabled(false);
                    others.setEnabled(false);
                } else if (gender.equals("Female")) {
                    female.setChecked(true);
                    male.setEnabled(false);
                } else {
                    others.setChecked(true);
                    male.setEnabled(false);
                    female.setEnabled(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Memberobject.has("documents") && Memberobject.has("membershipId") && Memberobject.has("cardNum")) {
            AddMembers.setEnabled(false);
        }
        mProgressDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        if (profilephoto != null) {
            toSave.putParcelable("profile", profilephoto);
        }
        if (proofphoto != null) {
            toSave.putParcelable("proof", proofphoto);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_refund) {
            Intent refundintent = new Intent(this, Refund.class);
            refundintent.putExtra("Name", Fname + " " + Lname);
            refundintent.putExtra("userid", Userid);
            startActivity(refundintent);
            finish();
        } else if (id == R.id.nav_topup) {
            Intent topupintent = new Intent(this, Topup.class);
            topupintent.putExtra("Name", Fname + " " + Lname);
            topupintent.putExtra("userid", Userid);
            startActivity(topupintent);
            finish();
        } else if (id == R.id.nav_tickets) {
            Intent ticketsintent = new Intent(this, Tickets_RC.class);
            ticketsintent.putExtra("Name", Fname + " " + Lname);
            ticketsintent.putExtra("userid", Userid);
            startActivity(ticketsintent);
            finish();
        } else if (id == R.id.nav_rides) {
            Intent ridesintent = new Intent(this, Rides.class);
            ridesintent.putExtra("userid", Userid);
            startActivity(ridesintent);
            finish();
        } else if (id == R.id.nav_payments) {
            Intent paymentintent = new Intent(this, Payment_History.class);
            paymentintent.putExtra("userid", Userid);
            startActivity(paymentintent);
            finish();
        }
        return true;
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject responsefromserver = new JSONObject(responseBody);
            String message = responsefromserver.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            JSONObject data = responsefromserver.getJSONObject("data");
            Userid = data.getInt("UserId");
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    public void showotpdialog() {

        AlertDialog.Builder OTPbuilder = new AlertDialog.Builder(Registration.this);
        OTPbuilder.setIcon(R.mipmap.logo);
        OTPbuilder.setTitle("OTP");
        LayoutInflater passwordinflate = LayoutInflater.from(Registration.this);
        View otpView = passwordinflate.inflate(R.layout.otplayout, null);
        OTPbuilder.setView(otpView);
        OtpNum = (EditText) otpView.findViewById(R.id.etotpno);
        OTPbuilder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String EnteredOTP = OtpNum.getText().toString();
                if (EnteredOTP.length() < 6) {
                    Toast.makeText(Registration.this, "OTP must be 6 Numbers", Toast.LENGTH_LONG).show();
                    showotpdialog();
                    return;
                }
                StringRequest verifyotprequest = new StringRequest(Request.Method.POST, API.verifyotp, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject verifiedmemberresponse = new JSONObject(response);
                            JSONObject verifieddata = verifiedmemberresponse.getJSONObject("data");
                            String isverified = verifieddata.getString("otpVerified");
                            Userid = verifieddata.getInt("UserID");
                            if (isverified.equals("true")) {
                                Toast.makeText(Registration.this, "OTP Verified.Please Continue.", Toast.LENGTH_LONG).show();
                                Selectplan.setVisibility(View.VISIBLE);
                                Payment.setVisibility(View.VISIBLE);
                                Smartcard.setVisibility(View.VISIBLE);
                                AddMembers.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Addmember(view);
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
                        if (error.networkResponse != null) {
                            parseVolleyError(error);
                            showotpdialog();
                            return;
                        }
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
                            Toast.makeText(Registration.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    Registration.this);
                            builder.setIcon(R.drawable.splashlogo);
                            builder.setTitle("NO INTERNET CONNECTION!!!");
                            builder.setMessage("Your offline !!! Please check your connection and come back later.");
                            builder.setPositiveButton("Exit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            finish();
                                        }
                                    });
                            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkinternet();
                                }
                            });
                            builder.show();
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
                        params.put("UserID", String.valueOf(Userid));
                        params.put("otp", EnteredOTP);
                        return params;
                    }
                };
                verifyotprequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(verifyotprequest);
            }
        });
        OTPbuilder.setNegativeButton("Resend OTP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                StringRequest resendotprequest = new StringRequest(Request.Method.POST, API.resendotp, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showotpdialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            parseVolleyError(error);
                            showotpdialog();
                            return;
                        }
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
                            Toast.makeText(Registration.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    Registration.this);
                            builder.setIcon(R.drawable.splashlogo);
                            builder.setTitle("NO INTERNET CONNECTION!!!");
                            builder.setMessage("Your offline !!! Please check your connection and come back later.");
                            builder.setPositiveButton("Exit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            finish();
                                        }
                                    });
                            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkinternet();
                                }
                            });
                            builder.show();
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
                        params.put("UserID", String.valueOf(Userid));
                        params.put("phoneNumber", Phone.getText().toString());
                        return params;
                    }
                };
                resendotprequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(resendotprequest);
            }
        });
        OTPbuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        OTPbuilder.setCancelable(false);
        OTPbuilder.show();
    }

    public void countrycodecheck() {
        CountryCode = (Spinner) findViewById(R.id.countrycodespinner);
        if (CountryCode.getSelectedItem().toString().equals("India (+91)")) {
            Selectplan.setVisibility(View.GONE);
            Payment.setVisibility(View.GONE);
            Smartcard.setVisibility(View.GONE);
            AddMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkdetailsandgenerateotp();
                }
            });
        } else {
            Selectplan.setVisibility(View.VISIBLE);
            Payment.setVisibility(View.VISIBLE);
            Smartcard.setVisibility(View.VISIBLE);
            AddMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Addmember(view);
                }
            });
        }
    }

    public void documentcheck() {
        if (DocType.getSelectedItem().toString().equals("Aadhar")) {
            Selectplan.setVisibility(View.VISIBLE);
            Payment.setVisibility(View.VISIBLE);
            Smartcard.setVisibility(View.VISIBLE);
            AddMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Addmember(view);
                }
            });
        } else {
            Selectplan.setVisibility(View.GONE);
            Payment.setVisibility(View.GONE);
            Smartcard.setVisibility(View.GONE);
            AddMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkdetailsandgenerateotp();
                }
            });
        }
    }
}
