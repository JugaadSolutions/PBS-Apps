package mytrintrin.com.mytrintrinportal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.net.http.SslError;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    private WebView webView;
    Location currentlocation;

    double latitude;
    double longitude;
    TextView privacypolicy;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getlocation();
        privacypolicy= (TextView) findViewById(R.id.textView);
        // getwebview();
    }

    private void getlocation() {
        /*To get Location*/
        GPSServices mGPSService = new GPSServices(this);
        mGPSService.getLocation();

        if (mGPSService.isLocationAvailable == false) {
            Toast.makeText(this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            currentlocation = new Location("");
            currentlocation.setLatitude(0);
            currentlocation.setLongitude(0);
            //return;
        } else {
            latitude = mGPSService.getLatitude();
            longitude = mGPSService.getLongitude();
            currentlocation = new Location("");
            currentlocation.setLatitude(latitude);
            currentlocation.setLongitude(longitude);
            Log.d("Current location", String.valueOf(currentlocation));


        }

        mGPSService.closeGPS();
         /*ends*/
        getwebview();
    }

    private void getwebview() {
        //Get webview
        webView = (WebView) findViewById(R.id.webView);
        //getlocation();
        if (AppStatus.getInstance(this).isOnline()) {
            Log.d("url", latitude + " " + longitude);
            //startWebView("http://user.mytrintrin.com/portal/?r_view=app&dev_lat=" + latitude + "&dev_long=" + longitude);
             startWebView("http://mytrintrin.com/portal/?r_view=app&dev_lat=" + latitude + "&dev_long=" + longitude);
            //startWebView("mytrintrin.com");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
                    Log.d("CHECK", "onReceivedSslError");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        }
        else {
            Toast.makeText(this, "You are offline!!!!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
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


        webView.setWebViewClient(new WebViewClient() {
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

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
       // webView.getSettings().setBuiltInZoomControls(true);

        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        */

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webView.loadUrl(url);


    }

    @Override
    protected void onPause() {
        super.onPause();
        getlocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getwebview();
       // getlocation();
    }

    @Override
     public void onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack();

        } else {
            super.onBackPressed();
            finish();
        }
    }


    public void onBack(View view) {

        onBackPressed();
    }

    public void gotoprivacypolicy(View view)
    {
        webView.loadUrl("http://www.mytrintrin.com/?page_id=6015&lang=en");
        //startWebView("https://www.mytrintrin.com/?page_id=6015&lang=en");
    }


}



