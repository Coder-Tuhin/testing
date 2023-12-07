package fragments.simplysave;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Structure.simplysave.SimplysaveResp;
import Structure.simplysave.bankReq;
import Structure.simplysave.investReq;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.SendDataToSimplysaveServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import models.InvestModel;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 05-Jun-2017.
 */
@SuppressLint("ValidFragment")
public class InvestFragment extends Fragment implements View.OnClickListener{
    private final String TAG = InvestFragment.class.getSimpleName();

    @BindView(R.id.invest)Button invest;
    @BindView(R.id.amtInvested)TextView amtInvested;
    @BindView(R.id.mktValue)TextView mktValue;
    @BindView(R.id.scheme_name)TextView scheme_name;
    @BindView(R.id.follio)TextView follio;
    @BindView(R.id.bankName)TextView bankName;
    @BindView(R.id.amount)EditText amount;
    @BindView(R.id.linearLayout)LinearLayout linearLayout;
    @BindView(R.id.webview)WebView webview;

    private InvestModel investModel;

    public InvestFragment(){super();}
    public InvestFragment(InvestModel investModel){
        this.investModel = investModel;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invest_screen,null);
        ButterKnife.bind(this, view);
        init();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, "SimplySave_Invest",UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    private void init() {
        if (investModel == null)return;
        amtInvested.setText(investModel.invested);
        mktValue.setText(investModel.mktValue);
        scheme_name.setText(investModel.scheme);
        follio.setText(investModel.Follio);
        bankName.setText(investModel.bankName);
        invest.setOnClickListener(this);
        if(investModel.bankName == null || investModel.bankName.equalsIgnoreCase("")){
            bankDetailReq();
        }
    }
    private void bankDetailReq() {
        try{
            bankReq bankreq = new bankReq();
            bankreq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            bankreq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(VenturaApplication.getPackage(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            bankreq.appVersion.setValue(pInfo.versionName);
            bankreq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.bankDetail(bankreq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleBankResponse(byte[] byteArray) {
        SimplysaveResp ssr = new SimplysaveResp(byteArray);
        String response = ssr.response.getValue();
        try {
            JSONArray jsonArray = new JSONArray(response);
            String _bankName = "";
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObj = jsonArray.optJSONObject(i);
                _bankName = (jsonObj.optString("BankName"));
            }
            investModel.bankName = _bankName;
            bankName.setText(investModel.bankName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.invest:
                boolean isalid = true;
                if (TextUtils.isEmpty(amount.getText().toString().trim())) {
                    GlobalClass.showToast(GlobalClass.latestContext,"Please Enter amount");
                    isalid = false;
                }else {
                    double enterAmount = Double.parseDouble(amount.getText().toString());
                    if (enterAmount < Double.parseDouble(investModel.minAmount)){
                        GlobalClass.showToast(GlobalClass.latestContext,"Minimum investment amount is \u20B9. "+investModel.minAmount);
                        isalid = false;
                    }
                    if(isalid && (enterAmount%100) > 0){
                        GlobalClass.showToast(GlobalClass.latestContext,"Kindly note amount should be in multiples of \u20B9. 100");
                        isalid = false;
                    }
                }
                if(isalid) {
                    openAddMoney();
                }
                break;
        }
    }

    private void openAddMoney() {
        try {
            GlobalClass.showProgressDialog("Please wait...");
            investReq investRequest = new investReq();
            investRequest.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            investRequest.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(VenturaApplication.getPackage(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            investRequest.appVersion.setValue(pInfo.versionName);
            investRequest.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            investRequest.type.setValue("");
            investRequest.amount.setValue(Double.parseDouble(amount.getText().toString()));
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.sendInvestDetail(investRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.investHandler  = new InvestHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.investHandler = null;
    }

    class InvestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case INVEST:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            case BANKDETAIL:
                                handleBankResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResponse(byte[] bytes) {
        GlobalClass.dismissdialog();
        SimplysaveResp ssr = new SimplysaveResp(bytes);
        String response = ssr.response.getValue();
        try {
            GlobalClass.log("Invest Resp : " + response);
            JSONObject jsonObj = new JSONObject(response);
            String url = jsonObj.optString("ShortUrl","");
            if(!url.equalsIgnoreCase("") && !url.equalsIgnoreCase("null")) {
                initWebView(url + "https://www.google.com/");
            }
            else{
                String msg = jsonObj.optString("message","");
                if(msg.equalsIgnoreCase("")){
                    msg = jsonObj.optString("Return_Message","");
                    if(msg.equalsIgnoreCase(""))
                        msg = "Unable to Process your Request please try after some time";
                }
                GlobalClass.showAlertDialog(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url) {
        linearLayout.setVisibility(View.GONE);
        webview.setVisibility(View.VISIBLE);
        WebSettings webSett = webview.getSettings();
        webSett.setJavaScriptEnabled(true);
        //webview.addJavascriptInterface(new JSObject(), "injectedObject");
        webSett.setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                GlobalClass.log("Simply URL","   "+url);
                if (url.startsWith("https://www.google.com")){
                    //ObjectHolder.isPassbook = true;
                    GlobalClass.fragmentManager.popBackStackImmediate();
                }
                else{
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
            public void onReceivedSslError(WebView view, SslErrorHandler handler,     SslError error) {
                super.onReceivedSslError(view, handler, error);
                GlobalClass.dismissdialog();
                handler.proceed();
                error.getCertificate();
            }
        });
        webview.loadUrl(url);
    }
}
