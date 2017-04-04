package com.mytrintrin.www.pbs_kiosk;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class KioskDisplay extends AppCompatActivity {

    private Toolbar KioskToolbar;
    NfcAdapter nfcAdapter;
    String CardRFID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiosk_display);
        KioskToolbar = (Toolbar) findViewById(R.id.kiosktoolbar);
        KioskToolbar.setTitle("Trin Trin");
        setSupportActionBar(KioskToolbar);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        checknfc();
    }

    private void checknfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NFC");
            builder.setMessage("NFC is not available in the mobile");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(this, KioskDisplay.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
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
            String RFID = sb.reverse().toString();
            String FinalRFID = "";
            for (int i = 0; i < RFID.length(); i = i + 3) {
                String s1 = RFID.substring(i, i + 1);
                String s2 = RFID.substring(i + 1, i + 2);
                int len1 = s1.length();
                s1 = s1 + s2;
                s2 = s1.substring(0, len1);
                s1 = s1.substring(len1);
                FinalRFID = FinalRFID + s1 + s2;
            }
            CardRFID = FinalRFID.toUpperCase() + "00000000";
            getmemberdetails();
        } else {
            String bicyletagid = sb.toString();
            String finalbicidbicyletagid = bicyletagid.replace(" ", "");
            Toast.makeText(this, "Invalid card detected", Toast.LENGTH_LONG).show();
        }
        return sb.toString();
    }

    public void getmemberdetails() {
        StringRequest GetUserDetailsRequest = new StringRequest(Request.Method.POST, API.getmemberdetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responsefromserver = new JSONObject(response);
                    JSONArray data = responsefromserver.getJSONArray("data");
                    if (data.length() == 0) {
                        Toast.makeText(KioskDisplay.this, "Invalid User", Toast.LENGTH_LONG).show();
                    } else if (data.length() > 0) {
                        JSONObject resultobject = data.getJSONObject(0);
                        JSONArray VehicleArray = resultobject.getJSONArray("vehicleId");
                        if (VehicleArray.length() > 0) {
                            Toast.makeText(KioskDisplay.this, "You have already taken bicycle", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder BalanceBuilder = new AlertDialog.Builder(KioskDisplay.this);
                            BalanceBuilder.setIcon(R.drawable.splashlogo);
                            BalanceBuilder.setTitle("KIOSK");
                            BalanceBuilder.setMessage("You have already taken bicycle!!!");
                            BalanceBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            BalanceBuilder.show();
                        } else if (VehicleArray.length() == 0) {
                            String Balance = resultobject.getString("creditBalance");
                            String Validity = resultobject.getString("validity");
                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(inFormat.parse(Validity));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(KioskDisplay.this, "Bal : " + Balance + "Validity : " + outFormat.format(c.getTime()), Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder BalanceBuilder = new AlertDialog.Builder(KioskDisplay.this);
                            BalanceBuilder.setIcon(R.drawable.splashlogo);
                            BalanceBuilder.setTitle("KIOSK");
                            BalanceBuilder.setMessage("Balance : " + Balance + "\n" + "Validity : " + outFormat.format(c.getTime()));
                            BalanceBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            BalanceBuilder.show();
                        }
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
                    return;
                }
                if (error instanceof ServerError) {
                    Toast.makeText(KioskDisplay.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(KioskDisplay.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(KioskDisplay.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(KioskDisplay.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            KioskDisplay.this);
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
                    Toast.makeText(KioskDisplay.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(KioskDisplay.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(KioskDisplay.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("name", CardRFID);
                return params;
            }
        };
        GetUserDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PBSSingleton.getInstance(getApplicationContext()).addtorequestqueue(GetUserDetailsRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("description");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    KioskDisplay.this);
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
}
