package com.mytrintrin.www.pbs_trintrin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GetStarted_MC extends AppCompatActivity {

    Toolbar GetstartedMCToolbar;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    TextView Versionname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started_mc);
        GetstartedMCToolbar = (Toolbar) findViewById(R.id.getstartedmctoolbar);
        GetstartedMCToolbar.setTitle("Trin Trin");
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();
        Versionname = (TextView) findViewById(R.id.versionname_mc);
        Versionname.setText("Version : "+BuildConfig.VERSION_NAME.toString());
        checkinternet();
    }

    public void gotomaintenance(View view)
    {
        startActivity(new Intent(this,Maintenance.class));
    }

    public void gotorepair(View view)
    {
        startActivity(new Intent(this,Repair.class));
    }

    public void gotoaddbicycle(View view)
    {
        startActivity(new Intent(this,Addcycle.class));
    }

    public void logoutfrommc(View view)
    {
        editor.putString("User-id", "");
        editor.putString("Role","");
        editor.commit();
        startActivity(new Intent(GetStarted_MC.this, Login.class));
        finish();
    }
    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    GetStarted_MC.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/
}
