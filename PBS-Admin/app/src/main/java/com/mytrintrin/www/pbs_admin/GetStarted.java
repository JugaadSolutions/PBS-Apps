package com.mytrintrin.www.pbs_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

public class GetStarted extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar GetStartedToolbar;
    DrawerLayout Getstarted_Drawer;
    ActionBarDrawerToggle mToogle;
    NavigationView GetStarted_Navigation;
    Menu Nav_Menu;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    EditText Message;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        GetStartedToolbar = (Toolbar) findViewById(R.id.getstartedtoolbar);
        GetStartedToolbar.setTitle("Trin Trin");
        setSupportActionBar(GetStartedToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Getstarted_Drawer  = (DrawerLayout) findViewById(R.id.get_started_drawer);
        mToogle = new ActionBarDrawerToggle(this,Getstarted_Drawer,R.string.open,R.string.close);
        Getstarted_Drawer.addDrawerListener(mToogle);
        mToogle.syncState();
        GetStarted_Navigation = (NavigationView) findViewById(R.id.navigationview_getstarted);
        GetStarted_Navigation.setNavigationItemSelectedListener(this);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.addcard_getstarted)
        {
            Toast.makeText(this, "Add Card", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,AddSmartcard.class));
        }
        if(id==R.id.addleave_getstarted)
        {
            Toast.makeText(this, "Add Leave", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,AddLeave.class));
        }
        if(id==R.id.addmessage_getstarted)
        {
            Toast.makeText(this, "Add Message", Toast.LENGTH_SHORT).show();
            showaddmessagedialog();
        }
        return false;
    }

    public void showaddmessagedialog()
    {
        AlertDialog.Builder messagedialog = new AlertDialog.Builder(this);
        messagedialog.setTitle("Message");
        messagedialog.setMessage("Message to all users");
        messagedialog.setIcon(R.mipmap.logo);
        LayoutInflater messageinflate = LayoutInflater.from(this);
        View messageview = messageinflate.inflate(R.layout.addmessage,null);
        messagedialog.setView(messageview);
        Message = (EditText) messageview.findViewById(R.id.newmessage);
        messagedialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                message = Message.getText().toString().trim();
                if(message.length()<5)
                {
                    Message.setError("Message");
                    Toast.makeText(GetStarted.this, "Message cannot be this short.", Toast.LENGTH_LONG).show();
                    showaddmessagedialog();
                    return;
                }
                else {
                    sendnewmessagetoserver();
                }
            }
        });
        messagedialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
            }
        });
        messagedialog.setCancelable(false);
        messagedialog.show();
    }

    public void sendnewmessagetoserver()
    {

    }


    public void Logoutfromadmin(View view)
    {
        editor.putString("User-id", "");
        editor.putString("Role", "");
        editor.commit();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
