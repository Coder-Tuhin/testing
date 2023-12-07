package fragments.edis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.Set;

import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import wealth.wealthStructure.StructDPHoldingRow;

public class EDISProcessFragmentReport extends Fragment {

    private ArrayList<edisonerowmodel> selectedHoldingRows;
    private edisclientdatamodel clientData;


    private WebView myWebView;
    private ProgressDialog progressBar;

    public EDISProcessFragmentReport(){}
    public EDISProcessFragmentReport(ArrayList<edisonerowmodel> selectedHoldingRow,edisclientdatamodel clientData) {
        this.selectedHoldingRows = selectedHoldingRow;
        this.clientData = clientData;
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
            String _url = "";
            try{

                JSONObject mainData = new JSONObject();
                mainData.put("clientid", clientData.clientid);
                mainData.put("uccid", UserSession.getLoginDetailsModel().getUserID());
                mainData.put("returnurl", "https://edis.ventura1.com/eDIS/index.html");
                mainData.put("exid", clientData.exid);
                mainData.put("segmentid", clientData.segmentid);

                JSONArray arr = new JSONArray();
                for(int i=0;i<selectedHoldingRows.size();i++) {
                    edisonerowmodel edisonerowmodel =  selectedHoldingRows.get(i);
                    if(edisonerowmodel.isCheckBoxSelected() && Integer.parseInt(edisonerowmodel.getEnterQty())>0) {
                        JSONObject txn1 = new JSONObject();
                        txn1.put("isin", edisonerowmodel.getIsin());
                        txn1.put("qty", (Integer.parseInt(edisonerowmodel.getEnterQty()) * 1000) + "");
                        txn1.put("isinname", edisonerowmodel.getName());
                        arr.put(txn1);
                    }
                }
                mainData.put("txndtls", arr);

                String jsonStr = mainData.toString();
                GlobalClass.log("JSON DATA : " + jsonStr);
                String encodedString = Base64.encodeToString(jsonStr.getBytes(),Base64.DEFAULT);

                GlobalClass.log("BASE64 DATA : " + encodedString);
                if(encodedString != null) {
                    _url = clientData.apiurl+"?reqfrom=mobile&edisdata=" + encodedString;
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
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
                    GlobalClass.log("EDIS URL","   "+url);
                    if (url.startsWith("https://edis.ventura1.com/eDIS/index.html")){
                        //https://www.ventura1.com/Home.aspx?ucc=92267900&status=1&msg=Order%20Captured%20Successfully&resid=1037237005294779&error=Order%20Captured%20Successfully
                        Uri uri = Uri.parse(url);
                        String status = uri.getQueryParameter("status");
                        String msg = uri.getQueryParameter("msg");
                        if(status.equalsIgnoreCase("1")) {
                            //String conf = "eDIS obtained successfully for "+selectedHoldingRow.getEdisQtyForSendToEDIS()+
                              //      " shares of "+selectedHoldingRow.getCompanyName()+". Click OK to proceed with the order";
                            //String refNo = uri.getQueryParameter("resid");
                            //selectedHoldingRow.setEdisQtyForSendToEDIS(-1000);
                            GlobalClass.showAlertDialog(msg);
                        }else{
                            String conf = "Order placement aborted." + msg;
                            GlobalClass.showAlertDialog(conf);
                        }
                        GlobalClass.fragmentManager.popBackStackImmediate();
                    }
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