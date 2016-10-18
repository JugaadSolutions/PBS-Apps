package com.mytrintrin.transactions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button checkin,checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkin= (Button) findViewById(R.id.bcheckin);
        checkout= (Button) findViewById(R.id.bcheckout);
    }
    public  void gotocheckout(View view)

    {
        startActivity(new Intent(this,Checkout.class));
    }

    public void gotocheckin(View view)
    {
        startActivity(new Intent(this,Checkin.class));
    }
}
