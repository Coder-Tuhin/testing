package fragments.sso;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
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
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.WebViewSSO;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eSSOApi;
import enums.eSSOJourney;
import enums.eSSOTag;
import fragments.sso.structure.SsoModel;
import models.LoginDetailsModel;
import utils.Connectivity;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class SsoLogin extends Fragment {

    TextInputEditText edit_text;
    TextView submit,new_user;
    TextInputLayout filledTextField;
    ArrayList<SsoModel> clientlist;
    RecyclerView recyclerView;
    private SSoAdapter adapter;
    private int rowHeight;
    ScrollView scrollview;

    TextView ortext;
    LinearLayout clientlist_ll;

    public static SsoLogin newInstance() {
        SsoLogin fragment = new SsoLogin();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.sso_login_screen, container, false);
        edit_text = mView.findViewById(R.id.edit_text);
        submit = mView.findViewById(R.id.submit);
        filledTextField = mView.findViewById(R.id.filledTextField);
        new_user = mView.findViewById(R.id.new_user);
        recyclerView = mView.findViewById(R.id.recyclerView);
        scrollview = mView.findViewById(R.id.scrollView);

        clientlist_ll = mView.findViewById(R.id.clientlist_ll);
        ortext = mView.findViewById(R.id.ortext);
        adapter = new SSoAdapter();
        recyclerView.setAdapter(adapter);
        enableSublitBtn(false);
        clientlist = new ArrayList<>();
        clientlist = PreferenceHandler.getSsoClientDetails();
        GlobalClass.OtpResponse = "";
        /*if(clientlist.size() > 0){
            adapter.reloadData(clientlist);
        }else {*/
            //ortext.setText("Log In");
            clientlist_ll.setVisibility(View.GONE);
        //}
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    enableSublitBtn(true);
                }else {
                    enableSublitBtn(false);
                }
                filledTextField.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(eSSOApi.NewUserUrl.value));
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filledTextField.setError("");
                if (edit_text.getText().toString().trim().length() > 0) {
                    LoginDetailsModel ldm = UserSession.getLoginDetailsModel();
                    ldm.setUserID(edit_text.getText().toString().toUpperCase());
                    filledTextField.setError("");
                    //GlobalClass.showdialog("Requesting...");
                    CallSSoAPI(edit_text.getText().toString().trim());
                } else {
                    filledTextField.setError("Please Enter Valid Mobile No/Email ID/Client ID");
                }
            }
        });

        return mView;
    }
    private void enableSublitBtn(boolean btn){
        submit.setEnabled(btn);
        submit.setAlpha(btn?1:0.5f);
    }

    private void CallSSoAPI(String Clientcode) {
        enableSublitBtn(false);
        if(!Connectivity.isNetworkConnected(getContext())){
            filledTextField.setError(Constants.NO_INTERNET);
            return;
        }
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.validateuser.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", Clientcode);
            jsonBody.put("application", "ventura_wealth");
            jsonBody.put("application_version", MobileInfo.getAppVersionCode()+"");

            JSONObject meta = new JSONObject();
            meta.put("os", "android");
            meta.put("os version", MobileInfo.getAppVersionName());
            meta.put("browser version", "98.0.4758.101");
            meta.put("browser type", "chrome");
            meta.put("device type", MobileInfo.model);

            jsonBody.put("meta", meta);
            jsonBody.put("datetime", currenttime);
            jsonBody.put(eSSOTag.uuid.name, MobileInfo.getDeviceID(getActivity()));

            final String mRequestBody = jsonBody.toString();
            GlobalClass.log("SSO Req param : " + mRequestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    ParseResponse(response);
                    VenturaException.SSOPrintLog(eSSOApi.validateuser.value,"SuccessRequestBody : "+mRequestBody+" "+currenttime+" : ResponseBody : "+response + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                        if(error instanceof TimeoutError){
                            filledTextField.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.validateuser.value, "ErrTimeOut RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        } else if(error instanceof NoConnectionError){
                            filledTextField.setError(Constants.NO_INTERNET2);
                            VenturaException.SSOPrintLog(eSSOApi.validateuser.value, "ErrNoConn RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        } else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY_ERR_RES", responseString + "Headers : " + error.networkResponse.headers.toString());
                            VenturaException.SSOPrintLog(eSSOApi.validateuser.value, "Err RequestBody : " + mRequestBody + " " + currenttime + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            erroResponse(responseString);
                        }
                    } catch (Exception e) {
                        GlobalClass.onError("",e);
                        filledTextField.setError(Constants.NO_INTERNET2);
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
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(eSSOTag.ContentType.name, eSSOTag.ContentType.value);
                    params.put(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                        GlobalClass.log("SSO_N validate : " + response.headers.toString());
                        PreferenceHandler.setSSOSessionID(response.headers.get("session_id"));
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            GlobalClass.onError("",e);
        }
    }

    private void ParseResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject job = new JSONObject(response);
            String msg = job.optString("message");
            //String uuid = jsonObject.optString("uuid");
            if(msg.equalsIgnoreCase("")){
                msg = job.optString("mssage","");
            }
            if (msg.length() > 0) {
                GlobalClass.showAlertDialog(msg);
            }
            String first_name = job.getString("fname");
            String last_name = job.getString("lname");
            String account_status = job.getString("acc_status");
            String client_id = job.getString("client_id");

            boolean first_time_user = job.getBoolean("newuser");
            //boolean account_locked = job.getBoolean("locked");
            boolean pin_available = job.getBoolean("pin_available");
            try {
                UserSession.getLoginDetailsModel().setUserID(client_id.toUpperCase());
                UserSession.getLoginDetailsModel().setClient(true);
                UserSession.getLoginDetailsModel().setClientName(first_name + " " + last_name);
                if(UserSession.getClientResponse() != null) {
                    UserSession.getClientResponse().charUserName.setValue(first_name + " " + last_name);
                }
                GlobalClass.GoogleAuthEnabled = job.optBoolean("google_auth_enabled");
                PreferenceHandler.setSSOCreateAuth(GlobalClass.GoogleAuthEnabled?"1":"0");

            }catch (Exception ex){}
            UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
            VenturaApplication.getPreference().setFamilyData(null);
            if(first_time_user){
                final Fragment fragment = ValidateUserByDOBPAN_Fragment.newInstance(eSSOJourney.firstlogin);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
            }else if(pin_available || account_status.equalsIgnoreCase("active")){
                //final Fragment fragment = ChooseVerificationMethodFragment.newInstance(response, eSSOJourney.login);
                if(GlobalClass.GoogleAuthEnabled){
                    final Fragment fragment = ValidateGoogleAuthFragment.newInstance(response);
                    GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
                }else{
                    final Fragment fragment = AskGoogleAuthFragment.newInstance(response, eSSOJourney.login);
                    GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
                }
            }else{
                GlobalClass.showAlertDialog("Account is not active",false);
            }

        } catch (Exception e) {
            GlobalClass.onError("",e);
        }
    }

    private void erroResponse(String response) {
        try {
            enableSublitBtn(true);
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.optString("message","");
            boolean locked = jsonObject.optBoolean("locked",false);
            if(msg.equalsIgnoreCase("")){
                msg = jsonObject.optString("mssage","");
            }
            if(locked){
                String first_name = jsonObject.optString("fname","");
                String last_name = jsonObject.optString("lname","");
                String client_id = jsonObject.optString("client_id","");
                //String account_status = jsonObject.optString("acc_status","");
                try {
                    if(!client_id.equalsIgnoreCase("")) {
                        UserSession.getLoginDetailsModel().setUserID(client_id.toUpperCase());
                    }
                    UserSession.getLoginDetailsModel().setClient(true);
                    if(!first_name.equalsIgnoreCase("") && !last_name.equalsIgnoreCase("")) {
                        UserSession.getLoginDetailsModel().setClientName(first_name + " " + last_name);
                    }

                    if(UserSession.getClientResponse() != null) {
                        UserSession.getClientResponse().charUserName.setValue(first_name + " " + last_name);
                    }
                }catch (Exception ex){}
                UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(""+msg);
                builder.setPositiveButton("Unlock Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Fragment fragment = ValidateUserByDOBPAN_Fragment.newInstance(eSSOJourney.unlock);
                        GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.show();
            }
            if (msg.length() > 0) {
                filledTextField.setError(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SSoAdapter extends RecyclerView.Adapter<SSoAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<SsoModel> mList;

        public SSoAdapter() {
            inflater = LayoutInflater.from(getActivity());
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<SsoModel> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        @Override
        public SSoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_sso, parent, false);
            SSoAdapter.MyViewHolder holder = new SSoAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SSoAdapter.MyViewHolder holder, final int position) {
            try {
                SsoModel model = mList.get(position);
                holder.clientcode.setText("Client Code : "+model.getClientCode());
                holder.clientname.setText(model.getClientName());
                char[] charArray = model.getClientName().toCharArray();
                char first = charArray[0];
                TextDrawable drawable2 = TextDrawable.builder().beginConfig()
                        .width(130)  // width in px
                        .height(130) // height in px
                        .endConfig()
                        // as we are building a circular drawable we
                        // are calling a build round method.
                        // in that method we are passing our text and color.
                        .buildRound(String.valueOf(first), getResources().getColor(R.color.ventura_color)); // radius in px
                holder.imageview.setImageDrawable(drawable2);
                holder.client_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CallSSoAPI(model.getClientCode());
                        //CallSSoAPIForViewActiveSession(model);
                    }
                });
                holder.img_cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<SsoModel> ssoModels = null;
                        ssoModels = PreferenceHandler.getSsoClientDetails();
                        ssoModels.remove(position);
                        PreferenceHandler.setSsoClientDetails(ssoModels);
                        adapter.reloadData(ssoModels);
                        if(ssoModels.size() <= 0){
                            ortext.setText("Log In");
                            clientlist_ll.setVisibility(View.GONE);
                        }
                    }
                });

                if(rowHeight <= 0) {
                    holder.itemView.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            rowHeight = holder.itemView.getHeight();// this will give you cell height dynamically
                            if(clientlist.size() > 3){
                                ViewGroup.LayoutParams params = scrollview.getLayoutParams();
                                // Changes the height and width to the specified pixels
                                params.height = rowHeight*3;
                                scrollview.setLayoutParams(params);
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.clientcode)
            TextView clientcode;
            @BindView(R.id.clientname)
            TextView clientname;
            @BindView(R.id.client_layout)
            LinearLayout client_layout;
            @BindView(R.id.imageview)
            ImageView imageview;
            @BindView(R.id.img_cross)
            TextView img_cross;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private void CallSSoAPIForViewActiveSession (SsoModel ssoModel){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "";
            URL = eSSOApi.BASEURL.value+ eSSOApi.token_validation.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", ssoModel.getClientCode());

            final String mRequestBody = jsonBody.toString();
            GlobalClass.log("Validate req : "+mRequestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY", response);
                    GlobalClass.dismissdialog();
                    ParseSessionResponse(response,ssoModel);
                    //VenturaException.SSOPrintLog(eSSOApi.token_validation.value,"RequestBody : "+mRequestBody+" : ResponseBody200: : "+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        GlobalClass.dismissdialog();
                        if(error instanceof TimeoutError){
                            GlobalClass.log("LOG_VOLLEY_ERR1", error.toString());
                            filledTextField.setError("Error : Request timeout. Please try after sometime.");
                            VenturaException.SSOPrintLog(eSSOApi.token_validation.value, "ErrTimeOut RequestBody : " + mRequestBody + " " + currenttime + " :session_id: " + ssoModel.getSessionID() + " :Authorization: " + "Bearer " + ssoModel.getAuthToken() + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        } else if(error instanceof NoConnectionError){
                            filledTextField.setError(Constants.NO_INTERNET2);
                            VenturaException.SSOPrintLog(eSSOApi.token_validation.value, "ErrNoConn RequestBody : " + mRequestBody + " " + currenttime + " :session_id: " + ssoModel.getSessionID() + " :Authorization: " + "Bearer " + ssoModel.getAuthToken() + " : ResponseBody : " + error.toString() + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                        } else {
                            String responseString = new String(error.networkResponse.data);
                            GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                            VenturaException.SSOPrintLog(eSSOApi.token_validation.value, "Err RequestBody : " + mRequestBody + " " + currenttime + " :session_id: " + ssoModel.getSessionID() + " :Authorization: " + "Bearer " + ssoModel.getAuthToken() + " : ResponseBody : " + responseString + " " + DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                            ParseSessionResponse(responseString, ssoModel);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        filledTextField.setError(Constants.NO_INTERNET2);
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
                    params.put(eSSOTag.ContentType.name, "Text");
                    params.put(eSSOTag.session_id.name, ssoModel.getSessionID());
                    params.put(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
                    params.put(eSSOTag.Authorization.name, "Bearer " + ssoModel.getAuthToken());
                    GlobalClass.log("Splash sso param : " + params.toString());
                    return params;
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                        String responsehead = response.headers.get("session_id");
                        PreferenceHandler.setSSOSessionID(responsehead);
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
            VenturaException.Print(e);
        }
    }

    private void ParseSessionResponse(String response,SsoModel model) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject.put("uuid", model.getAuthToken());


            if(jsonObject.optString("valid_token","").equalsIgnoreCase("true")) {
                PreferenceHandler.setSSORefreshToken(model.getRefreshToken());
                PreferenceHandler.setSSOAuthToken(model.getAuthToken());
                UserSession.getLoginDetailsModel().setUserID(model.getClientCode());
                UserSession.getLoginDetailsModel().setClientName(model.getClientName());
                final Fragment fragment = Sso_validatePin.newInstance();
                GlobalClass.fragmentTransaction(fragment,R.id.layout,false,fragment.getClass().getSimpleName());
            }else {
                CallSSoAPI(model.getClientCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
