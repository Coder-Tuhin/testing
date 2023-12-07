package wealth.new_mutualfund.menus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ventura.venturawealth.R;

import org.json.JSONObject;

import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;


public class MFFundTransfer extends Fragment {

    private WebView myWebView;
    private View layout;
    private ProgressDialog progressBar;

    public MFFundTransfer() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MFFundTransfer newInstance() {
        MFFundTransfer fragment = new MFFundTransfer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            myWebView.getSettings().setLoadsImagesAutomatically(true);
            myWebView.getSettings().setDomStorageEnabled(true);
            myWebView.setBackgroundColor(Color.BLACK);
            myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

            progressBar = ProgressDialog.show(getActivity(), "", "Please Wait...");
            progressBar.setCancelable(false);
            progressBar.setCanceledOnTouchOutside(false);

            //https://secure.ventura1.com/Mobintermediate.aspx?authid=
            String _url = "https://secure.ventura1.com/Mobintermediate.aspx?authid=" +
                    UserSession.getClientResponse().charAuthId.getValue();

            myWebView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    GlobalClass.log("MFFundtransfer URL","   "+url);
                    if (url.startsWith("https://secure.ventura1.com/RedirectToMobileNegativeBalancePlaceOrder.aspx")){
                        //GlobalClass.showAlertDialog("Your Purchase order has been placed successfully (subject to payment confirmation).");
                        //GlobalClass.fragmentManager.popBackStackImmediate();
                        new NegativeBalanceReq(eMessageCodeWealth.NEGATIVE_BALANCE_ORDER.value).execute();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    //progressBar.setVisibility(View.GONE);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    handler.proceed();
                    error.getCertificate();
                }
            });
            myWebView.loadUrl(_url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    class NegativeBalanceReq extends AsyncTask<String, Void, String> {
        int msgCode;

        NegativeBalanceReq(int mCode){
            this.msgCode = mCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if(msgCode == eMessageCodeWealth.NEGATIVE_BALANCE_ORDER.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.NEGATIVE_BALANCE_ORDER.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        displayError(err);
                    } else {
                        if (msgCode == eMessageCodeWealth.NEGATIVE_BALANCE_ORDER.value) {
                            displayError(jsonData.toString());
                        }
                    }
                    GlobalClass.fragmentManager.popBackStackImmediate();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
}