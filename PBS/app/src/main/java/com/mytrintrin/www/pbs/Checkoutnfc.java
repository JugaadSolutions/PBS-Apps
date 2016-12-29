package com.mytrintrin.www.pbs;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Checkoutnfc extends AppCompatActivity {

    Toolbar checkoutnfctoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutnfc);
        checkoutnfctoolbar = (Toolbar) findViewById(R.id.checkoutnfctoolbar);
        checkoutnfctoolbar.setTitle("Check Out(NFC)");
        checkoutnfctoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(checkoutnfctoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public  void gotocheckoutmanual(View view)
    {
        startActivity(new Intent(this,Checkoutmanually.class));
    }
}
