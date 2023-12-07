package com.ventura.venturawealth.activities.splash;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Structure.Request.Auth.GuestLoginRequest;
import Structure.Request.Auth.LoginRequest;
import Structure.Response.AuthRelated.ClientLoginResponse;
import Structure.Response.AuthRelated.GuestLoginResponse;
import connection.Connect;
import connection.ConnectionProcess;
import connection.SendDataToAuthServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eLoginType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eSocketClient;
import utils.Constants;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class SplashPresenterImpl implements SplashContract.ISplashPresenter {

    private WeakReference<SplashContract.ISplashView> view;

    SplashPresenterImpl(SplashContract.ISplashView view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public void startTimer() {
        new Thread() {
            public void run() {
                try {
                    if (view.get() != null && view.get().getBaseActivity().isNetworkConnected()) {
                        if(!isDeviceRooted()) {
                            if (!PreferenceHandler.getTermsCondition()) {
                                sleep(2000);
                                view.get().getBaseActivity().runOnUiThread(() -> {
                                    view.get().openTermsConditionScreen();
                                });
                            } else if (!PreferenceHandler.getPermissionGranted()) {
                                sleep(1200);
                                view.get().getBaseActivity().runOnUiThread(() -> {
                                    view.get().openPermissionScreen();
                                });
                            } else if (TextUtils.isEmpty(UserSession.getLoginDetailsModel().getUserID())) {
                                sleep(1200);
                                view.get().getBaseActivity().runOnUiThread(() -> {
                                    view.get().openFirstScreen();
                                });
                            } else {
                                view.get().getBaseActivity().runOnUiThread(() -> {
                                    view.get().openHomeScreen();
                                });
                                //domainNameIP = ObjectHolder.connconfig.getAuthIP();
                                //Connect.connect(view.get().getContext(), SplashPresenterImpl.this, eSocketClient.AUTH);
                            }
                        }else{
                            //GlobalClass.addAndroidLog(eLogType.NONPING.name,MobileInfo.getMobileData(),"");
                            sleep(100);
                            view.get().getBaseActivity().showMsgDialog("Failed to Open!",R.string.root_msg,true);
                        }
                    } else {
                        GlobalClass.addAndroidLog(eLogType.NONETWORK.name,MobileInfo.getMobileData(),"");
                        sleep(100);
                        view.get().getBaseActivity().showMsgDialog("Failed to Connect!",R.string.No_internet,true);
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);
                }
            }
        }.start();
    }

    private boolean isDeviceRooted() {
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {}

        return false;
    }


    private boolean executeCommand(){
        GlobalClass.log("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            GlobalClass.log(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }catch (InterruptedException ignore) {
            ignore.printStackTrace();
            GlobalClass.log(" Exception:"+ignore);
        }catch (IOException e) {
            e.printStackTrace();
            GlobalClass.log(" Exception:"+e);
        }
        return false;
    }


    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onViewAttached() {
    }

    @Override
    public void onViewDetached() {
    }

    private void loginFinalization(ClientLoginResponse clr){
        try{
            ObjectHolder.connconfig.setbCastServerIP(clr.getBCastDomainName());
            ObjectHolder.connconfig.setTradeServerIP(clr.getTradeDomainName());

            ObjectHolder.connconfig.setBcServerPort(clr.intPortBC.getValue());
            ObjectHolder.connconfig.setRcServerPort(clr.intPortRC.getValue());
            ObjectHolder.connconfig.setWealthServerPort(clr.intPortWealth.getValue());
            VenturaApplication.getPreference().setConnectionConfig(ObjectHolder.connconfig);
            if (!TextUtils.isEmpty(clr.mobileNumber.getValue())){
                UserSession.getLoginDetailsModel().setMobileNo(clr.mobileNumber.getValue());
                UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
            }
            final String msg = clr.charResMsg.getValue();
            if (!TextUtils.isEmpty(msg) && view.get()!=null){
                view.get().getBaseActivity().showToast(msg);
            }
            view.get().getBaseActivity().runOnUiThread(() -> {
                view.get().openHomeScreen();
            });
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
}