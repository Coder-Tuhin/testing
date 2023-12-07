package com.ventura.venturawealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.SharedPref;

/**
 * Created by XTREMSOFT on 10/18/2016.
 */
@SuppressLint("StaticFieldLeak")
public class VenturaApplication extends MultiDexApplication {
    private static VenturaApplication _mInstance;
    private static Context _mContext;
    private static String _mPackageName;
    private static SharedPref _mPreference;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        _mInstance = this;
        _mContext = getApplicationContext();
        GlobalClass.latestContext = getApplicationContext();
        _mPackageName = getPackageName();

        MobileInfo.getIPAddress(true);
        setHeightWidth();
    }

    public static Context getContext() {
        return _mContext;
    }

    public static VenturaApplication getInstance() {
        return _mInstance;
    }

    public static String getPackage() {
        return _mPackageName;
    }

    public static SharedPref getPreference(){
      if (_mPreference == null){
          _mPreference = new SharedPref();
      }
      return _mPreference;
    }

    private void setHeightWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ObjectHolder.dHeight  =  displayMetrics.heightPixels;
        ObjectHolder.dWidth  = displayMetrics.widthPixels;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setHeightWidth();
    }
}
