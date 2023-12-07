package fragments.sso;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
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
import com.ventura.venturawealth.FingerprintHandler;
import com.ventura.venturawealth.FingerprintHandlerforpopup;
import com.ventura.venturawealth.FingerprintInterfaceforpopup;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
import fragments.edis.edisclientdatamodel;
import fragments.edis.edisonerowmodel;
import fragments.sso.structure.SsoModel;
import interfaces.OnCloseClick;
import utils.AeSimpleSHA1;
import utils.Biometric.BiometricCallbackV28;
import utils.Biometric.BiometricUtils;
import utils.Connectivity;
import utils.Constants;
import utils.CustomProgressDialog;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import view.BrokerageDialog;
import view.RiskDisclosureDialog;

public class Sso_validatePin extends Fragment implements ConnectionProcess,FingerprintInterfaceforpopup, OnCloseClick {
    
    private TextView btn_submit,tv_name,tv_switchUser;
    private TextInputEditText et_pin;
    private TextInputLayout TIL_pin;
    private LinearLayout fingerLayout;
    private TextView errtextView,tv_forgotpin,google_auth;
    private ImageView fingerTouchimageView;
    private FingerprintHandler fingerHelper;
    public static Handler validatePinHandler;
    public static eSSOJourney journey = eSSOJourney.forgotpin;

    public static Sso_validatePin newInstance() {
        Sso_validatePin fragment = new Sso_validatePin();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_enter_pin_screen,container,false);
        et_pin = mView.findViewById(R.id.et_password);
        TIL_pin = mView.findViewById(R.id.TIL_password);
        btn_submit = mView.findViewById(R.id.btn_submit);
        tv_name = mView.findViewById(R.id.tv_name);

        //for fingertouch
        fingerLayout = (LinearLayout)mView.findViewById(R.id.fingerLayout);
        errtextView = (TextView)mView.findViewById(R.id.errorText);
        fingerTouchimageView = (ImageView)mView.findViewById(R.id.icon);
        google_auth = (TextView)mView.findViewById(R.id.google_auth);
        btn_submit.setEnabled(false);
        btn_submit.setAlpha(0.5f);
        et_pin.setTransformationMethod(new PasswordTransformationMethod());

        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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

        google_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApp(getActivity(), "com.google.android.apps.authenticator2");
            }
        });
        fingerTouchimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeTouch();
            }
        });
        try {
            tv_name.setText("Hi "+ UserSession.getLoginDetailsModel().getClientName());
        }catch (Exception e){
            e.printStackTrace();
        }
        tv_forgotpin = mView.findViewById(R.id.tv_forgotpin);
        tv_switchUser = mView.findViewById(R.id.tv_switchUser);
        /*if(!GlobalClass.isSSOTag){
            tv_switchUser.setVisibility(View.INVISIBLE);
            tv_forgotpin.setVisibility(View.INVISIBLE);
        }*/
        tv_switchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SsoModel model = new SsoModel();
                model.setAuthToken(PreferenceHandler.getSSOAuthToken());
                model.setClientCode(UserSession.getLoginDetailsModel().getUserID());
                model.setSessionID(PreferenceHandler.getSSOSessionID());
                model.setClientName(UserSession.getLoginDetailsModel().getClientName());
                model.setRefreshToken(PreferenceHandler.getSSORefreshToken());
                ArrayList<SsoModel> list1 = PreferenceHandler.getSsoClientDetails();
                String _clientcode = model.getClientCode();
                for (int i = 0 ; i < list1.size() ; i++){
                    SsoModel model1 = list1.get(i);
                    if(model1.getClientCode().equalsIgnoreCase(_clientcode)){
                        list1.remove(model1);
                    }
                }
                list1.add(model);
                if(list1.size()>4){
                    list1.remove(0);
                }
                PreferenceHandler.setSsoClientDetails(list1);
                final Fragment fragment = SsoLogin.newInstance();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                String FRAGMENT_NAME = fragment.getClass().getSimpleName();
                fragmentTransaction.replace(R.id.layout, fragment, FRAGMENT_NAME);
                fragmentTransaction.commit();
                PreferenceHandler.setSSOSessionID("");
                PreferenceHandler.setSSOAuthToken("");
                UserSession.getLoginDetailsModel().setClientName("");
            }
        });
        tv_forgotpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Fragment fragment = ValidateUserByDOBPAN_Fragment.newInstance(journey);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIL_pin.setError("");
                if(et_pin.getText().length() == 0){
                    TIL_pin.setError("Please enter your PIN");
                }else if(et_pin.getText().toString().trim().equalsIgnoreCase("")){
                    TIL_pin.setError("Please enter 4 digit PIN");
                }else if(et_pin.getText().length() < 4){
                    TIL_pin.setError("Please enter 4 digit PIN");
                }else {
                    //if(GlobalClass.isSSOTag){
                        CallSSoAPI(UserSession.getLoginDetailsModel().getUserID(), et_pin.getText().toString());
                    /*}else {
                        try {
                            AeSimpleSHA1 aeSimpleSHA1 = new AeSimpleSHA1();
                            String mdPin = aeSimpleSHA1.MD5(et_pin.getText().toString().trim());
                            if(mdPin.equalsIgnoreCase(UserSession.getLoginDetailsModel().getPin())) {
                                goToHomeScreen();
                            }else{
                                TIL_pin.setError("Invalid PIN");
                            }
                        }catch (Exception ex){
                            GlobalClass.showAlertDialog("There was some issue, please try after sometimes. Error : " + ex.getMessage());
                        }
                    }*/
                }
            }
        });
