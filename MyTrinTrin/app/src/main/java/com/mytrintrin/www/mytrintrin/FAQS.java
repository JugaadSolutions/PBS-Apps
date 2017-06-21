package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQS extends AppCompatActivity {

    private Toolbar FaqToolbar;
    private WebView webView;
    private ProgressDialog mProgressDialog;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqs);
        FaqToolbar = (Toolbar) findViewById(R.id.faqstoolbar);
        FaqToolbar.setTitle("FAQ's");
        setSupportActionBar(FaqToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkinternet();
        //getwebview();

        listView = (ExpandableListView)findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);
    }

    //checking internet
    public void checkinternet() {
        if (AppStatus.getInstance(this).isOnline()) {
            //Log.d("Internet Status", "Online");
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                    FAQS.this);
            builder.setIcon(R.drawable.splashlogo);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.setNegativeButton("Retry Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    checkinternet();
                }
            });
            builder.show();
        }
    }
    /*ends*/


    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("What are the different payment options?");
        listDataHeader.add("What type of bicycles are available?");
        listDataHeader.add("How does the TrinTrin system work?");
        listDataHeader.add("How do I make an online payment?");
        listDataHeader.add("Are there are any restrictions on the usage of multi-speed bicycles?");
        listDataHeader.add("What are the steps for registration?");
        listDataHeader.add("What are the steps for releasing the bicycle from the bicycle bay?");
        listDataHeader.add("What are the steps for returning the bicycle to the bicycle bay?");
        listDataHeader.add("How do I check the availability of the bicycles?");
        listDataHeader.add("What should I do if no bicycle is available at a bicycle hub?");
        listDataHeader.add("What should I do if no empty port is available at a bicycle hub?");
        listDataHeader.add("By the time I complete my ride, it is already past your closing time (10 PM). Can I keep the bicycle overnight and return it tomorrow?");
        listDataHeader.add("What should I do if I lost-my smart card or my card gets stolen?");
        listDataHeader.add("Why does the user fees go up steeply with usage time?");
        listDataHeader.add("Why is first 60 minutes free and how many times in a day can I avail it?");
        listDataHeader.add("How do I recharge my account balance?");
        listDataHeader.add("About cancellation of membership and refunds");

        List<String> payment = new ArrayList<>();
        payment.add("Members can make the payment in the following methods\n" +
                "1.Cash payment can be made only at the 6 registration centers and karnataka one centers\n" +
                "2.Credit card / Debit card payments can be made at the 6 registration centers,karnataka one centers and also through online payment\n" +
                "3.Net banking payments can be made only through our member portal (www.mytrintrin.com) or through the smartphone app.");

        List<String> typeofbicycles = new ArrayList<>();
        typeofbicycles.add("1.430 regular bicycles which can be used across the city\n" +
                "2.20 multispeed bicycles (multi-geared) available only at Chamundi hill foot and Chamundi hill top. these bicycles are provided with multi speed capability to help the riders to shift to appropriate gears during climbing the chamundi hills\n" +
                "3.On an experimental basis, 5 bicycles with electric motor assist will also be provided only at Chamundi hill foot and Chamundi hill top.");


        List<String> systemworks = new ArrayList<>();
        systemworks.add("TrinTrin public sharing system consists of completely automated bicycle hubs equipped with modern technology to monitor, unlock and lock the bicycles to the docking units. All the bicycle hubs are connected by communication network through which all the bicycles hubs are monitored from the central control centre. This enables the user to borrow the bicycle at one docking station and return it at any other docking station. Users have to register by providing necessary information, an identity proof, address proof(preferably AADHAR card) and membership fees. At the end of the registration, each user will be given an unique smart card and by using these smart cards, users will be able to unlock the bicycle from the bicycle bay and start using the bicycle.");

        List<String> onlinepay = new ArrayList<>();
        onlinepay.add("The online payment can be made by registering online and then logging in to your account.");

        List<String> restrictions = new ArrayList<>();
        restrictions.add("Yes. the multi-speed bicycles will be found only at the Chamundi hill foot and Chamundi hill top bicycle hubs. These bicycles should only be used between Chamundi hill foot and Chamundi hill top. Riders will not be able to return these bicycles at any other bicycle hubs.");

        List<String> stepsforregistration = new ArrayList<>();
        stepsforregistration.add("User registration involves 3 steps.  \n" +
                "Providing your information and an identity proof.\n" +
                "1.This step can be completed online or through our smartphone app or by visiting any of our 6 registration centers or any karnataka one centers.\n" +
                "2.Provide your information like name, address, contact phone number etc.\n" +
                "3.Provide an identity proof like Aadhar card, Passport, Driver’s License, Voter ID etc.\n" +
                "Choosing an appropriate membership plan and making the payment.\n" +
                "This step can be completed online or through our smartphone app or by visiting any of our 6 registration centers or any karnataka one centers.\n" +
                "Choose an appropriate membership plan and make the payment\n" +
                "Verification of the identity proof and obtaining the smart card\n" +
                "1.This step can be completed only by visiting any of our 6 registration centres or any karnataka one centers.\n" +
                "2.One of our employees at registration centers or karnataka one centers  will verify your identity proof and will issue a smartcard. They will also explain how to use the system and help you with any of the questions you may have.\n" +
                "3.Using the smart card, unlock the bicycle from the bay and you are now ready to go.\n" +
                "4.Practice how to release (unlock) and return (lock) the bicycle from the bicycle hub.\n" +
                " Understand the important aspects of the TrinTrin system and DOs and DONTs before you start using the TrinTrin system. Please ride safely.");

        List<String> releasecycle = new ArrayList<>();
        releasecycle.add("1.Place the TrinTrin smartcard on the reader next to the required bicycle.\n" +
                "2.Once the LED indication shows that the bicycle is unlocked, pull out the bicycle from the bay. You are now ready to go.");

        List<String> returncycle = new ArrayList<>();
        returncycle.add("1.Slide the bicycle-clip firmly into an empty port.Make sure that bicycle is properly locked by gently pulling on the bicycle.\n" +
                "2.Wait for “Transaction Completed” light to blink.\n" +
                "3.Wait till the “Ready” LED light is turned on.\n" +
                "4.Bicycle return process is now complete. Tap your smart card at the Kiosk to update your card. The bicycle is now returned, locked and the smartcard is updated with the latest balance.");

        List<String> availablecycle = new ArrayList<>();
        availablecycle.add("Please check the real time availability of the bicycles and empty port at the \"Hubs\".");

        List<String> nocycle = new ArrayList<>();
        nocycle.add("We have a dedicated team to redistribute the bicycles from bicycle hubs that are full to hubs that are empty. If the bicycle hub is empty, a new set of bicycles will soon be delivered to that bicycle hub. Please wait at the bicycle hub and the bicycles will arrive soon.");

        List<String> noemptyport = new ArrayList<>();
        noemptyport.add("We have a dedicated team to redistribute the bicycles from bicycle hubs that are full to hubs that are empty. If the bicycle hub is full and you are unable to find an empty port to return your bicycle then please wait. Our bicycle redistribution team will soon arrive to make empty ports available for you.");

        List<String> closingtime = new ArrayList<>();
        closingtime.add("No. You cannot keep the bicycle overnight. You can return the bicycle even after we close the operations at 10 PM. You simply follow the steps to return the bicycle even after 10 PM.");

        List<String> stolencard = new ArrayList<>();
        stolencard.add("Please call our help line(0821-2333000) immediately and report to us. We will block the card from further use. However, if someone who found the card may have already used it illegally. Hence, please inform us without any delay.");

        List<String> usagetime = new ArrayList<>();
        usagetime.add("TrinTrin is set up as a bicycle sharing system. It is important that the utilization of the bicycles is maximised to ensure that the system serves as many users as possible. In order to ensure that TrinTrin bikes are always in active service and do not stay idle with the members during their non-riding hours, members are advised to return their bikes immediately as they reach a hub closest to their destination. Our usage charges are designed to encourage the bicycle usage for short durations (remember that the first 60 minutes are free). If required, rent another bike for further transits. You can undertake this kind of point-to-point, ‘break-and-resume’ rides any number of times and thus free up the bicycles for other people.");

        List<String> sixtyminutes = new ArrayList<>();
        sixtyminutes.add("To encourage regular usage of the system and to encourage the bicycle culture, the first 60 minutes are granted free for members.For every transactions you make you will have first 60 minutes free .");

        List<String> rechargebalance = new ArrayList<>();
        rechargebalance.add("You can recharge your account balance by making the payment \n1. This step can be completed online or through our smartphone app or by visiting any of our 6 registration centres or any karnataka one centres.\n 2. To make the payment online or through our smartphone, you have to register and login to the member portal.");

        List<String> cancelmembership = new ArrayList<>();
        cancelmembership.add("Please visit one of our TrinTrin registration centres or any karnataka one centres and complete the membership cancellation steps and return the smartcard provided to you during registration. Your security deposit will be refunded as soon the membership cancellation is complete.");


        listHash.put(listDataHeader.get(0),payment);
        listHash.put(listDataHeader.get(1),typeofbicycles);
        listHash.put(listDataHeader.get(2),systemworks);
        listHash.put(listDataHeader.get(3),onlinepay);
        listHash.put(listDataHeader.get(4),restrictions);
        listHash.put(listDataHeader.get(5),stepsforregistration);
        listHash.put(listDataHeader.get(6),releasecycle);
        listHash.put(listDataHeader.get(7),returncycle);
        listHash.put(listDataHeader.get(8),availablecycle);
        listHash.put(listDataHeader.get(9),nocycle);
        listHash.put(listDataHeader.get(10),noemptyport);
        listHash.put(listDataHeader.get(11),closingtime);
        listHash.put(listDataHeader.get(12),stolencard);
        listHash.put(listDataHeader.get(13),usagetime);
        listHash.put(listDataHeader.get(14),sixtyminutes);
        listHash.put(listDataHeader.get(15),rechargebalance);
        listHash.put(listDataHeader.get(16),cancelmembership);

    }


   /* private void getwebview() {

        webView = (WebView) findViewById(R.id.FaqwebView);
        if (AppStatus.getInstance(this).isOnline()) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            startWebView("https://www.mytrintrin.com/?page_id=4308");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
                    Log.d("CHECK", "onReceivedSslError");
                    AlertDialog.Builder builder = new AlertDialog.Builder(FAQS.this);
                    final AlertDialog alertDialog = builder.create();

                    String message = "Certificate error.";
                    switch (error.getPrimaryError()) {
                        case SslError.SSL_UNTRUSTED:
                            message = "The certificate authority is not trusted.";
                            break;
                        case SslError.SSL_EXPIRED:
                            message = "The certificate has expired.";
                            break;
                        case SslError.SSL_IDMISMATCH:
                            message = "The certificate Hostname mismatch.";
                            break;
                        case SslError.SSL_NOTYETVALID:
                            message = "The certificate is not yet valid.";
                            break;
                    }
                    message += " Do you want to continue anyway?";
                    alertDialog.setTitle("SSL Certificate Error");
                    alertDialog.setMessage(message);

                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
                    alertDialog.show();
                    //To perform dynamic click for ssl error
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

                }
            });
        } else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    FAQS.this);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setIcon(R.drawable.splashlogo);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.show();
        }
    }

    private void startWebView(String url) {
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                mProgressDialog.dismiss();
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    mProgressDialog.dismiss();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        mProgressDialog.dismiss();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        mProgressDialog.dismiss();
    }*/
}
