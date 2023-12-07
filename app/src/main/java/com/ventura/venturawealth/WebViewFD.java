package com.ventura.venturawealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.eLogType;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import wealth.Dialogs;
import wealth.VenturaServerConnect;

public class WebViewFD extends AppCompatActivity {
    ProgressDialog progressBar;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_web_view_layout);
        initialize();
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(){
        new GetTaskFirst().execute();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
    }

    private void ViewInitialization(){
        String url = "https://fixedincome.ventura1.com/Client/MobIntermediate.aspx?authid="
                +UserSession.getClientResponse().charAuthId.getValue();;
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.setBackgroundColor(Color.BLACK);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        GlobalClass.log("LINK : " + url);
        progressBar = ProgressDialog.show(WebViewFD.this, "", "Please Wait...");

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

    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialog = Dialogs.getProgressDialog(WebViewFD.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if(UserSession.getClientResponse().isNeedAccordLogin() || !VenturaServerConnect.accordSessionCheck("FundTransfer")){
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    return clientLoginResponse.charResMsg.getValue();
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mDialog.dismiss();
                if(result.equalsIgnoreCase("")) {
                    ViewInitialization();
                }else{
                    if(result.toLowerCase().contains(Constants.WEALTH_ERR)){
                        GlobalClass.homeActivity.logoutAlert("Logout",Constants.LOGOUT_FOR_WEALTH,false);
                    }else {
                        GlobalClass.showAlertDialog(result);
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

}