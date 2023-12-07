package fragments.excalationmatrix;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

import fragments.DeactivateMarginFragment;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import wealth.wealthStructure.StructDPHoldingRow;

public class ExcalationMatrixWebpage extends Fragment {

    private WebView myWebView;
    private ProgressDialog progressBar;

    public ExcalationMatrixWebpage(){}

    public static ExcalationMatrixWebpage newInstance(){
        return new ExcalationMatrixWebpage();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.transferprocess_layout, container, false);
        try{
            clearCookie();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        myWebView = layout.findViewById(R.id.webview);
        initialize();
        return layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(){
        try {
            myWebView.clearCache(true);
            myWebView.clearFormData();
            myWebView.clearHistory();
            myWebView.clearSslPreferences();

            myWebView.getSettings().setJavaScriptEnabled(true);
            //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
            myWebView.getSettings().setLoadsImagesAutomatically(true);
            myWebView.getSettings().setDomStorageEnabled(true);
            myWebView.setBackgroundColor(Color.BLACK);
            myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            String _url = "https://new.ventura1.com/escalation-matrix";
            if(!_url.equalsIgnoreCase("")) {
                LoadURL(_url);
            }else{
                GlobalClass.fragmentManager.popBackStackImmediate();
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clearCookie(){
        WebStorage.getInstance().deleteAllData();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(new ValueCallback<Boolean>() { @Override public void onReceiveValue(Boolean value) { } });
        CookieManager.getInstance().flush();
    }

    public void LoadURL(String _url){
        try {
            progressBar = ProgressDialog.show(getActivity(), "", "Please Wait...");
            progressBar.setCancelable(true);
            progressBar.setCanceledOnTouchOutside(true);
            myWebView.loadUrl(_url);
            //myWebView.setWebChromeClient(new WebChromeClient());
            /*
            myWebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress == 100) {
                        //do your task
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                    }
                }
            });
            */
            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    /*if (url.toLowerCase().endsWith(".pdf")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        // if want to download pdf manually create AsyncTask here
                        // and download file
                        return true;
                    }*/
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                @Override
                public void onPageCommitVisible (WebView view, String url){
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
}