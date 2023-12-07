package com.ventura.venturawealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import utils.GlobalClass;

public class WebViewSSO extends AppCompatActivity {
    ProgressDialog progressBar;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_web_view_layout);
        initialize();
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(){
        Intent intent = getIntent();

        String url = intent.getStringExtra("link");
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.setBackgroundColor(Color.BLACK);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        GlobalClass.log("LINK : " + url);
        progressBar = ProgressDialog.show(WebViewSSO.this, "", "Please Wait...");

        GlobalClass.log("LINK : " + url);
        myWebView.loadUrl(url);

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //GlobalClass.logTAG, "Processing webview url click...");
                GlobalClass.log("Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                //GlobalClass.logTAG, "Finished loading URL: " + url);
                GlobalClass.log("Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            myWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }


    @Override
    public void onBackPressed( ) {
        super.onBackPressed();
        finish();
    }
}