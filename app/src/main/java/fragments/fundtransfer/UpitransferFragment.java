package fragments.fundtransfer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.upi.merchanttoolkit.security.UPISecurity;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import Structure.Request.BC.ErrorLOG;
import Structure.Response.AuthRelated.ClientLoginResponse;
import connection.SendDataToBCServer;
import enums.eLogType;
import enums.ePrefTAG;
import utils.Constants;
import utils.CustomProgressDialog;
import utils.Encriptions.AES_Encryption.AES_Encryption;
import utils.Encriptions.AES_Encryption.AccordAPIResp;
import utils.Encriptions.AES_Encryption.GetBankAccList;
import utils.Encriptions.AES_Encryption.GetClientSegmentList;
import utils.Encriptions.AES_Encryption.ReqResponse;
import utils.Encriptions.ReusableLogics;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;
import wealth.Dialogs;


public  class UpitransferFragment extends Fragment {
    private String MerchantRefNo;
    private View layout;
    private Button button_submit;
    private EditText edt_upi_amount;
    double amount = 0.00;
    private ProgressDialog progressBar;
    protected boolean mIsResumed;
    private  Context latestContext;
    private TextView txtTransfer;
    private LinearLayout amount_linearLayout;
    private LinearLayout linearButton;
    private RadioGroup rgrpSegment;
    //private RadioGroup rgrpExchange;
    //private RadioButton rbtnNSE,rbtnBSE,rbtnNSEFNO,rbtnNSECURRENCY,rbtnMCX,rbtnNCDEX;
    private String strExchange ="",strSegment="";

    private UPISecurity security = null;
    //private static final String urlFetchRespStat = "https://upitest.hdfcbank.com/upi/transactionStatusQuery";
    private static final String urlFetchRespStat = "https://upi.hdfcbank.com/upi/transactionStatusQuery";

    //String MerchantKey = "8d4900159566444a4ec175924fbfada8"; //uat
    //String MerchantId = "HDFC000000648567"; //uat

    private String MerchantKey = "bc428105f68f39b31517746353d354b2";
    private String MerchantId = "HDFC000011752678";
    private final int UPI_CHOOSER = 1020;
    private LinearLayout radiogrpschemlist_ll;
    private JSONArray segmentJsonArray;


    public static UpitransferFragment newInstance(){
        UpitransferFragment utf = new UpitransferFragment();
        return utf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_upitransfer, container, false);
        init(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
        fetchClientSegments();
        return layout;
    }

