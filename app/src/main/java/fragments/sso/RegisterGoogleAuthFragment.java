package fragments.sso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import enums.eSSOApi;
import enums.eSSOTag;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class RegisterGoogleAuthFragment extends Fragment {
    TextInputEditText pin_view;
    TextInputLayout TIL_auth;
    TextView btn_verify,btn_get_otp,ortext,authdescription;
    private static int fromscreen = 0;

    public static RegisterGoogleAuthFragment newInstance(int fromscrn) {
        RegisterGoogleAuthFragment fragment = new RegisterGoogleAuthFragment();
        fromscreen = fromscrn;

        return fragment;

    }
    public static RegisterGoogleAuthFragment newInstance() {
        RegisterGoogleAuthFragment fragment = new RegisterGoogleAuthFragment();

        return fragment;

    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.register_gacode,container,false);
        pin_view = mView.findViewById(R.id.pin_view);
        btn_verify = mView.findViewById(R.id.btn_verify);
        btn_get_otp = mView.findViewById(R.id.btn_get_otp);
        ortext = mView.findViewById(R.id.ortext);
        TIL_auth = mView.findViewById(R.id.TIL_auth);
        authdescription = mView.findViewById(R.id.authdescription);
        pin_view.setTransformationMethod(new PasswordTransformationMethod());
        enableVerifyBtn(false);
        if(fromscreen == 1){
            btn_get_otp.setVisibility(View.GONE);
            ortext.setVisibility(View.GONE);
        }else {
            btn_get_otp.setVisibility(View.VISIBLE);
            ortext.setVisibility(View.VISIBLE);
        }


        authdescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(getActivity(), "com.google.android.apps.authenticator2");
            }
        });
        pin_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 6){
                    GlobalClass.hideKeyboard(pin_view,getContext());
                    enableVerifyBtn(true);
                }else {
                    enableVerifyBtn(false);
                }
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pin_view.getText().toString().length() < 6){
                    GlobalClass.Alert("Please enter 6 digit Code",getActivity());
                }else {
                    CallSSoAPIFOrvalidateGoogleAuth(UserSession.getLoginDetailsModel().getUserID(),pin_view.getText().toString());
                }
            }
        });

        return mView;

    }


    private void enableVerifyBtn(boolean btn){
        btn_verify.setEnabled(btn);
        btn_verify.setAlpha(btn?1:0.5f);
    }



    private void CallSSoAPIFOrvalidateGoogleAuth (String Clientcode,String mfa_code){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.registerGoogleAuth_Code.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            jsonBody.put("mfa_code", mfa_code);

            final String mRequestBody = jsonBody.toString();
            GlobalClass.log("LOG_VOLLEY_Req", mRequestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY_Res", response);
                    GlobalClass.dismissdialog();
                    ParseAuthCodeResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();

                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            TIL_auth.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.registerGoogleAuth_Code.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                        }else {
                            if (error.networkResponse != null) {
                                String responseString = new String(error.networkResponse.data);
                                VenturaException.SSOPrintLog(eSSOApi.registerGoogleAuth_Code.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                                GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                                parseErrorresponse(responseString);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        TIL_auth.setError(Constants.ERR_MSG);
                        enableVerifyBtn(true);
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120*1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseErrorresponse(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            String client_id = jsonObject.optString("client_id","");
            boolean verified = jsonObject.optBoolean("verified");
            String msg = jsonObject.optString("message");
            if(!verified){
                pin_view.setText("");
                TIL_auth.setError(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ParseAuthCodeResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean authenticator_enabled = obj.optBoolean("authenticator_enabled");
            String msg = obj.optString("message");
            if(authenticator_enabled){
                PreferenceHandler.setSSOCreateAuth("1");
                Activity currentActivity = getActivity();
                if(currentActivity instanceof HomeActivity){
                    GlobalClass.fragmentManager.popBackStackImmediate();
                }else {
                    OpenHomeActivity();
                }
            } else {
                TIL_auth.setError(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            OpenHomeActivity();
        }

    }
    public void OpenHomeActivity(){
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    public void openApp(Context context, String packageName) {

        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                e.printStackTrace();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }
}