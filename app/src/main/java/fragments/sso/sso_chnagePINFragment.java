package fragments.sso;

import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import utils.AeSimpleSHA1;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class sso_chnagePINFragment extends Fragment {
    TextInputEditText et_oldpassword, et_newpassword, confirmPassword;
    TextInputLayout TIL_confirmpass, TIL_newpassword, TIL_oldpassword;
    TextView btn_Submit;
    private HomeActivity homeActivity;


    public static sso_chnagePINFragment newInstance() {
        sso_chnagePINFragment fragment = new sso_chnagePINFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_chnagepin, container, false);
        TIL_confirmpass = mView.findViewById(R.id.TIL_confirmpass);
        TIL_newpassword = mView.findViewById(R.id.TIL_newpassword);
        TIL_oldpassword = mView.findViewById(R.id.TIL_oldpassword);
        et_oldpassword = mView.findViewById(R.id.et_oldpassword);
        et_newpassword = mView.findViewById(R.id.et_newpassword);
        confirmPassword = mView.findViewById(R.id.confirmPassword);
        btn_Submit = mView.findViewById(R.id.btn_Submit);
        btn_Submit.setAlpha(0.5f);
        btn_Submit.setEnabled(false);
        et_oldpassword.setTransformationMethod(new PasswordTransformationMethod());
        et_newpassword.setTransformationMethod(new PasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());

        et_newpassword.setEnabled(false);
        confirmPassword.setEnabled(false);


        et_oldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TIL_oldpassword.setError("");
                if(charSequence.length() > 3){
                    et_newpassword.setEnabled(true);
                    confirmPassword.setEnabled(false);
                }else {
                    et_newpassword.setEnabled(false);
                    confirmPassword.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        et_newpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TIL_newpassword.setError("");
                if(charSequence.length() > 3){
                    confirmPassword.setEnabled(true);
                }else {
                    confirmPassword.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TIL_confirmpass.setError("");
                if(charSequence.length() > 3){
                    enableSublitBtn(true);
                }else {
                    enableSublitBtn(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIL_oldpassword.setError("");
                TIL_confirmpass.setError("");
                TIL_newpassword.setError("");

                if (et_oldpassword.getText().length() < 4) {
                    TIL_oldpassword.setError("Please enter 4 digit PIN");
                } else if (et_newpassword.getText().length() < 4) {
                    TIL_newpassword.setError("Please enter 4 digit PIN");
                } else if (confirmPassword.getText().length() < 4) {
                    TIL_confirmpass.setError("Confirm New PIN");
                } else if (!confirmPassword.getText().toString().equalsIgnoreCase(et_newpassword.getText().toString())){
                    TIL_confirmpass.setError("New Pin does not match");
                } else{
                    CallSSoAPIForChangePassword(confirmPassword.getText().toString(), et_oldpassword.getText().toString());
                }
            }
        });

        return mView;
    }

    private void enableSublitBtn(boolean btn){
        btn_Submit.setEnabled(btn);
        btn_Submit.setAlpha(btn?1:0.5f);
    }
    private void CallSSoAPIForChangePassword(String password, String oldpass) {
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            enableSublitBtn(false);
            AeSimpleSHA1 encodePwd = new AeSimpleSHA1();
            GlobalClass.showProgressDialog("Please wait...");
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "";
            URL = eSSOApi.BASEURL.value + eSSOApi.change_pin.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", UserSession.getLoginDetailsModel().getUserID());
            jsonBody.put("oldpin", encodePwd.MD5(oldpass));
            jsonBody.put("newpin", encodePwd.MD5(password));
            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);

                    ParseResponse(response);
                    VenturaException.SSOPrintLog(eSSOApi.change_pin.value,"SuccessRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+response + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    GlobalClass.log("LOG_VOLLEY", error.toString());
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            VenturaException.SSOPrintLog(eSSOApi.change_pin.value, "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            GlobalClass.showAlertDialog("Error : Request timeout. Please try after sometime.");
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY", responseString);
                            VenturaException.SSOPrintLog(eSSOApi.change_pin.value, "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            ParseResponse(responseString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        TIL_confirmpass.setError(Constants.ERR_MSG);
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
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("session_id", PreferenceHandler.getSSOSessionID());
                    params.put(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
                    params.put("Authorization", "Bearer " + PreferenceHandler.getSSOAuthToken());
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120*1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void ParseResponse(String response) {
        try {
            GlobalClass.dismissdialog();
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.optString("message", "");
            if (message.equalsIgnoreCase("")) {
                message = jsonObject.optString("Message", "");
            }
            GlobalClass.showAlertDialog(message);
            String pinchnaged = jsonObject.optString("pinchanged");
            if (pinchnaged.equalsIgnoreCase("OK")) {
                if (GlobalClass.fragmentManager.getBackStackEntryCount() > 0)
                    GlobalClass.fragmentManager.popBackStackImmediate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
