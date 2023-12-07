package fragments.homeGroups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.OnBalloonOutsideTouchListener;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Structure.Other.HoldingDataStatusModel;
import Structure.Request.BC.ErrorLOG;
import Structure.Request.BC.ShortMktWatchRequest;
import Structure.Response.BC.StructxMKTEventRes;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.news.StructNewsDisclaimer;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.Config;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eScreen;
import enums.eShowDepth;
import enums.eWatchs;
import fragments.SettingsFragment;
import handler.GroupDetail;
import interfaces.OnActionWatchClick;
import interfaces.OnAlertListener;
import interfaces.OnFontChange;
import interfaces.OnPopupListener;
import structure.StructDate;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;
import view.ActionWatch;
import view.WatchNews;
import view.CustomPopupWindow;
import view.DualListView;
import view.Gridview;
import wealth.VenturaServerConnect;
import wealth.wealthStructure.StructBondEquityDepositoryDetailNew;
import wealth.wealthStructure.StructFNODepositoryDetail;
import wealth.wealthStructure.StructHoldingDateCheckingRes;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
@SuppressLint("ValidFragment")
public class WatchFragment extends Fragment implements View.OnClickListener, OnPopupListener,
        AdapterView.OnItemSelectedListener, OnFontChange, OnAlertListener, OnActionWatchClick {

    private DualListView m_duelListView;
    private Gridview m_gridview;
    private ArrayAdapter<String> m_grpAdapter;
    private GroupDetail m_groupDetail;
    private View layout;

    private List<String> rr;
    @BindView(R.id.watch_spinner)
    Spinner watch_spinner;
    @BindView(R.id.searchscript_button)
    ImageButton searchscript_button;
    @BindView(R.id.body)
    LinearLayout body;
    @BindView(R.id.listgrid_button)
    ImageButton listgrid_button;
    @BindView(R.id.event_setting)
    ImageButton event_setting;
    @BindView(R.id.event_linear)
    LinearLayout event_linear;
    @BindView(R.id.latestNews)
    TextView latestNews;
    @BindView(R.id.sharebtn)
    ImageView sharebtn;
    @BindView(R.id.latestnewslinear)
    LinearLayout latestnewslinear;

    public WatchFragment(){super();}

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.WATCH);
        if (layout != null) return layout;
        layout = inflater.inflate(R.layout.marketwatch_screen, container, false);
        ButterKnife.bind(this, layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        initialization();
        return layout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private WatchNews watchNews = null;
    private ActionWatch actionWatch = null;

    @Override
    public void onResume() {
        super.onResume();
        VenturaServerConnect.closeSocket();
        rr = VenturaApplication.getPreference().getRoundRobin();
        GlobalClass.mktWatchUiHandler = new MktWatchHandler();
        m_groupDetail = GlobalClass.groupHandler.getUserDefineGroup();
        if (m_groupDetail != null)refreshSpinner(false);
        if (m_duelListView != null) {
            m_duelListView.notifiList();
            body.removeAllViews();
            body.addView(m_duelListView);
        }
        if(GlobalClass.isReloadMktWatch){
            GlobalClass.isReloadMktWatch = false;
            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            startTimer();
        }
        if (watchNews == null) {
            watchNews = new WatchNews(latestNews, sharebtn, latestnewslinear, this.getContext(), this, ePrefTAG.WATCHNEWS.name);
        }
        watchNews.startNewsTime();
        if(actionWatch == null){
            actionWatch = new ActionWatch(event_linear,this.getContext(),this);
        }
        actionWatch.reloadConfig();
        boolean isshow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.MKTWATCH_TOOLTIP_HELP.name,  false);
        if (!isshow) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToolTip();
                }
            }, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.mktWatchUiHandler = null;
        if(watchNews != null) {
            watchNews.cancleTimer();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listgrid_button:
                GlobalClass.gridselection = 0;
                boolean tempIsList = !PreferenceHandler.isListView();
                PreferenceHandler.setListView(tempIsList);
                new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case R.id.searchscript_button: {

                boolean isOldSearchPopup = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.OLD_SEARCH_POPUP.name,  false);
                if(ObjectHolder.connconfig.getSearchEngineIP().equalsIgnoreCase("") || isOldSearchPopup) {
                    new CustomPopupWindow(this).openSearchScripWindow();
                }else {
                    SearchFragment m_fragment = new SearchFragment();
                    GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "searchf");
                }
                //showToolTip();
            }
                break;
            case R.id.event_setting:
                openEventSetting();
                break;
        }
    }
    private void showToolTip(){
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.MKTWATCH_TOOLTIP_HELP.name, true);
       try{
           displayTooltip();
       }catch (Exception ex) {
           GlobalClass.onError("",ex);
       }
    }
    Balloon balloon = null;
    private void displayTooltip() throws Exception{

        if(balloon == null) {
            String msg = "You can search for any scrip by using this icon.";

            balloon = new Balloon.Builder(this.getContext())
                    .setArrowSize(110)
                    .setArrowLeftPadding(50)
                    .setArrowRightPadding(45)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
                    .setArrowPosition(0.94f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(340)
                    .setWidthRatio(0.95f)
                    .setTextSize(25f)
                    .setTextTypeface(Typeface.BOLD)
                    .setCornerRadius(8f)
                    .setAlpha(0.95f)
                    .setText(msg)
                    .setPaddingHorizontal(10)
                    .setTextColor(ContextCompat.getColor(this.getContext(), R.color.ventura_color))
                    .setTextIsHtml(true)
                    .setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.white))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                    .setAutoDismissDuration(15000L)
                    .setLifecycleOwner(this)
                    .build();

            balloon.setOnBalloonOutsideTouchListener(new OnBalloonOutsideTouchListener() {
                @Override
                public void onBalloonOutsideTouch(@NotNull View view, @NotNull MotionEvent motionEvent) {
                    balloon.dismiss();
                }
            });
            balloon.showAlignBottom(searchscript_button);
        }
    }
    private void openEventSetting() {
        GlobalClass.fragmentTransaction(new SettingsFragment(this),R.id.container_body,true, eFragments.SETTINGS.name);
    }

    private void initialization() {
        m_grpAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item);
        m_grpAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        watch_spinner.setAdapter(m_grpAdapter);
        listgrid_button.setOnClickListener(this);
        event_setting.setOnClickListener(this);
        searchscript_button.setOnClickListener(this);
    }

    private void refreshSpinner(boolean isfromHandler) {
        try {
            ArrayList<String> itemList = m_groupDetail.getGroupNameList();
            if (itemList.size() > 0) {
                itemList.add(Constants.ADD_NEW_WATCHLIST);
                m_grpAdapter.clear();
                m_grpAdapter.addAll(itemList);
                m_grpAdapter.notifyDataSetChanged();
                if (watch_spinner.getOnItemSelectedListener() == null)
                    watch_spinner.setOnItemSelectedListener(WatchFragment.this);
                int positon = m_groupDetail.getIndexOfGroupName(VenturaApplication.getPreference().getSelectedGroup());
                int grandPositon = positon < 0? 0 : positon;
                watch_spinner.setSelection(grandPositon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String prevSelectedItem = "";
    private boolean newWatchClick = false;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
        try {
            String selectedItem = adapterView.getSelectedItem().toString();
            if (selectedItem.equalsIgnoreCase(Constants.ADD_NEW_WATCHLIST)) {
                newWatchClick = true;
                if (!UserSession.getLoginDetailsModel().isClient()){
                    watch_spinner.setSelection(0);
                }else {
                    int positon = m_grpAdapter.getPosition(prevSelectedItem)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ;
                    if (positon < 0) positon = 0;
                    watch_spinner.setSelection(positon);
                }
                int grpSize = m_groupDetail.getEditableGroupStructureList().size();
                if(grpSize < Constants.GROUP_LENGTH) {
                    new CustomPopupWindow(this).addNewGroupWindow();
                }
                else{
                    GlobalClass.showToast(GlobalClass.latestContext, Constants.ADD_MAX_GROUP_MSG);
                }

            } else{
                GroupsRespDetails m_grpResDetail = m_groupDetail.getGrpDetailFromGrpName(selectedItem, eWatchs.MKTWATCH);
                //if ((GlobalClass.srr.shortMktWatch.getValue() == 1) || m_grpResDetail.isInitialDataTobeSend){
                if(m_grpResDetail != null){
                    GlobalClass.currGrpCode = m_grpResDetail.groupCode.getValue();
                    ShortMktWatchRequest smr = new ShortMktWatchRequest();
                    smr.prevGrp.setValue(GlobalClass.groupHandler.prevGrpCode);
                    smr.currGrp.setValue(GlobalClass.currGrpCode);
                    smr.columnTag.setValue(DualListView.getColumnTag());
                    smr.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    smr.isInitialDataTobeSend.setValue(m_grpResDetail.isInitialDataTobeSend);
                    smr.currGrpSize.setValue(m_grpResDetail.getGroupSize());
                    SendDataToBCServer sdbs = new SendDataToBCServer();
                    sdbs.sendsShortMarketWatchReq(smr);
                    m_grpResDetail.isInitialDataTobeSend = false;
                    GlobalClass.groupHandler.prevGrpCode = GlobalClass.currGrpCode;
                }

                    if (m_grpResDetail.groupCode.getValue()>100)
                        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.ADDGROUP_TO.name,selectedItem);
                    if (!newWatchClick) {
                        VenturaApplication.getPreference().setSelectedGroup(selectedItem);
                        new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    newWatchClick = false;

                prevSelectedItem = selectedItem;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onFontChange(int textStyle) {
        getActivity().setTheme(textStyle);
    }

    @Override
    public void onOk(String tag) {

        if(tag.equalsIgnoreCase("watchnews")){
            PreferenceHandler.setNotificationActive(true);
            StructNewsDisclaimer snd = new StructNewsDisclaimer();
            snd.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            snd.model.setValue(android.os.Build.MODEL);
            snd.IMEI.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            new SendDataToBCServer().sendNotificationAgreeReq(snd);

            if(watchNews != null){
                watchNews.startNewsTime();
            }
        }
    }

    @Override
    public void onCancel(String tag) {

    }

    @Override
    public void onActionWatchClick(GroupsTokenDetails groupsTokenDetails) {
        try {
            ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
            grplist.add(groupsTokenDetails);
            GlobalClass.openDepth(groupsTokenDetails.scripCode.getValue(), eShowDepth.MKTWATCH, grplist, null);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
    private void setView(GroupsRespDetails m_grpResDetail){
        if (!PreferenceHandler.isListView()) {
            if (m_gridview == null) {
                m_gridview = new Gridview(GlobalClass.latestContext, m_grpResDetail, null,
                        eShowDepth.MKTWATCH, HomeActivity.RadioButtons.WATCH);
                body.addView(m_gridview);
                listgrid_button.setImageResource(R.mipmap.icon_listview);
            }
        } else {
            if (m_duelListView == null) {
                m_duelListView = new DualListView(GlobalClass.latestContext, m_grpResDetail, eWatchs.MKTWATCH,
                        eShowDepth.MKTWATCH, HomeActivity.RadioButtons.WATCH);
                body.addView(m_duelListView);
                listgrid_button.setImageResource(R.mipmap.icon_gridview);
            }
        }
    }

    class DisplayView extends AsyncTask<String, Void, String> {
        DisplayView(){
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Please wait...");
        }
        @Override
        protected String doInBackground(String... strings) {
            ((Activity) GlobalClass.latestContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String grpName = watch_spinner.getSelectedItem().toString();
                        GlobalClass.groupHandler.setSelectedGrpName(grpName);
                        m_duelListView = null;
                        m_gridview = null;
                        final GroupsRespDetails m_grpResDetail = m_groupDetail.getGrpDetailFromGrpName(grpName, eWatchs.MKTWATCH);
                        if (!UserSession.getLoginDetailsModel().isClient()
                                && ((m_grpResDetail.groupCode.getValue()<50
                                && m_grpResDetail.groupCode.getValue()>0)
                                ||  (m_grpResDetail.groupCode.getValue()<100
                                && m_grpResDetail.groupCode.getValue()>90))) return;
                        body.removeAllViews();
                        if (m_grpResDetail.hm_grpTokenDetails.size() > 0) {
                            setView(m_grpResDetail);
                        }else if(m_grpResDetail.isGroupDetailRespCame){
                            if (grpName.equalsIgnoreCase("My Market Watch")){
                                body.removeAllViews();
                                LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(GlobalClass.latestContext);
                                //GlobalClass.mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View childNodata = inflater.inflate(R.layout.nodata_available, null);
                                body.addView(childNodata);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        body.getMeasuredHeight());
                                childNodata.setLayoutParams(lp);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            checkAndSendRequestForDPHoldingData();
            if(GlobalClass.deeplinkScreen > 0){
                eScreen screenType = eScreen.fromValue(GlobalClass.deeplinkScreen);
                GlobalClass.deeplinkScreen = -1;
                try {
                    ErrorLOG log = new ErrorLOG();
                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    log.errorMsg.setValue(screenType.name);
                    log.logType.setValue(eLogType.DEEPLINK.name);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendAndroidLog(log);
                }catch (Exception ex){ex.printStackTrace();}
                if(screenType != eScreen.NONE) {
                    GlobalClass.showScreen(screenType);
                }
            }
        }
    }

    private void refreshMktWatch(eMessageCode emessagecode, int token) {

        if (PreferenceHandler.isListView() && m_duelListView != null) {
            m_duelListView.callRefreshData(emessagecode, token);
        } else if (!PreferenceHandler.isListView() && m_gridview != null) {
            m_gridview.callRefreshData(emessagecode, token);
        }
    }
    @Override
    public void onPopupClose() {
    }

    private class MktWatchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case LITE_MW:
                        case STATIC_MW:
                        case OpenInt:
                            refreshMktWatch(emessagecode, scripCode);
                            break;
                        case NEW_GROUPLIST:
                            refreshSpinner(true);
                            break;
                        case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP:
                            refreshSpinner(false);
                            break;
                        case NEW_GROUPDETAILS:
                            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case ADDSCRIPT_TOGROUP:
                            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            startTimer();
                            break;
                        case TOP_TRADED:
                        case TOP_GAINER:
                        case TOP_LOOSER:
                            break;
                        case DARTEVENT:
                            if(actionWatch != null) {
                                actionWatch.setEvent(new StructxMKTEventRes(refreshBundle.getByteArray(eForHandler.RESDATA.name)));
                            }
                            break;
                        case NOTIFICATION:
                            String title = refreshBundle.getString(eForHandler.RESPONSE.name);
                            if (rr!= null && rr.contains(title) && (watchNews != null)){
                                watchNews.newsTimer(true);
                            }
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                GlobalClass.dismissdialog();
            }
        }
    }

    Timer t = null;
    private void startTimer(/*GroupsTokenDetails tokenDetails*/){
        try {
            if(t != null){
                t.cancel();
                t = null;
            }
            if (t == null) {
                t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        GlobalClass.groupHandler.setChangesForScripAddColorChange();
                        //tokenDetails.isNewlyAdded = false;
                        //saveUserDefineData();
                        t=null;
                    }
                };
                t.schedule(tt, 30000);
            }
        }catch (Exception ex){}
    }
    private void checkAndSendRequestForDPHoldingData(){
        if(UserSession.getClientResponse() != null) {
            if (GlobalClass.oneTimeTryForHoldingDetail) {
                GlobalClass.oneTimeTryForHoldingDetail = false;
                //HashMap<String, StructFNODepositoryRow> fnoHoldingList = VenturaApplication.getPreference().getFNOHoldingList();
                //if (dpHoldingReq || fnoHoldingList == null) {
                    GlobalClass.log("Holding req from Watch");
                    GetTaskFirstForHolding getTaskFirst = new GetTaskFirstForHolding();
                    getTaskFirst.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //}
            }
        }
    }

    class GetTaskFirstForHolding extends AsyncTask<Object, Void, String> {
        String str = "";
        public GetTaskFirstForHolding(){
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Object... params) {
            try {
                if (VenturaServerConnect.connectToWealthServer(false)) {
                    HoldingDataStatusModel holdingStatus = PreferenceHandler.getHoldingStatus();

                    if(!holdingStatus.isDPHoldingStore || !holdingStatus.isFNOHoldingStore){

                        StructBondEquityDepositoryDetailNew equityDepositoryDetail = VenturaServerConnect.getEquityDepositoryDetail(UserSession.getLoginDetailsModel().getUserID(),(byte) eScreen.DPHOLDING.value);
                        //StructBondEquityDepositoryDetailNew equityDepositoryDetail = VenturaServerConnect.getEquityDepositoryDetail("40G4186",(byte) eScreen.DPHOLDING.value);

                        StructFNODepositoryDetail fnoDepositoryDetail = VenturaServerConnect.getFNODepositoryDetail(238);
                        holdingStatus.isDPHoldingStore = equityDepositoryDetail.isHoldingStore;
                        holdingStatus.isFNOHoldingStore = fnoDepositoryDetail.isHoldingStore;
                        holdingStatus.lastDPUpdateDate = new StructDate("",new Date()).getDateInNumber();
                        holdingStatus.lastFNOUpdateDate = holdingStatus.lastDPUpdateDate;
                    } else {
                        StructHoldingDateCheckingRes holdingDataUpdateCheck = VenturaServerConnect.getHoldingDataUpdateCheck(holdingStatus.lastDPUpdateDate,holdingStatus.lastFNOUpdateDate);
                        GlobalClass.log(holdingDataUpdateCheck.toString());
                        if (holdingDataUpdateCheck.isNeedToUpdateDP.getValue()) {
                            holdingStatus.isDPHoldingStore = false;
                            StructBondEquityDepositoryDetailNew equityDepositoryDetail = VenturaServerConnect.getEquityDepositoryDetail(UserSession.getLoginDetailsModel().getUserID(),(byte)eScreen.DPHOLDING.value);
                            holdingStatus.isDPHoldingStore = equityDepositoryDetail.isHoldingStore;
                            holdingStatus.lastDPUpdateDate = holdingDataUpdateCheck.updateDateDP.getDateInNumber();
                        }
                        if (holdingDataUpdateCheck.isNeedToUpdateFNO.getValue()) {
                            holdingStatus.isFNOHoldingStore = false;
                            StructFNODepositoryDetail fnoDepositoryDetail = VenturaServerConnect.getFNODepositoryDetail(238);
                            holdingStatus.isFNOHoldingStore = fnoDepositoryDetail.isHoldingStore;
                            holdingStatus.lastFNOUpdateDate = holdingDataUpdateCheck.updateDateFNO.getDateInNumber();
                        }
                    }
                    GlobalClass.log("Called for DP Holding from MarketWatch3 :" + holdingStatus.lastDPUpdateDate + ":"+holdingStatus.lastFNOUpdateDate
                            + ":"+holdingStatus.isDPHoldingStore+":"+holdingStatus.isFNOHoldingStore);
                    PreferenceHandler.setHoldingStatus(holdingStatus);
                }
            } catch (Exception ie) {
                str = ie.toString();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            VenturaServerConnect.closeSocket();
        }
    }
}