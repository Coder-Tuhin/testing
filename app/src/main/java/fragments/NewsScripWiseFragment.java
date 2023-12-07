package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;

import Structure.Response.BC.NewsScripOuterStructure;
import Structure.Response.BC.NewsScripResponseInnerStructure;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;


public class NewsScripWiseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    @BindView(R.id.pendingLinear)
    LinearLayout pendingLinear;

    private int scripCode;
    private String scripName;
    private ArrayList<NewsScripResponseInnerStructure> _mNews = new ArrayList<>();
    ArrayList<NewsScripResponseInnerStructure> _tempList = new ArrayList<>();


    public static final NewsScripWiseFragment newInstance(int scripC, String scripName) {

        NewsScripWiseFragment fragment = new NewsScripWiseFragment();
        Bundle bundle = new Bundle(2);
        bundle.putInt("scripcode", scripC);
        bundle.putString("scripname", scripName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public NewsScripWiseFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
        GlobalClass.dismissdialog();
        View layout = inflater.inflate(R.layout.fragment_news_scrip_wise, container, false);
        ButterKnife.bind(this, layout);

        scripCode = getArguments().getInt("scripcode");
        scripName = getArguments().getString("scripname");
        sendNewsReqToserver();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.newsScripHandler = null;
    }

    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity){
            homeActivity = (HomeActivity)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            GlobalClass.newsScripHandler = new NewsScripWiseHandler();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    private void sendNewsReqToserver() {
        try {
            homeActivity.showProgress();
            String sArr[] = scripName.split("-");
            if (sArr.length>1){
                String symbol = sArr[1];
                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                sendDataToServer.sendMktDepthNewsReq(scripCode,symbol,1);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private class NewsScripWiseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case NEWS_SCRIP:
                                handleScripNews(refreshBundle);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleScripNews(Bundle refreshBundle) {
        try {

            NewsScripOuterStructure nsos = new NewsScripOuterStructure(refreshBundle.getByteArray(eForHandler.RESDATA.name));
            for (int i = 0;i<nsos.newsArray.length;i++){
                NewsScripResponseInnerStructure nsris = nsos.newsArray[i];
                _tempList.add(nsris);
            }
            if (nsos.isDownloadCompleted()){
                _mNews.clear();
                _mNews.addAll(_tempList);
                _tempList.clear();
                handleNews();
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void handleNews() {
        homeActivity.dismisProgress();
        pendingLinear.removeAllViews();
        for (NewsScripResponseInnerStructure nsis: _mNews){
            try{
                View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.notification_item, null);
                TextView body =  view.findViewById(R.id.notification_body);
                TextView type =  view.findViewById(R.id.notification_type);
                TextView time =  view.findViewById(R.id.notification_time);
                body.setText(nsis.news.getValue());
                type.setText("News");
                time.setText(nsis.date.getValue());
                pendingLinear.addView(view);
            }catch (Exception e){
                VenturaException.Print(e);
            }
        }
    }
}