package com.mytrintrin.www.pbs_admin;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSmartcard extends AppCompatActivity {

    private Toolbar AddSmartCardToolbar;
    NfcAdapter nfcAdapter;
    EditText CardFrom,CardTo;
    String From,To,CardRFID,loginuserid,cardtype;
    ArrayList<Integer> FromArray;
    ArrayList<String> SmartcardRfidList,CardTypeList;
    LinearLayout CardNumlayout,AddSmartCardLayout,CardErrorLayout;
    EditText CardNo,CardRfid;
    LinearLayout.LayoutParams CardNoParams;
    int rfidlistlength,m=0;
    Button SubmitCard;
    ImageView ClearCard;
    Spinner CardTypeSpinner;
    TextView Cardtype_TV;

    private ProgressDialog mProgressDialog;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_smartcard);
        AddSmartCardToolbar = (Toolbar) findViewById(R.id.addsmartcardtoolbar);
        AddSmartCardToolbar.setTitle("Smart Card");
        setSupportActionBar(AddSmartCardToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        CardErrorLayout = (LinearLayout) findViewById(R.id.carderrorlayout);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        loginuserid = loginpref.getString("User-id", null);
        CardTypeSpinner = (Spinner) findViewById(R.id.cardtype);
        Cardtype_TV = (TextView) findViewById(R.id.tv_cardtype);

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        checknfc();
        SubmitCard = (Button) findViewById(R.id.cardsubmit);
        FromArray = new ArrayList<>();
        AddSmartCardLayout = (LinearLayout) findViewById(R.id.addsmardcardlayout);
        FromArray = new ArrayList<>();
        SmartcardRfidList = new ArrayList<>();
        CardTypeList = new ArrayList<>();
        CardTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0 : cardtype="1";
                        break;
                    case 1 : cardtype="2";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public  void Cardrangedialog()
    {
        AlertDialog.Builder Rangedialog = new AlertDialog.Builder(this);
        Rangedialog.setTitle("Smart Card");
        Rangedialog.setMessage("Enter Smart Card Range");
        Rangedialog.setIcon(R.mipmap.logo);
        LayoutInflater rangeinflate = LayoutInflater.from(this);
        View rangeView = rangeinflate.inflate(R.layout.cardrange,null);
        CardFrom = (EditText) rangeView.findViewById(R.id.cardfrom);
        CardTo = (EditText) rangeView.findViewById(R.id.cardto);
        Rangedialog.setView(rangeView);
        Rangedialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                From = CardFrom.getText().toString();
                To = CardTo.getText().toString();
                if(From.length()<3)
                {
                    CardFrom.setError("Minimum 3 Nos");
                    Toast.makeText(AddSmartcard.this, "Minimum is 3 Nos", Toast.LENGTH_LONG).show();
                    Cardrangedialog();
                    return;
                }
                if(To.length()<3)
                {
                    CardTo.setError("Minimum 3 Nos");
                    Toast.makeText(AddSmartcard.this, "Minimum is 3 Nos", Toast.LENGTH_LONG).show();
                    Cardrangedialog();
                    return;
                }
                if(Integer.parseInt(To)<Integer.parseInt(From))
                {
                    Toast.makeText(AddSmartcard.this, "Please give correct range", Toast.LENGTH_LONG).show();
                    Cardrangedialog();
                    return;
                }
                Toast.makeText(AddSmartcard.this, "You have choosen Card Number From:"+From+"To:"+To, Toast.LENGTH_SHORT).show();
                for(int j = Integer.parseInt(From);j<= Integer.parseInt(To) ; j++)
                {
                    FromArray.add(j);
                }
                createeditext();

            }
        });
        Rangedialog.setCancelable(false);
        Rangedialog.show();
    }

    private void checknfc() {
        if (nfcAdapter == null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
            builder.setCancelable(false);
            builder.show();
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (nfcAdapter.isEnabled()) {
                onNewIntent(getIntent());
                Cardrangedialog();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                builder.setCancelable(false);
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
        Intent intent = new Intent(this, AddSmartcard.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(AddSmartcard.this, pendingIntent, intentFilters, null);
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
            CardRFID=finaltagid.toUpperCase()+"00000000";
            Toast.makeText(this,"RFID id is "+CardRFID,Toast.LENGTH_LONG).show();
            if(SmartcardRfidList.contains(CardRFID))
            {
                Toast.makeText(this,"Already Scanned : "+CardRFID,Toast.LENGTH_LONG).show();
            }
            else {
                if (m < FromArray.size())
                {
                    CardRfid.setText(CardRFID);
                SmartcardRfidList.add(CardRFID);
                    CardTypeList.add(cardtype);
                rfidlistlength = SmartcardRfidList.size();
                AlertDialog.Builder ScanBuilder = new AlertDialog.Builder(AddSmartcard.this);
                ScanBuilder.setIcon(R.mipmap.logo);
                ScanBuilder.setTitle("Smart Card");
                ScanBuilder.setMessage("Scanned Successfully.Please Tap another card");
                ScanBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        m++;
                        CardTypeSpinner.setSelection(0);
                        createeditext();
                    }
                });
                ScanBuilder.setCancelable(false);
                ScanBuilder.show();
            }
            }
        }
        else {
            Toast.makeText(this,"Invalid Card",Toast.LENGTH_LONG).show();
        }
        return sb.toString();
    }


    public void createeditext() {
        if (m < FromArray.size()) {
            AddSmartCardLayout.removeAllViews();

            CardNumlayout = new LinearLayout(AddSmartcard.this);
            CardNumlayout.setOrientation(LinearLayout.HORIZONTAL);

            CardNo = new EditText(AddSmartcard.this);
            LinearLayout.LayoutParams cardnumparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CardNo.setLayoutParams(cardnumparams);
            cardnumparams.weight=1;
            cardnumparams.setMargins(0,0,0,15);

            CardNo.setBackgroundResource(R.drawable.input_outline);
            CardNo.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            CardNo.setText(String.valueOf(FromArray.get(m)));
            CardNo.setEnabled(false);

            ClearCard = new ImageView(AddSmartcard.this);
            LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
            ClearCard.setLayoutParams(ivparams);
            ClearCard.setImageResource(R.drawable.clearbutton);
            ivparams.weight = 1;
            ClearCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddSmartCardLayout.removeAllViews();
                    FromArray.remove(m);
                    int cardtypelength = CardTypeList.size();
                    if(cardtypelength > m || cardtypelength == m)
                    {
                        Log.d("Card length", String.valueOf(cardtypelength));
                    }
                    else
                    {
                        CardTypeList.remove(m);
                    }
                    createeditext();
                }
            });

            CardNumlayout.addView(CardNo);
            CardNumlayout.addView(ClearCard);

            CardRfid = new EditText(AddSmartcard.this);
            CardRfid.setBackgroundResource(R.drawable.input_outline);
            CardRfid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            CardRfid.setEnabled(false);

            AddSmartCardLayout.addView(CardNumlayout);
            AddSmartCardLayout.addView(CardRfid);
        }
        else
        {
            Toast.makeText(this, "You have reached maximum", Toast.LENGTH_SHORT).show();
            SubmitCard.setVisibility(View.VISIBLE);
            SubmitCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getdetails();
                }
            });
        }
    }


    public void getdetails()
    {
        for(int i =0;i<FromArray.size();i++)
        {
            Toast.makeText(this, "Cardnum : "+FromArray.get(i)+"RFID : "+ SmartcardRfidList.get(i)+"Card Type : "+CardTypeList.get(i), Toast.LENGTH_SHORT).show();
            sendtoserver(FromArray.get(i),SmartcardRfidList.get(i),CardTypeList.get(i));
        }
    }

    public void sendtoserver(final int CardNum, final String CardId,final String Cardtype)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding Cycle...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        StringRequest Addcardrequest = new StringRequest(Request.Method.POST, API.addsmartcard, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                Toast.makeText(AddSmartcard.this, response, Toast.LENGTH_LONG).show();
                FromArray.clear();
                CardTypeList.clear();
                SmartcardRfidList.clear();
                AddSmartCardLayout.removeAllViews();
                SubmitCard.setEnabled(false);
                SubmitCard.setVisibility(View.GONE);
                CardTypeSpinner.setVisibility(View.GONE);
                Cardtype_TV.setVisibility(View.GONE);
                Toast.makeText(AddSmartcard.this, "That's all folks.", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SubmitCard.setEnabled(false);
                SubmitCard.setVisibility(View.GONE);
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(AddSmartcard.this, "Server is under maintenance.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AddSmartcard.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AddSmartcard.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AddSmartcard.this, "Please check your connection-.", Toast.LENGTH_LONG).show();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            AddSmartcard.this);
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
                    Toast.makeText(AddSmartcard.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AddSmartcard.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AddSmartcard.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

                params.put("cardNumber", String.valueOf(CardNum));
                params.put("cardRFID", CardId);
                params.put("cardType", Cardtype);
                params.put("createdBy", loginuserid);
                return params;
            }
        };
        Addcardrequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(Addcardrequest);
        mProgressDialog.dismiss();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    AddSmartcard.this);
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

    public void parseVolleyError(VolleyError error) {
        try {
            CardErrorLayout.setVisibility(View.VISIBLE);
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            LinearLayout addcycleerror = new LinearLayout(AddSmartcard.this);
            LinearLayout.LayoutParams errorparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            errorparams.weight = 1;
            addcycleerror.setOrientation(LinearLayout.HORIZONTAL);
            TextView errorcycleid = new TextView(AddSmartcard.this);
            errorcycleid.setLayoutParams(errorparams);
            errorcycleid.setText(message);
            errorcycleid.setTextSize(16);
            errorcycleid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            TextView errormess = new TextView(AddSmartcard.this);
            errormess.setText("Duplicate Entry");
            errormess.setTextSize(16);
            errormess.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            errormess.setLayoutParams(errorparams);
            addcycleerror.addView(errorcycleid);
            addcycleerror.addView(errormess);

            CardErrorLayout.addView(addcycleerror);


        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}
