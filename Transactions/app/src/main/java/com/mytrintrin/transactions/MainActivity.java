package com.mytrintrin.transactions;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button checkin,checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkin= (Button) findViewById(R.id.bcheckin);
        checkout= (Button) findViewById(R.id.bcheckout);
        checkinternet();
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {

            Log.d("Internet Status", "Online");

        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            Log.d("Internet Status", "Offline");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setIcon(R.drawable.ic_wifi);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }

    /*ends*/



    public  void gotocheckout(View view)

    {
        startActivity(new Intent(this,Checkout.class));
    }

    public void gotocheckin(View view)
    {
        startActivity(new Intent(this,Checkin.class));
    }
}
