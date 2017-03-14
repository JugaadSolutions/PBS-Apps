package com.mytrintrin.www.pbs;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Checkoutnfc extends AppCompatActivity {

    Toolbar checkoutnfctoolbar;
    NfcAdapter nfcAdapter;


    public static ArrayList<String> FleetIDArrayList = new ArrayList<String>();
    public static ArrayList<String> FleetNameArrayList = new ArrayList<String>();
    public static ArrayList<String> RVIDArrayList = new ArrayList<String>();
    public static ArrayList<String> RVNameArrayList = new ArrayList<String>();
    public static ArrayList<String> MCIDArrayList = new ArrayList<String>();
    public static ArrayList<String> MCNameArrayList = new ArrayList<String>();
    public static ArrayList<String> HANameArrayList = new ArrayList<String>();
    public static ArrayList<String> HAIDArrayList = new ArrayList<String>();

    public static Spinner Checkoutstation_NFC,  HA_NFC_CO_spinner, RV_NFC_CO_spinner, MC_NFC_CO_spinner,Fleet_NFC_CO_spinner;
    public ArrayAdapter<String> HA_NFC_CO_adapter,RV_NFC_CO_adapter,MC_NFC_CO_adapter,Fleet_NFC_CO_adapter;

    String HA_NFC_CO_portid,RV_NFC_CO_portid,MC_NFC_CO_portid,Fleet_NFC_CO_portid;
    String Holdingarea_NFC_CO,Restribution_NFC_CO,Maintenance_NFC_CO,Fleet_NFC_CO;

    String checkoutnfccycleid;
    String checkoutnfccardid;

    String[] strings;

    EditText nfccycleid,nfccardid;

    ArrayList<String> checkoutcycleid = new ArrayList<String>();

    List<EditText> Allcycleid;

    String[] checkoutbicycleid;

    public static int a = 100,j=0;

    LinearLayout Bicyleidnfclayout,ll,CheckoutnfcErrorLayout;

    ImageView imageView;

    int cycleidcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutnfc);
        checkoutnfctoolbar = (Toolbar) findViewById(R.id.checkoutnfctoolbar);
        checkoutnfctoolbar.setTitle("Check Out(NFC)");
        checkoutnfctoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(checkoutnfctoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nfccycleid = (EditText) findViewById(R.id.checkoutnfccycleid);
        nfccycleid.setEnabled(false);
        nfccardid = (EditText) findViewById(R.id.checkoutnfcccardid);

        Checkoutstation_NFC = (Spinner) findViewById(R.id.checkoutnfcstationspinner);
        HA_NFC_CO_spinner = (Spinner) findViewById(R.id.HA_NFC_CO_spinner);
        RV_NFC_CO_spinner = (Spinner) findViewById(R.id.RV_NFC_CO_spinner);
        MC_NFC_CO_spinner = (Spinner) findViewById(R.id.MC_NFC_CO_spinner);
        Fleet_NFC_CO_spinner = (Spinner) findViewById(R.id.Fleet_NFC_CO_spinner);

        Bicyleidnfclayout = (LinearLayout) findViewById(R.id.checkoutnfcbicylelayout);

        CheckoutnfcErrorLayout = (LinearLayout) findViewById(R.id.checkoutnfcerrorlayout);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        getFleetdetails();
        getRestributiondetails();
        getMCdetails();
        getHAdetails();

        if(nfcAdapter.isEnabled())
        {

            onNewIntent(getIntent());
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        Allcycleid = new ArrayList<EditText>();

        checkinternet();
        checkoutnfcstations();

    }


    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    Checkoutnfc.this);
            builder.setIcon(R.drawable.ic_wifi);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                            //startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
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
                    for (int i = 0; i < fleetdataarray.length(); i++) {
                        JSONObject getid = fleetdataarray.getJSONObject(i);
                        String id = getid.getString("_id");
                        String name = getid.getString("Name");
                        Log.d("ID", id);
                        FleetIDArrayList.add(id);
                        FleetNameArrayList.add(name);
                        Log.d("Array ID", String.valueOf(FleetIDArrayList));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    //Toast.makeText(Splash.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Toast.makeText(Checkoutnfc.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(fleetrequest);
    }



    public void getRestributiondetails() {
        StringRequest restributionrequest = new StringRequest(Request.Method.GET, API.RVidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject restributionfromserver = new JSONObject(response);
                    JSONArray restributiondataarray = restributionfromserver.getJSONArray("data");
                    for (int i = 0; i < restributiondataarray.length(); i++) {
                        JSONObject portid = restributiondataarray.getJSONObject(i);
                        String id = portid.getString("_id");
                        String name = portid.getString("Name");
                        Log.d("RV", id);
                        RVIDArrayList.add(id);
                        Log.d("Array ID", String.valueOf(RVIDArrayList));
                        RVNameArrayList.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutnfc.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(restributionrequest);
    }

    public void getMCdetails() {
        StringRequest maintenancerequest = new StringRequest(Request.Method.GET, API.MCidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject maintenancefromserver = new JSONObject(response);
                    JSONArray maintenancedataarray = maintenancefromserver.getJSONArray("data");
                    for (int i = 0; i < maintenancedataarray.length(); i++) {
                        JSONObject maintenanceid = maintenancedataarray.getJSONObject(i);
                        String id = maintenanceid.getString("_id");
                        String name = maintenanceid.getString("Name");
                        Log.d("MC ID", id);
                        MCIDArrayList.add(id);
                        MCNameArrayList.add(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutnfc.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(maintenancerequest);
    }


    public void getHAdetails()

    {
        StringRequest holdingarearequest = new StringRequest(Request.Method.GET, API.HAidurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject holdingareafromserver = new JSONObject(response);
                    JSONArray holdingareadataarray = holdingareafromserver.getJSONArray("data");
                    for (int i = 0; i < holdingareadataarray.length(); i++) {
                        JSONObject holdingareaid = holdingareadataarray.getJSONObject(i);
                        String id = holdingareaid.getString("_id");
                        String name = holdingareaid.getString("Name");
                        Log.d("HA ID", id);
                        HANameArrayList.add(name);
                        HAIDArrayList.add(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutnfc.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        PBSSingleton.getInstance(this).addtorequestqueue(holdingarearequest);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Toast.makeText(this,"NFC Tag Detected",Toast.LENGTH_SHORT).show();
        Log.d("NFC Tag","Detected");
        resolveIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, Checkoutnfc.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if(nfcAdapter==null)
        {
            return;
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter==null)
        {
            return;
        }
        nfcAdapter.disableForegroundDispatch(Checkoutnfc.this);
        if (HA_NFC_CO_adapter==null||RV_NFC_CO_adapter==null||MC_NFC_CO_adapter==null||Fleet_NFC_CO_adapter==null)
        {
            HA_NFC_CO_adapter=RV_NFC_CO_adapter=MC_NFC_CO_adapter=Fleet_NFC_CO_adapter=null;
        }
        else {
            HA_NFC_CO_adapter.clear();
            RV_NFC_CO_adapter.clear();
            MC_NFC_CO_adapter.clear();
            Fleet_NFC_CO_adapter.clear();
        }
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
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
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
            checkoutnfccardid=finaltagid+"00000000";
            Toast.makeText(this,"Your id is"+checkoutnfccardid,Toast.LENGTH_LONG).show();
            nfccardid.setText(checkoutnfccardid);
        }
        else {
            String bicyletagid= sb.toString();
            String finalbicidbicyletagid= bicyletagid.replace(" ","");
            checkoutnfccycleid = finalbicidbicyletagid.toUpperCase();
            Toast.makeText(this,"Your cycle id is"+checkoutnfccycleid,Toast.LENGTH_LONG).show();
            nfccycleid.setText(checkoutnfccycleid);
            checkoutcycleid.add(checkoutnfccycleid);
            int total =checkoutcycleid.size();
            if(total>1)
            {

                ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);

                EditText editText = new EditText(this);
                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                etparams.setMargins(0, 0, 0, 15);
                editText.setLayoutParams(etparams);
                editText.setHint("Bicycle ID");
                editText.setBackgroundResource(R.drawable.input_outline);
                editText.setPadding(30, 30, 30, 30);
                editText.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                editText.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
                editText.setTypeface(null, Typeface.BOLD);
                etparams.weight = 1;
                editText.setId(a + 1);
                editText.setEnabled(false);
                a++;
                Log.d("et id", String.valueOf(a));
                editText.setText(checkoutnfccycleid);
                Allcycleid.add(editText);
                cycleidcount = Allcycleid.size();

                Log.d("total", String.valueOf(Allcycleid.size()));

                imageView = new ImageView(this);
                LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
                imageView.setLayoutParams(ivparams);
                imageView.setImageResource(R.drawable.clearbutton);
                ivparams.weight = 1;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewGroup) ll.getParent()).removeView(ll);
                        cycleidcount--;
                        Toast.makeText(Checkoutnfc.this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();
                    }
                });
                ll.addView(editText);
                ll.addView(imageView);

                Bicyleidnfclayout.addView(ll);

            }
            Toast.makeText(this, String.valueOf(total), Toast.LENGTH_SHORT).show();

        }
        return sb.toString();
    }



    private void checkoutnfcstations() {

        List<String> categories = new ArrayList<String>();
        categories.add("Select Station");
        categories.add("Fleet");
        categories.add("Holding Area");
        categories.add("Redistrubution Vehicle");
        categories.add("Maintainence Centre");
        ArrayAdapter<String> dockingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dockingadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Checkoutstation_NFC.setAdapter(dockingadapter);
        Checkoutstation_NFC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RV_NFC_CO_spinner.setVisibility(View.GONE);
                        MC_NFC_CO_spinner.setVisibility(View.GONE);
                        HA_NFC_CO_spinner.setVisibility(View.GONE);
                        Fleet_NFC_CO_spinner.setVisibility(View.GONE);
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkoutPortid=TransactionAPI.fleetid;
                        Fleet_NFC_CO_spinner.setVisibility(View.VISIBLE);
                        // HAportid=RVportid=MCportid=Fleetportid=Holdingarea=Restribution=Maintenance=Fleet="";
                        HA_NFC_CO_portid=RV_NFC_CO_portid=MC_NFC_CO_portid=Holdingarea_NFC_CO=Restribution_NFC_CO=Maintenance_NFC_CO="";
                        Fleet_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutnfc.this, android.R.layout.simple_spinner_dropdown_item, FleetNameArrayList);
                        Fleet_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Fleet_NFC_CO_spinner.setAdapter(Fleet_NFC_CO_adapter);
                        Fleet_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Fleet_NFC_CO=Fleet_NFC_CO_spinner.getSelectedItem().toString();
                                Fleet_NFC_CO_portid = FleetIDArrayList.get(position);
                                Log.d("Holding area port id", Fleet_NFC_CO_portid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RV_NFC_CO_spinner.setVisibility(View.GONE);
                        MC_NFC_CO_spinner.setVisibility(View.GONE);
                        HA_NFC_CO_spinner.setVisibility(View.GONE);
                        break;

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkoutPortid=TransactionAPI.holdingareaid;
                        HA_NFC_CO_spinner.setVisibility(View.VISIBLE);
                        RV_NFC_CO_portid=MC_NFC_CO_portid=Fleet_NFC_CO_portid=Restribution_NFC_CO=Maintenance_NFC_CO=Fleet_NFC_CO="";
                        HA_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutnfc.this, android.R.layout.simple_spinner_dropdown_item, HANameArrayList);
                        HA_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HA_NFC_CO_spinner.setAdapter(HA_NFC_CO_adapter);
                        HA_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea_NFC_CO=HA_NFC_CO_spinner.getSelectedItem().toString();
                                HA_NFC_CO_portid = HAIDArrayList.get(position);
                                Log.d("Holding area port id", HA_NFC_CO_portid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RV_NFC_CO_spinner.setVisibility(View.GONE);
                        MC_NFC_CO_spinner.setVisibility(View.GONE);
                        Fleet_NFC_CO_spinner.setVisibility(View.GONE);
                        break;

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        // checkoutPortid=TransactionAPI.redistrubutionid;
                        RV_NFC_CO_spinner.setVisibility(View.VISIBLE);
                        HA_NFC_CO_portid=MC_NFC_CO_portid=Fleet_NFC_CO_portid=Holdingarea_NFC_CO=Maintenance_NFC_CO=Fleet_NFC_CO="";
                        RV_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutnfc.this, android.R.layout.simple_spinner_dropdown_item,RVNameArrayList);
                        RV_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RV_NFC_CO_spinner.setAdapter(RV_NFC_CO_adapter);
                        RV_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution_NFC_CO=RV_NFC_CO_spinner.getSelectedItem().toString();
                                RV_NFC_CO_portid = RVIDArrayList.get(position);
                                Log.d("Redistribution port id", RV_NFC_CO_portid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        MC_NFC_CO_spinner.setVisibility(View.GONE);
                        HA_NFC_CO_spinner.setVisibility(View.GONE);
                        Fleet_NFC_CO_spinner.setVisibility(View.GONE);
                        break;

                    case 4:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //checkoutPortid=TransactionAPI.maintainenceid;
                        MC_NFC_CO_spinner.setVisibility(View.VISIBLE);
                        HA_NFC_CO_portid=RV_NFC_CO_portid=Fleet_NFC_CO_portid=Holdingarea_NFC_CO=Restribution_NFC_CO=Fleet_NFC_CO="";
                        MC_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutnfc.this, android.R.layout.simple_spinner_dropdown_item, MCNameArrayList);
                        MC_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // MCadapter.add("Select Maintenance Center");
                        MC_NFC_CO_spinner.setAdapter(MC_NFC_CO_adapter);
                        MC_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance_NFC_CO=MC_NFC_CO_spinner.getSelectedItem().toString();
                                MC_NFC_CO_portid = MCIDArrayList.get(position);
                                Log.d("Maintenance port id", MC_NFC_CO_portid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        HA_NFC_CO_spinner.setVisibility(View.GONE);
                        RV_NFC_CO_spinner.setVisibility(View.GONE);
                        Fleet_NFC_CO_spinner.setVisibility(View.GONE);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void getcheckoutnfcdetails() {
        //Holdingarea=HAspinner.getSelectedItem().toString();
        if (Fleet_NFC_CO == ""||Fleet_NFC_CO_portid==null) {
            Fleet_NFC_CO = "";
            Fleet_NFC_CO_portid="";
        } else {
            Fleet_NFC_CO = Fleet_NFC_CO_spinner.getSelectedItem().toString();
        }

        if (Holdingarea_NFC_CO == ""||HA_NFC_CO_portid==null) {
            Holdingarea_NFC_CO = "";
            HA_NFC_CO_portid="";
        } else {
            Holdingarea_NFC_CO = HA_NFC_CO_spinner.getSelectedItem().toString();
        }
        // Maintenance=MCspinner.getSelectedItem().toString();
        if (Maintenance_NFC_CO == ""||MC_NFC_CO_portid==null) {
            Maintenance_NFC_CO = "";
            MC_NFC_CO_portid="";
        } else {
            Maintenance_NFC_CO = MC_NFC_CO_spinner.getSelectedItem().toString();
        }
        // Restribution=RVspinner.getSelectedItem().toString();
        if (Restribution_NFC_CO == ""||RV_NFC_CO_portid==null) {
            Restribution_NFC_CO = "";
            RV_NFC_CO_portid="";
        } else {
            Restribution_NFC_CO = RV_NFC_CO_spinner.getSelectedItem().toString();
        }

    }

    public void getallvalues_nfc(View view) {

        cycleidcount = Allcycleid.size();
        cycleidcount = cycleidcount + 1;
        strings = new String[cycleidcount];
        strings[0] = nfccycleid.getText().toString().trim();
        for (int i = 1; i < cycleidcount; i++) {
            strings[i] = Allcycleid.get(i - 1).getText().toString();
            Log.d("string value", String.valueOf(strings));
        }

        Toast.makeText(this, String.valueOf(cycleidcount), Toast.LENGTH_SHORT).show();


        for (j = 0; j < cycleidcount; j++) {
            checkinternet();
            getcheckoutnfcdetails();
            if (nfccycleid.getText().toString().trim().equals("")) {
                nfccycleid.setError("Please Enter Bicycle ID");
                return;
            }
            if (nfccardid.getText().toString().trim().equals("")) {
                nfccardid.setError("Please Enter Card ID");
                return;
            }
            String checkoutstationvalue = Checkoutstation_NFC.getSelectedItem().toString();
            if (checkoutstationvalue.equals("Select Station")) {
                Toast.makeText(this, "Please select the stations", Toast.LENGTH_LONG).show();
                cycleidcount = Allcycleid.size();
                return;
            }
            Calendar calendar = Calendar.getInstance();
            // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());

            /*GMT +5:30*/
            // final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());
        /*Ends*/

        /*GMT 0*/
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final String checkouttime = dateFormatGmt.format(new Date()) + "";
        /*Ends*/


            sendToserver(strings[j]);


        }
    }

    private void sendToserver(final String string) {



        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String checkouttime = dateFormatGmt.format(new Date()) + "";

        StringRequest checkoutrequest = new StringRequest(Request.Method.POST, API.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response", response);
                // ((ViewGroup) ll.getParent()).removeView(ll);
                try {
                    cycleidcount = Allcycleid.size();
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONObject data = responsefromserver.getJSONObject("data");
                    String errorstatus = data.getString("errorStatus");
                    if (errorstatus.equals("0")) {
                        Toast.makeText(Checkoutnfc.this, "Check out Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        CheckoutnfcErrorLayout.setVisibility(View.VISIBLE);
                        String errormessage = data.getString("errorMsg");
                        Toast.makeText(Checkoutnfc.this, string + " Check out Unsuccessfull ", Toast.LENGTH_SHORT).show();
                        LinearLayout checkouterror = new LinearLayout(Checkoutnfc.this);
                        LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        errorparams.weight = 1;
                        checkouterror.setOrientation(LinearLayout.HORIZONTAL);
                        TextView errorcycleid = new TextView(Checkoutnfc.this);
                        errorcycleid.setLayoutParams(errorparams);
                        errorcycleid.setText(string);
                        errorcycleid.setTextSize(16);
                        errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        TextView errormess = new TextView(Checkoutnfc.this);
                        errormess.setText(errormessage);
                        errormess.setTextSize(16);
                        errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                        errormess.setLayoutParams(errorparams);
                        checkouterror.addView(errorcycleid);
                        checkouterror.addView(errormess);
                        CheckoutnfcErrorLayout.addView(checkouterror);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutnfc.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

                params.put("vehicleId", string);
                params.put("cardId", nfccardid.getText().toString().trim());
                params.put("fromPort", HA_NFC_CO_portid + "" + RV_NFC_CO_portid + "" + MC_NFC_CO_portid + "" + Fleet_NFC_CO_portid);
                params.put("checkOutTime", checkouttime);
                return params;
            }
        };

        PBSSingleton.getInstance(this).addtorequestqueue(checkoutrequest);
        //Toast.makeText(this,HAportid+""+RVportid+""+MCportid+""+Fleetportid, Toast.LENGTH_LONG).show();



    }

    public void sendallcheckoutnfcdetails(View view)
    {
        cycleidcount = Allcycleid.size();
        cycleidcount = cycleidcount + 1;
        strings = new String[cycleidcount];
        strings[0] = nfccycleid.getText().toString().trim();
        for (int i = 1; i < cycleidcount; i++) {
            strings[i] = Allcycleid.get(i - 1).getText().toString();
            Log.d("string value", String.valueOf(strings));
        }
    }


    public void sendcheckoutnfcdetails(View view)
    {
        checkinternet();
        getcheckoutnfcdetails();
        if(nfccycleid.getText().toString().trim().equals(""))
        {
            nfccycleid.setError("Please Scan Bicycle");
            return;
        }
        if(nfccycleid.getText().toString().trim().equals(""))
        {
            nfccycleid.setError("Please Scan Your Card");
            return;
        }
        String checkoutnfcstationvalue = Checkoutstation_NFC.getSelectedItem().toString();
        if (checkoutnfcstationvalue.equals("Select Station")) {
            Toast.makeText(this, "Please select the stations", Toast.LENGTH_LONG).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        final String checkouttime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(calendar.getTime());

        StringRequest checkoutnfcrequest = new StringRequest(Request.Method.POST, API.checkouturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check out Response",response);
                Holdingarea_NFC_CO = "";
                HA_NFC_CO_portid="";
                Maintenance_NFC_CO = "";
                MC_NFC_CO_portid="";
                Restribution_NFC_CO = "";
                RV_NFC_CO_portid="";
                Fleet_NFC_CO = "";
                Fleet_NFC_CO_portid="";
                checkoutnfcstations();
                nfccycleid.setText("");
                nfccardid.setText("");
                Toast.makeText(Checkoutnfc.this,"Check out Successfully",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutnfc.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutnfc.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutnfc.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutnfc.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutnfc.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutnfc.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutnfc.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("vehicleId",nfccycleid.getText().toString().trim());
                params.put("cardId",nfccardid.getText().toString().trim());
                params.put("fromPort",HA_NFC_CO_portid+""+RV_NFC_CO_portid+""+MC_NFC_CO_portid+""+Fleet_NFC_CO_portid);
                params.put("checkOutTime",checkouttime);
                return params;
            }
        };

        PBSSingleton.getInstance(this).addtorequestqueue(checkoutnfcrequest);

    }





    public  void gotocheckoutmanual(View view)
    {
        startActivity(new Intent(this,Checkoutmanually.class));
    }
}
