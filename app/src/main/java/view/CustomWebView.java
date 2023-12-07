package view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.ventura.venturawealth.activities.BaseActivity;

import utils.GlobalClass;
import utils.VenturaException;

public class CustomWebView extends WebView {

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Failed to load WebView provider: No WebView installed")) {
                BaseActivity _baseAct = (BaseActivity) GlobalClass.homeActivity;
                _baseAct.showToast("Failed to load WebView provider: No WebView installed");
                VenturaException.Print(e);
            } else {
                throw e;
            }
        }
    }
}