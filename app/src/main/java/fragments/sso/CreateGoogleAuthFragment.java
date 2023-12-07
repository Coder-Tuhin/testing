package fragments.sso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class CreateGoogleAuthFragment extends Fragment {
    TextView text_key,btn_continue;
    ImageView copy_key;

    public static CreateGoogleAuthFragment newInstance() {
        CreateGoogleAuthFragment fragment = new CreateGoogleAuthFragment();
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.authenticator_code_generate,container,false);
        text_key = mView.findViewById(R.id.text_key);
        copy_key = mView.findViewById(R.id.copy_key);
        btn_continue = mView.findViewById(R.id.btn_continue);
        CallSSoAPIFOrgenerateGoogleAuth(UserSession.getLoginDetailsModel().getUserID());
        copy_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text_key.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    openApp(getActivity(), "com.google.android.apps.authenticator2");

                    Activity currentActivity = getActivity();
                    if(currentActivity instanceof HomeActivity){
                        final Fragment fragment2 = RegisterGoogleAuthFragment.newInstance(1);
                        GlobalClass.fragmentTransaction(
                                fragment2, R.id.container_body, false, fragment2.getClass().getSimpleName());

                    }else {
                        final Fragment fragment = RegisterGoogleAuthFragment.newInstance(1);
                        GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    final Fragment fragment = RegisterGoogleAuthFragment.newInstance(1);
                    GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
                }
            }
        });
        return mView;

    }

    private void CallSSoAPIFOrgenerateGoogleAuth (String Clientcode){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.GoogleAuth_Code.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
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
                            //TIL_Otp.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.GoogleAuth_Code.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        }else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                            VenturaException.SSOPrintLog(eSSOApi.GoogleAuth_Code.value, "RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            //ParseErrorResponse(responseString);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
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

    private void ParseAuthCodeResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String AuthCode = jsonObject.optString("auth_code");
            if(AuthCode.length() > 0){
                text_key.setText(AuthCode);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openApp(Context context, String packageName) {

        try {
            String issuer = "Ventura Securities : "+UserSession.getLoginDetailsModel().getUserID();
            String uri = "otpauth://totp/" + issuer + "?secret="+text_key.getText().toString();

            // Launch Google Authenticator
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }catch (Exception e){
            PackageManager manager = requireContext().getPackageManager();
            requireContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2")));
        }

        /*
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
        context.startActivity(i);*/
    }
}
