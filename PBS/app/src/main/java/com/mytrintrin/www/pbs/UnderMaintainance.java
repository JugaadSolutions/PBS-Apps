package com.mytrintrin.www.pbs;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class UnderMaintainance extends AppCompatActivity {

    Toolbar UnderMaintainanceToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_maintainance);
        UnderMaintainanceToolbar = (Toolbar) findViewById(R.id.undermaintainancetoolbar);
        UnderMaintainanceToolbar.setTitle("Under Maintainance");
        UnderMaintainanceToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(UnderMaintainanceToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
