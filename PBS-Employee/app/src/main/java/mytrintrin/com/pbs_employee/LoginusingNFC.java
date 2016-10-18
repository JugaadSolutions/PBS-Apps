package mytrintrin.com.pbs_employee;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class LoginusingNFC extends AppCompatActivity {

   private NfcAdapter nfcAdapter;
    String Empid;
    String cycleid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginusing_nfc);
        checknfc();
    }

    private void checknfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NFC");
            builder.setMessage("NFC is not available in the mobile");
            builder.setPositiveButton("Login using mail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(LoginusingNFC.this,Login.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            Toast.makeText(this,"NFC is not available",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            if(nfcAdapter.isEnabled())
            {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("NFC");
                builder.setMessage("NFC is not available in the mobile");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });*/
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
        Intent intent = new Intent(this, LoginusingNFC.class);
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
        nfcAdapter.disableForegroundDispatch(this);

       // finish();
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
            Empid=finaltagid+"00000000";
            Toast.makeText(this,"Your id is"+Empid,Toast.LENGTH_LONG).show();
           // textView.setText(finaltagid + "00000000");
        }
        else {
            String bicyletagid= sb.toString();
            String finalbicidbicyletagid= bicyletagid.replace(" ","");
            cycleid = finalbicidbicyletagid;
            Toast.makeText(this,"Your cycle id is"+cycleid,Toast.LENGTH_LONG).show();
           // textView.setText(finalbicidbicyletagid);
        }
        return sb.toString();
    }



    public void gotologin(View view)
    {
        startActivity(new Intent(this,Login.class));
    }
}
