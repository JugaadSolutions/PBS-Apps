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
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Add_Cycle extends AppCompatActivity {

    Toolbar AddCycletoolbar;

    NfcAdapter nfcAdapter;

    EditText BicycleNum,BicycleRFID,MCcardID;

    String MCCycleRFID,MCCard,MCCycleNum;

    ArrayList<String> addcyclerfidlist = new ArrayList<String>();
    ArrayList<String> addcyclenumlist = new ArrayList<String>();
    ArrayList<String> testarray = new ArrayList<String>();

    List<EditText> Allcyclenum;

    int totalcyclerfid,totalcyclenum,a=0,b=0;

    LinearLayout CycleDetails,Dynamiccyledetails;
    LinearLayout alldetails;

    String carnumarray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cycle);
        AddCycletoolbar = (Toolbar) findViewById(R.id.addcycletoolbar);
        AddCycletoolbar.setTitle("Cycles");
        AddCycletoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(AddCycletoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BicycleNum = (EditText) findViewById(R.id.mccyclenumber);
        BicycleRFID = (EditText) findViewById(R.id.mccyclerfid);
        MCcardID = (EditText) findViewById(R.id.mccardnumber);

        CycleDetails = (LinearLayout) findViewById(R.id.addcycledetailslayout);

        Allcyclenum = new ArrayList<EditText>();


        checknfc();
    }

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
                    startActivity(new Intent(Add_Cycle.this, Login.class));
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
        Intent intent = new Intent(this, Add_Cycle.class);
        intent.addFlags(intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(Add_Cycle.this, pendingIntent, intentFilters, null);
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
            String bicyletagid= sb.toString();
            String finalbicidbicyletagid= bicyletagid.replace(" ","");
            MCCycleRFID = finalbicidbicyletagid.toUpperCase();
            Toast.makeText(this,"Your cycle id is"+MCCycleRFID,Toast.LENGTH_LONG).show();
            addcyclerfidlist.add(MCCycleRFID);
            totalcyclerfid = addcyclerfidlist.size();
            BicycleRFID.setText(addcyclerfidlist.get(0));
            if(totalcyclerfid >1)
            {
                alldetails = new LinearLayout(this);
                alldetails.setOrientation(LinearLayout.VERTICAL);

                final LinearLayout cardnumlayout = new LinearLayout(this);
                cardnumlayout.setOrientation(LinearLayout.HORIZONTAL);

                EditText dynamiccyclenum = new EditText(this);
                LinearLayout.LayoutParams cardnumparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardnumparams.setMargins(0, 0, 0, 15);
                dynamiccyclenum.setLayoutParams(cardnumparams);
                dynamiccyclenum.setHint("Bicycle Number");
                dynamiccyclenum.setBackgroundResource(R.drawable.input_outline);
                dynamiccyclenum.setPadding(30, 30, 30, 30);
                dynamiccyclenum.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                dynamiccyclenum.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
                dynamiccyclenum.setTypeface(null, Typeface.BOLD);
                cardnumparams.weight = 1;
                dynamiccyclenum.setId(a + 1);
                a++;
                Log.d("et id", String.valueOf(a));
                Allcyclenum.add(dynamiccyclenum);


                ImageView  dynamicclear = new ImageView(this);
                LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
                dynamicclear.setLayoutParams(ivparams);
                dynamicclear.setImageResource(R.drawable.clearbutton);
                ivparams.weight = 1;
                dynamicclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewGroup) alldetails.getParent()).removeView(alldetails);
                        Toast.makeText(Add_Cycle.this, String.valueOf(a), Toast.LENGTH_SHORT).show();
                        a--;
                        totalcyclerfid--;
                        addcyclerfidlist.remove(a);
                        testarray.remove(a);

                        Toast.makeText(Add_Cycle.this,testarray.remove(a) , Toast.LENGTH_SHORT).show();
                        Toast.makeText(Add_Cycle.this, "Cycle Num "+a+"Rfid num "+totalcyclerfid, Toast.LENGTH_SHORT).show();



                    }
                });

                EditText dynamiccyclerfid = new EditText(this);
                LinearLayout.LayoutParams cyclerfidparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dynamiccyclerfid.setLayoutParams(cyclerfidparams);
                dynamiccyclerfid.setHint("Cycle RFID");
                dynamiccyclerfid.setBackgroundResource(R.drawable.input_outline);
                dynamiccyclerfid.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
                dynamiccyclerfid.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                dynamiccyclerfid.setTypeface(null,Typeface.BOLD);
                dynamiccyclerfid.setPadding(30, 30, 30, 30);
                cyclerfidparams.setMargins(0,0,0,15);
                dynamiccyclerfid.setId(b + 1);
                dynamiccyclerfid.setEnabled(false);
                b++;
                Log.d("et id", String.valueOf(a));
                dynamiccyclerfid.setText(MCCycleRFID);


                cardnumlayout.addView(dynamiccyclenum);
                cardnumlayout.addView(dynamicclear);
                alldetails.addView(cardnumlayout);
                alldetails.addView(dynamiccyclerfid);
                CycleDetails.addView(alldetails);


            }

        }
        return sb.toString();
    }


    public  void addcycletoserver(View view)
    {
        totalcyclenum =a;
        totalcyclenum = totalcyclenum+1;

        testarray.clear();
        testarray.add(0,BicycleNum.getText().toString().trim());
        for(int i=1;i< totalcyclenum;i++)
        {
            testarray.add(i,Allcyclenum.get(i - 1).getText().toString());
            Log.d("cardnum value", String.valueOf(testarray));
            Toast.makeText(this, String.valueOf(testarray), Toast.LENGTH_SHORT).show();
        }

        /*carnumarray = new String[totalcyclenum];
        carnumarray[0] = BicycleNum.getText().toString().trim();

        for (int i = 1; i < totalcyclenum; i++) {
            carnumarray[i] = Allcyclenum.get(i - 1).getText().toString();
            Log.d("cardnum value", String.valueOf(carnumarray));
            addcyclenumlist.add(carnumarray[i]);
        }*/

        for(int i = 0; i< totalcyclerfid; i++)
        {
            Toast.makeText(this, "Num "+testarray.get(i) +"Rfid "+addcyclerfidlist.get(i), Toast.LENGTH_SHORT).show();

        }
        /*MCCycleNum = BicycleNum.getText().toString().trim();
        Toast.makeText(this,"Cycle Num " +MCCycleNum+"Cycle RFID "+MCCycleRFID+"CArd RFID "+MCCard, Toast.LENGTH_SHORT).show();*/
    }





}
