package fragments.sso;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ventura.venturawealth.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import enums.eSSOApi;
import enums.eSSOJourney;
import enums.eSSOTag;
import fcm_gsm_receiver.OTPReceiver;
import utils.Connectivity;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class OtpValidationFragmnet extends Fragment {
    private static String clientdata = "";
    private static eSSOJourney journey;
    TextInputEditText et_otp;
    TextInputLayout TIL_Otp;
    TextView btn_submit,msgtitle,tv_resentotp,timertext,google_auth,ortext;
    private JSONObject clientjson;

    public static OtpValidationFragmnet newInstance(String jsonObject,eSSOJourney _journey) {
        OtpValidationFragmnet fragment = new OtpValidationFragmnet();
        clientdata = jsonObject;
        journey = _journey;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_otpvalidation_screen,container,false);
        et_otp = mView.findViewById(R.id.et_otp);
        TIL_Otp = mView.findViewById(R.id.TIL_Otp);
        btn_submit = mView.findViewById(R.id.btn_submit);
        msgtitle = mView.findViewById(R.id.msgtitle);
        tv_resentotp = mView.findViewById(R.id.tv_resentotp);
        timertext = mView.findViewById(R.id.timertext);
        google_auth = mView.findViewById(R.id.google_auth);
        ortext = mView.findViewById(R.id.ortext);
        btn_submit.setEnabled(false);
        btn_submit.setAlpha(0.5f);
        if(GlobalClass.GoogleAuthEnabled && (journey == eSSOJourney.login)){
            google_auth.setVisibility(View.VISIBLE);
            ortext.setVisibility(View.VISIBLE);
        }else {
            google_auth.setVisibility(View.GONE);
            ortext.setVisibility(View.GONE);
        }
        et_otp.setTransformationMethod(new PasswordTransformationMethod());
        et_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>5){
                    btn_submit.setEnabled(true);
                    btn_submit.setAlpha(1);
                }else {
                    btn_submit.setEnabled(false);
                    btn_submit.setAlpha(0.5f);
                }
                TIL_Otp.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        google_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = ValidateGoogleAuthFragment.newInstance(clientdata);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
            }
        });

        try {
            clientjson = new JSONObject(clientdata);
            String Otpmsg = clientjson.optString("message","");
            if(!Otpmsg.equalsIgnoreCase("")) {
                msgtitle.setText(Otpmsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (journey == eSSOJourney.login){
            if(GlobalClass.OtpResponse.equalsIgnoreCase("")){
                CallSSoAPIFOrSendOTP(UserSession.getLoginDetailsModel().getUserID());
            }else {
                msgtitle.setText(GlobalClass.OtpResponse);
                StartCountdown();
            }
        }else{//eSSOJourney.unlock , eSSOJourney.firstlogin, eSSOJourney.forgotpin
            StartCountdown();
            String Otpmsg = clientjson.optString("message","");
            if(!Otpmsg.equalsIgnoreCase("")) {
                GlobalClass.OtpResponse = Otpmsg;
            }
        }
        tv_resentotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallSSoAPIFOrReSendOTP();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIL_Otp.setError("");
                if(et_otp.getText().length()<6){
                    TIL_Otp.setError("Please enter Valid 6 digit OTP");
                }else {
                    CallSSoAPI(et_otp.getText().toString());
                }
            }
        });
        startSmartUserConsent();

        return mView;
    }
    private void enableSublitBtn(boolean btn){
        btn_submit.setEnabled(btn);
        btn_submit.setAlpha(btn?1:0.5f);
    }
    private CountDownTimer timer;
    private void StartCountdown(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        final SimpleDateFormat formatter= new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        timer = new CountDownTimer(600000, 1000) {
            public void onTick(long millisUntilFinished) {

                Date date = new Date(millisUntilFinished);
                String formatted = formatter.format(date );
                timertext.setText("OTP Validity(mm:ss) "+formatted);
            }
            public void onFinish() {

            }
        }.start();
    }

    private void CallSSoAPI (String OTP){
        String currenttime = DateUtil.dateFormatter(new Date(),eSSOTag.DATE_Patter.value);
        try {
            if(!Connectivity.isNetworkConnected(getContext())){
                TIL_Otp.setError(Constants.NO_INTERNET);
                return;
            }
            enableSublitBtn(false);
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value+ eSSOApi.validateotp.value;

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", UserSession.getLoginDetailsModel().getUserID());
            jsonBody.put("otp", Integer.parseInt(OTP));
            jsonBody.put("journey", journey.name);
            /*
            if(fromscreen == 5){
                jsonBody.put("journey", "unlock");
            }else {
                jsonBody.put("journey", "login");
            }*/

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
                            TIL_Otp.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.validateotp.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                        }else if(error instanceof NoConnectionError){
                            TIL_Otp.setError(Constants.NO_INTERNET2);
                            VenturaException.SSOPrintLog(eSSOApi.validateotp.value, "ErrNoConn : RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }
                        else {
                            String responseString = new String(error.networkResponse.data);
                            VenturaException.SSOPrintLog(eSSOApi.validateotp.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                            ParseErrorResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_Otp.setError(Constants.NO_INTERNET2);
                    }
                    enableSublitBtn(true);
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

    private void CallSSoAPIFOrSendOTP (String Clientcode){
        String currenttime = DateUtil.dateFormatter(new Date(),eSSOTag.DATE_Patter.value);
        try {
            enableSublitBtn(false);
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.sendotp.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            jsonBody.put("journey", journey.name);
            /*
            if(fromscreen == 5){
                jsonBody.put("journey", "unlock");
            }else {
                jsonBody.put("journey", "login");
            }*/
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    sendOTPParseResponse(response,true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            TIL_Otp.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.sendotp.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                        }else {
                            if (error.networkResponse != null) {
                                String responseString = new String(error.networkResponse.data);
                                VenturaException.SSOPrintLog(eSSOApi.sendotp.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                                GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                                sendOTPParseResponse(responseString, false);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_Otp.setError(Constants.ERR_MSG);
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120*1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CallSSoAPIFOrReSendOTP (){
        String currenttime = DateUtil.dateFormatter(new Date(),eSSOTag.DATE_Patter.value);
        try {
            if(!Connectivity.isNetworkConnected(getContext())){
                TIL_Otp.setError(Constants.NO_INTERNET);
                return;
            }
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.resendotp.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", UserSession.getLoginDetailsModel().getUserID());
            jsonBody.put("journey", journey.name);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();

                    parseResentOtpResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            TIL_Otp.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.resendotp.value, "ErrTimeOut RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else if(error instanceof NoConnectionError){
                            TIL_Otp.setError(Constants.NO_INTERNET2);
                            VenturaException.SSOPrintLog(eSSOApi.resendotp.value, "ErrNoConn RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            VenturaException.SSOPrintLog(eSSOApi.resendotp.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                            parseResentOtpResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_Otp.setError(Constants.NO_INTERNET2);
                    }
                    enableSublitBtn(true);
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120*1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseResentOtpResponse(String response) {
        try {
//            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message", "");
            String otp_sent = jsonObject.optString("otp_sent", "");
            if(otp_sent.equalsIgnoreCase("True")){
                StartCountdown();
                GlobalClass.showAlertDialog(""+msg);
            }else{
                TIL_Otp.setError(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendOTPParseResponse(String response,boolean issuccess) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message","");
            if(msg.length()>0){
                if(issuccess) {
                    timertext.setVisibility(View.VISIBLE);
                    tv_resentotp.setVisibility(View.VISIBLE);
                    msgtitle.setText(msg);
                    GlobalClass.OtpResponse = msg;
                    StartCountdown();
                }
                else
                    TIL_Otp.setError(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ParseErrorResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message", "");
            String timeleft = jsonObject.optString("time_left","");
            int attempt_left = jsonObject.optInt("attempts_left",1);
            if (msg.equalsIgnoreCase("")) {
                msg = jsonObject.optString("error");
            }
            if(attempt_left == 0){
                timertext.setVisibility(View.GONE);
                tv_resentotp.setVisibility(View.GONE);
            }
            if (msg.length() > 0) {

                if(timeleft.length() > 0){
                    TIL_Otp.setError(msg+" Time left : "+timeleft);
                }else {
                    TIL_Otp.setError(msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ParseResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            Boolean otpValidated = jsonObject.optBoolean("otpvalidated",false);
            if(otpValidated) {
                GlobalClass.OtpResponse = "";
                String auth_token = jsonObject.optString("auth_token");
                String refresh_token = jsonObject.optString("refresh_token");
                PreferenceHandler.setSSOAuthToken(auth_token);
                PreferenceHandler.setSSORefreshToken(refresh_token);
                if (journey == eSSOJourney.login) {
                    final Fragment fragment = Sso_validatePin.newInstance();
                    GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
                }else {
                    final Fragment fragment = Sso_setPinFragmnet.newInstance(journey);
                    GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
                }
            }else {
                String msg = jsonObject.optString("message", "");
                if (msg.equalsIgnoreCase("")) {
                    msg = jsonObject.optString("error");
                }
                if (msg.length() > 0) {
                    TIL_Otp.setError(msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final int REQ_USER_CONSENT = 200;
    private OTPReceiver otpReceiver;
    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        client.startSmsUserConsent(null);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT){

            if ((resultCode == Activity.RESULT_OK) && (data != null)){
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }


    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()){
            et_otp.setText(matcher.group(0));
        }
    }

    private void registerBroadcastReceiver(){

        otpReceiver = new OTPReceiver();

        otpReceiver.smsBroadcastReceiverListener = new OTPReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent,REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        requireActivity().registerReceiver(otpReceiver,intentFilter);

    }
    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(otpReceiver);
    }
}
