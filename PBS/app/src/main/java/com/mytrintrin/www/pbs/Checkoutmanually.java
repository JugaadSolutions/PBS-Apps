package com.mytrintrin.www.pbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class Checkoutmanually extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    Toolbar checkoutmanual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutmanually);
        checkoutmanual = (Toolbar) findViewById(R.id.checkoutmanaultoolbar);
        checkoutmanual.setTitle("Checkout");
        checkoutmanual.setTitleTextColor(Color.WHITE);
        setSupportActionBar(checkoutmanual);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
