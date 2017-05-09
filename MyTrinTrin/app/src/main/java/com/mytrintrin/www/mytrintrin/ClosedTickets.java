package com.mytrintrin.www.mytrintrin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClosedTickets extends AppCompatActivity {

    JSONObject ClosedTicketsObject;
    String closedtickets;

    CardView Ticketcardview;
    Context context;
    LinearLayout Ticketslayout, TicketDetailsLayout;
    LinearLayout.LayoutParams Ticketcardparams,Ticketlayoutparams;
    int i=0,ticketposition;
    JSONArray TicketArray;
    JSONObject Ticketsdetails;
    TextView TicketStatus,TicketId,TicketCreatedDate,TicketDescription,Closedtickets;
    private Toolbar Ticketstoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.closed_tickets);
        context = getApplicationContext();
        closedtickets = getIntent().getStringExtra("closedticketobject");
        TicketDetailsLayout = (LinearLayout) findViewById(R.id.getticketslayout);
        Ticketstoolbar= (Toolbar) findViewById(R.id.closeticketstoolbar);
        Ticketstoolbar.setTitle("Tickets");
        setSupportActionBar(Ticketstoolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        try {
            ClosedTicketsObject = new JSONObject(closedtickets);
            JSONArray data = ClosedTicketsObject.getJSONArray("data");
            if(data.length()>0)
            {
                TicketArray = new JSONArray();

                for(i=0;i<data.length();i++)
                {
                    JSONObject ticketobject = data.getJSONObject(i);
                    TicketArray.put(ticketobject);
                    String TicketID = ticketobject.getString("uuId");
                    String date = ticketobject.getString("ticketdate");
                    SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(inFormat.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String Subject = ticketobject.getString("subject");
                    String Ticketstatus = ticketobject.getString("status");

                    Ticketcardview = new CardView(context);
                    Ticketcardparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Ticketcardparams.bottomMargin = 10;
                    Ticketcardview.setLayoutParams(Ticketcardparams);
                    Ticketcardview.setRadius(15);
                    Ticketcardview.setPadding(25, 25, 25, 25);
                    Ticketcardview.setCardBackgroundColor(Color.parseColor("#009746"));
                    Ticketcardview.setVisibility(View.VISIBLE);
                    Ticketcardview.setMaxCardElevation(30);
                    Ticketcardview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ticketposition= TicketDetailsLayout.indexOfChild(view);
                            ticketdetailsdialog();
                        }
                    });

                    Ticketslayout = new LinearLayout(context);
                    Ticketlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Ticketslayout.setOrientation(LinearLayout.VERTICAL);
                    Ticketslayout.setGravity(Gravity.END);
                    Ticketslayout.setPadding(25, 25, 25, 25);
                    Ticketslayout.setLayoutParams(Ticketlayoutparams);

                    TicketId = new TextView(context);
                    TicketId.setText("Ticket Id: UT"+TicketID);
                    TicketId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    TicketId.setTextColor(Color.WHITE);

                    TicketDescription = new TextView(context);
                    TicketDescription.setText("Subject: "+Subject);
                    TicketDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    TicketDescription.setTextColor(Color.WHITE);

                    TicketStatus= new TextView(context);
                    TicketStatus.setText("Status : "+Ticketstatus);
                    TicketStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    TicketStatus.setTextColor(Color.WHITE);

                    Ticketslayout.addView(TicketId);
                    Ticketslayout.addView(TicketDescription);
                    Ticketslayout.addView(TicketStatus);

                    Ticketcardview.addView(Ticketslayout);
                    if(Ticketstatus.equals("Open"))
                    {
                        Ticketcardview.setVisibility(View.GONE);

                    }
                    TicketDetailsLayout.addView(Ticketcardview);

                }
            }
            else
            {
                AlertDialog.Builder TicketBuilder = new AlertDialog.Builder(ClosedTickets.this);
                TicketBuilder.setIcon(R.drawable.splashlogo);
                TicketBuilder.setTitle("Tickets");
                TicketBuilder.setMessage("No tickets to display");
                TicketBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                TicketBuilder.show();
                Toast.makeText(ClosedTickets.this, "No tickets to display", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ticketdetailsdialog()
    {
        try {
            Ticketsdetails = TicketArray.getJSONObject(ticketposition);
            String TicketID = Ticketsdetails.getString("uuId");
            String Name = Ticketsdetails.getString("name");
            String Subject=Ticketsdetails.getString("subject");
            String Description = Ticketsdetails.getString("description");
            String Date = Ticketsdetails.getString("ticketdate");
            String Status = Ticketsdetails.getString("status");
            LinearLayout TicketLayout = new LinearLayout(ClosedTickets.this);
            TicketLayout.setPadding(8,8,8,8);
            TicketLayout.setOrientation(LinearLayout.VERTICAL);

            TextView ID = new TextView(ClosedTickets.this);
            ID.setText("Ticket No: UT"+TicketID);
            ID.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView name = new TextView(ClosedTickets.this);
            name.setText("Name: "+Name);
            name.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView subject = new TextView(ClosedTickets.this);
            subject.setText("Subject: "+Subject);
            subject.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView description = new TextView(ClosedTickets.this);
            description.setText("Description: "+Description);
            description.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView date = new TextView(ClosedTickets.this);
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outFormat = new SimpleDateFormat("MMM dd, yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(inFormat.parse(Date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date.setText("Date: "+outFormat.format(c.getTime()));
            date.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TextView status = new TextView(ClosedTickets.this);
            status.setText("Status: "+Status);
            status.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

            TicketLayout.addView(ID);
            TicketLayout.addView(name);
            TicketLayout.addView(subject);
            TicketLayout.addView(description);
            TicketLayout.addView(date);
            TicketLayout.addView(status);

            AlertDialog.Builder TicketBuilder = new AlertDialog.Builder(ClosedTickets.this);
            TicketBuilder.setTitle("Ticket Details");
            TicketBuilder.setIcon(R.drawable.splashlogo);
            TicketBuilder.setView(TicketLayout);
            TicketBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            TicketBuilder.setNegativeButton("View Details/Reply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent ticketdetails = new Intent(ClosedTickets.this,TicketDetails.class);
                    ticketdetails.putExtra("TicketObject",Ticketsdetails.toString());
                    startActivity(ticketdetails);
                }
            });
            TicketBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            TicketBuilder.setCancelable(false);
            TicketBuilder.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
