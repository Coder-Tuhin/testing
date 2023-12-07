package com.ventura.venturawealth.activities.splash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.BaseContract;
import com.ventura.venturawealth.activities.permission.PermissionActivity;
import com.ventura.venturawealth.activities.ssologin.Sso_Login_Activity;
import com.ventura.venturawealth.activities.termscondition.TermsConditionActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import enums.eMFClientType;
import enums.eSSOApi;
import enums.eSSOTag;
import utils.DateUtil;
import utils.Encriptions.AES_Encryption.AES_Encryption;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;

/**
 * Created by XTREMSOFT on 10/7/2016.
 */
public class SplashActivity extends BaseActivity implements SplashContract.ISplashView {

    private SplashContract.ISplashPresenter presenter;

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.splashscreen;
    }

    @Override
    protected BaseContract.IPresenter getPresenter() {
        if (presenter == null){
            presenter = new SplashPresenterImpl(this);
        }
        return presenter;
    }

    @Override
    public Context getContext() {
        return SplashActivity.this;
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void onBackPressed() {
        if (GlobalClass.tradeBCClient != null)
            GlobalClass.tradeBCClient.stopClient();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        GlobalClass.deeplinkScreen = -1;
        try{
            List<String> diplinkParam = getIntent().getData().getPathSegments();
            for(String param : diplinkParam){
                if(param.contains("screentype")){
                    String[] sp = param.split("=");
                    GlobalClass.deeplinkScreen = Integer.parseInt(sp[1]);
                }
            }
            System.out.println("diplinkParam : " + diplinkParam.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GlobalClass.fragmentManager = getSupportFragmentManager();
        GlobalClass.showregisterauthtag = false;
        GlobalClass.isMaintanceMode = false;
        VenturaServerConnect.mfClientType = eMFClientType.NONE;
        if(GlobalClass.groupHandler !=  null){
            GlobalClass.groupHandler.isDataFetchFromMemory = false;
        }
        VenturaException.mCrashlytics = null;
        assert presenter!=null;
        presenter.startTimer();
        //String salt = AES_Encryption.getBASE64Salt();
        //byte[] saltBytes = salt.getBytes();
        try {
            String data = "VENTURA";
            byte[] key = AES_Encryption.generateSecureKey();
            GlobalClass.log("Key used :"+key.length);
            String encryptedSalt = AES_Encryption.EncryptWithKey(data.getBytes(AES_Encryption.CHARACTER_ENCODING), key);
            GlobalClass.log("Encrypted value of :"+data + " is " + encryptedSalt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openHomeScreen() {
        gotoFinalSceen();
        //new eRestAPIReq().execute();
    }
    @Override
    public void openFirstScreen() {
        gotoFinalSceen();
        //new eRestAPIReq().execute();
    }

    @Override
    public void openPermissionScreen() {
        startActivity(new Intent(this, PermissionActivity.class));
        finish(false);
    }

    @Override
    public void openTermsConditionScreen() {
        startActivity(new Intent(this, TermsConditionActivity.class));
        finish(false);
    }

    @Override
    public void openClientLoginScreen() {
        startActivity(new Intent(this, Sso_Login_Activity.class));
        finish(false);
    }

    @Override
    public void openAadharLink() {
        Intent myWebLink = new Intent(Intent.ACTION_VIEW);
        myWebLink.setData(Uri.parse("http://bit.ly/2iMAodl"));
        startActivity(myWebLink);
    }

    @Override
    public void onAppUpdate() {
        Uri uri = Uri.parse("market://details?id=" + VenturaApplication.getPackage() );
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(goToMarket);
        finish(false);
    }

    private class eSplashValidateTokenReq extends AsyncTask<String, Void, String>  {

        String clientCode;
        String authToken;

        int responseCode;

        eSplashValidateTokenReq(String clCode,String authToken){
            this.clientCode = clCode;
            this.authToken = authToken;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Please wait...");
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(String... urls)  {
            try {
                APIResp apiResp = CallSSoAPIForValidateToken(clientCode, authToken);
                if(apiResp.respData.equalsIgnoreCase("")){
                    apiResp = CallSSoAPIForValidateToken2(clientCode, authToken);
                }
                responseCode = apiResp.responseCode;
                return apiResp.respData;
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
                if(s != null) {
                    ParseResponse(s);
                }else{
                    ParseResponse("");
                }
            }catch (Exception ex){
                GlobalClass.onError("",ex);
                ParseResponse("");
            }
        }
    }

/*
    private class eRestAPIReq extends AsyncTask<String, Void, String>  {

        eRestAPIReq(){
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
                String url = "https://vw.ventura1.com/authrestapi/getSSOTag";
                String strresponse = sendRequest(url);
                GlobalClass.log("getSSOTag Res: "+strresponse);
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
                if(s != null && !s.isEmpty()) {
                    GlobalClass.isSSOTag = s.equalsIgnoreCase("1");
                }
                gotoFinalSceen();
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
    }*/
    private void gotoFinalSceen(){
        if (TextUtils.isEmpty(UserSession.getLoginDetailsModel().getUserID())){
            startActivity(new Intent(SplashActivity.this, Sso_Login_Activity.class));
            finish(false);
        }else {
            //if (GlobalClass.isSSOTag) {
                String ssotoken = PreferenceHandler.getSSOAuthToken();
                if (ssotoken.equalsIgnoreCase("")) {
                    startActivity(new Intent(SplashActivity.this, Sso_Login_Activity.class));
                    finish(false);
                } else {
                    new eSplashValidateTokenReq(UserSession.getLoginDetailsModel().getUserID(), PreferenceHandler.getSSOAuthToken()).execute();
                }
            /*} else {
                //check is pin available or not...if available goto pin validation sceen
                //else NonSSOLogin Screen...
                Intent intent = new Intent(SplashActivity.this, Sso_Login_Activity.class);
                intent.putExtra("msg", "");
                intent.putExtra("issessionvalid", true);

                GlobalClass.isFromNotificationClick = getIntent().getBooleanExtra(StaticVariables.FROM_NOTIFICATION, false);
                startActivity(intent);
            }*/
        }
    }

    class APIResp{
        String respData = "";
        int responseCode;
    }

    private  String sendRequest(String url){

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
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            //  xModuleClass.httpURLConnection.put(url, conn);
            //}
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
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
    public APIResp CallSSoAPIForValidateToken(String Clientcode, String authToken){

        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        APIResp apiResp = new APIResp();
        HttpsURLConnection connection=null;
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            final String mRequestBody = jsonBody.toString();
            //InetAddress addI = InetAddress.getByName("http://api.ipify.org");

            StringBuffer strresponse = new StringBuffer();
            //InetAddress address = InetAddress.getByName("sso.ventura1.com");
            URL obj = new URL(eSSOApi.BASEURL.value + eSSOApi.token_validation.value);
            connection = (HttpsURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setUseCaches(false);
            //60 sec timeout
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            connection.setRequestProperty(eSSOTag.ContentType.name, eSSOTag.ContentType.value);
            connection.setRequestProperty(eSSOTag.session_id.name, PreferenceHandler.getSSOSessionID());
            connection.setRequestProperty(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
            connection.setRequestProperty(eSSOTag.Authorization.name, "Bearer " + authToken);

            GlobalClass.log("Session : "+PreferenceHandler.getSSOSessionID());
            GlobalClass.log("xapikey : "+eSSOTag.xapikey.value);
            GlobalClass.log("token : "+"Bearer " + authToken);

            connection.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {

                writer.write(mRequestBody);
                writer.flush();
                apiResp.responseCode = connection.getResponseCode();
                GlobalClass.log("Response Code : "+apiResp.responseCode);

                if (apiResp.responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();
                    connection.disconnect();
                    GlobalClass.log("ValidToken sso: " + strresponse.toString());
                    apiResp.respData = strresponse.toString();
                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    connection.disconnect();
                    GlobalClass.log("Error sso: " + errorresponse.toString());
                    VenturaException.SSOPrintLog(eSSOApi.token_validation.value,"RequestBody : "+mRequestBody+" "+currenttime +" :session_id: "+PreferenceHandler.getSSOSessionID() + " :Authorization: "+"Bearer " + authToken+" : ResponseBody : "+errorresponse.toString() + " "+DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                    apiResp.respData = errorresponse.toString();
                }
            }
        }
        catch(Exception e){
           e.printStackTrace();
           VenturaException.Print(e);
        }
        finally{
            if(connection != null){
                connection.disconnect();
            }
        }
        return apiResp;
    }

    public APIResp CallSSoAPIForValidateToken2(String Clientcode, String authToken){

        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        APIResp apiResp = new APIResp();
        HttpsURLConnection connection=null;
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", Clientcode);
            final String mRequestBody = jsonBody.toString();
            //InetAddress addI = InetAddress.getByName("http://api.ipify.org");

            StringBuffer strresponse = new StringBuffer();
            InetAddress address = InetAddress.getByName("sso.ventura1.com");

            URL obj = new URL(eSSOApi.BASEURL.value + eSSOApi.token_validation.value);
            connection = (HttpsURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setUseCaches(false);
            //60 sec timeout
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            connection.setRequestProperty(eSSOTag.ContentType.name, eSSOTag.ContentType.value);
            connection.setRequestProperty(eSSOTag.session_id.name, PreferenceHandler.getSSOSessionID());
            connection.setRequestProperty(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
            connection.setRequestProperty(eSSOTag.Authorization.name, "Bearer " + authToken);

            GlobalClass.log("Session : "+PreferenceHandler.getSSOSessionID());
            GlobalClass.log("xapikey : "+eSSOTag.xapikey.value);
            GlobalClass.log("token : "+"Bearer " + authToken);

            connection.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {

                writer.write(mRequestBody);
                writer.flush();
                apiResp.responseCode = connection.getResponseCode();
                GlobalClass.log("Response Code : "+apiResp.responseCode);

                if (apiResp.responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();
                    connection.disconnect();
                    GlobalClass.log("ValidToken sso: " + strresponse.toString());
                    apiResp.respData = strresponse.toString();
                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    connection.disconnect();
                    GlobalClass.log("Error sso: " + errorresponse.toString());
                    VenturaException.SSOPrintLog(eSSOApi.token_validation.value,"RequestBody : "+mRequestBody+" "+currenttime +" :session_id: "+PreferenceHandler.getSSOSessionID() + " :Authorization: "+"Bearer " + authToken+" : ResponseBody : "+errorresponse.toString() + " "+DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                    apiResp.respData = errorresponse.toString();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            VenturaException.Print(e);
        }
        finally{
            if(connection != null){
                connection.disconnect();
            }
        }
        return apiResp;
    }
    private void ParseResponse(String response) {
        try {
            if(!response.equalsIgnoreCase("")) {
                JSONObject jsonObject = new JSONObject(response);

                Intent intent = new Intent(SplashActivity.this, Sso_Login_Activity.class);
                intent.putExtra("msg", jsonObject.optString("message"));
                if (!jsonObject.optString("valid_token", "").equalsIgnoreCase("true")) {
                    intent.putExtra("issessionvalid", false);
                } else {
                    intent.putExtra("issessionvalid", true);
                }
                GlobalClass.isFromNotificationClick = getIntent().getBooleanExtra(StaticVariables.FROM_NOTIFICATION, false);
                startActivity(intent);
            }else{
                Intent intent = new Intent(SplashActivity.this, Sso_Login_Activity.class);
                intent.putExtra("msg", "");
                intent.putExtra("issessionvalid", false);
                GlobalClass.isFromNotificationClick = getIntent().getBooleanExtra(StaticVariables.FROM_NOTIFICATION, false);
                startActivity(intent);
            }
            finish(false);
        }catch (Exception e){
            GlobalClass.showAlertDialog("Error1 :" + e.toString(),true);
            VenturaException.Print(e);
        }
    }
}