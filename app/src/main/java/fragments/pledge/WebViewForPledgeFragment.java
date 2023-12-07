package fragments.pledge;

import static com.blankj.utilcode.util.SnackbarUtils.getView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import enums.eMFJsonTag;
import utils.GlobalClass;
import wealth.new_mutualfund.menus.WebViewForMF;

public class WebViewForPledgeFragment extends AppCompatActivity {

    private WebView myWebView;

    private HomeActivity homeActivity;

    public WebViewForPledgeFragment() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewformf);
//        if (context instanceof HomeActivity) {
        //  homeActivity = (HomeActivity) this;
//        }
        init();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        try {
            myWebView = (WebView) findViewById(R.id.webview);


            // myWebView.getSettings().setJavaScriptEnabled(true);
            //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
//            myWebView.setWebViewClient(new WebViewClient(){
//                @Override
//                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    super.onPageStarted(view, url, favicon);
//                    GlobalClass.log("SimplyURL","   "+url);
////                    if (url.equalsIgnoreCase("https://mf.ventura1.com/Client/AppPaymentResult")){
////                        ObjectHolder.isPassbook = true;
////                        GlobalClass.showAlertDialog("Your Purchase order has been placed successfully (subject to payment confirmation).");
////                        GlobalClass.fragmentManager.popBackStackImmediate();
////                    } else{
////                        GlobalClass.showdialog("Please Wait...");
////                    }
//                }
//
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    super.onPageFinished(view, url);
//                    GlobalClass.dismissdialog();
//                }
//
//                @Override
//                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                    super.onReceivedError(view, request, error);
//                    //progressBar.setVisibility(View.GONE);
//                    GlobalClass.dismissdialog();
//                }
//
//                @Override
//                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    super.onReceivedSslError(view, handler, error);
//                    GlobalClass.dismissdialog();
//                    handler.proceed();
//                    error.getCertificate();
//                }
//            });

            //   Bundle args = getArguments();
            String strHtml = getIntent().getStringExtra("Url");

            GlobalClass.log("init: ", strHtml);
            //   myWebView.loadDataWithBaseURL("",strHtml, "text/html", "UTF-8","");

            startWebView(strHtml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        myWebView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                GlobalClass.log("SimplyURL", "   " + url);
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(WebViewForPledgeFragment.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }

            public void onPageFinished(WebView view, String url) {
                GlobalClass.log("onPageFinishedurl: ", url);
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();

                    GlobalClass.log("onPageFinished: ", exception.getMessage());
                }

//                GlobalClass.fragmentManager.popBackStackImmediate();
//            myWebView.goBack();

            }

        });

        // Javascript inabled on webview
        myWebView.getSettings().setJavaScriptEnabled(true);

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
        myWebView.loadUrl(url);


    }

    // Open previous opened link from history on webview when back button pressed

    //    @Override
//    // Detect when the back button is pressed
    public void onBackPressed() {
//        if(myWebView.canGoBack()) {
//            myWebView.goBack();
//        } else {
        // Let the system handle the back button
//        PledgeFragment ls = new PledgeFragment();
//        homeActivity.FragmentTransaction(ls, R.id.container_body, false);

        PledgeFragment.shouldRefreshOnResume=true;
        super.onBackPressed();


//        Intent intent=new Intent(WebViewForPledgeFragment.this,HomeActivity.class);
//        intent.putExtra("from","1");
//        startActivity(intent);

//        PledgeFragment f2 = new PledgeFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container_body, f2);
//        transaction.addToBackStack(null);
//        transaction.commit();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (getView() == null) {
//            return;
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    // handle back button's click listener
//                    PledgeFragment ls = new PledgeFragment();
//                    homeActivity.FragmentTransaction(ls, R.id.container_body, false);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//
    }
}

