package fragments.fundtransfer;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ventura.venturawealth.R;

import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;

public class TransferProcessFragment extends Fragment {

    private int _selectedPosition = 0;
    private WebView myWebView;
    private ProgressDialog progressBar;

    public static TransferProcessFragment newInstance(int _selectedPosition){
        TransferProcessFragment tpf = new TransferProcessFragment();
        try {
            Bundle args = new Bundle();
            args.putInt(StaticVariables.ARG_1, _selectedPosition);
            tpf.setArguments(args);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return tpf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args  = getArguments();
        try {
            assert args!=null;
            _selectedPosition = args.getInt(StaticVariables.ARG_1,0);
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    private void initialize(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
            } else {
                CookieManager.getInstance().setAcceptCookie(true);
            }
            myWebView.clearCache(true);
            myWebView.clearFormData();
            myWebView.clearHistory();
            myWebView.clearSslPreferences();

            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.getSettings().setSupportMultipleWindows(true);
            myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
            myWebView.getSettings().setLoadsImagesAutomatically(true);
            myWebView.getSettings().setDomStorageEnabled(true);
            myWebView.setBackgroundColor(Color.BLACK);
            myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

            String _url = "";
            switch (_selectedPosition){
                case 1: //PayOUT
                    _url = "https://services.ventura1.com/PAM/Mobintermediate.aspx?authid=" +
                            UserSession.getClientResponse().charAuthId.getValue();
                    break;
                case 2: //Intermediate
                    _url = "https://secure.ventura1.com/Mobintermediate.aspx?authid="
                            + UserSession.getClientResponse().charAuthId.getValue()+"&opt=IFT";
                    break;
                default: //PayIN
                    _url = "https://secure.ventura1.com/Mobintermediate.aspx?authid="
                            + UserSession.getClientResponse().charAuthId.getValue();
                    break;
            }
            LoadURL(_url);
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
                public void onPageFinished(WebView view, String url) {
                    /*if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }*/
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