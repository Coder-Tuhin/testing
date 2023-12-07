package fragments.sso;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import enums.eSSOApi;
import enums.eSSOJourney;
import enums.eSSOTag;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class ValidateUserByDOBPAN_Fragment extends Fragment implements DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {
    TextInputLayout PanTextfield;
    TextInputEditText Et_pan;
    TextView Submit,tv_name;
    private static eSSOJourney journey;

    public static ValidateUserByDOBPAN_Fragment newInstance(eSSOJourney _journey) {
        ValidateUserByDOBPAN_Fragment fragment = new ValidateUserByDOBPAN_Fragment();
        journey = _journey;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_uservalidity_screen,container,false);
        Submit = mView.findViewById(R.id.Submit);
        PanTextfield = mView.findViewById(R.id.PanTextfield);
        Et_pan  = mView.findViewById(R.id.Et_pan);
        tv_name = mView.findViewById(R.id.tv_name);
        Submit.setEnabled(false);
        Submit.setAlpha(0.5f);
        Et_pan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>9){
                    enableSublitBtn(true);
                }else {
                   enableSublitBtn(false);
                }
                PanTextfield.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        try {
            if(journey != eSSOJourney.firstlogin) {
                String Name = UserSession.getLoginDetailsModel().getClientName();//obj.optString("fname")+' '+obj.optString("lname");
                tv_name.setText("Hi " + Name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidPAN(Et_pan.getText().toString().trim().toUpperCase())){
                    PanTextfield.setError("Please Enter Valid PAN");
                }else  {
                    CallSSoAPI(Et_pan.getText().toString().trim().toUpperCase());
                }
            }
        });

        return mView;
    }

    private void enableSublitBtn(boolean btn){
        Submit.setEnabled(btn);
        Submit.setAlpha(btn?1:0.5f);
    }
    private boolean isValidPAN(String value) {
        Pattern pattern1 = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher1 = pattern1.matcher(value);
        if (matcher1.matches()) {
            return true;
        } /*else {
            Pattern pattern2 = Pattern.compile("[A-Z]{5}[0-9]{5}");
            Matcher matcher2 = pattern2.matcher(value);
            return matcher2.matches();
        }*/
        return false;
    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        int month = monthOfYear + 1;
        String monthString = month < 10 ? "0" + month : "" + month;
        String date = "" + dayString + "/" + (monthString) + "/" + year;
    }

    private void CallSSoAPI (String pan){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            enableSublitBtn(false);
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.PanValidationURL.value;

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", UserSession.getLoginDetailsModel().getUserID());
            jsonBody.put("pan", pan);
            jsonBody.put("journey",journey.name);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    ParseResponse(response);
                    VenturaException.SSOPrintLog("ValidatePAN","SuccessRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+response + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            PanTextfield.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog("ValidatePAN", "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                            VenturaException.SSOPrintLog("ValidatePAN", "ErrRequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));

                            ParseErrorResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        PanTextfield.setError(Constants.ERR_MSG);
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
                        GlobalClass.log("SSO_N pan : " + response.headers.toString());
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
            String Otpmsg = jsonObject.optString("message");
            PanTextfield.setError(Otpmsg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ParseResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            boolean otp_sent = jsonObject.optBoolean("otp_sent");
            //String Otpmsg = jsonObject.optString("message");
            if (otp_sent) {
               final Fragment fragment = OtpValidationFragmnet.newInstance(response, journey);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                String FRAGMENT_NAME = fragment.getClass().getSimpleName();
                fragmentTransaction.replace(R.id.layout, fragment, FRAGMENT_NAME);
                fragmentTransaction.commit();
            }else{
                String Otpmsg = jsonObject.optString("message");
                if(!Otpmsg.equalsIgnoreCase(""))
                PanTextfield.setError(Otpmsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
