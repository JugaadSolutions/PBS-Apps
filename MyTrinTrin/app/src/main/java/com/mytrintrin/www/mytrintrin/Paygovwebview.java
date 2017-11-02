package com.mytrintrin.www.mytrintrin;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URLEncoder;

public class Paygovwebview extends AppCompatActivity {

    WebView PayGovView;
    String paygovresponse;
    Toolbar Payment;
    SwipeRefreshLayout PayGovRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paygovwebview);
         paygovresponse = getIntent().getStringExtra("paygovresponse");
        PayGovView= (WebView) findViewById(R.id.paygov_webview);
        Payment= (Toolbar) findViewById(R.id.paygovtoolbar);
        Payment.setTitle("Payment");
        setSupportActionBar(Payment);
        PayGovView.getSettings().setJavaScriptEnabled(true);

        PayGovView.getSettings().setLoadWithOverviewMode(true);
        PayGovView.getSettings().setUseWideViewPort(true);
        PayGovView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        PayGovView.setScrollbarFadingEnabled(false);
        PayGovView.getSettings().setBuiltInZoomControls(true);
        PayGovView.getSettings().setJavaScriptEnabled(true);


        /*PayGovRefresh = (SwipeRefreshLayout) findViewById(R.id.paygovrefresh);

        PayGovRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        PayGovView.reload();
                    }
                }
        );*/


        getwebview();
    }

    private void getwebview() {
        //Get webview
        if (AppStatus.getInstance(this).isOnline()) {

            startWebView(paygovresponse);

        }
        else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Paygovwebview.this);
            builder.setTitle("NO INTERNET CONNECTION!!!");
            builder.setMessage("Your offline !!! Please check your connection and come back later.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }

    private void startWebView(String url) {


        PayGovView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.d("URL => ", url);
                view.loadUrl(url);

                return true;

            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    // progressDialog = new ProgressDialog(MainActivity.this);
                    //  progressDialog.setMessage("Loading...");
                    // progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        // Javascript enabled on webview
        PayGovView.getSettings().setLoadWithOverviewMode(true);
        PayGovView.getSettings().setUseWideViewPort(true);
        PayGovView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        PayGovView.setScrollbarFadingEnabled(false);
        PayGovView.getSettings().setBuiltInZoomControls(true);
        PayGovView.getSettings().setJavaScriptEnabled(true);

        //Load url in webview
        PayGovView.loadData(url,"text/html",null);

        PayGovView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        PayGovView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(PayGovView, url);
                if (url.indexOf("/paygovResponse.php") != -1) {
                    PayGovView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressWarnings("unused")
    class MyJavaScriptInterface {
        @JavascriptInterface
        public void processHTML(String html) {
            // process the html as needed by the app
            String status = null;
            if (html.indexOf("Failure") != -1) {
                status = "Transaction Declined!";
            } else if (html.indexOf("Success") != -1) {
                status = "Transaction Successful!";
            } else if (html.indexOf("Aborted") != -1) {
                status = "Transaction Cancelled!";
            } else {
                status = "Status Not Known!";
            }
            //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
            intent.putExtra("transStatus", status);
            startActivity(intent);
            finish();
        }
    }


}
