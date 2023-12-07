package com.ventura.venturawealth.activities;

import android.content.Context;

public class BaseContract {
    public interface IPresenter {
        void onResume();
        void onPause();
        void onViewAttached();
        void onViewDetached();
    }

    public interface IView {
        Context getContext();
        BaseActivity getBaseActivity();
    }

}
