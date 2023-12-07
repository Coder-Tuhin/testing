package chart;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.ventura.venturawealth.R;

import Structure.Request.BC.BcClientRegistration;
import Structure.Request.BC.MarketWatchRequest;
import enums.eMessageCode;
import okio.ByteString;
import utils.Connectivity;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;
import utils.VenturaException;

public class TradingViewChart extends AppCompatActivity {

    String chartingLibraryUrl;
    private WebView webView;
    ImageView rotateView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_view_chart);

        rotateView = findViewById(R.id.rotateTvw);

        rotateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = TradingViewChart.this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        Intent intent = getIntent();
        String scripName = intent.getStringExtra("SCRIPNAME");
        int scripCode = intent.getIntExtra("SCRIPCODE", 0);
        String EXCHANGE = intent.getStringExtra("EXCHANGE");
        String theme = "dark";

        chartingLibraryUrl = "http://43.242.213.73:51529/?scripcode=" + scripCode +
                "&exchange=" + EXCHANGE +
                "&name=" + scripName +
                "&theme=" + theme;

        GlobalClass.log(TAG+ "chartingLibraryUrl: " + chartingLibraryUrl);

        JSApplicationBridge jsBridge = new JSApplicationBridge(this);
        webView.addJavascriptInterface(jsBridge, "ApplicationBridge");

        webView.loadUrl(chartingLibraryUrl);

        WebView.setWebContentsDebuggingEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
                rotateView.setVisibility(View.VISIBLE);
                findViewById(R.id.webProgress).setVisibility(View.GONE);

                /*
                webView.evaluateJavascript(""
                        + "tvWidget.onChartReady(function() {"
                        + "tvWidget.chart().onIntervalChanged().subscribe("
                        + "null,"
                        + "function(interval) {"
                        + "ApplicationBridge.onIntervalChanged(interval);"
                        + "}"
                        + ");"
                        + "});", null);*/
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                GlobalClass.log(TAG+ "onReceivedError: errorCode" + errorCode + ", description: " + description + ", failingUrl: " + failingUrl);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (webView != null) {
                webView.destroy();
            }
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }
}