package fragments.sso;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import Structure.Request.Auth.GuestLoginRequest;
import Structure.Request.Auth.LoginRequest;
import Structure.Request.Auth.NewServerIPRequest;
import Structure.Request.Auth.NewServerIPResp;
import Structure.Response.AuthRelated.ClientLoginResponse;
import connection.Connect;
import connection.ConnectionProcess;
import connection.SendDataToAuthServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eLoginType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eSSOApi;
import enums.eSSOJourney;
import enums.eSSOTag;
import enums.eSocketClient;
import utils.AeSimpleSHA1;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;

public class Sso_setPinFragmnet extends Fragment implements ConnectionProcess {
    TextView btn_confirm;
    TextInputEditText et_confirmpin,et_pin;
    TextInputLayout TIL_pin,confirmPinText;
    private static eSSOJourney journey;
    public static Handler setPinHandler;

    public static Sso_setPinFragmnet newInstance() {
        Sso_setPinFragmnet fragment = new Sso_setPinFragmnet();
        return fragment;
    }
    public static Sso_setPinFragmnet newInstance(eSSOJourney _journey) {
        Sso_setPinFragmnet fragment = new Sso_setPinFragmnet();
        journey = _journey;
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        setPinHandler = new SetPinHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setPinHandler = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_clientpin_screen,container,false);
        btn_confirm = mView.findViewById(R.id.btn_confirm);
        et_confirmpin = mView.findViewById(R.id.et_confirmpin);
        et_pin = mView.findViewById(R.id.et_pin);
        TIL_pin = mView.findViewById(R.id.TIL_pin);
        confirmPinText = mView.findViewById(R.id.confirmPinText);
        btn_confirm.setEnabled(false);
        btn_confirm.setAlpha(0.5f);
        et_pin.setTransformationMethod(new PasswordTransformationMethod());
        et_confirmpin.setTransformationMethod(new PasswordTransformationMethod());
        et_confirmpin.setEnabled(false);
        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TIL_pin.setError(null);
                if(charSequence.length()>3){
                    //btn_confirm.setEnabled(true);
                    et_confirmpin.setEnabled(true);
                }else {
                    et_confirmpin.setEnabled(false);
                    enableSublitBtn(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_confirmpin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPinText.setError(null);
                if(charSequence.length()>3){
                    enableSublitBtn(true);
                }else {
                    enableSublitBtn(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIL_pin.setError("");
                confirmPinText.setError("");
                if(et_pin.getText().toString().trim().equalsIgnoreCase("")){
                    TIL_pin.setError("Please Enter Valid PIN");
                }else if(et_pin.getText().length() < 4){
                    TIL_pin.setError("Please enter 4 digit PIN");
                }else if(et_confirmpin.getText().toString().trim().equalsIgnoreCase("")){
                    confirmPinText.setError("Please Enter Valid PIN");
                }else if(et_confirmpin.getText().length() < 4){
                    confirmPinText.setError("Please enter 4 digit PIN");
                }else if(!et_confirmpin.getText().toString().equalsIgnoreCase(et_pin.getText().toString())){
                    confirmPinText.setError("New Pin does not match");
                }else {
                    CallSSoAPI(UserSession.getLoginDetailsModel().getUserID(),et_pin.getText().toString());
                }
            }
        });
        return mView;
    }

    private void enableSublitBtn(boolean btn){
        btn_confirm.setEnabled(btn);
        btn_confirm.setAlpha(btn?1:0.5f);
    }