//        if(!UserSession.getLoginDetailsModel().getPin().equalsIgnoreCase("")){
            fingerLayout.setVisibility(View.VISIBLE);
            initializeTouch();
//        }
        return mView;
    }
    private void enableSublitBtn(boolean btn){
        btn_submit.setEnabled(btn);
        btn_submit.setAlpha(btn?1:0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        validatePinHandler = new ValidatePinHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        validatePinHandler = null;
    }
    public static void openApp(Context context, String packageName) {

        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }

    private void CallSSoAPIBiometric (String Clientcode,boolean isBiometric){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            //AeSimpleSHA1 encodePwd = new AeSimpleSHA1();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.biometric_validated.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            jsonBody.put("biometric_validated", "true");

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    String refreshtoken = PreferenceHandler.getSSORefreshToken();
                    GlobalClass.log(refreshtoken);
                    ParseResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            TIL_pin.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.biometric_validated.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            VenturaException.SSOPrintLog(eSSOApi.biometric_validated.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
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
                    params.put(eSSOTag.Refresh_token.name, PreferenceHandler.getSSORefreshToken());
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void CallSSoAPI (String Clientcode, String pin){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            if(!Connectivity.isNetworkConnected(getContext())){
                TIL_pin.setError(Constants.NO_INTERNET);
                return;
            }
            enableSublitBtn(false);
            GlobalClass.showProgressDialog(Constants.pleasewait);
            AeSimpleSHA1 encodePwd = new AeSimpleSHA1();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.validatepin.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            jsonBody.put("pin", encodePwd.MD5(pin));

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    ParseResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            TIL_pin.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.validatepin.value, "ErrTimeOut RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else if(error instanceof NoConnectionError){
                            TIL_pin.setError(Constants.NO_INTERNET2);
                            VenturaException.SSOPrintLog(eSSOApi.validatepin.value, "ErrNoCon RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            VenturaException.SSOPrintLog(eSSOApi.validatepin.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            ParseErrorResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_pin.setError(Constants.NO_INTERNET2);
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
                    params.put(eSSOTag.Refresh_token.name, PreferenceHandler.getSSORefreshToken());
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000,
                    3,
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
            String msg = jsonObject.optString("message");
            int attempts_left = jsonObject.optInt("attempts_left",3);
            if(attempts_left == 0){
                journey = eSSOJourney.unlock;
                //setTimerForBtnDisable();
            }
            if(!msg.isEmpty()) {
                TIL_pin.setError(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ParseResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String client_id = jsonObject.optString("client_id","");

            //"folionumber": "1", "aadhar": "", "isequity": "T", "iscommodity": "T",
            // "mtfflag": "T", "mflogin": "Equity", "mtfflagdetective": "F",
            // "nsecash": "T", "nsefno": "T", "bsecash": "T", "nseslbm": "T", "nsecds": "T",
            // "poa": "T", "accesstype": "T", "username": "TAPAS NAYAK",
            // "platform_access_list": ["ventura_backoffice", "pointer", "boom", "neutrino"]}

            if(client_id.length()>0){
                if(!et_pin.getText().toString().equalsIgnoreCase("")) {
                    AeSimpleSHA1 encodePwd = new AeSimpleSHA1();
                    String encPin = encodePwd.MD5(et_pin.getText().toString());
                    UserSession.getLoginDetailsModel().setPin(encPin);
                }
                String folionumber = jsonObject.optString("folionumber");
                if(folionumber.length() > 5){
                    UserSession.getClientResponse().follio.setValue(folionumber);
                }
                String isequity = jsonObject.optString("isequity");
                String iscommodity = jsonObject.optString("iscommodity");
                if(isequity.equalsIgnoreCase("T") && iscommodity.equalsIgnoreCase("T")){
                    UserSession.getClientResponse().clientAcType.setValue(1);
                }else if(iscommodity.equalsIgnoreCase("T")){
                    UserSession.getClientResponse().clientAcType.setValue(3);
                }else{
                    UserSession.getClientResponse().clientAcType.setValue(2);
                }
                String nseslbm = jsonObject.optString("nseslbm");
                if(!nseslbm.equalsIgnoreCase("")){
                    UserSession.getClientResponse().isNSEslbm.setValue(nseslbm.charAt(0));
                }
                String accesstype = jsonObject.optString("accesstype");
                if(!accesstype.equalsIgnoreCase("")){
                    UserSession.getClientResponse().charUserRight.setValue(accesstype.charAt(0));
                }
                UserSession.getLoginDetailsModel().setFolioNumber(UserSession.getClientResponse().follio.getValue());
                //UserSession.getLoginDetailsModel().setClientName(UserSession.getClientResponse().charUserName.getValue());
                UserSession.getLoginDetailsModel().setActiveUser(UserSession.getClientResponse().getActiveUser());
                UserSession.getLoginDetailsModel().setClient(true);
                UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());

                goToHomeScreen();
            }else {
                String msg = jsonObject.optString("message");
                if(!msg.isEmpty()) {
                    TIL_pin.setError(msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //for Finger Authentication
    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidkey";
    private Cipher cipher;
    @SuppressLint("MissingPermission")
    private void initializeTouch(){
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        try {
            KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) getContext().getSystemService(FINGERPRINT_SERVICE);
            }
            // Check whether the device has a Fingerprint sensor.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
                    errtextView.setText("Your Device does not have a Fingerprint Sensor");
                    fingerLayout.setVisibility(View.GONE);
                } else {
                    // Checks whether fingerprint permission is set on manifest
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        errtextView.setText("Fingerprint authentication permission not enabled");
                        fingerLayout.setVisibility(View.GONE);
                    } else {
                        // Check whether at least one fingerprint is registered
                        if (!fingerprintManager.hasEnrolledFingerprints()) {
                            errtextView.setText("Register at least one fingerprint in Settings");
                            fingerLayout.setVisibility(View.GONE);
                        } else {
                            // Checks whether lock screen security is enabled or not
                            if (!keyguardManager.isKeyguardSecure()) {
                                errtextView.setText("Lock screen security not enabled in Settings");
                                fingerLayout.setVisibility(View.GONE);
                            } else {
                                fingerLayout.setVisibility(View.VISIBLE);
                                if (BiometricUtils.isBiometricPromptEnabled()) {
                                    displayBiometricPrompt(this);
                                } else {
                                    fingerTouchimageView.setEnabled(false);
                                    generateKey();
                                    if (cipherInit()) {
                                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                        FingerprintHandlerforpopup helper = new FingerprintHandlerforpopup(GlobalClass.latestContext, this);
                                        helper.startAuth(fingerprintManager, cryptoObject);
                                    }
                                }
                            }
                        }
                    }
                    //}
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            fingerLayout.setVisibility(View.GONE);
        }
    }

    protected CancellationSignal mCancellationSignal = new CancellationSignal();

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final FingerprintInterfaceforpopup biometricCallback) {
        fingerTouchimageView.setEnabled(false);
        new BiometricPrompt.Builder(GlobalClass.latestContext)
                .setTitle("Biometric Authentication")
                .setSubtitle("")
                .setDescription("Please touch the finger scanner.")
                .setNegativeButton("Cancel", GlobalClass.latestContext.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthFailed();
                    }
                })
                .build()
                .authenticate(mCancellationSignal, GlobalClass.latestContext.getMainExecutor(),new BiometricCallbackV28(biometricCallback));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void onAuthSucceeded() {
        this.update("Fingerprint Authentication succeeded.",true);
    }

    @Override
    public void onAuthFailed() {
        //this.update("Fingerprint Authentication failed.", false);
        fingerTouchimageView.setEnabled(true);
    }

    @Override
    public void onAuthHelp() {
        this.update("Fingerprint Authentication help.", false);
    }

    @Override
    public void onAuthError() {
        this.update("Fingerprint Authentication error.", false);
        fingerTouchimageView.setEnabled(true);
    }
    public void update(String e, Boolean success){
        try {
            if (fingerLayout.getVisibility() == View.VISIBLE) {
                //errtextView.setText(e);
                if (success) {
                    //if(GlobalClass.isSSOTag){
                        CallSSoAPIBiometric(UserSession.getLoginDetailsModel().getUserID(), true);
                    /*}else {
                        goToHomeScreen();
                    }*/
                }
            }
        }catch (Exception ex){ex.printStackTrace();}
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

    //go to home screen
    private String domainNameIP = ObjectHolder.connconfig.getAuthIP();;
    private int AuthConnectionRetryCount = 0;
    boolean isTryToConnectBcast = false;

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
        } /*else if(googleauthTag.equalsIgnoreCase("0") && !askViewDate.equalsIgnoreCase(currDate)){
            PreferenceHandler.setGoogleAuthAskViewDate(currDate);
            final Fragment fragment = AskGoogleAuthFragment.newInstance();
            GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
        } */else {

            if(UserSession.getClientResponse().isRiskDisclosureToShow.getValue()) {
                try {
                    if (GlobalClass.latestContext != null) {
                        AppCompatActivity aca = (AppCompatActivity) GlobalClass.latestContext;
                        aca.runOnUiThread(() -> {
                            try {
                                RiskDisclosureDialog dialog = new RiskDisclosureDialog(getContext(), this);
                                dialog.show();
                            } catch (Exception e) {
                                VenturaException.Print(e);
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                    }
                } catch (Exception ex) {
                    GlobalClass.onError("", ex);
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }else {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
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

    //from Risk Diskclosure dialog
    @Override
    public void onCloseClick() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
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

    private void handleClientLogin() {
        try {
            GlobalClass.dismissdialog();
            ClientLoginResponse clr = UserSession.getClientResponse();//new ClientLoginResponse(response);
            switch (clr.getSuccess()){
                case SUCCESS:
                case SYSTEM_UNDER_MAINTAINANCE: {
                    if(clr.getSuccess() == ClientLoginResponse.eClientLoginSuccess.SYSTEM_UNDER_MAINTAINANCE){
                        GlobalClass.isMaintanceMode = true;
                    }
                    if (clr.getAppUpdate() == ClientLoginResponse.eAppUpdate.COMPULSARY) {
                        AppUpdateAlert();
                    } else if (clr.getAppUpdate() == ClientLoginResponse.eAppUpdate.TRUE) {
                        AppUpdateAlertOptional(clr);
                    } else {
                        loginFinalization(clr);
                    }
                }
                    break;
                //case SYSTEM_UNDER_MAINTAINANCE:
                    //GlobalClass.showAlertDialog(clr.charResMsg.getValue(),true);
                    //break;
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

            if (!TextUtils.isEmpty(msg) && !GlobalClass.isMaintanceMode){
                GlobalClass.showToast(GlobalClass.latestContext,msg);
            }
            goToHomeScreen();

        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private class ValidatePinHandler extends Handler {
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
            GlobalClass.showProgressDialog("Please wait connecting to Trade Server..."+ObjectHolder.connconfig.getBCastLastDigit());
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
    private void handleRestAPIResp(String response) throws Exception{
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
        } else{
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
                    handleRestAPIResp(s);
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
                    handleRestAPIResp(s);
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