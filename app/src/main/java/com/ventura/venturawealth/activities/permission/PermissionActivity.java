package com.ventura.venturawealth.activities.permission;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.BaseContract;
import com.ventura.venturawealth.activities.splash.SplashActivity;
import com.ventura.venturawealth.activities.ssologin.Sso_Login_Activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.StaticVariables;
import utils.VenturaException;

public class PermissionActivity extends BaseActivity{

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_permission;
    }

    @Override
    protected BaseContract.IPresenter getPresenter() {
        return null;
    }

    @Override
    protected void onPermissionGranted() {
        PreferenceHandler.setPermissionGranted(true);
        //new eRestAPIReq().execute();
        startActivity(new Intent(PermissionActivity.this, Sso_Login_Activity.class));
        //startActivity(new Intent(PermissionActivity.this, FirstScreenActivity.class));
        PermissionActivity.this.finish();
    }

    public void grantPermissionClicked(View view) {
      checkMendatoryPermissions();
    }


    @Override
    public Context getContext() {
        return PermissionActivity.this;
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    /*
    private class eRestAPIReq extends AsyncTask<String, Void, String> {

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
                startActivity(new Intent(PermissionActivity.this, Sso_Login_Activity.class));
                //startActivity(new Intent(PermissionActivity.this, FirstScreenActivity.class));
                PermissionActivity.this.finish();
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
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
    }*/

}
