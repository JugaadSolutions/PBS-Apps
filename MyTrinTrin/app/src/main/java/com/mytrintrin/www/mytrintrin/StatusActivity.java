package com.mytrintrin.www.mytrintrin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status);

        Intent mainIntent = getIntent();
        /*TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));*/
        String status  = mainIntent.getStringExtra("transStatus");
        if (mainIntent.getStringExtra("transStatus").equals("Transaction Successful!")) {
            startActivity(new Intent(StatusActivity.this, MyAccount.class));
        }
        if (mainIntent.getStringExtra("transStatus").equals("Transaction Declined!")) {
            startActivity(new Intent(StatusActivity.this, MyAccount.class));
            Toast.makeText(this, "Sorry,Transaction Declined.Please try again later.", Toast.LENGTH_LONG).show();
        }
        if (mainIntent.getStringExtra("transStatus").equals("")) {

        }

    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }
} 