package com.mytrintrin.www.pbs_trintrin;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Addcycle extends AppCompatActivity {

    Toolbar AddCycletoolbar;
    NfcAdapter nfcAdapter;
    EditText BicycleNum,BicycleRFID,MCcardID;
    String MCCycleRFID,MCCard;
    ArrayList<String> addcyclerfidlist = new ArrayList<String>();
    ArrayList<String> addcyclenumlist = new ArrayList<String>();
    ArrayList<EditText> Allcyclenum,Allcyclerfid;
    ArrayList<ImageView> Allcycleclear;
    ArrayList<LinearLayout> Alldetailslist;

    int totalcyclerfid,totalcyclenum,a=0;
    LinearLayout CycleDetails,CycleErrorDetails,ErrorLayout;
    LinearLayout alldetails,cardnumlayout;
    public static ArrayList<String> FleetIDArrayList = new ArrayList<String>();
    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    public static ArrayList<String> StationNameArrayList = new ArrayList<String>();
    public ArrayAdapter<String> Add_Cycle_Fleet_adapter;

    Spinner AddCycleSpinner,FleetAddCycleSpinner;
    String addcyclefleet,addcyclefleetid,stationname_addcycle,loginuserid;

    private ProgressDialog mProgressDialog;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    JSONArray Fleetarray;
    JSONObject Fleetobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcycle);
        AddCycletoolbar = (Toolbar) findViewById(R.id.addcycletoolbar);
        AddCycletoolbar.setTitle("Cycle");
        setSupportActionBar(AddCycletoolbar);
        BicycleNum = (EditText) findViewById(R.id.mccyclenumber);
        BicycleRFID = (EditText) findViewById(R.id.mccyclerfid);
        BicycleRFID.setEnabled(false);

        CycleDetails = (LinearLayout) findViewById(R.id.addcycledetailslayout);
        CycleErrorDetails = (LinearLayout) findViewById(R.id.addcycleerrorlayout);
        ErrorLayout = (LinearLayout) findViewById(R.id.errorlayout);

        Allcyclenum = new ArrayList<EditText>();
        Allcyclerfid = new ArrayList<EditText>();
        Allcycleclear = new ArrayList<ImageView>();

        CycleDetails = (LinearLayout) findViewById(R.id.addcycledetailslayout);

        Allcyclenum = new ArrayList<EditText>();
        Allcyclerfid = new ArrayList<EditText>();
        Allcycleclear = new ArrayList<ImageView>();

        Alldetailslist = new ArrayList<LinearLayout>();
        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends
        checknfc();
        checkinternet();
        getFleetdetails();

        AddCycleSpinner = (Spinner) findViewById(R.id.addcyclespinner);
        FleetAddCycleSpinner = (Spinner) findViewById(R.id.fleetaddcyclespinner);

        checkoutnfcstations();

        BicycleNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==0)
                {
                    Toast.makeText(Addcycle.this, "Bicycle cannot be empty", Toast.LENGTH_SHORT).show();
                }
                if(editable.length()<3)
                {
                    Toast.makeText(Addcycle.this, "Bicyle number should have 3 numbers", Toast.LENGTH_SHORT).show();
                }
                if(editable.length()==3)
                {
                    Toast.makeText(Addcycle.this, "Can be added to array", Toast.LENGTH_SHORT).show();
                    addcyclenumlist.add(0, String.valueOf(editable));
                }
            }
        });

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Addcycle.this);
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


    public void getFleetdetails() {
        StringRequest fleetrequest = new StringRequest(Request.Method.GET, API.fleetidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject fleetresponsefronserver = new JSONObject(response);
                    JSONArray fleetdataarray = fleetresponsefronserver.getJSONArray("data");
                    Fleetarray = fleetdataarray;
                    fleetdetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Addcycle.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Addcycle.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Addcycle.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Addcycle.this, "Cannot connect to Internet...Please check your connection!/Server is under maintainence...", Toast.LENGTH_LONG).show();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Addcycle.this);
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
                    Toast.makeText(Addcycle.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Addcycle.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Addcycle.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        fleetrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(this).addtorequestqueue(fleetrequest);
    }

    public void  fleetdetails()
    {
        FleetNameArrayList.clear();
        for (int i = 0; i < Fleetarray.length(); i++) {
            try {
                Fleetobject = Fleetarray.getJSONObject(i);
                String id = Fleetobject.getString("_id");
                String name = Fleetobject.getString("Name");
                Log.d("ID", id);
                FleetIDArrayList.add(id);
                FleetNameArrayList.add(name);
                JSONObject Stationname = Fleetobject.getJSONObject("StationId");
                String sname =  Stationname.getString("name");
                StationNameArrayList.add(sname);
                Log.d("Array ID", String.valueOf(FleetIDArrayList));
                Log.d("Station Name", sname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checknfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.logo);
            builder.setTitle("NFC");
            builder.setMessage("NFC is not available in the mobile");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                builder.setIcon(R.mipmap.logo);
                builder.setTitle("NFC");
                builder.setMessage("NFC is not enable in the mobile?");
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        Intent intent = new Intent(this, Addcycle.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(Addcycle.this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.disableForegroundDispatch(this);
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
            }
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");

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
        int checklength=sb.toString().trim().length();
        if(checklength==11) {
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
            MCCard=finaltagid+"00000000";
            Toast.makeText(this,"Your id is"+MCCard,Toast.LENGTH_LONG).show();
            MCcardID.setText(MCCard);
        }
        else {
            String bicyletagid = sb.toString();
            String finalbicidbicyletagid = bicyletagid.replace(" ", "");
            MCCycleRFID = finalbicidbicyletagid.toUpperCase();
            Toast.makeText(this, "Your cycle id is" + MCCycleRFID, Toast.LENGTH_LONG).show();

            if(addcyclerfidlist.contains(MCCycleRFID))
            {
                Toast.makeText(this, "Already scaned " + MCCycleRFID, Toast.LENGTH_LONG).show();

            }
            else
            {
                addcyclerfidlist.add(MCCycleRFID);
                totalcyclerfid = addcyclerfidlist.size();
                BicycleRFID.setText(addcyclerfidlist.get(0));
                if(totalcyclerfid>1)
                {
                    alldetails = new LinearLayout(this);
                    alldetails.setId(a);
                    alldetails.setOrientation(LinearLayout.VERTICAL);
                    cardnumlayout = new LinearLayout(this);
                    cardnumlayout.setId(a);
                    cardnumlayout.setOrientation(LinearLayout.HORIZONTAL);

                    final EditText dynamiccardnum = new EditText(this);
                    LinearLayout.LayoutParams cardnumparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dynamiccardnum.setLayoutParams(cardnumparams);
                    cardnumparams.weight = 1;
                    cardnumparams.setMargins(0, 0, 0, 15);
                    dynamiccardnum.setHint("Bicycle Number");
                    dynamiccardnum.setBackgroundResource(R.drawable.input_outline);
                    dynamiccardnum.setId(a);
                    dynamiccardnum.setPadding(30,30,30,30);
                    dynamiccardnum.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dynamiccardnum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    Allcyclenum.add(dynamiccardnum);

                    dynamiccardnum.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {

                            if(editable.length()==0)
                            {
                                Toast.makeText(Addcycle.this, "Bicycle cannot be empty", Toast.LENGTH_SHORT).show();
                            }

                            if(editable.length()<3)
                            {
                                Toast.makeText(Addcycle.this, "Bicyle number should have 3 numbers", Toast.LENGTH_SHORT).show();
                            }

                            if(editable.length()==3)
                            {
                                if(addcyclenumlist.contains(String.valueOf(editable)))
                                {
                                    Toast.makeText(Addcycle.this, "You have already entered the same number", Toast.LENGTH_SHORT).show();
                                    dynamiccardnum.setText("");
                                }
                                else {
                                    addcyclenumlist.add(a, String.valueOf(editable));
                                }
                            }
                        }
                    });

                    ImageView dynamicclear = new ImageView(this);
                    LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
                    dynamicclear.setLayoutParams(ivparams);
                    dynamicclear.setImageResource(R.drawable.clearbutton);
                    ivparams.weight = 1;
                    dynamicclear.setId(a);
                    dynamicclear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            int toberemovedid =Allcycleclear.indexOf(view);
                            Toast.makeText(Addcycle.this, "Hello sir" + toberemovedid, Toast.LENGTH_SHORT).show();
                            Allcycleclear.remove(toberemovedid);
                            Allcyclenum.remove(toberemovedid);
                            Allcyclerfid.remove(toberemovedid);
                            addcyclerfidlist.remove(toberemovedid+1);
                            Alldetailslist.remove(toberemovedid);
                            Toast.makeText(Addcycle.this, "Current cycle clear "+Allcycleclear.size() +"current card num "+Allcyclenum.size() + "current card rfid "+Allcyclerfid.size(), Toast.LENGTH_SHORT).show();

                            View Remove =CycleDetails.getChildAt(toberemovedid);
                            CycleDetails.removeView(Remove);

                        }
                    });

                    Allcycleclear.add(dynamicclear);
                    cardnumlayout.addView(dynamiccardnum);
                    cardnumlayout.addView(dynamicclear);

                    EditText dynamiccyclerfid = new EditText(this);
                    LinearLayout.LayoutParams cyclerfidparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dynamiccyclerfid.setLayoutParams(cyclerfidparams);
                    dynamiccyclerfid.setHint("Cycle RFID");
                    dynamiccyclerfid.setBackgroundResource(R.drawable.input_outline);
                    dynamiccyclerfid.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    dynamiccyclerfid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    dynamiccyclerfid.setTypeface(null, Typeface.BOLD);
                    dynamiccyclerfid.setPadding(30, 30, 30, 30);
                    cyclerfidparams.setMargins(0, 0, 0, 15);
                    dynamiccyclerfid.setId(a);
                    dynamiccyclerfid.setEnabled(false);
                    dynamiccyclerfid.setText(MCCycleRFID);
                    Allcyclerfid.add(dynamiccyclerfid);

                    alldetails.addView(cardnumlayout);
                    alldetails.addView(dynamiccyclerfid);
                    Alldetailslist.add(alldetails);

                    CycleDetails.addView(alldetails);

                    Toast.makeText(this, "All Cycle Clear "+Allcycleclear.size() +"All card num "+Allcyclenum.size()+ "All Cycle RFIT "+Allcyclerfid.size(), Toast.LENGTH_SHORT).show();
                    a++;
                }
            }
        }
        return sb.toString();
    }

    public  void addcycletoserver(View view)
    {
        checkinternet();
        if (BicycleNum.getText().toString().trim().equals("") || BicycleNum.getText().toString().trim().equals(null)) {
            BicycleNum.setError("Bicycle Number");
            return;
        }
        if (BicycleRFID.getText().toString().trim().equals("") || BicycleRFID.getText().toString().trim().equals(null)) {
            BicycleRFID.setError("Bicycle RFID");
            return;
        }
        if (AddCycleSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select Station", Toast.LENGTH_SHORT).show();
            return;
        }
        totalcyclenum = Allcyclenum.size();
        totalcyclenum = totalcyclenum + 1;
        addcyclenumlist.add(0, BicycleNum.getText().toString().trim());
        for (int i = 1; i < totalcyclenum; i++) {
            addcyclenumlist.add(i, Allcyclenum.get(i - 1).getText().toString());
        }
        for (int i = 0; i < totalcyclenum; i++) {
            Toast.makeText(this, "Num " + addcyclenumlist.get(i) + "Rfid " + addcyclerfidlist.get(i), Toast.LENGTH_SHORT).show();
            senddetailstoserver(addcyclenumlist.get(i), addcyclerfidlist.get(i));

        }
        CycleDetails.removeAllViews();
        BicycleNum.setText("");
        BicycleRFID.setText("");
        BicycleRFID.setEnabled(false);
        fleetdetails();
        AddCycleSpinner.setSelection(0);
        addcyclerfidlist.clear();
        a=0;
        addcyclenumlist.clear();
        Allcyclenum.clear();
        Allcyclerfid.clear();
    }

    public void senddetailstoserver(final String cyclenum, final String cyclerfid)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding Cycle...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        StringRequest addcycle = new StringRequest(Request.Method.POST, API.addtofleet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                Toast.makeText(Addcycle.this, response, Toast.LENGTH_LONG).show();
                CycleErrorDetails.setVisibility(View.GONE);
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
                    Toast.makeText(Addcycle.this, "Server is under maintenance.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Addcycle.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Addcycle.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Addcycle.this, "Please check your connection-.", Toast.LENGTH_LONG).show();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            Addcycle.this);
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
                    Toast.makeText(Addcycle.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Addcycle.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Addcycle.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

                params.put("vehicleNumber",stationname_addcycle+"-"+cyclenum);
                params.put("vehicleRFID", cyclerfid);
                params.put("fleetId",addcyclefleetid);
                params.put("createdBy",loginuserid);
                return params;
            }
        };
        addcycle.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addcycle);
        mProgressDialog.dismiss();
    }

    private void checkoutnfcstations() {

        List<String> categories = new ArrayList<String>();
        categories.add("Select Station");
        categories.add("Fleet");
        ArrayAdapter<String> dockingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dockingadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AddCycleSpinner.setAdapter(dockingadapter);
        AddCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        FleetAddCycleSpinner.setVisibility(View.GONE);
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                        FleetAddCycleSpinner.setVisibility(View.VISIBLE);

                        Add_Cycle_Fleet_adapter = new ArrayAdapter<String>(Addcycle.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
                        Add_Cycle_Fleet_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        FleetAddCycleSpinner.setAdapter(Add_Cycle_Fleet_adapter);
                        FleetAddCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                addcyclefleet = FleetAddCycleSpinner.getSelectedItem().toString();
                                addcyclefleetid = FleetIDArrayList.get(position);
                                stationname_addcycle = StationNameArrayList.get(position);
                                Log.d("Fleet port id", addcyclefleetid);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void parseVolleyError(VolleyError error) {
        try {
            CycleErrorDetails.setVisibility(View.VISIBLE);
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            LinearLayout addcycleerror = new LinearLayout(Addcycle.this);
            LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            errorparams.weight = 1;
            addcycleerror.setOrientation(LinearLayout.HORIZONTAL);
            TextView errorcycleid = new TextView(Addcycle.this);
            errorcycleid.setLayoutParams(errorparams);
            errorcycleid.setText(message);
            errorcycleid.setTextSize(16);
            errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            TextView errormess = new TextView(Addcycle.this);
            errormess.setText("Duplicate Entry");
            errormess.setTextSize(16);
            errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            errormess.setLayoutParams(errorparams);
            addcycleerror.addView(errorcycleid);
            addcycleerror.addView(errormess);
            ErrorLayout.addView(addcycleerror);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
