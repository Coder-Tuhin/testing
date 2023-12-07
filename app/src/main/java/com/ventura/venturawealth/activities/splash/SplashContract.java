package com.ventura.venturawealth.activities.splash;

import com.ventura.venturawealth.activities.BaseContract;

public class SplashContract {
    public interface ISplashPresenter extends BaseContract.IPresenter {
        void startTimer();
    }
    public interface ISplashView extends BaseContract.IView {
        void openHomeScreen();
        void openFirstScreen();
        void openPermissionScreen();
        void openTermsConditionScreen();
        void openClientLoginScreen();
        void openAadharLink();
        void onAppUpdate();
    }
}
