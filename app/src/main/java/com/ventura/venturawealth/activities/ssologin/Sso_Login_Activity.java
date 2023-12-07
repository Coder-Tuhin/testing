package com.ventura.venturawealth.activities.ssologin;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ventura.venturawealth.R;

import fragments.sso.AskGoogleAuthFragment;
import fragments.sso.CreateGoogleAuthFragment;
import fragments.sso.NonSSO_Login;
import fragments.sso.OtpValidationFragmnet;
import fragments.sso.RegisterGoogleAuthFragment;
import fragments.sso.SsoLogin;
import fragments.sso.Sso_setPinFragmnet;
import fragments.sso.Sso_validatePin;
import fragments.sso.ValidateGoogleAuthFragment;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.VenturaException;
import view.AlertBox;

public class Sso_Login_Activity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragInstance = fm.findFragmentById(R.id.layout);
            LinearLayout ll = (LinearLayout) findViewById(R.id.layout);

        /* if (fragInstance instanceof SsoLogin){
            showExitAlert();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        } else {
            super.onBackPressed();
        }*/

            if (fragInstance instanceof SsoLogin) {
                showExitAlert();
            } else if (fragInstance instanceof CreateGoogleAuthFragment) {
                ll.removeAllViews();
                getSupportFragmentManager().popBackStackImmediate();
            } else if (fragInstance instanceof RegisterGoogleAuthFragment) {
                //ll.removeAllViews();
                //getSupportFragmentManager().popBackStackImmediate();
                final Fragment fragment = Sso_validatePin.newInstance();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                String FRAGMENT_NAME = fragment.getClass().getSimpleName();
                fragmentTransaction.replace(R.id.layout, fragment, FRAGMENT_NAME);
                fragmentTransaction.commit();
            } else if (fragInstance instanceof AskGoogleAuthFragment) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                } else {
                    ll.removeAllViews();
                    getSupportFragmentManager().popBackStackImmediate();
                }
            /*final Fragment fragment = Sso_validatePin.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            String FRAGMENT_NAME = fragment.getClass().getSimpleName();
            fragmentTransaction.replace(R.id.layout, fragment, FRAGMENT_NAME);
            fragmentTransaction.commit();*/
            } else if (fragInstance instanceof OtpValidationFragmnet) {
                finish();
            } else if (fragInstance instanceof Sso_validatePin) {
                finish();
            } else if (fragInstance instanceof Sso_setPinFragmnet) {
                finish();
            } else if (fragInstance instanceof ValidateGoogleAuthFragment) {
                ll.removeAllViews();
                getSupportFragmentManager().popBackStackImmediate();

            } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso_login);
        GlobalClass.latestContext = this;
        GlobalClass.fragmentManager = getSupportFragmentManager();

        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.layout);
        if(f != null){
            Fragment Frament = f;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.layout, Frament);
            fragmentTransaction.commit();
        }else {

                try {
                    final Fragment fragment;
                    Bundle b = getIntent().getExtras();
                    if (b != null) {
                        boolean issessionvalid = b.getBoolean("issessionvalid", false);
                        if (issessionvalid) {
                            String googleauthTag = PreferenceHandler.getSSOCreateAuth();
                            String askViewDate = PreferenceHandler.getGoogleAuthAskViewDate();
                            String currDate = DateUtil.getCurrentDate();
                            if(googleauthTag.equalsIgnoreCase("0") && !askViewDate.equalsIgnoreCase(currDate)) {
                                PreferenceHandler.setGoogleAuthAskViewDate(currDate);
                                fragment = AskGoogleAuthFragment.newInstance();
                            }else {
                                fragment = Sso_validatePin.newInstance();
                            }
                        } else {
                            /*if(!GlobalClass.isSSOTag){
                                fragment = NonSSO_Login.newInstance();
                            }else{*/
                                fragment = SsoLogin.newInstance();
                            //}
                        }
                    }else{
                        /*if(!GlobalClass.isSSOTag){
                            fragment = NonSSO_Login.newInstance();
                        }else{*/
                            fragment = SsoLogin.newInstance();
                        //}
                    }
                    GlobalClass.fragmentTransaction(fragment, R.id.layout, true, fragment.getClass().getSimpleName());
                } catch (Exception e) {
                    GlobalClass.showAlertDialog("Error2 :" + e.toString(), false);
                    VenturaException.Print(e);
                }
        }
    }
    private void showExitAlert(){
        try{
            new AlertBox(GlobalClass.latestContext, "Ventura Wealth", "EXIT",
                    "Are you sure you want to exit?", true);
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
    }
}