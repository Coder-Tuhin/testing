package fragments.reports;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eLogType;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;

public class ExchMsgView extends LinearLayout {

    private WebView webView;

    public ExchMsgView(Context context,LinearLayout reportframe) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
         LayoutInflater.from(context).inflate(R.layout.exchmsg_screen, this,true);
        initialization(reportframe);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
    }

    private void initialization(LinearLayout reportframe) {
        webView = findViewById(R.id.webView);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,reportframe.getMeasuredHeight());
        webView.setLayoutParams(lp);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // webView.getSettings().setLoadWithOverviewMode(true);
        // webView.getSettings().setUseWideViewPort(true);
        // webView.getSettings().setSupportZoom(true);
        // webView.getSettings().setBuiltInZoomControls(true);
        // webView.addJavascriptInterface(new JavaScriptInterface(GlobalClass.latestContext), "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                GlobalClass.showProgressDialog("Please wait...");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                GlobalClass.dismissdialog();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String url = "https://vw.ventura1.com/exchmsgs_web/index.jsp";
        webView.loadUrl(url);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
