package wealth.new_mutualfund.menus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import enums.eMFJsonTag;
import fragments.sso.SsoLogin;
import utils.GlobalClass;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.newMF.CreateMandateFragmentNew;


public class WebViewForMF extends Fragment {

    private WebView myWebView;
    private View layout;
    private ProgressDialog progressBar;
    androidx.appcompat.app.AlertDialog alertDialog1;
    private HomeActivity homeActivity;


    public WebViewForMF() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WebViewForMF newInstance(String strHtml) {
        WebViewForMF fragment = new WebViewForMF();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, strHtml);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.webviewformf, container, false);
        init(layout);
        return layout;

    }
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(View layout) {
        try {
            myWebView = (WebView) layout.findViewById(R.id.webview);
            myWebView.getSettings().setJavaScriptEnabled(true);
            //myWebView.addJavascriptInterface(new JSObject(), "injectedObject");
            myWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                    GlobalClass.log("Simply URL","   "+url);
                    if (url.equalsIgnoreCase("https://mf.ventura1.com/Client/AppPaymentResult")){
                        //ObjectHolder.isPassbook = true;
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.mandate_eligible, viewGroup, false);
                        Button OK_btn = dialogView.findViewById(R.id.OK_btn);
                        TextView mandatetext2 = dialogView.findViewById(R.id.mandatetext2);
                        TextView mandatetext = dialogView.findViewById(R.id.mandatetext);

                        mandatetext.setText("Your payment for first installment has been successful ");
                        mandatetext2.setVisibility(View.GONE);
                        OK_btn.setText("OK");


                        OK_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog1.dismiss();
                                if(GlobalClass.SigleMandate == 0) {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_body, new CreateMandateFragmentNew());
                                    fragmentTransaction.commit();
                                }else{
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_body, new MutualFundMenuNew());
                                    fragmentTransaction.commit();
                                }

                            }
                        });
                        builder.setView(dialogView);
                        alertDialog1 = builder.create();
                        alertDialog1.show();



                    } else{
                        GlobalClass.showProgressDialog("Please Wait...");
                    }

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    GlobalClass.dismissdialog();
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    //progressBar.setVisibility(View.GONE);
                    GlobalClass.dismissdialog();
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    GlobalClass.dismissdialog();
                    handler.proceed();
                    error.getCertificate();
                }
            });

            Bundle args = getArguments();
            String strHtml = args.getString(eMFJsonTag.JDATA.name, "");

            GlobalClass.log("Payment : " +strHtml);
            myWebView.loadDataWithBaseURL("",strHtml, "text/html", "UTF-8","");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
