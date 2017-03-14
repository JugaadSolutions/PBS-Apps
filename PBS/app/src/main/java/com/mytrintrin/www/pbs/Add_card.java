package com.mytrintrin.www.pbs;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class Add_card extends AppCompatActivity {

    Toolbar AddCardToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        AddCardToolbar = (Toolbar) findViewById(R.id.addcardtoolbar);
        AddCardToolbar.setTitle("Card");
        AddCardToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(AddCardToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
