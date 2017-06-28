package com.mytrintrin.www.mytrintrin;

import android.app.Activity;
import android.content.DialogInterface;
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
            /*startActivity(new Intent(StatusActivity.this, MyAccount.class));
            Toast.makeText(this, "Transaction Successful!", Toast.LENGTH_LONG).show();*/
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    StatusActivity.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("Payment!!!");
            builder.setMessage("Transaction Successful.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            startActivity(new Intent(StatusActivity.this, MyAccount.class));
                            Toast.makeText(StatusActivity.this, "Transaction Successful.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        }
        if (mainIntent.getStringExtra("transStatus").equals("Transaction Declined!")) {
            /*startActivity(new Intent(StatusActivity.this, MyAccount.class));
            Toast.makeText(this, "Sorry,Transaction Declined.Please try again.", Toast.LENGTH_LONG).show();*/
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    StatusActivity.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("Payment!!!");
            builder.setMessage("Sorry,Transaction Declined.Please try again.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            startActivity(new Intent(StatusActivity.this, MyAccount.class));
                            Toast.makeText(StatusActivity.this, "Sorry,Transaction Declined.Please try again.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        }
        if (mainIntent.getStringExtra("transStatus").equals("Transaction Cancelled!")) {

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    StatusActivity.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("Payment!!!");
            builder.setMessage("Sorry,Transaction Cancelled.Please try again.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            startActivity(new Intent(StatusActivity.this, MyAccount.class));
                            Toast.makeText(StatusActivity.this, "Sorry,Transaction Cancelled.Please try again.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
            builder.setCancelable(false);
            builder.show();

        }

    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }
} 