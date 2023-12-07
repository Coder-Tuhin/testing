package com.ventura.venturawealth.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import enums.ePrefTAG;
import utils.CustomProgressDialog;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.ScreenColor;
import utils.StaticMessages;
import utils.VenturaException;
import view.FullScreenDialog;
import view.UPIHandlerDialogFragment;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class BaseActivity extends AppCompatActivity implements BaseContract.IView {
    protected boolean mIsResumed = false;

    public static Context getLatestContext(){
        return GlobalClass.latestContext;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (getStatusbarColor()!=0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(getStatusbarColor()));
//        }
//        if (Build.VERSION.SDK_INT >= 26) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //BaseActivity.latestContext = this;
        if (getLayoutResource() != 0) {
            setContentView(getLayoutResource());
        }
        //TODO Remove later
        GlobalClass.latestContext = this;
    }

    protected abstract int getLayoutResource();
    //  protected abstract int getStatusb`arColor();

    public Context getLetestContext() {
        return this;
    }

    public static BaseActivity getActivity() {
        return (BaseActivity) GlobalClass.latestContext;
    }

    /*
    private FirebaseCrashlytics mCrashlytics;

    public FirebaseCrashlytics getCrashlytics(){
        return mCrashlytics;
    }*/

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        BaseContract.IPresenter presenter = getPresenter();
        if (presenter != null) presenter.onViewAttached();
        VenturaApplication application = (VenturaApplication) getApplication();
        if (this instanceof HomeActivity) {
            setTheme(PreferenceHandler.getFontStyle());
            //Login to Febric
            //Crashlytics.setUserIdentifier(UserSession.getLoginDetailsModel().getMobileNo());
            //Crashlytics.setUserName(UserSession.getLoginDetailsModel().getClientName());
        }
        /*
        mCrashlytics = FirebaseCrashlytics.getInstance();
        // Add some custom values and identifiers to be included in crash reports
        mCrashlytics.setCustomKey("Build Version", String.valueOf(BuildConfig.VERSION_CODE));
        String _tepuser = UserSession.getLoginDetailsModel().getUserID().isEmpty()?"Non Login":UserSession.getLoginDetailsModel().getUserID();
        mCrashlytics.setUserId(_tepuser);
        mCrashlytics.setCrashlyticsCollectionEnabled(true);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseContract.IPresenter presenter = getPresenter();
        if (presenter != null) presenter.onViewDetached();
    }

    @Override
    protected void onResume() {
        //BaseActivity.latestContext = this;
        GlobalClass.latestContext = this;
        mIsResumed = true;
        PreferenceHandler.REMOVE_PREVIOUS_DAY_DATA();
        BaseContract.IPresenter presenter = getPresenter();
        if (presenter != null) presenter.onResume();
        if (this instanceof HomeActivity) {
            if (!isSessionExpired()) {
                handleInactiveSession();
            } else {
                InterectionAlert();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mIsResumed = false;
        dismisProgress();
        BaseContract.IPresenter presenter = getPresenter();
        if (presenter != null)
            presenter.onPause();
        super.onPause();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }
    */

    public boolean isActivityResumed() {
        return mIsResumed;
    }

    public void hidesoftKeyBoard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    public void showToast(int stingResourceID) {
        String msg = getResources().getString(stingResourceID);
        showToast(msg);
    }

    public void showToast(String msg) {
        if (isActivityResumed()) {
            runOnUiThread(() -> Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show());
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean haveNetwork(){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

    private AlertDialog msgDialog;

    public void showMsgDialog(int stingResourceID) {
        String msg = getResources().getString(stingResourceID);
        showMsgDialog(msg, false);
    }
    public void showMsgDialog(String msg) {
        showMsgDialog(msg, false);
    }

    public void showMsgDialog(String title,int stingResourceID) {
        String msg = getResources().getString(stingResourceID);
        showMsgDialog(title,msg, false);
    }

    public void showMsgDialog(int stingResourceID, boolean exit) {
        String msg = getResources().getString(stingResourceID);
        showMsgDialog(msg, exit);
    }

    public void showMsgDialog(String msg, DialogInterface.OnClickListener okListener) {
        showMsgDialog(msg, okListener, null);
    }

    public void showMsgDialogOk(String msg, DialogInterface.OnClickListener okListener) {
        showMsgDialogOkWithNoTitle(msg, okListener, null);
    }
    public void showMsgDialog(String title,String msg,DialogInterface.OnClickListener okListener,
                              DialogInterface.OnClickListener cancelListener) {
        showMsgDialog(title,msg,"Ok","Cancel",okListener,cancelListener);
    }

    public void showMsgDialog(String msg, boolean exit) {
        showMsgDialog("Alert!", msg, exit);
    }

    public void showMsgDialog(String title, int stingResourceID, boolean exit) {
        String msg = getResources().getString(stingResourceID);
        showMsgDialog(title, msg, exit);
    }

    public void showMsgDialog(String title, String msg, boolean exit) {
        if (isActivityResumed() && !isSessionExpired()) {
            dismissMsgDialog();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getLetestContext());
                        builder.setIcon(R.drawable.ventura_icon);
                        builder.setTitle(title);
                        builder.setMessage(msg);
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            if (exit) {
                                finishAffinity();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        });
                        if (exit) builder.setCancelable(false);
                        CreateDialog(builder);
                    }catch (Exception e){
                        VenturaException.Print(e);
                    }

                }
            });
        }
    }

    public void showMsgDialog(String msg, DialogInterface.OnClickListener okListener,
                              DialogInterface.OnClickListener cancelListener) {
        showMsgDialog("Alert!", msg, "Ok", "Cancel", okListener, cancelListener);
    }

    public void showMsgDialogOkWithNoTitle(String msg, DialogInterface.OnClickListener okListener,
                              DialogInterface.OnClickListener cancelListener) {
        showMsgDialog("", msg, "Ok", "Cancel", okListener, cancelListener);
    }

    public void showMsgDialogOkWithNoTitleNoCancel(String msg, DialogInterface.OnClickListener okListener) {
        showMsgDialog("", msg, "Ok", null, okListener, null);
    }

    public void showMsgDialog(String title, String msg, String Ok, String Cancel, DialogInterface.OnClickListener okListener,
                              DialogInterface.OnClickListener cancelListener) {
        if (isActivityResumed() && !isSessionExpired()) {
            runOnUiThread(() -> {
                dismissMsgDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ventura_icon);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setCancelable(false);
                builder.setPositiveButton(Ok, okListener);
                if (cancelListener != null)
                    builder.setNegativeButton(Cancel, cancelListener);
                CreateDialog(builder);
            });
        }
    }

    private void CreateDialog(AlertDialog.Builder builder) {
        dismissMsgDialog();
        msgDialog = builder.create();
        msgDialog.show();
        msgDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);
        msgDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ScreenColor.VENTURA);
    }

    private void dismissMsgDialog() {
        if (msgDialog != null) {
            msgDialog.dismiss();
            msgDialog = null;
        }
    }


    protected abstract BaseContract.IPresenter getPresenter();


    public void finish(boolean transaction) {
        super.finish();
        if (transaction) overridePendingTransitionExit();
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {

        super.startActivityForResult(intent, requestCode);
        //  overridePendingTransitionEnter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
        overridePendingTransitionExit();
    }

    private final int REQUEST_CODE_PERMISSION = 100;

    public void checkMendatoryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        } else {
            onPermissionGranted();
        }
    }

    private boolean checkPermission() {
        int _permission = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int _permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        //int _permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int _permission3 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        //int _permission4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return _permission == PackageManager.PERMISSION_GRANTED
                && _permission1 == PackageManager.PERMISSION_GRANTED
                //&& _permission2 == PackageManager.PERMISSION_GRANTED
                && _permission3 == PackageManager.PERMISSION_GRANTED
                //&& _permission4 == PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermission() {
       // ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE,
         //       ACCESS_WIFI_STATE, WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);

        ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE,
                ACCESS_WIFI_STATE}, REQUEST_CODE_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0) {
                    boolean phonestateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean wifiAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    //boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    //boolean storageAccepted2 = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (phonestateAccepted && wifiAccepted /*&& storageAccepted && storageAccepted2*/) {
                        onPermissionGranted();
                    } else {
                        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)
                                || shouldShowRequestPermissionRationale(ACCESS_WIFI_STATE)
                                //|| shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)
                                //|| shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                        ) {
                            showMessageOKCancel((dialog, which) -> requestPermission());
                            return;
                        }
                    }
                }
                break;
        }
    }

    protected abstract void onPermissionGranted();

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied!")
                .setMessage("You need to allow all the permissions to access Features")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private CustomProgressDialog progressDialog;
    public void showProgress() {
        showProgress("Please wait...");
    }

    public void showProgress(int resource) {
        try {
            String msg = getResources().getString(resource);
            showProgress(msg);
        }catch (Exception Ex){
            Ex.printStackTrace();
        }
    }

    public void showProgress(final String msg) {
        if (isActivityResumed()) {
            dismisProgress();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        progressDialog = new CustomProgressDialog(GlobalClass.latestContext, msg);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                    } catch (Exception e) {
                        VenturaException.Print(e);
                    }
                }
            });
        }
    }

    public void dismisProgress() {
        if (progressDialog != null ){//&& progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
/*
    public void REMOVE_PREVIOUS_DAY_DATA() {
        Date currDate = Calendar.getInstance().getTime();
        String currDateStr = DateUtil.DDMMMYYYY.format(currDate);
        if (!PreferenceHandler.getPreviousLoginDate().equals(currDateStr)) {
            try {
                PreferenceHandler.setPreviousLoginDate(currDateStr);
                PreferenceHandler.setHoldingReqRequired(true);
                PreferenceHandler.setUnitWithdraw(true);
                PreferenceHandler.getMessageList().clear();
                PreferenceHandler.setMessageList();
                PreferenceHandler.getNotificationList().clear();
                PreferenceHandler.setNotificationList();

                VenturaApplication.getPreference().setGroupDetail(null);

                TradeLoginModel tradeDetails = VenturaApplication.getPreference().getTradeDetails();
                if (tradeDetails != null) {
                    tradeDetails.setDayFirstLogin(true);
                    VenturaApplication.getPreference().setTradeDetails(tradeDetails);
                    MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
                    mpinDetails.addMPIN(UserSession.getLoginDetailsModel().getUserID(),tradeDetails);
                    VenturaApplication.getPreference().setMPINDetails(mpinDetails);
                    //int dateDiff = Math.abs(tradeDetails.getSaveTime().compareTo(currDate));
                    //if (dateDiff>=90){
                      //  VenturaApplication.getPreference().setTradeDetails(null);
                        //PreferenceHandler.setMpinRetryCount(1);
                    }
                }
            } catch (Exception ex) {
                VenturaException.Print(ex);
            }
        }
    }*/

    public void ViewEnableAfterDelay(final View view) {
        if (view != null) {
            view.setEnabled(false);
            view.postDelayed(() -> {
                view.setEnabled(true);
            }, 2000);
        }
    }

    public void notifySystemSound() {
        try{
            MediaPlayer.create(this, R.raw.notification).start();
        }catch (Exception ex){
            VenturaException.Print(ex);
        }/*
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            VenturaException.Print(e);
        }*/
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (this instanceof HomeActivity) {
            handleInactiveSession();
        }
    }

    private CountDownTimer delayCountDownTimer;
    private AlertDialog delayDialog;
    private TextView alertText;
    private Handler delayHandler;
    private final String EXPIRED_MSG = "Your session is expired.";
    private boolean SessionExpired = false;

    public boolean isSessionExpired() {
        return SessionExpired;
    }

    private Runnable delayRunnable = () -> {
        if (isActivityResumed()) {
            InterectionAlert();
        } else {
            SessionExpired = true;
        }
        removeDelayCallbacks();
    };

    private void handleInactiveSession() {
        try {
            int securityTime = Integer.parseInt(VenturaApplication.getPreference().getSharedPrefFromTag(
                    ePrefTAG.SECURITY.name, "60"));
            int inActiveMillis = securityTime * 1000 * 60;
            removeDelayCallbacks();
            delayHandler = new Handler();
            delayHandler.postDelayed(delayRunnable, inActiveMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDelayCallbacks() {
        if (delayHandler != null) {
            delayHandler.removeCallbacks(delayRunnable);
            delayHandler = null;
        }
    }


    private void dismissDelayDialog() {
        if (delayDialog != null) {
            delayDialog.dismiss();
            delayDialog = null;
        }
    }


    private void InterectionAlert() {
        try {
            dismissDelayDialog();
            final AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(this);
            m_alertBuilder.setTitle("Ventura Wealth");
            m_alertBuilder.setIcon(R.drawable.ventura_icon);
            m_alertBuilder.setMessage("");
            m_alertBuilder.setCancelable(false);
            m_alertBuilder.setPositiveButton("OK",
                    (dialog, id) -> {
                        dismissDelayDialog();
                        if (alertText.getText().toString().equals(EXPIRED_MSG)) {
                            finishAffinity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        } else {
                            if (delayCountDownTimer != null) {
                                delayCountDownTimer.cancel();
                            }
                            handleInactiveSession();
                        }
                    });
            delayDialog = m_alertBuilder.create();
            delayDialog.show();
            alertText = delayDialog.findViewById(android.R.id.message);
            delayDialog.getButton(delayDialog.BUTTON_POSITIVE).setTextColor(getResources()
                    .getColor(R.color.ventura_color));
            if (!isSessionExpired()) {
                delayCountDownTimer = new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String msg = "Your session will be expire after " +
                                (millisUntilFinished / 1000) + "seconds. Do you want active?";
                        alertText.setText(msg);
                    }

                    public void onFinish() {
                        SessionExpired = true;
                        GlobalClass.isSessionExpired = true;
                        alertText.setText(EXPIRED_MSG);
                    }
                }.start();
            } else {
                alertText.setText(EXPIRED_MSG);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    public void showFullScreenDialog(int resourceID){
        try {
            FragmentTransaction _ft = getSupportFragmentManager().beginTransaction();
            FullScreenDialog newFragment = FullScreenDialog.newInstance(resourceID);
            newFragment.show(_ft, "dialog");
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public void showUPIHandlerDialogFragment(){
        try {
            FragmentTransaction _ft = getSupportFragmentManager().beginTransaction();
            UPIHandlerDialogFragment newFragment = UPIHandlerDialogFragment.newInstance();
            newFragment.show(_ft, "dialog");
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public void showFullScreenDialog(String title,int stingResourceID){
        try {
            String msg = getResources().getString(stingResourceID);
            FragmentTransaction _ft = getSupportFragmentManager().beginTransaction();
            FullScreenDialog newFragment = FullScreenDialog.newInstance(title,msg);
            newFragment.show(_ft, "dialog");
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    public void FragmentTransaction(final Fragment fragment, final int frame, final boolean isAdd) {
        try {
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                String FRAGMENT_NAME = fragment.getClass().getSimpleName();
                fragmentTransaction.replace(frame, fragment, FRAGMENT_NAME);
                if (isAdd) fragmentTransaction.addToBackStack(FRAGMENT_NAME);
                fragmentTransaction.commit();
            }else {
                ShowToast(StaticMessages.PAGE_TRANSACTION_EXCEPTION);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    public void onFragmentBack(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    public void ShowToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
    }

    public void showCutOffTimings(){
        try{
            View cutoffTimingPopup = getLayoutInflater().inflate(R.layout.cutoff_timings,null);
             AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(cutoffTimingPopup)
                    .setIcon(R.drawable.ventura_icon)
                    .setTitle("Cut-Off Timings")
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
             Button posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
             posBtn.setTextColor(Color.parseColor("#F96129"));
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
}