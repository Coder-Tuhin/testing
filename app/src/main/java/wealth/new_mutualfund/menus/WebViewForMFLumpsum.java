package wealth.new_mutualfund.menus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;

import enums.eMFJsonTag;
import utils.GlobalClass;


public class WebViewForMFLumpsum extends Fragment {

    private WebView myWebView;
    private View layout;
    private ProgressDialog progressBar;

    public WebViewForMFLumpsum() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WebViewForMFLumpsum newInstance(String strHtml) {
        WebViewForMFLumpsum fragment = new WebViewForMFLumpsum();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, strHtml);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.webviewformf, container, false);
        init(layout);
        return layout;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(View layout) {
        try {
            myWebView = (WebView) layout.findViewById(R.id.webview);
            myWebView.getSettings().setJavaScriptEnabled(true);
            //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
            myWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    GlobalClass.log("Simply URL","   "+url);
                    if (url.equalsIgnoreCase("https://mf.ventura1.com/Client/AppPaymentResult")){
                        //ObjectHolder.isPassbook = true;
                        GlobalClass.showAlertDialog("Your Purchase order has been placed successfully (subject to payment confirmation).");
                        GlobalClass.fragmentManager.popBackStackImmediate();
                    } else{
                        GlobalClass.showProgressDialog("Please Wait...");
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    GlobalClass.dismissdialog();
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    //progressBar.setVisibility(View.GONE);
                    GlobalClass.dismissdialog();
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    GlobalClass.dismissdialog();
                    handler.proceed();
                    error.getCertificate();
                }
            });

            Bundle args = getArguments();
            String strHtml = args.getString(eMFJsonTag.JDATA.name, "");

            GlobalClass.log("Payment : " +strHtml);
            myWebView.loadDataWithBaseURL("",strHtml, "text/html", "UTF-8","");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}