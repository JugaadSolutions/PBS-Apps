package com.mytrintrin.transactions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class testspinner extends AppCompatActivity {



    public static Spinner stationspinner, HAspinner, RVspinner, MCspinner,Fleetspinner;
    public  ArrayAdapter<String> HAadapter,RVadapter,MCadapter,Fleetadapter;

    String HAportid,RVportid,MCportid,Fleetportid;
    String Holdingarea,Restribution,Maintenance,Fleet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testspinner);
        stationspinner = (Spinner) findViewById(R.id.stationspinner);
        HAspinner = (Spinner) findViewById(R.id.HAspinner);
        RVspinner = (Spinner) findViewById(R.id.RVspinner);
        MCspinner = (Spinner) findViewById(R.id.MCspinner);
        Fleetspinner = (Spinner) findViewById(R.id.Fleetspinner);
        stations();


    }



    @Override
    protected void onPause() {
        super.onPause();
        if (HAadapter==null||RVadapter==null||MCadapter==null)
        {
            HAadapter=RVadapter=MCadapter=null;
        }
        else {
            HAadapter.clear();
            RVadapter.clear();
            MCadapter.clear();
        }
    }

    private void stations() {
        List<String> stationtype = new ArrayList<String>();
        stationtype.add("Select Stations");
        stationtype.add("Fleet");
        stationtype.add("Holding Area");
        stationtype.add("Redistrubution Vehicle");
        stationtype.add("Maintainence Centre");
        ArrayAdapter<String> stationadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stationtype);
        stationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationspinner.setAdapter(stationadapter);
        stationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 1:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        Fleetspinner.setVisibility(View.VISIBLE);
                       // HAportid=RVportid=MCportid=Fleetportid=Holdingarea=Restribution=Maintenance=Fleet="";
                        HAportid=RVportid=MCportid=Holdingarea=Restribution=Maintenance="";
                        Fleetadapter = new ArrayAdapter<String>(testspinner.this, android.R.layout.simple_spinner_dropdown_item, Splash.FleetNameArrayList);
                        Fleetadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Fleetspinner.setAdapter(Fleetadapter);
                        Fleetspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Fleet=Fleetspinner.getSelectedItem().toString();
                                Fleetportid = Splash.FleetIDArrayList.get(position);
                                Log.d("Holding area port id", Fleetportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);

                        break;

                    case 2:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        HAspinner.setVisibility(View.VISIBLE);
                        RVportid=MCportid=Fleetportid=Restribution=Maintenance=Fleet="";
                        HAadapter = new ArrayAdapter<String>(testspinner.this, android.R.layout.simple_spinner_dropdown_item, Splash.HANameArrayList);
                        HAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HAspinner.setAdapter(HAadapter);
                        HAspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Holdingarea=HAspinner.getSelectedItem().toString();
                                HAportid = Splash.HAIDArrayList.get(position);
                                Log.d("Holding area port id", HAportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RVspinner.setVisibility(View.GONE);
                        MCspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 3:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        RVspinner.setVisibility(View.VISIBLE);
                        HAportid=MCportid=Fleetportid=Holdingarea=Maintenance=Fleet="";
                        RVadapter = new ArrayAdapter<String>(testspinner.this, android.R.layout.simple_spinner_dropdown_item, Splash.RVNameArrayList);
                        RVadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //RVadapter.add("Select Redistribution Vehicle");
                        RVspinner.setAdapter(RVadapter);
                        RVspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Restribution=RVspinner.getSelectedItem().toString();
                                RVportid = Splash.RVIDArrayList.get(position);
                                Log.d("Redistribution port id", RVportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        MCspinner.setVisibility(View.GONE);
                        HAspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                    case 4:
                        Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        MCspinner.setVisibility(View.VISIBLE);
                        HAportid=RVportid=Fleetportid=Holdingarea=Restribution=Fleet="";
                        MCadapter = new ArrayAdapter<String>(testspinner.this, android.R.layout.simple_spinner_dropdown_item, Splash.MCNameArrayList);
                        MCadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                       // MCadapter.add("Select Maintenance Center");
                        MCspinner.setAdapter(MCadapter);
                        MCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Maintenance=MCspinner.getSelectedItem().toString();
                                MCportid = Splash.MCIDArrayList.get(position);
                                Log.d("Maintenance port id", MCportid);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        HAspinner.setVisibility(View.GONE);
                        RVspinner.setVisibility(View.GONE);
                        Fleetspinner.setVisibility(View.GONE);
                        break;

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void getalldetails() {
        //Holdingarea=HAspinner.getSelectedItem().toString();
        if (Fleet == ""||Fleetportid==null) {
            Fleet = "";
            Fleetportid="";
        } else {
            Fleet = Fleetspinner.getSelectedItem().toString();
        }

        if (Holdingarea == ""||HAportid==null) {
            Holdingarea = "";
            HAportid="";
        } else {
            Holdingarea = HAspinner.getSelectedItem().toString();
        }
       // Maintenance=MCspinner.getSelectedItem().toString();
        if (Maintenance == ""||MCportid==null) {
            Maintenance = "";
            MCportid="";
        } else {
            Maintenance = MCspinner.getSelectedItem().toString();
        }
       // Restribution=RVspinner.getSelectedItem().toString();
        if (Restribution == ""||RVportid==null) {
            Restribution = "";
            RVportid="";
        } else {
            Restribution = RVspinner.getSelectedItem().toString();
        }

    }



    public void Sendetails(View view) {
       // stations();
        getalldetails();
        Toast.makeText(this,HAportid+""+RVportid+""+MCportid+""+Fleetportid, Toast.LENGTH_LONG).show();
        Holdingarea = "";
        HAportid="";
        Maintenance = "";
        MCportid="";
        Restribution = "";
        RVportid="";
        Fleet = "";
        Fleetportid="";
        stations();



    }





}