    private void init(View view){

        button_submit = (Button)view.findViewById(R.id.button_submit);
        edt_upi_amount = (EditText)view.findViewById(R.id.edt_upi_amount);
        txtTransfer = (TextView)view.findViewById(R.id.txtTransfer);
        amount_linearLayout = (LinearLayout)view.findViewById(R.id.amount_linearLayout);
        linearButton = (LinearLayout)view.findViewById(R.id.linearButton);
        rgrpSegment = (RadioGroup)view.findViewById(R.id.rdg_ec);
        radiogrpschemlist_ll = (LinearLayout) view.findViewById(R.id.radiogrpschemlist_ll);

        if(UserSession.getClientResponse()!=null) {
            if (UserSession.getClientResponse().getAccountType() == ClientLoginResponse.ClientAccountType.Equity) {
                view.findViewById(R.id.rbtnCommodity).setVisibility(View.INVISIBLE);
            } else if(UserSession.getClientResponse().getAccountType() == ClientLoginResponse.ClientAccountType.COMMODITY){
                view.findViewById(R.id.rbtnEquity).setVisibility(View.INVISIBLE);
            }
        }

        rgrpSegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                GlobalClass.log("i", "onCheckedChanged: "+i);
                //rgrpExchange.clearCheck();
                switch (i){
                    case R.id.rbtnEquity:{
                        radioBtnClick(true);
                    }break;
                    case R.id.rbtnCommodity:{
                        radioBtnClick(false);
                    }break;
                }
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAmount = edt_upi_amount.getText().toString();
                if(!strAmount.matches("")){
                    amount = Double.parseDouble(strAmount);
                }
                if(strAmount.matches("")){
                    GlobalClass.showAlertDialog("Amount cannot be blank");
                }else if(amount<1){
                    GlobalClass.showAlertDialog("Invalid Amount");
                }else if(amount>100000){
                    GlobalClass.showAlertDialog("UPI per transaction limit is Rs.1,00,000/-");
                } else if(strExchange.equalsIgnoreCase("")){
                    GlobalClass.showAlertDialog("Exchange not selected");
                } else {//  upiTransfer(amount);
                    progressBar = ProgressDialog.show(getActivity(), "", "Please Wait...");
                    fetchBankAccounts();
                }
            }
        });
    }

    private void radioBtnClick(boolean isEquity){
        if(segmentJsonArray != null) {
            if (isEquity) {
                strSegment = "E";
                strExchange = "";
                createRadioGroup(segmentJsonArray,true);
            } else {
                strSegment = "C";
                strExchange = "";
                createRadioGroup(segmentJsonArray,false);
            }
        }
    }
    private void fetchClientSegments(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GetClientSegmentList segmentList = new GetClientSegmentList();
                AccordAPIResp resp = segmentList.sendReqToAccord("PlaceOrder",getActivity());
                GlobalClass.log("resp.getStatus()", "run: "+resp.getStatus());

                int status = resp.getStatus();
                if(status != 0){
                    /*try {
                        ErrorLOG log = new ErrorLOG();
                        log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                        log.errorMsg.setValue("GETBankACCLIST : " + resp.getData());
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendErrorLog(log);
                    }
                    catch (Exception ex){ex.printStackTrace();}*/
                    GlobalClass.log("resp.getData()", "run: "+resp.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(resp.getData());
                        segmentJsonArray = new JSONArray(jsonObject.getJSONArray("data").toString());
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                radioBtnClick(true);
                            }
                        },500);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            GlobalClass.showAlertDialog(Constants.ERR_MSG2);
                        }
                    },500);
                }
            }
        });
        thread.start();
    }
    @SuppressLint("NewApi")
    private void createRadioGroup(JSONArray jarr,boolean isequity) {
        try {
            radiogrpschemlist_ll.removeAllViews();
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[] {getResources().getColor(R.color.white) }
            );
            RadioGroup rg = new RadioGroup(getActivity()); //create the RadioGroup
            rg.removeAllViews();
            rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
            for(int i=0; i<jarr.length(); i++){
                JSONObject jonbj = jarr.getJSONObject(i);
                String segment  = jonbj.optString("Segment");
                String Exchange = jonbj.optString("Exchange");
                if(isequity){
                    final AppCompatRadioButton[] rb = new AppCompatRadioButton[jarr.length()];
                    if(Exchange.equalsIgnoreCase("Equity")) {
                        rb[i] = new AppCompatRadioButton(getActivity());
                        rb[i].setText(segment);
                        rb[i].setTextColor(Color.WHITE);
                        rb[i].setButtonTintList(colorStateList);
                        rb[i].setTextSize(16);
                        rb[i].setTag(jonbj.optString("Value"));
                        rg.addView(rb[i]);
                    }
                }else {
                    if (Exchange.equalsIgnoreCase("Commodity")) {
                        final AppCompatRadioButton[] rb = new AppCompatRadioButton[jarr.length()];
                        rb[i] = new AppCompatRadioButton(getActivity());
                        rb[i].setText(segment);
                        rb[i].setTextColor(Color.WHITE);
                        rb[i].setButtonTintList(colorStateList);
                        rb[i].setTextSize(16);
                        rb[i].setTag(jonbj.optString("Value"));
                        rg.addView(rb[i]);
                    }
                }
            }
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    GlobalClass.log("strExchange", "onCheckedChanged: "+i);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    AppCompatRadioButton radioButton = radioGroup.findViewById(selectedId);
                    strExchange = radioButton.getTag().toString();
                    GlobalClass.log("strExchange", "onCheckedChanged: "+strExchange);
                }
            });
            radiogrpschemlist_ll.addView(rg);//you add the whole RadioGroup to the layout
            dismissProgressBar();

        }catch (Exception e){
            GlobalClass.onError("",e);
        }

    }

    @SuppressLint("RestrictedApi")
    private void upiTransfer(double amount) {

        dismissProgressBar();

        String UPI = "upi://pay?pa=vslupi@hdfcbank&pn=:VENTURA%20SECURITIES%20LTD&tr="+ MerchantRefNo +"&am="+amount+"&tn=Ventura UPI Payment";
        Uri urupi = Uri.parse(UPI);
        Intent intent = new Intent(Intent.ACTION_VIEW,urupi);

        Intent chooser = Intent.createChooser(intent, "UPI Transfer With");
        if(MobileInfo.sdkVersion > 24){
            startActivityForResult(intent, UPI_CHOOSER, null);
        } else{
            startActivityForResult(chooser, UPI_CHOOSER, null);
        }
    }

    private void fetchBankAccounts(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GetBankAccList bankAcc = new GetBankAccList();
                AccordAPIResp resp = bankAcc.sendReqToAccord("GETBankACCLIST",getActivity());
                GlobalClass.log("resp.getStatus()", "run: "+resp.getStatus());

                int status = resp.getStatus();
                if(status!=0){
                    /*try {
                        ErrorLOG log = new ErrorLOG();
                        log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                        log.errorMsg.setValue("GETBankACCLIST : " + resp.getData());
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendErrorLog(log);
                    }
                    catch (Exception ex){ex.printStackTrace();}*/
                    GlobalClass.log("resp.getData()", "run: "+resp.getData());
                    try {
                        String bankDetails = "";
                        JSONObject jsonObject = new JSONObject(resp.getData());

                        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("data").toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if(i==0) {
                                bankDetails = jsonArray.getJSONObject(i).getString("AccNo");
                            }else {
                                bankDetails += "!"+jsonArray.getJSONObject(i).getString("AccNo");
                            }
                        }
                        generateRefNum(bankDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            GlobalClass.showAlertDialog(Constants.ERR_MSG2);
                        }
                    },500);
                }
            }
        });
        thread.start();
    }
    private void sendUPiRequest(String bankDetails){

        // String url = "https://upitest.hdfcbank.com/upi/mePayInetentReq";
        String url = "https://upi.hdfcbank.com/upi/mePayInetentReq";
        // String TransactionID = "200619911620";
        security  = new UPISecurity();

        String TransactionID = MerchantRefNo;
        String PaymentType = "P2M";
        String TransactionType = "Pay";
        String TransactionDescp = "Ventura UPI Payment";
        String Amount = amount + "";//"1";

        final JSONObject jsonObject = new JSONObject();
        try {
            //String request = MerchantId+"|"+TransactionID+"||"+PaymentType+"|"+TransactionType+"|"+TransactionDescp+"|||"+Amount+"||||||||NA|NA|NA";
            String request = MerchantId+"|"+TransactionID+"||"+PaymentType+"|"+TransactionType+"|"+TransactionDescp+"|||"+Amount+"||||||MEBR|"+bankDetails+"|NA|NA|NA";
            GlobalClass.log("request", "sendUPiRequest: "+request);
            try {
                ErrorLOG log = new ErrorLOG();
                log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                log.errorMsg.setValue(request);
                log.logType.setValue(eLogType.UPIRequest.name);
                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                sendDataToServer.sendAndroidLog(log);
            }
            catch (Exception ex){ex.printStackTrace();}

            String encMsg = security.encrypt(request,MerchantKey);
            jsonObject.put("requestMsg",encMsg);
            jsonObject.put("pgMerchantId",MerchantId);
            GlobalClass.log("jsonObject", "sendUPiRequest: "+jsonObject.toString());
            sendRequest(jsonObject,url);

        } catch (JSONException e) {
            GlobalClass.onError("",e);
        } catch (Exception e) {
            GlobalClass.onError("",e);
        }
    }


    private void sendRequest(final JSONObject jsonObject, final String url){

        RequestQueue requestQueue  = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GlobalClass.log("response", "onResponse: "+response.toString());
                decryptData(response,url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalClass.log("error", "onResponse: "+error.toString());
            }}){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void decryptData(String data,String url){
        try {
            String decryptedMsg = security.decrypt(data,MerchantKey);
            GlobalClass.log("decryptedMsg", "decryptData: "+decryptedMsg);
            String[] TransactionMessage = decryptedMsg.split(Pattern.quote("|"));
            try {
                ErrorLOG log = new ErrorLOG();
                log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                log.errorMsg.setValue(url+" : "+ decryptedMsg);
                log.logType.setValue(eLogType.UPIResponse.name);
                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                sendDataToServer.sendAndroidLog(log);
            }
            catch (Exception ex){ex.printStackTrace();}
            if(url.equalsIgnoreCase(urlFetchRespStat)){
               /*
                txtTransfer.setText("Transaction Date Time : "+TransactionMessage[3] +"\n"+"Transaction Amount : "+TransactionMessage[2]
                        +"\n"+"Ventura Reference No. : "+TransactionMessage[1]+"\n"+"UTI Reference No. : "+TransactionMessage[9]+"\n"
                        +"Transaction Status :"+TransactionMessage[4]+"\n"+"Transaction Description : "+TransactionMessage[5]);
                amount_linearLayout.setVisibility(View.GONE);
                linearButton.setVisibility(View.GONE);
                rgrpSegment.setVisibility(View.GONE);
                rgrpExchange.setVisibility(View.GONE);*/

                new GetTaskUPISuccessTransaction(TransactionMessage).execute();
                //sendTransactionStatusToAccord(TransactionMessage);
            }else{
                upiTransfer(amount);
            }
        } catch (Exception e) {
            GlobalClass.onError("",e);
        }
    }

    public void  showMsgDialog(String msg, boolean exit) {
        showMsgDialog("Alert!",msg, exit);
    }

    public void showMsgDialog(String title,String msg, boolean exit) {
        if (isActivityResumed() && !isSessionExpired()){
            getActivity().runOnUiThread(() -> {
                dismissMsgDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.drawable.ventura_icon);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (exit){
                        getActivity().finishAffinity();
                        Process.killProcess(Process.myPid());
                        System.exit(0);
                    }
                });
                if (exit) builder.setCancelable(false);
                CreateDialog(builder);
            });

        }
    }

    @Override
    public void onResume() {
        latestContext = getActivity();
        GlobalClass.latestContext = getActivity();
        mIsResumed = true;
        //REMOVE_PREVIOUS_DAY_DATA();
        if (this instanceof UpitransferFragment){
            if (!isSessionExpired()){
                handleInactiveSession();
            }else {
                InterectionAlert();
            }
        }
        super.onResume();
    }
    /*
    private void REMOVE_PREVIOUS_DAY_DATA() {
        Date currDate = Calendar.getInstance().getTime();
        String currDateStr = DateUtil.DDMMMYYYY.format(currDate);
        if (!PreferenceHandler.getPreviousLoginDate().equals(currDateStr)) {
            try {
                PreferenceHandler.setPreviousLoginDate(currDateStr);
                PreferenceHandler.setUnitWithdraw(true);
            } catch (Exception ex) {
                VenturaException.Print(ex);
            }
        }
    }*/

    @Override
    public void onPause() {
        mIsResumed = false;
        dismisProgress();
        super.onPause();
    }

    public boolean isActivityResumed() {
        return mIsResumed;
    }

    private boolean SessionExpired = false;
    public boolean isSessionExpired() {
        return SessionExpired;
    }

    private Runnable delayRunnable = () -> {
        if (isActivityResumed()){
            InterectionAlert();
        }else {
            SessionExpired = true;
        }
        removeDelayCallbacks();
    };

    private AlertDialog msgDialog;

    private void CreateDialog(AlertDialog.Builder builder){
        dismissMsgDialog();
        msgDialog = builder.create();
        msgDialog.show();
        msgDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);
        msgDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ScreenColor.VENTURA);
    }

    private void dismissMsgDialog(){
        if (msgDialog!=null){
            msgDialog.dismiss();
            msgDialog=null;
        }
    }

    private void generateRefNum(String bankDetails){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ClientSessionKey",UserSession.getClientResponse().charAuthId.getValue());
            jsonObject.put("ClientCode",UserSession.getLoginDetailsModel().getUserID());
            jsonObject.put("BankCode",1);
            jsonObject.put("UPIAddress","");
            jsonObject.put("TransAmount",amount);
            jsonObject.put("Exchange",strExchange);
            jsonObject.put("Segment",strSegment);
            jsonObject.put("DeviceCode",MobileInfo.getDeviceID(getActivity()));
            jsonObject.put("LastVisitedPage","Ventura1.com");

            fetchAPIResponse(jsonObject,"https://pg.ventura1.com/HDFCUPI/HDFCUPIAppTransactionRequestData",bankDetails);

        } catch (JSONException e) {
            GlobalClass.onError("",e);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_CHOOSER){
            GlobalClass.log("result", "onActivityResult: "+data);
            String response = "";
            Object value = null;
            if(data!=null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        value = bundle.get(key);
                        if (value != null) {
                            GlobalClass.log(" ", String.format("%s %s (%s)", key,
                                    value.toString(), value.getClass().getName()));
                            response = response + " " + value.toString();
                        }
                    }
                    if (value!=null){
                        fetchTransactionResponse();
                    }
                }
            }else {
                Toast.makeText(getActivity(), "Transaction Cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTransactionResponse(){
        final JSONObject jsonObject = new JSONObject();
        try {
            String request = MerchantId+"|"+ MerchantRefNo +"|||||||||||NA|NA";
            GlobalClass.log("request", "sendUPiRequest: "+request);
            String encMsg = security.encrypt(request,MerchantKey);
            jsonObject.put("requestMsg",encMsg);
            jsonObject.put("pgMerchantId",MerchantId);
            GlobalClass.log("jsonObject", "sendUPiRequest: "+jsonObject.toString());
            sendRequest(jsonObject,urlFetchRespStat);
        } catch (JSONException e) {
            GlobalClass.onError("",e);
        } catch (Exception e) {
            GlobalClass.onError("",e);
        }
    }
/*
    private void fetchTransactionResponseFromAccord(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ClientSessionKey",UserSession.getClientResponse().charAuthId.getValue());
            jsonObject.put("DeviceCode",MobileInfo.getDeviceID(getActivity()));
            jsonObject.put("LastVisitedPage","Ventura1.com");
            jsonObject.put("ClientCode",UserSession.getLoginDetailsModel().getUserID());
            jsonObject.put("MerchantRefNo", MerchantRefNo);
            jsonObject.put("BankCode",1);
            jsonObject.put("UPIAddress","");
            jsonObject.put("TransAmount",amount);
            jsonObject.put("Exchange",strExchange);
            jsonObject.put("Segment",strSegment);

            fetchAPIResponse(jsonObject,"https://pg.ventura1.com/HDFCUPI/HDFCUPIAppTransactionResponse","");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

*/
    private CountDownTimer delayCountDownTimer;
    private AlertDialog delayDialog;
    private TextView alertText;
    private Handler delayHandler;
    private final String EXPIRED_MSG = "Your session is expired.";

    private void InterectionAlert() {
        try{
            dismissDelayDialog();
            final AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(getActivity());
            m_alertBuilder.setTitle("Ventura Wealth");
            m_alertBuilder.setIcon(R.drawable.ventura_icon);
            m_alertBuilder.setMessage("");
            m_alertBuilder.setCancelable(false);
            m_alertBuilder.setPositiveButton("OK",
                    (dialog, id) -> {
                        dismissDelayDialog();
                        if (alertText.getText().toString().equals(EXPIRED_MSG)){
                            getActivity().finishAffinity();
                            Process.killProcess(Process.myPid());
                            System.exit(0);
                        }else {
                            if (delayCountDownTimer != null) {
                                delayCountDownTimer.cancel();
                            }
                            handleInactiveSession();
                        }
                    });
            delayDialog = m_alertBuilder.create();
            delayDialog.show();
            alertText = delayDialog.findViewById(android.R.id.message);
            delayDialog.getButton(delayDialog.BUTTON_POSITIVE).setTextColor(getResources()
                    .getColor(R.color.ventura_color));
            if (!isSessionExpired()){
                delayCountDownTimer = new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String msg = "Your session will be expire after " +
                                (millisUntilFinished / 1000) + "seconds. Do you want active?";
                        alertText.setText(msg); }
                    public void onFinish() {
                        SessionExpired = true;
                        GlobalClass.isSessionExpired = true;
                        alertText.setText(EXPIRED_MSG);
                    }
                }.start();
            }else {
                alertText.setText(EXPIRED_MSG);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void removeDelayCallbacks() {
        if (delayHandler != null) {
            delayHandler.removeCallbacks(delayRunnable);
            delayHandler = null;
        }
    }

    private void dismissDelayDialog(){
        if (delayDialog!=null){
            delayDialog.dismiss();
            delayDialog=null;
        }
    }

    private void handleInactiveSession() {
        try{
            int securityTime = Integer.parseInt(VenturaApplication.getPreference().getSharedPrefFromTag(
                    ePrefTAG.SECURITY.name, "60"));
            int inActiveMillis = securityTime * 1000 * 60;
            removeDelayCallbacks();
            delayHandler = new Handler();
            delayHandler.postDelayed(delayRunnable, inActiveMillis);
        }catch (Exception e){
            GlobalClass.onError("",e);
        }
    }

    private CustomProgressDialog progressDialog;
    public void dismisProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    private String fetchAPIResponse(JSONObject jsonObject,String url,String bankDetails){

        final String[] finalResponse = {new String()};
        GlobalClass.log("jsonObject", "fetchAPIResponse: "+jsonObject.toString());
        byte[] jsonData = new byte[0];
        try {
            jsonData = jsonObject.toString().getBytes("UTF-8");
            String strKey = ReusableLogics.getKeyStr(getActivity());
            byte[] key = AES_Encryption.base64ToByteArr(strKey);
            String EncryptionKey = ReusableLogics.getEncryptionKey(getActivity());
            final byte[] encryptedSaltBytes = AES_Encryption.base64ToByteArr(EncryptionKey);
            String encryptedData =   AES_Encryption.EncryptWithKey(jsonData,encryptedSaltBytes);

            final JSONObject jsonBodyRequest = new JSONObject();
            jsonBodyRequest.put("key",strKey);
            jsonBodyRequest.put("data",encryptedData);

            GlobalClass.log("jsonBodyRequest", "fetchAPIResponse: "+jsonBodyRequest.toString());

            Thread thread =   new Thread(new Runnable() {
                @Override
                public void run() {
                    ReqResponse reqResponse = new ReqResponse();
                    AccordAPIResp apiResp =    reqResponse.getDataFromRestPOST(url,jsonBodyRequest.toString(),null);

                    int status = apiResp.getStatus();
                    if(status!=0){

                        GlobalClass.log("apiResp", "fetchAPIResponse: "+apiResp.getData());
                        try {
                            JSONObject jsonObjectResp = new JSONObject(apiResp.getData());
                            String data =  jsonObjectResp.getString("data");

                            byte[] response = AES_Encryption.DecryptWithKey(data,encryptedSaltBytes);
                            finalResponse[0] = new String(response, Charset.forName("UTF-8"));

                            GlobalClass.log("AccordRefNo", "run: "+ finalResponse[0]);
                            JSONObject jsonObject1 = new JSONObject(finalResponse[0]);

                            String MerchantRefNo = jsonObject1.getJSONArray("data").getJSONObject(0).getString("MerchantRefNo");
                            GlobalClass.log("MerchantRefNo", "run: "+MerchantRefNo);
                            UpitransferFragment.this.MerchantRefNo = MerchantRefNo;

                            sendUPiRequest(bankDetails);
                        } catch (Exception e) {
                            GlobalClass.onError("",e);
                        }
                    }else {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                                Toast.makeText(latestContext, "error GenRefNo:"+apiResp.getError(), Toast.LENGTH_LONG).show();
                            }
                        },500);
                    }
                }
            });
            thread.start();
        } catch (UnsupportedEncodingException e) {
            GlobalClass.onError("",e);
        } catch (JSONException e) {
            GlobalClass.onError("",e);
        }
        return finalResponse[0];
    }

    private void dismissProgressBar(){
        if(progressBar!=null){
            progressBar.dismiss();
        }
    }

    class GetTaskUPISuccessTransaction extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;
        String[] transactionMessage;

        GetTaskUPISuccessTransaction(String[] _transactionMessage){
            transactionMessage = _transactionMessage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {

                String merchantRefNo = "";
                String TransDateTime = "";
                String TransAmount = "";
                String CustomerUTIRefNo = "";
                String TransStatus = "";
                String TransStatusDesc = "";
                String AccountNum = "";
                String IFSC = "";
                try {
                    if(transactionMessage.length>1){
                        merchantRefNo = transactionMessage[1];
                    }
                    if(transactionMessage.length>2){
                        TransAmount = transactionMessage[2];
                    }
                    if(transactionMessage.length>3){
                        TransDateTime = transactionMessage[3];
                    }
                    if(transactionMessage.length>4){
                        TransStatus = transactionMessage[4];
                    }
                    if(transactionMessage.length>5){
                        TransStatusDesc = transactionMessage[5];
                    }
                    if(transactionMessage.length>9){
                        CustomerUTIRefNo = transactionMessage[9];
                    }
                    if(transactionMessage.length>16){
                        String[] AcountDetails = transactionMessage[16].split(Pattern.quote("!"));
                        if(AcountDetails.length>2) {
                            AccountNum = AcountDetails[1];
                            IFSC = AcountDetails[2];
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ClientSessionKey",UserSession.getClientResponse().charAuthId.getValue());
                jsonObject.put("DeviceCode",MobileInfo.getDeviceID(getActivity()));
                jsonObject.put("LastVisitedPage","Ventura1.com");
                jsonObject.put("ClientCode",UserSession.getLoginDetailsModel().getUserID());
                jsonObject.put("MerchantRefNo",merchantRefNo);
                jsonObject.put("TransDateTime",TransDateTime);
                jsonObject.put("TransAmount",TransAmount);
                jsonObject.put("CustomerUTIRefNo",CustomerUTIRefNo);
                jsonObject.put("TransStatus",TransStatus);
                jsonObject.put("TransStatusDesc",TransStatusDesc);
                jsonObject.put("UPIBankAccountNo",AccountNum);
                jsonObject.put("UPIBankIFSCCode",IFSC);

                GlobalClass.log("jsonObject", "sendTransactionStatusToAccord: "+jsonObject.toString());
                byte[] jsonData = jsonObject.toString().getBytes("UTF-8");
                String strKey = ReusableLogics.getKeyStr(getActivity());
                byte[] key = AES_Encryption.base64ToByteArr(strKey);
                String EncryptionKey = ReusableLogics.getEncryptionKey(getActivity());
                final byte[] encryptedSaltBytes = AES_Encryption.base64ToByteArr(EncryptionKey);
                String encryptedData =   AES_Encryption.EncryptWithKey(jsonData,encryptedSaltBytes);

                final JSONObject jsonBody = new JSONObject();
                jsonBody.put("key",strKey);
                jsonBody.put("data",encryptedData);

                GlobalClass.log("jsonBody", "sendTransactionStatusToAccord: "+jsonBody.toString());

                ReqResponse reqResponse = new ReqResponse();
                AccordAPIResp apiResp =    reqResponse.getDataFromRestPOST("https://pg.ventura1.com/HDFCUPI/HDFCUPIAppTransactionResponse",jsonBody.toString(),null);

                GlobalClass.log("apiResp", "sendTransactionStatusToAccord: "+apiResp.getData());
                JSONObject jsonObjectResp = new JSONObject(apiResp.getData());
                String data =  jsonObjectResp.getString("data");

                byte[] response = AES_Encryption.DecryptWithKey(data,encryptedSaltBytes);
                String finalResponse = new String(response, Charset.forName("UTF-8"));
                //GlobalClass.showAlertDialog("AccordResp : " + finalResponse);
                GlobalClass.log("finalAccordResponse", "run: "+finalResponse);

                return finalResponse;


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

                txtTransfer.setText("Transaction Date Time : "+transactionMessage[3] +"\n"+"Transaction Amount : "+transactionMessage[2]
                        +"\n"+"Ventura Reference No. : "+transactionMessage[1]+"\n"+"UTI Reference No. : "+transactionMessage[9]+"\n"
                        +"Transaction Status :"+transactionMessage[4]+"\n"+"Transaction Description : "+transactionMessage[5]);
                amount_linearLayout.setVisibility(View.GONE);
                linearButton.setVisibility(View.GONE);
                rgrpSegment.setVisibility(View.GONE);
                radiogrpschemlist_ll.setVisibility(View.GONE);
                if(!result.equalsIgnoreCase("")){
                    GlobalClass.showToast(GlobalClass.latestContext,result);
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
/*
    private void sendTransactionStatusToAccord(String[] TransactionMessage ){

        JSONObject jsonObject = new JSONObject();
        String[] AcountDetails = TransactionMessage[16].split(Pattern.quote("!"));
        String AccountNum = AcountDetails[1];
        String IFSC = AcountDetails[2];
        try {
            jsonObject.put("ClientSessionKey",UserSession.getClientResponse().charAuthId.getValue());
            jsonObject.put("DeviceCode",MobileInfo.getDeviceID(getActivity()));
            jsonObject.put("LastVisitedPage","Ventura1.com");
            jsonObject.put("ClientCode",UserSession.getLoginDetailsModel().getUserID());
            jsonObject.put("MerchantRefNo",TransactionMessage[1]);
            jsonObject.put("TransDateTime",TransactionMessage[3]);
            jsonObject.put("TransAmount",TransactionMessage[2]);
            jsonObject.put("CustomerUTIRefNo",TransactionMessage[9]);
            jsonObject.put("TransStatus",TransactionMessage[4]);
            jsonObject.put("TransStatusDesc",TransactionMessage[5]);
            jsonObject.put("UPIBankAccountNo",AccountNum);
            jsonObject.put("UPIBankIFSCCode",IFSC);
            try {

                GlobalClass.log("jsonObject", "sendTransactionStatusToAccord: "+jsonObject.toString());
                byte[] jsonData = jsonObject.toString().getBytes("UTF-8");
                String strKey = ReusableLogics.getKeyStr(getActivity());
                byte[] key = AES_Encryption.base64ToByteArr(strKey);
                String EncryptionKey = ReusableLogics.getEncryptionKey(getActivity());
                final byte[] encryptedSaltBytes = AES_Encryption.base64ToByteArr(EncryptionKey);
                String encryptedData =   AES_Encryption.EncryptWithKey(jsonData,encryptedSaltBytes);

                final JSONObject jsonBody = new JSONObject();
                jsonBody.put("key",strKey);
                jsonBody.put("data",encryptedData);

                GlobalClass.log("jsonBody", "sendTransactionStatusToAccord: "+jsonBody.toString());

                Thread thread =   new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReqResponse reqResponse = new ReqResponse();
                        AccordAPIResp apiResp =    reqResponse.getDataFromRestPOST("https://pg.ventura1.com/HDFCUPI/HDFCUPIAppTransactionResponse",jsonBody.toString(),null);

                        GlobalClass.log("apiResp", "sendTransactionStatusToAccord: "+apiResp.getData());
                        try {
                            JSONObject jsonObjectResp = new JSONObject(apiResp.getData());
                            String data =  jsonObjectResp.getString("data");

                            byte[] response = AES_Encryption.DecryptWithKey(data,encryptedSaltBytes);
                            String finalResponse = new String(response, Charset.forName("UTF-8"));

                            //GlobalClass.showAlertDialog("AccordResp : " + finalResponse);
                            GlobalClass.log("finalAccordResponse", "run: "+finalResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}