package fragments.sso;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.net.URL;
import java.util.Base64;

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
import enums.eMessage;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eSocketClient;
import models.LoginDetailsModel;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NonSSO_Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NonSSO_Login extends Fragment implements RadioGroup.OnCheckedChangeListener, ConnectionProcess {

    TextInputEditText userid_text, pin_edittext,pandob_edittext;
    TextInputLayout useridfilledTextField, pin_filledTextField,pandob_filledTextField;

    private RadioGroup mTradeRDgrp;
    private RadioButton mPanRDbtn, mDobRDbtn;
    TextView submit;

    public static Handler nonSSOHandler;

    public NonSSO_Login() {
        // Required empty public constructor
    }
    public static NonSSO_Login newInstance() {
        NonSSO_Login fragment = new NonSSO_Login();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTheme(R.style.AppThemenew);
        View mView =  inflater.inflate(R.layout.fragment_non_sso_login, container, false);
        pin_edittext = mView.findViewById(R.id.pin_edittext);
        pin_edittext.setTransformationMethod(new PasswordTransformationMethod());
        userid_text = mView.findViewById(R.id.userid_text);
        pandob_edittext = mView.findViewById(R.id.pandob_edittext);

        submit = mView.findViewById(R.id.submit);
        useridfilledTextField = mView.findViewById(R.id.useridfilledTextField);
        pin_filledTextField = mView.findViewById(R.id.pin_filledTextField);
        pandob_filledTextField = mView.findViewById(R.id.pandob_filledTextField);

        mTradeRDgrp = (RadioGroup) mView.findViewById(R.id.tradeRDgroup);
        mPanRDbtn = (RadioButton) mView.findViewById(R.id.panRDbutton);
        mDobRDbtn = (RadioButton) mView.findViewById(R.id.dobRDbutton);
        mTradeRDgrp.setOnCheckedChangeListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useridfilledTextField.setError("");
                pin_filledTextField.setError("");
                pandob_filledTextField.setError("");

                if (userid_text.getText().toString().isEmpty()) {
                    useridfilledTextField.setError("Please enter valid Client ID");
                } else if(pin_edittext.getText().toString().isEmpty()){
                    pin_filledTextField.setError("Please Please enter valid Client ID");
                } else if(pandob_edittext.getText().toString().isEmpty()){
                    pandob_filledTextField.setError("Please Please enter PAN or DOB");
                } else if (mDobRDbtn.isChecked() && TextUtils.isEmpty(pandob_edittext.getText().toString())) {
                    pandob_filledTextField.setError(eMessage.BLANK_DOB.name);
                } else if (mPanRDbtn.isChecked() && TextUtils.isEmpty(pandob_edittext.getText().toString())) {
                    pandob_filledTextField.setError(eMessage.BLANK_PAN.name);
                }else {

                    try {
                        AeSimpleSHA1 aeSimpleSHA1 = new AeSimpleSHA1();
                        LoginDetailsModel ldm = UserSession.getLoginDetailsModel();
                        ldm.setUserID(userid_text.getText().toString().toUpperCase());
                        ldm.setPassword(aeSimpleSHA1.MD5(pin_edittext.getText().toString().trim()));
                        ldm.setPin(aeSimpleSHA1.MD5(pin_edittext.getText().toString().trim()));
                        ldm.setType(mDobRDbtn.isChecked()?PDType.DOB.value : PDType.PAN.value);
                        ldm.setPan(pandob_edittext.getText().toString().trim().toUpperCase());

                        goToHomeScreen();
                        //new SendLoginReq().execute();
                        //GlobalClass.showdialog("Requesting...");
                    }catch (Exception ex){
                        VenturaException.Print(ex);
                    }
                }
            }
        });
        nonSSOHandler = new NonSSOHandler();
        return mView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.panRDbutton: {
                pandob_filledTextField.setHint(R.string.pan_hint);
                //pandob_edittext.addTextChangedListener(null);
            }
                break;
            case R.id.dobRDbutton: {
                pandob_filledTextField.setHint(R.string.dob_hint);
                //pandob_edittext.addTextChangedListener(watcher);
            }
                break;
            default:
                break;
        }
    }
    /*
    private int _prevTextLength = 0;
    private int _currTextlength = 0;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String str = pandob_edittext.getText().toString();
            _prevTextLength = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = pandob_edittext.getText().toString();
            _currTextlength = str.length();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (_prevTextLength < _currTextlength) {
                if (_currTextlength == 2) {
                    pandob_edittext.append("/");
                } else if (_currTextlength == 3 && editable.charAt(2) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(2);
                    String str = _tempText.substring(0, 2) + addChar;
                    pandob_edittext.setText(str);
                    pandob_edittext.setSelection(str.length());
                } else if (_currTextlength == 5) {
                    pandob_edittext.append("/");
                } else if (_currTextlength == 6 && editable.charAt(5) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(5);
                    String str = _tempText.substring(0, 5) + addChar;
                    pandob_edittext.setText(str);
                    pandob_edittext.setSelection(str.length());
                }
            }
        }
    };

     */

    private void goToHomeScreen(){
        if (ObjectHolder.connconfig.getbCastServerIP().equalsIgnoreCase("")) {
            //Connect to Auth Server...
            connectToAuthServer();
            //new eAuthRestAPIReq().execute();
        } else {
            if (UserSession.getLoginDetailsModel().isActiveUser()) {
                connectToTradeBCastServer();
            }else {
                OpenFinalScreen();
            }
        }
    }
    public void OpenFinalScreen(){
        String googleauthTag = PreferenceHandler.getSSOCreateAuth();
        String askViewDate = PreferenceHandler.getGoogleAuthAskViewDate();
        String currDate = DateUtil.getCurrentDate();
        if(GlobalClass.showregisterauthtag){
            GlobalClass.showregisterauthtag = false;
            final Fragment fragment = CreateGoogleAuthFragment.newInstance();
            GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
        } else if(googleauthTag.equalsIgnoreCase("0") && !askViewDate.equalsIgnoreCase(currDate)){
            PreferenceHandler.setGoogleAuthAskViewDate(currDate);
            final Fragment fragment = AskGoogleAuthFragment.newInstance();
            GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
        }else {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public enum PDType{
        PAN(1),
        DOB(2);
        int value;
        PDType(int value){
            this.value = value;
        }
    }

    private class SendLoginReq extends AsyncTask<Void, Void, Void> {

        private SendDataToAuthServer sendDataToAuthServer = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                eMessageCode msgCode;
                byte[] data;

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

    private class NonSSOHandler extends Handler {
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

    //go to home screen
    private String domainNameIP = ObjectHolder.connconfig.getAuthIP();;
    private int AuthConnectionRetryCount = 0;
    boolean isTryToConnectBcast = false;

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
        } else if(serverConnect == eSocketClient.AUTH) {

            GlobalClass.addAndroidLog(eLogType.AuthConnectFailed.name, "authip|" + ObjectHolder.connconfig.getAuthIP() + "|" + MobileInfo.getMobileData() + "|splash", "");
            if(!isTryToConnectBcast) {
                if (AuthConnectionRetryCount < 2) {
                    String tempAuthIP = this.getContext().getString(R.string.AUTH_IP);
                    if (ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(domainNameIP)) {
                        tempAuthIP = this.getContext().getString(R.string.AUTH_IP_1);
                    }/* else if (ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(this.getContext().getString(R.string.AUTH_IP_1))) {
                        tempAuthIP = this.getContext().getString(R.string.AUTH_IP_2);
                    } else if (ObjectHolder.connconfig.getAuthIP().equalsIgnoreCase(this.getContext().getString(R.string.AUTH_IP_2))) {
                        tempAuthIP = this.getContext().getString(R.string.AUTH_IP_3);
                    }*/
                    ObjectHolder.connconfig.setAuthIP(tempAuthIP);
                    connectToAuthServer();
                } else {
                    new eAuthRestAPIReq().execute();
                    //GlobalClass.log("AuthConnectionRetryCount : " + AuthConnectionRetryCount);
                    //GlobalClass.showAlertDialog(Constants.ERR_SERVER_CONNECTION + ObjectHolder.connconfig.getAuthLastDigit(), true);
                }
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
            GlobalClass.showProgressDialog("Please wait connecting to Trade Server..."+ ObjectHolder.connconfig.getBCastLastDigit());
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

    private void loginFinalization(ClientLoginResponse clr){
        try{
            UserSession.getLoginDetailsModel().setClient(true);
            if (!TextUtils.isEmpty(clr.mobileNumber.getValue())){
                UserSession.getLoginDetailsModel().setMobileNo(clr.mobileNumber.getValue());
            }
            final String msg = clr.charResMsg.getValue();
            if (!TextUtils.isEmpty(msg)){
                GlobalClass.showToast(GlobalClass.latestContext,msg);
            }
            UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
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
                    JSONObject jsonObject = new JSONObject(s);
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

                        UserSession.getClientResponse().charAuthId.setValue("bypass");

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
                }else{
                    new eAuthRestAPIReq73().execute();
                    //GlobalClass.showAlertDialog(Constants.ERR_SERVER_CONNECTION + ObjectHolder.connconfig.getAuthLastDigit(), true);
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
                    JSONObject jsonObject = new JSONObject(s);
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

                        UserSession.getClientResponse().charAuthId.setValue("bypass");

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
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
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