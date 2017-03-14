package com.mytrintrin.www.portal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class GetStarted extends AppCompatActivity {

    private static final String TAG = "";
    WebView Adminwebview;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipe.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        Adminwebview.reload();
                    }
                },3000);
            }
        });
        getwebview();
    }

    private void getwebview() {

        Adminwebview = (WebView) findViewById(R.id.webView);
        if (AppStatus.getInstance(this).isOnline()) {


        /*To load Angular js website*/
            WebSettings ws = Adminwebview.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setAllowFileAccess(true);
            Adminwebview.getSettings().setJavaScriptEnabled(true);
            Adminwebview.getSettings().setLoadWithOverviewMode(true);
            Adminwebview.getSettings().setUseWideViewPort(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                try {
                    Log.d(TAG, "Enabling HTML5-Features");
                    Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                    m1.invoke(ws, Boolean.TRUE);

                    Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE});
                    m2.invoke(ws, Boolean.TRUE);

                    Method m3 = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                    m3.invoke(ws, "/data/data/" + getPackageName() + "/databases/");

                    Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE});
                    m4.invoke(ws, 1024 * 1024 * 8);

                    Method m5 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                    m5.invoke(ws, "/data/data/" + getPackageName() + "/cache/");

                    Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE});
                    m6.invoke(ws, Boolean.TRUE);

                    Log.d(TAG, "Enabled HTML5-Features");
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "Reflection fail", e);
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Reflection fail", e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Reflection fail", e);
                }
            }
                    /*angular js ends*/

            startWebView("http://www.mytrintrin.com/userportal/#/login");

            Adminwebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
                    Log.d("CHECK", "onReceivedSslError");
                    AlertDialog.Builder builder = new AlertDialog.Builder(GetStarted.this);
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
                            Log.d("CHECK", "Button ok pressed");
                            // Ignore SSL certificate errors
                            handler.proceed();

                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("CHECK", "Button cancel pressed");
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
                    GetStarted.this);
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
        Adminwebview.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("URL => ", url);
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
        Adminwebview.loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        if (Adminwebview.canGoBack()) {
            Adminwebview.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}


    /* Adminwebview.getSettings().setJavaScriptEnabled(true);
            Adminwebview.getSettings().setLoadWithOverviewMode(true);
            Adminwebview.getSettings().setUseWideViewPort(true);
            Adminwebview.getSettings().setAllowFileAccess(true);
            Adminwebview.getSettings().setDomStorageEnabled(true);
            Adminwebview.getSettings().setAppCacheMaxSize(1024*1024*8);
            Adminwebview.getSettings().setAppCacheEnabled(true);
            Adminwebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            Adminwebview.getSettings().setBuiltInZoomControls(true);
            Adminwebview.getSettings().setDisplayZoomControls(false);
            Adminwebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            Adminwebview.setScrollbarFadingEnabled(false);
            Adminwebview.getSettings().setSupportMultipleWindows(true);
            Adminwebview.setInitialScale(getScale());*/
