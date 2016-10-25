package mytrintrin.com.pbs_employee;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkoutcycle extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    String checkoutnfccycleid;
    String checkoutnfccardid;

    EditText nfccycleid,nfccardid;

    public static Spinner Checkoutstation_NFC,  HA_NFC_CO_spinner, RV_NFC_CO_spinner, MC_NFC_CO_spinner,Fleet_NFC_CO_spinner;
    public  ArrayAdapter<String> HA_NFC_CO_adapter,RV_NFC_CO_adapter,MC_NFC_CO_adapter,Fleet_NFC_CO_adapter;

    String HA_NFC_CO_portid,RV_NFC_CO_portid,MC_NFC_CO_portid,Fleet_NFC_CO_portid;
    String Holdingarea_NFC_CO,Restribution_NFC_CO,Maintenance_NFC_CO,Fleet_NFC_CO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutcycle);
        nfccycleid = (EditText) findViewById(R.id.etcheckoutnfccycleid);
        nfccardid = (EditText) findViewById(R.id.etcheckoutnfcardid);
        Checkoutstation_NFC = (Spinner) findViewById(R.id.checkoutnfcstationspinner);
        HA_NFC_CO_spinner = (Spinner) findViewById(R.id.HA_NFC_CO_spinner);
        RV_NFC_CO_spinner = (Spinner) findViewById(R.id.RV_NFC_CO_spinner);
        MC_NFC_CO_spinner = (Spinner) findViewById(R.id.MC_NFC_CO_spinner);
        Fleet_NFC_CO_spinner = (Spinner) findViewById(R.id.Fleet_NFC_CO_spinner);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if(nfcAdapter==null)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NFC");
            builder.setMessage("NFC is not available in the mobile");
            builder.setPositiveButton("Enter details manually", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Checkoutcycle.this,"Manual",Toast.LENGTH_LONG).show();
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
        else {
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
        }

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
                    Checkoutcycle.this);
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
        Intent intent = new Intent(this, Checkoutcycle.class);
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
        nfcAdapter.disableForegroundDispatch(Checkoutcycle.this);
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
            checkoutnfccycleid = finalbicidbicyletagid;
            Toast.makeText(this,"Your cycle id is"+checkoutnfccycleid,Toast.LENGTH_LONG).show();
            nfccycleid.setText(checkoutnfccycleid);
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
                        Fleet_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutcycle.this, android.R.layout.simple_spinner_dropdown_item, Splash.FleetNameArrayList);
                        Fleet_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Fleet_NFC_CO_spinner.setAdapter(Fleet_NFC_CO_adapter);
                        Fleet_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Fleet_NFC_CO=Fleet_NFC_CO_spinner.getSelectedItem().toString();
                                Fleet_NFC_CO_portid = Splash.FleetIDArrayList.get(position);
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
                        HA_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutcycle.this, android.R.layout.simple_spinner_dropdown_item, Splash.HANameArrayList);
                        HA_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HA_NFC_CO_spinner.setAdapter(HA_NFC_CO_adapter);
                        HA_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea_NFC_CO=HA_NFC_CO_spinner.getSelectedItem().toString();
                                HA_NFC_CO_portid = Splash.HAIDArrayList.get(position);
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
                        RV_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutcycle.this, android.R.layout.simple_spinner_dropdown_item, Splash.RVNameArrayList);
                        RV_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RV_NFC_CO_spinner.setAdapter(RV_NFC_CO_adapter);
                        RV_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution_NFC_CO=RV_NFC_CO_spinner.getSelectedItem().toString();
                                RV_NFC_CO_portid = Splash.RVIDArrayList.get(position);
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
                        MC_NFC_CO_adapter = new ArrayAdapter<String>(Checkoutcycle.this, android.R.layout.simple_spinner_dropdown_item, Splash.MCNameArrayList);
                        MC_NFC_CO_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // MCadapter.add("Select Maintenance Center");
                        MC_NFC_CO_spinner.setAdapter(MC_NFC_CO_adapter);
                        MC_NFC_CO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance_NFC_CO=MC_NFC_CO_spinner.getSelectedItem().toString();
                                MC_NFC_CO_portid = Splash.MCIDArrayList.get(position);
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

        StringRequest checkoutnfcrequest = new StringRequest(Request.Method.POST, TransactionAPI.checkouturl, new Response.Listener<String>() {
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
                Toast.makeText(Checkoutcycle.this,"Check out Successfully",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    Toast.makeText(Checkoutcycle.this, "can't checkout try later", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Checkoutcycle.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(Checkoutcycle.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(Checkoutcycle.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Checkoutcycle.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Checkoutcycle.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(Checkoutcycle.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

        Mysingleton.getInstance(this).addtorequestqueue(checkoutnfcrequest);

    }
}