    private void CallSSoAPI (String Clientcode,String pass){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            enableSublitBtn(false);
            GlobalClass.showProgressDialog(Constants.pleasewait);
            AeSimpleSHA1 encodePwd = new AeSimpleSHA1();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "";
            JSONObject jsonBody = new JSONObject();
            if(journey == eSSOJourney.firstlogin){
                URL = eSSOApi.BASEURL.value + eSSOApi.setpin.value;
                jsonBody.put("pin", encodePwd.MD5(pass));
            }else {
                URL = eSSOApi.BASEURL.value + eSSOApi.updatepin.value;
                jsonBody.put("newpin", encodePwd.MD5(pass));
            }

            jsonBody.put("client_id", Clientcode);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    ParseResponse(response);
                    if(journey == eSSOJourney.firstlogin){
                        VenturaException.SSOPrintLog(eSSOApi.setpin.value,"SuccessRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+response + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                    }else {
                        VenturaException.SSOPrintLog(eSSOApi.updatepin.value,"SuccessRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+response + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();

                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            if(journey == eSSOJourney.firstlogin){
                                VenturaException.SSOPrintLog(eSSOApi.setpin.value,"ErrRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            }else {
                                VenturaException.SSOPrintLog(eSSOApi.updatepin.value,"ErrRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            }
                            TIL_pin.setError("Error : Request timeout. Please try after sometime.");
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                            if (journey == eSSOJourney.firstlogin) {
                                VenturaException.SSOPrintLog(eSSOApi.setpin.value, "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            } else {
                                VenturaException.SSOPrintLog(eSSOApi.updatepin.value, "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            }
                            ParseErrorResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_pin.setError(Constants.ERR_MSG);
                        enableSublitBtn(true);
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(eSSOTag.ContentType.name, eSSOTag.ContentType.value);
                    params.put(eSSOTag.session_id.name, PreferenceHandler.getSSOSessionID());
                    params.put(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
                    params.put(eSSOTag.Authorization.name, "Bearer " + PreferenceHandler.getSSOAuthToken());
                    return params;
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                        PreferenceHandler.setSSOSessionID(response.headers.get("session_id"));
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(180*1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ParseErrorResponse(String response) {

        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message","");
            TIL_pin.setError(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ParseResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message","");
            if(msg.contains("You can not keep new PIN as your old PIN")){
                TIL_pin.setError(msg);
            }else {
                goToHomeScreen();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //go to home screen
    private String domainNameIP = ObjectHolder.connconfig.getAuthIP();;
    private int AuthConnectionRetryCount = 0;
    boolean isTryToConnectBcast = false;

    private void goToHomeScreen(){

        if (ObjectHolder.connconfig.getbCastServerIP().equalsIgnoreCase("")) {
            //Connect to Auth Server...
            connectToAuthServer();
        }else {
            if (UserSession.getLoginDetailsModel().isActiveUser()) {
                connectToTradeBCastServer();
            }else {
                OpenFinalScreen();
            }
        }
    }
    public void OpenFinalScreen(){
        if(journey == eSSOJourney.firstlogin){
            final Fragment fragment = AskGoogleAuthFragment.newInstance();
            GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
        }else {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void connected() {
        if(serverConnect == eSocketClient.AUTH){
            if(isTryToConnectBcast){
                new SendSecondIPReq().execute();
            } else {
                AuthConnectionRetryCount = 0;
                new SendLoginReq().execute();
            }
        }
    }

    @Override
    public void serverNotAvailable() {
        if(serverConnect == eSocketClient.BC){
            socketNotAvailableButton();
        }
        else if(serverConnect == eSocketClient.AUTH) {
            GlobalClass.addAndroidLog(eLogType.AuthConnectFailed.name, "authip|" + ObjectHolder.connconfig.getAuthIP() + "|" + MobileInfo.getMobileData() + "|splash", "");
            if (AuthConnectionRetryCount < 2) {
                String tempAuthIP = this.getContext().getString(R.string.AUTH_IP);
                if(ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(domainNameIP)){
                    tempAuthIP = this.getContext().getString(R.string.AUTH_IP_1);
                }/* else if(ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(this.getContext().getString(R.string.AUTH_IP_1))){
                    tempAuthIP = this.getContext().getString(R.string.AUTH_IP_2);
                }else if (ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(this.getContext().getString(R.string.AUTH_IP_2))) {
                    tempAuthIP = this.getContext().getString(R.string.AUTH_IP_3);
                }*/
                ObjectHolder.connconfig.setAuthIP(tempAuthIP);
                connectToAuthServer();
            } else {
                new eAuthRestAPIReq().execute();
               //GlobalClass.showAlertDialog(Constants.ERR_SERVER_CONNECTION + ObjectHolder.connconfig.getAuthLastDigit(), true);
            }
        }
    }

    @Override
    public void sensexNiftyCame() {
        OpenFinalScreen();
    }

    //for second IP
    private eSocketClient serverConnect = eSocketClient.BC;
    //int countT = 0;
    private void connectToTradeBCastServer(){
        try {
            /*
            if(countT == 0){
                //ObjectHolder.connconfig.setTradeServerIP(serverIP.tradeServerDomainName.getValue());
                ObjectHolder.connconfig.setBcServerPort(1234);
                //UserSession.getClientResponse().setTradeServerIP(serverIP.serverIp.getValue());
                //UserSession.getClientResponse().setTradeDomainName(serverIP.tradeServerDomainName.getValue());
                UserSession.getClientResponse().setBCPort(1234);
            }
            countT++;*/
            isTryToConnectBcast = true;
            serverConnect = eSocketClient.BC;
            GlobalClass.showProgressDialog("Please wait connecting to Server...");
            Connect.connect(this.getContext(), this, eSocketClient.BC);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void connectToAuthServer(){
        try {
            String authIP = ObjectHolder.connconfig.getAuthIP();
            GlobalClass.showProgressDialog("Connecting to Auth Server..." + authIP.substring(authIP.length()-2));
            serverConnect = eSocketClient.AUTH;
            AuthConnectionRetryCount++;
            Connect.connect(getContext(), this, eSocketClient.AUTH);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private class SendSecondIPReq extends AsyncTask<Void, Void, Void> {

        private SendDataToAuthServer sendDataToAuthServer = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                eMessageCode msgCode;
                byte[] data;

                msgCode = eMessageCode.GET_SERVER_SECONDIP_BCAST;
                //Config config = VenturaApplication.getPreference().getConnectionConfig();

                NewServerIPRequest loginRequest = new NewServerIPRequest();
                loginRequest.username.setValue(UserSession.getLoginDetailsModel().getUserID());
                loginRequest.versionNumber.setValue(MobileInfo.getAppVersionCode());
                loginRequest.osType.setValue(1);
                loginRequest.noOfTy.setValue(authServerConnectionCount);
                loginRequest.currentIP.setValue(UserSession.getClientResponse().getBCastServerIP());
                data = loginRequest.data.getByteArr((short) msgCode.value);
                sendDataToAuthServer = new SendDataToAuthServer(getContext(), null, msgCode, data);

            } catch (Exception e) {
                VenturaException.Print(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendDataToAuthServer.execute();
        }
    }
    private int authServerConnectionCount = 0;
    private void socketNotAvailableButton(){
        AppCompatActivity act = (AppCompatActivity) GlobalClass.latestContext;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(authServerConnectionCount < 3) {
                    authServerConnectionCount++;
                    connectToAuthServer();
                }else{
                    new AlertBox(GlobalClass.latestContext, "", "OK", Constants.ERR_MSG_SERVERNOTAVL_CONNECTION + ObjectHolder.connconfig.getBCastLastDigit(), true);
                }
            }
        });

    }
    private void handleServerIP(NewServerIPResp serverIP){
        try{
            GlobalClass.log("Second IP Bcast : " + serverIP.toString());
            ObjectHolder.connconfig.setbCastServerIP(serverIP.tradeServerDomainName.getValue());
            ObjectHolder.connconfig.setBcServerPort(serverIP.portBC.getValue());
            VenturaApplication.getPreference().setConnectionConfig(ObjectHolder.connconfig);
            UserSession.getClientResponse().setBCastServerIP(serverIP.serverIp.getValue());
            UserSession.getClientResponse().setBCastDomainName(serverIP.tradeServerDomainName.getValue());
            UserSession.getClientResponse().setBCPort(serverIP.portBC.getValue());
            connectToTradeBCastServer();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private class SendLoginReq extends AsyncTask<Void, Void, Void> {

        private SendDataToAuthServer sendDataToAuthServer = null;


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                eMessageCode msgCode;
                byte[] data;
                if (UserSession.getLoginDetailsModel().isClient()) {
                    msgCode = eMessageCode.CLIENT_LOGIN_WITH2FACTOR;
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.username.setValue(UserSession.getLoginDetailsModel().getUserID());
                    loginRequest.password.setValue(UserSession.getLoginDetailsModel().getPassword());
                    loginRequest.versionNumber.setValue(MobileInfo.getAppVersionCode());
                    loginRequest.imeiNumber.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
                    loginRequest.panDOB.setValue(UserSession.getLoginDetailsModel().getPan());
                    loginRequest.isPAN.setValue(UserSession.getLoginDetailsModel().getType());
                    loginRequest.deviceName.setValue(Build.MODEL);
                    //TODO have to checked
                    loginRequest.fcmToken.setValue(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.TOKEN.name,""));
                    loginRequest.osType.setValue(1);
                    loginRequest.publicIP.setValue(MobileInfo.getIPAddress(true));
                    loginRequest.androidVersion.setValue(Build.VERSION.RELEASE);
                    loginRequest.appVersionname.setValue(MobileInfo.getAppVersionName());
                    loginRequest.mobileNumber.setValue(UserSession.getLoginDetailsModel().getMobileNo());
                    loginRequest.loginType.setValue(eLoginType.AUTOLOGIN.value);
                    data = loginRequest.data.getByteArr((short) msgCode.value);
                } else {
                    msgCode = eMessageCode.GUESTLOGIN_RCKEY_REQUEST;
                    GuestLoginRequest loginRequest = new GuestLoginRequest();
                    loginRequest.charUserName.setValue(UserSession.getLoginDetailsModel().getMobileNo());
                    loginRequest.charMobileNo.setValue(UserSession.getLoginDetailsModel().getMobileNo());
                    loginRequest.charImeiNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
                    loginRequest.charModelNo.setValue(Build.MODEL);
                    data = loginRequest.data.getByteArr((short) msgCode.value);
                }
                sendDataToAuthServer = new SendDataToAuthServer(GlobalClass.latestContext, null, msgCode, data);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendDataToAuthServer.execute();
        }
    }

    private void handleClientLogin() {
        try {
            GlobalClass.dismissdialog();
            ClientLoginResponse clr = UserSession.getClientResponse();//new ClientLoginResponse(response);
            switch (clr.getSuccess()){
                case SUCCESS:
                    if (clr.getAppUpdate() == ClientLoginResponse.eAppUpdate.COMPULSARY){
                        AppUpdateAlert();
                    } else if (clr.getAppUpdate() == ClientLoginResponse.eAppUpdate.TRUE) {
                        AppUpdateAlertOptional(clr);
                    } else{
                        loginFinalization(clr);
                    }
                    break;
                case SYSTEM_UNDER_MAINTAINANCE:
                    GlobalClass.showAlertDialog(clr.charResMsg.getValue(),true);
                    break;
                default:
                    UserSession.InitWithMobileNo();
                    final String msg = clr.charResMsg.getValue();
                    if (!TextUtils.isEmpty(msg)){
                        GlobalClass.showToast(GlobalClass.latestContext,msg);
                    }
                    /*
                    final AppCompatActivity act  = (AppCompatActivity) GlobalClass.latestContext;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //openClientLoginScreen
                        }
                    });*/

                    break;
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    private void loginFinalization(ClientLoginResponse clr){
        try{
            if (!TextUtils.isEmpty(clr.mobileNumber.getValue())){
                UserSession.getLoginDetailsModel().setMobileNo(clr.mobileNumber.getValue());
                UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
            }
            final String msg = clr.charResMsg.getValue();
            if (!TextUtils.isEmpty(msg)){
                GlobalClass.showToast(GlobalClass.latestContext,msg);
            }
            goToHomeScreen();

        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void AppUpdateAlert() {

        final AppCompatActivity act  = (AppCompatActivity) GlobalClass.latestContext;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(GlobalClass.latestContext)
                        .setTitle("New Version Available!")
                        .setMessage("Current Version is " + MobileInfo.getAppVersionName() + ". Update Available.")
                        .setPositiveButton("Update", (dialogInterface, i) -> {
                            onAppUpdate();
                        })
                        //.setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        });
    }
    private void AppUpdateAlertOptional(ClientLoginResponse clr) {

        final AppCompatActivity act  = (AppCompatActivity) GlobalClass.latestContext;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(GlobalClass.latestContext)
                        .setTitle("New Version Available!")
                        .setMessage("Current Version is " + MobileInfo.getAppVersionName() + " Update Available.")
                        .setPositiveButton("Update", (dialogInterface, i) -> {
                            onAppUpdate();
                        })
                        .setNegativeButton("Later", (dialogInterface, i) -> {
                            loginFinalization(clr);
                        })
                        .create()
                        .show();
            }
        });
    }
    private void onAppUpdate(){
        Uri uri = Uri.parse("market://details?id=" + VenturaApplication.getPackage() );
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(goToMarket);
        getActivity().finish();
    }

    private class SetPinHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case CLIENT_LOGIN_WITH2FACTOR:
                            handleClientLogin();
                            break;
                        case GET_SERVER_SECONDIP_BCAST:
                            NewServerIPResp serverIP =  new NewServerIPResp(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                            handleServerIP(serverIP);
                            break;
                        default:
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
    private void handleRestApiResp(String response) throws Exception{
        JSONObject jsonObject = new JSONObject(response);
        //{"msg":"","PortWealth":46033,"PortBC":51531,"PortRC":51525,"tradingServerType":1,
        // "bCastDomainName":"180.179.130.247","bcastServerIp":"180.179.130.247",
        // "tradeDomainName":"180.179.130.247","WealthIP":"43.242.213.75",
        // "tradeServerIP":"180.179.130.247","status":1}
        int status = jsonObject.getInt("status");
        if(status == 1){
            int PortWealth = jsonObject.getInt("PortWealth");
            int PortBC = jsonObject.getInt("PortBC");
            int PortRC = jsonObject.getInt("PortRC");
            int tradingServerType = jsonObject.getInt("tradingServerType");
            String bCastDomainName = jsonObject.getString("bCastDomainName");
            String bcastServerIp = jsonObject.getString("bcastServerIp");
            String tradeDomainName = jsonObject.getString("tradeDomainName");
            String WealthIP = jsonObject.getString("WealthIP");
            String tradeServerIP = jsonObject.getString("tradeServerIP");
            int searchport = jsonObject.getInt("searchport");
            String searchip = jsonObject.getString("searchip");
            if(!jsonObject.isNull("charIsUpdateAvailable")) {
                String charIsUpdateAvailable = jsonObject.getString("charIsUpdateAvailable");
                if(charIsUpdateAvailable.length() > 0) {
                    UserSession.getClientResponse().charIsUpdateAvailable.setValue(charIsUpdateAvailable.charAt(0));
                }else{
                    UserSession.getClientResponse().charIsUpdateAvailable.setValue(' ');
                }
            }else{
                UserSession.getClientResponse().charIsUpdateAvailable.setValue(' ');
            }
            UserSession.getClientResponse().setBCastServerIP(bcastServerIp);
            UserSession.getClientResponse().setBCastDomainName(bCastDomainName);
            UserSession.getClientResponse().setBCPort(PortBC);

            UserSession.getClientResponse().setTradeServerIP(tradeServerIP);
            UserSession.getClientResponse().setTradeDomainName(tradeDomainName);
            UserSession.getClientResponse().setRCPort(PortRC);

            UserSession.getClientResponse().setWealthDomainName(WealthIP);
            UserSession.getClientResponse().intPortWealth.setValue(PortWealth);
            UserSession.getClientResponse().setServerType(tradingServerType);

            UserSession.getClientResponse().searchIP.setValue(searchip);
            UserSession.getClientResponse().searchPort.setValue(searchport);

            UserSession.getClientResponse().charAuthId.setValue("bypass");

            UserSession.setClientResponse(UserSession.getClientResponse());

            ObjectHolder.connconfig.setbCastServerIP(UserSession.getClientResponse().getBCastDomainName());
            ObjectHolder.connconfig.setTradeServerIP(UserSession.getClientResponse().getTradeDomainName());
            ObjectHolder.connconfig.setWealthServerIP(UserSession.getClientResponse().getWealthDomainName());

            ObjectHolder.connconfig.setBcServerPort(UserSession.getClientResponse().intPortBC.getValue());
            ObjectHolder.connconfig.setRcServerPort(UserSession.getClientResponse().intPortRC.getValue());
            ObjectHolder.connconfig.setWealthServerPort(UserSession.getClientResponse().intPortWealth.getValue());
            ObjectHolder.connconfig.setSearchEngineIP(UserSession.getClientResponse().searchIP.getValue());
            ObjectHolder.connconfig.setSearchEngineServerPort(UserSession.getClientResponse().searchPort.getValue());

            VenturaApplication.getPreference().setConnectionConfig(ObjectHolder.connconfig);

            if (UserSession.getClientResponse().getAppUpdate() == ClientLoginResponse.eAppUpdate.COMPULSARY){
                AppUpdateAlert();
            } else if (UserSession.getClientResponse().getAppUpdate() == ClientLoginResponse.eAppUpdate.TRUE) {
                AppUpdateAlertOptional(UserSession.getClientResponse());
            }else {
                loginFinalization(UserSession.getClientResponse());
            }
        }else{
            String msg = jsonObject.getString("msg");
            GlobalClass.log("Rest Api Error : " + msg);
            //GlobalClass.showAlertDialog(msg);
            GlobalClass.showAlertDialog(Constants.ERR_SERVER_CONNECTION + ObjectHolder.connconfig.getAuthLastDigit(), true);
        }
    }

    private class eAuthRestAPIReq extends AsyncTask<String, Void, String>  {

        eAuthRestAPIReq(){
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Connecting to Auth Server...");
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(String... urls)  {

            //strToLog = (url + "   Param :" + param + " \n");
            try {
                //String url = "https://vwaws.ventura1.com/authrestapi/getTradeServer";
                String url = "https://vw.ventura1.com/authrestapi/getTradeServer";

                JSONObject jreqData = new JSONObject();
                jreqData.put("username",UserSession.getLoginDetailsModel().getUserID());
                jreqData.put("versionNumber",MobileInfo.getAppVersionCode()+"");
                jreqData.put("imeiNumber",MobileInfo.getDeviceID(GlobalClass.latestContext));
                jreqData.put("deviceName",Build.MODEL);
                jreqData.put("fcmToken",VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.TOKEN.name,""));
                jreqData.put("osType",1+"");
                jreqData.put("publicIP",MobileInfo.getIPAddress(true));
                jreqData.put("androidVersion",Build.VERSION.RELEASE);
                jreqData.put("appVersionname",MobileInfo.getAppVersionName());
                jreqData.put("mobileNumber",UserSession.getLoginDetailsModel().getMobileNo());
                jreqData.put("loginType",eLoginType.AUTOLOGIN.value+"");

                String dataS = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dataS = Base64.getEncoder().encodeToString(jreqData.toString().getBytes());
                }
                String param = "data="+dataS+"&ucc="+ UserSession.getLoginDetailsModel().getUserID();
                String strresponse = sendRequest(url, param);
                GlobalClass.log("EDIS Res: "+strresponse + " req: "+param);
                return strresponse;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)  {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            try {
                if(s != null && !s.equalsIgnoreCase("")) {
                    handleRestApiResp(s);
                }else{
                    new eAuthRestAPIReq73().execute();
                }
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
    }
    private class eAuthRestAPIReq73 extends AsyncTask<String, Void, String>  {

        eAuthRestAPIReq73(){
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Connecting to Auth Server...");
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(String... urls)  {

            //strToLog = (url + "   Param :" + param + " \n");
            try {
                //String url = "https://vwaws.ventura1.com/authrestapi/getTradeServer";
                String url = "https://vw73.ventura1.com/authrestapi/getTradeServer";

                JSONObject jreqData = new JSONObject();
                jreqData.put("username",UserSession.getLoginDetailsModel().getUserID());
                jreqData.put("versionNumber",MobileInfo.getAppVersionCode()+"");
                jreqData.put("imeiNumber",MobileInfo.getDeviceID(GlobalClass.latestContext));
                jreqData.put("deviceName",Build.MODEL);
                jreqData.put("fcmToken",VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.TOKEN.name,""));
                jreqData.put("osType",1+"");
                jreqData.put("publicIP",MobileInfo.getIPAddress(true));
                jreqData.put("androidVersion",Build.VERSION.RELEASE);
                jreqData.put("appVersionname",MobileInfo.getAppVersionName());
                jreqData.put("mobileNumber",UserSession.getLoginDetailsModel().getMobileNo());
                jreqData.put("loginType",eLoginType.AUTOLOGIN.value+"");

                String dataS = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dataS = Base64.getEncoder().encodeToString(jreqData.toString().getBytes());
                }
                String param = "data="+dataS+"&ucc="+ UserSession.getLoginDetailsModel().getUserID();
                String strresponse = sendRequest(url, param);
                GlobalClass.log("EDIS Res: "+strresponse + " req: "+param);
                return strresponse;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)  {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            try {
                if(s != null && !s.equalsIgnoreCase("")) {
                    handleRestApiResp(s);
                }else{
                    GlobalClass.showAlertDialog(Constants.ERR_SERVER_CONNECTION + ObjectHolder.connconfig.getAuthLastDigit(), true);
                }
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
    }

    private  String sendRequest(String url,String param){

        String callTime = DateUtil.getcurrentTimeToN()+"";
        int responseCode = 0;
        HttpsURLConnection conn = null;
        try {
            StringBuffer strresponse = new StringBuffer();

            URL obj = new URL(url);
            conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            //60 sec timeout
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            //  xModuleClass.httpURLConnection.put(url, conn);
            //}
            param = param + "&cachetime="+callTime;
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(param);
                writer.flush();
                responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();

                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    GlobalClass.log("edis callPOSTHTTPMethod: " + errorresponse.toString());
                }
            }
            conn.disconnect();
            return strresponse.toString();
        } catch(Exception e){
            e.printStackTrace();
            return "";
        }
        finally{
            if(conn != null){
                conn.disconnect();
            }
        }
    }
}
