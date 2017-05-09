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
import android.widget.Toast;

public class FAQS extends AppCompatActivity {

    private Toolbar FaqToolbar;
    private WebView webView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqs);
        FaqToolbar = (Toolbar) findViewById(R.id.faqstoolbar);
        FaqToolbar.setTitle("FAQ's");
        setSupportActionBar(FaqToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //To bypass ssl
        Login.NukeSSLCerts nukeSSLCerts = new Login.NukeSSLCerts();
        nukeSSLCerts.nuke();
        //ends

        getwebview();
    }

    private void getwebview() {

        webView = (WebView) findViewById(R.id.FaqwebView);
        if (AppStatus.getInstance(this).isOnline()) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            startWebView("http://www.mytrintrin.com/?page_id=4308");

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
            builder.setIcon(R.mipmap.ic_signal_wifi_off_black_24dp);
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
    }
}
