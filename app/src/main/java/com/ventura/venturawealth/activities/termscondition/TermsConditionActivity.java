package com.ventura.venturawealth.activities.termscondition;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ventura.venturawealth.activities.permission.PermissionActivity;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.BaseContract;

import utils.PreferenceHandler;

public class TermsConditionActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.terms_condition_screen;
    }

    @Override
    protected BaseContract.IPresenter getPresenter() {
        return null;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public Context getContext() {
        return TermsConditionActivity.this;
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    public void agreeClicked(View view){
        PreferenceHandler.setTermsCondition(true);
        startActivity(new Intent(TermsConditionActivity.this, PermissionActivity.class));
        finish(false);

    }

    public void disagreeClicked(View view){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
