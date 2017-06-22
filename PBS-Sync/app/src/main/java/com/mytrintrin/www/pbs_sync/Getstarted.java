package com.mytrintrin.www.pbs_sync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class Getstarted extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle mToogle;
    DrawerLayout GetstartedDrawer;
    NavigationView Getstarted_navigation;
    Toolbar Getstarted_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getstarted);

        Getstarted_toolbar = (Toolbar) findViewById(R.id.getstarted_toolbar);
        setSupportActionBar(Getstarted_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GetstartedDrawer = (DrawerLayout) findViewById(R.id.getstarted);
        mToogle = new ActionBarDrawerToggle(this, GetstartedDrawer, R.string.open, R.string.close);
        GetstartedDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        Getstarted_navigation = (NavigationView) findViewById(R.id.getstarted_navigationview);
        Getstarted_navigation.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.nav_alluser)
        {
            startActivity(new Intent(Getstarted.this,Allusers.class));
        }

        if(item.getItemId()==R.id.nav_allvehicles)
        {
            startActivity(new Intent(Getstarted.this,Allvehicles.class));
        }


        if(item.getItemId()==R.id.nav_particularvehicle)
        {
            startActivity(new Intent(Getstarted.this,Vehicle.class));
        }


        if(item.getItemId()==R.id.nav_particularuser)
        {
            startActivity(new Intent(Getstarted.this,User.class));
        }

        if(item.getItemId()==R.id.nav_clearcheckout)
        {
            startActivity(new Intent(Getstarted.this,Clearcheckout.class));
        }

        if(item.getItemId()==R.id.nav_createcheckin)
        {
            startActivity(new Intent(Getstarted.this,Checkin.class));
        }

        if(item.getItemId()==R.id.nav_createcheckout)
        {
            startActivity(new Intent(Getstarted.this,Checkout.class));
        }

        if(item.getItemId()==R.id.nav_clearcheckin)
        {
            startActivity(new Intent(Getstarted.this,Clearcheckin.class));
        }

        if(item.getItemId()==R.id.nav_multipleclearcheckout)
        {
            startActivity(new Intent(Getstarted.this,multipleCheckout_clear.class));
        }


        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
