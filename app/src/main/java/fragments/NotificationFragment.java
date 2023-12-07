package fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import Structure.news.StructNotificationReq;
import adapters.NotificationAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eMsgType;
import enums.ePrefTAG;
import fcm_gsm_receiver.VenturaNotificationService;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public class NotificationFragment extends Fragment{

    @BindView(R.id.home_tabs)TabLayout home_tabs;
    @BindView(R.id.disclaimer)ScrollView disclaimer;

    private RecyclerView message_recycler;
    private NotificationAdapter notificationAdapter;
    private LinearLayoutManager linearLayoutManager ;
    private int selectTab = 0;
    private static final String POS_TAG = "pos_tag";

    public static final NotificationFragment newInstance(int selectTab)
    {
        NotificationFragment fragment = new NotificationFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(POS_TAG, selectTab);
        fragment.setArguments(bundle);
        return fragment ;
    }
    public NotificationFragment(){
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalClass.currentNotificatonTab = selectTab = getArguments().getInt(POS_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
        GlobalClass.dismissdialog();
        View layout = inflater.inflate(R.layout.notification_screen, container, false);
        ButterKnife.bind(this, layout);
        cancelAllNotification();
        message_recycler = (RecyclerView) layout.findViewById(R.id.total_message_recycler);
        linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);
        message_recycler.setLayoutManager(linearLayoutManager);
        initialization();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.notificationHandler = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GlobalClass.currentNotificatonTab = 0;
    }

    private void cancelAllNotification() {
        NotificationManager notificationManager =
                (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void initialization() {

        home_tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        home_tabs.addTab(home_tabs.newTab().setText("All"));
        home_tabs.addTab(home_tabs.newTab().setText("F&O Trades"));
        home_tabs.addTab(home_tabs.newTab().setText("Cash Trades"));
        home_tabs.addTab(home_tabs.newTab().setText("Event / Scrip Alerts"));
        home_tabs.addTab(home_tabs.newTab().setText("Research"));
        home_tabs.addTab(home_tabs.newTab().setText("News"));
        home_tabs.addTab(home_tabs.newTab().setText("Trading calls"));
        home_tabs.addTab(home_tabs.newTab().setText("IPO"));
        home_tabs.addTab(home_tabs.newTab().setText("BOND"));
        home_tabs.addTab(home_tabs.newTab().setText("SGB"));
        home_tabs.addTab(home_tabs.newTab().setText("NFO"));
        home_tabs.addTab(home_tabs.newTab().setText("FD"));
        home_tabs.addTab(home_tabs.newTab().setText("NPS"));
        home_tabs.addTab(home_tabs.newTab().setText("MF"));
        home_tabs.addTab(home_tabs.newTab().setText("Others"));
        home_tabs.addTab(home_tabs.newTab().setText("Disclaimer"));

        notificationAdapter = new NotificationAdapter();
        message_recycler.setAdapter(notificationAdapter);
        message_recycler.setOnClickListener(null);
        /*int deviderHeight = (int) getResources().getDimension(R.dimen.divider_height);
        message_recycler.addItemDecoration(new DividerItemDecoration(ObjectHolder.SILVER,deviderHeight));*/
        home_tabs.getTabAt(selectTab).select();
        home_tabs.post(new Runnable() {
            @Override
            public void run() {
                home_tabs.setSmoothScrollingEnabled(true);
                home_tabs.setScrollPosition(selectTab,0f,true);
            }
        });

        home_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshNotification(home_tabs.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            refreshNotification(GlobalClass.currentNotificatonTab);
            GlobalClass.notificationHandler = new NotificationHandler();
            VenturaNotificationService.notificationList.clear();
            new SendNotificationReq(false).execute();
            NotificationManager notifManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert notifManager != null;
            notifManager.cancelAll();
            VenturaNotificationService.NotificationId =0;
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void refreshNotification(int position) {
        GlobalClass.currentNotificatonTab = position;
        if (position == home_tabs.getTabCount()-1){
            message_recycler.setVisibility(View.GONE);
            disclaimer.setVisibility(View.VISIBLE);
        }else {
            message_recycler.setVisibility(View.VISIBLE);
            disclaimer.setVisibility(View.GONE);
            notificationAdapter.refreshAdapter();
            cancelAllNotification();
        }
    }

    private class NotificationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case NEWS_FETCH:
                                handleNews();
                                break;
                            case NOTIFICATION_FETCH:
                                new SendNotificationReq(true).execute();
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

    private void handleNews() {
        refreshNotification(GlobalClass.currentNotificatonTab);
    }

    private class SendNotificationReq extends AsyncTask<Void,Void,Void>{
        private boolean isOnlyTradecall;

        SendNotificationReq(boolean isOnlyTradecall){
            this.isOnlyTradecall = isOnlyTradecall;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if (!isOnlyTradecall){
                StructNotificationReq structNotificationReq = new StructNotificationReq();
                structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                structNotificationReq.msgType.setValue(eMsgType.NEWS.value);
                structNotificationReq.fromMsgTime.setValue(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.LAST_NEWSTIME.name,0));
                new SendDataToBCServer().sendNotificationReq(structNotificationReq,eMessageCode.NEWS_FETCH);
            }
            StructNotificationReq structNotificationReq = new StructNotificationReq();
            structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            structNotificationReq.msgType.setValue(eMsgType.TRADING_CALL.value);
            structNotificationReq.fromMsgTime.setValue(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.LAST_NOTIFICATION_TIME.name,0));
            new SendDataToBCServer().sendNotificationReq(structNotificationReq,eMessageCode.NOTIFICATION_FETCH);
            return null;
        }
    }

}
