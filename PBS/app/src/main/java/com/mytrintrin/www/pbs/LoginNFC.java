package com.mytrintrin.www.pbs;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class LoginNFC extends AppCompatActivity {

    Toolbar loginNFCtoolbar;
    NfcAdapter nfcAdapter;
    public  String Empid;
    public static SharedPreferences loginnfcid;
    public static SharedPreferences.Editor loginnfceditor;

    EditText password_nfc;
    String loginnfcpassword;
    CheckBox showpassword_nfc;

    public static final int RequestPermissionCode = 1;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_nfc);
        loginNFCtoolbar = (Toolbar) findViewById(R.id.loginnfcToolbar);
        loginNFCtoolbar.setTitle("Login");
        loginnfcid = getSharedPreferences("LoginPref", MODE_PRIVATE);
        checknfc();
        onpermision();

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

        ActivityCompat.requestPermissions(LoginNFC.this, new String[]
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
                    //  boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                  // boolean ReadRecordaudioPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;


                    if (CameraPermission&& LocationPermission) {

                        Toast.makeText(LoginNFC.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginNFC.this,LoginNFC.class));
                    } else {
                        Toast.makeText(LoginNFC.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        //int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;


    }

    /*Requesting permission ends*/



    private void checknfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("NFC");
            builder.setMessage("NFC is not available in the mobile");
            builder.setPositiveButton("Login using mail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(LoginNFC.this, Login.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (nfcAdapter.isEnabled()) {
                onNewIntent(getIntent());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.logo);
                builder.setTitle("NFC");
                builder.setMessage("NFC is not enable in the mobile?");
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                });
                builder.setNegativeButton("Login Using mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginNFC.this,Login.class));
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NFC Tag", "Detected");
        resolveIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, LoginNFC.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(LoginNFC.this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.disableForegroundDispatch(this);
        Empid = "";

    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                //msgs = new NdefMessage[] { msg };
            }
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        // sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        //sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        int checklength = sb.toString().trim().length();
        if (checklength == 11) {
            String tagid = sb.reverse().toString();
            String finaltagid = "";
            for (int i = 0; i < tagid.length(); i = i + 3) {

                String s1 = tagid.substring(i, i + 1);
                String s2 = tagid.substring(i + 1, i + 2);
                int len1 = s1.length();
                s1 = s1 + s2;
                s2 = s1.substring(0, len1);
                s1 = s1.substring(len1);
                finaltagid = finaltagid + s1 + s2;
            }
            Empid = finaltagid + "00000000";
            Toast.makeText(this, "Your id is" + Empid, Toast.LENGTH_LONG).show();


            LayoutInflater passwordinflate = LayoutInflater.from(this);
            View passwordView = passwordinflate.inflate(R.layout.loginpassword_nfc, null);
            AlertDialog.Builder passwordbuilder = new AlertDialog.Builder(this);
            passwordbuilder.setView(passwordView);
            passwordbuilder.setTitle("Password");
            passwordbuilder.setIcon(R.drawable.logo);
            password_nfc = (EditText) passwordView.findViewById(R.id.etpassword_nfc);
            showpassword_nfc = (CheckBox) passwordView.findViewById(R.id.loginnfcshowpassword);

            passwordbuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    loginnfcpassword = password_nfc.getText().toString().trim();

                    if(loginnfcpassword.equals("")||loginnfcpassword.equals(null))
                    {
                        password_nfc.setError("Please Enter Password");
                    }
                    else{
                        checkinternet();
                        mProgressDialog = new ProgressDialog(LoginNFC.this);
                        mProgressDialog.setMessage("Please wait...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.show();
                        StringRequest loginnfcrequest = new StringRequest(Request.Method.POST, API.loginnfcapi, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject responsefromserver = new JSONObject(response);
                                    Boolean error = responsefromserver.getBoolean("error");
                                    String message = responsefromserver.getString("message");
                                    JSONObject data = responsefromserver.getJSONObject("data");
                                    String userid = data.getString("id");
                                    String role = data.getString("role");
                                    loginnfceditor = loginnfcid.edit();
                                    loginnfceditor.putString("User-id", userid);
                                    loginnfceditor.commit();
                                    Log.d("role", role);
                                    Log.d("User-id", userid);
                                    mProgressDialog.dismiss();
                                    if (role.equals("registration-employee")) {
                                        Toast.makeText(LoginNFC.this, role, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginNFC.this, GetStarted.class));
                                    }
                                    else if (role.equals("redistribution-employee"))
                                    {
                                        Toast.makeText(LoginNFC.this, role, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginNFC.this, Redistribution.class));

                                    }
                                    else if (role.equals("maintenancecentre-employee"))
                                    {
                                        Toast.makeText(LoginNFC.this, role, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginNFC.this,Maintainance_Centre.class));
                                    }

                                    else {
                                        android.support.v7.app.AlertDialog.Builder LoginBuilder = new android.support.v7.app.AlertDialog.Builder(LoginNFC.this);
                                        LoginBuilder.setIcon(R.drawable.logo);
                                        LoginBuilder.setTitle("Authorization Fail!");
                                        LoginBuilder.setMessage("Invalid user??");
                                        LoginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(LoginNFC.this, "Try to login with valid credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        LoginBuilder.show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                if (error instanceof ServerError) {
                                    Toast.makeText(LoginNFC.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                    Log.d("Error", String.valueOf(error instanceof ServerError));
                                    android.support.v7.app.AlertDialog.Builder LoginBuilder = new android.support.v7.app.AlertDialog.Builder(LoginNFC.this);
                                    LoginBuilder.setIcon(R.drawable.logo);
                                    LoginBuilder.setTitle("Invalid user");
                                    LoginBuilder.setMessage("Authorization Fail!");
                                    LoginBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(LoginNFC.this, "Try to login with valid credentials", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    LoginBuilder.show();
                                    error.printStackTrace();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(LoginNFC.this, "Authentication Error", Toast.LENGTH_LONG).show();
                                    Log.d("Error", "Authentication Error");
                                    error.printStackTrace();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(LoginNFC.this, "Parse Error", Toast.LENGTH_LONG).show();
                                    Log.d("Error", "Parse Error");
                                    error.printStackTrace();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(LoginNFC.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                                    Log.d("Error", "Network Error");
                                    error.printStackTrace();
                                } else if (error instanceof TimeoutError) {
                                    Toast.makeText(LoginNFC.this, "Timeout Error", Toast.LENGTH_LONG).show();
                                    Log.d("Error", "Timeout Error");
                                    error.printStackTrace();
                                } else if (error instanceof NoConnectionError) {
                                    Toast.makeText(LoginNFC.this, "No Connection Error", Toast.LENGTH_LONG).show();
                                    Log.d("Error", "No Connection Error");
                                    error.printStackTrace();
                                } else {
                                    Toast.makeText(LoginNFC.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                                params.put("username",Empid.toUpperCase());
                                params.put("password",loginnfcpassword);
                                return params;
                            }

                        };
                        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(loginnfcrequest);
                    }

                }
            });

            passwordbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Toast.makeText(LoginNFC.this,"Login cancelled",Toast.LENGTH_LONG).show();

                }
            });

            passwordbuilder.show();


        } else {
            String bicyletagid = sb.toString();
            String finalbicidbicyletagid = bicyletagid.replace(" ", "");
            //  cycleid = finalbicidbicyletagid;
            //Toast.makeText(this,"Your cycle id is "+cycleid,Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Invalid card detected", Toast.LENGTH_LONG).show();
            // textView.setText(finalbicidbicyletagid);
            AlertDialog.Builder Invalid = new AlertDialog.Builder(this);
            Invalid.setIcon(R.drawable.logo);
            Invalid.setTitle("Invalid Card!!!");
            Invalid.setMessage("Try to login with valid card");
            Invalid.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            Invalid.show();
        }
        return sb.toString();
    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    LoginNFC.this);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }

    /*ends*/

    public void showpassword(View view)
    {
        if(showpassword_nfc.isChecked())
        {
            password_nfc.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            password_nfc.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }



    public void gotologin(View view) {
        startActivity(new Intent(this, Login.class));
    }
}
