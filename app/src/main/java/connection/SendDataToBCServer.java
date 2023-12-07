package connection;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import java.util.ArrayList;

import Structure.Request.BC.AddGroup;
import Structure.Request.BC.AddScripGroup;
import Structure.Request.BC.BCStopReq;
import Structure.Request.BC.BcClientRegistration;
import Structure.Request.BC.DeleteAlert;
import Structure.Request.BC.ErrorLOG;
import Structure.Request.BC.GetNotificationSetting;
import Structure.Request.BC.GroupDetailReq;
import Structure.Request.BC.GroupListReq;
import Structure.Request.BC.InsertMobileInfo;
import Structure.Request.BC.LogoutReq;
import Structure.Request.BC.MarketWatchRequest;
import Structure.Request.BC.MktMoverReq;
import Structure.Request.BC.MultipleMKtWatch;
import Structure.Request.BC.MultipleScripDetail;
import Structure.Request.BC.NewsScripReq;
import Structure.Request.BC.NseBseScripCode;
import Structure.Request.BC.ScripDetailReq;
import Structure.Request.BC.SearchScript;
import Structure.Request.BC.SetAlertReq;
import Structure.Request.BC.SetNotificationSetting;
import Structure.Request.BC.ShortMktWatchRequest;
import Structure.Request.BC.StructBondIPOApply;
import Structure.Request.BC.StructChartReq;
import Structure.Request.BC.StructCustomDialogRequest;
import Structure.Request.BC.StructOptionChainReqNew;
import Structure.news.StructNewsDisclaimer;
import Structure.news.StructNotificationReq;
import enums.eMessageCode;
import enums.eMobileOS;
import fragments.OptionChainNew.utility.StructScripNamesReq;
import models.CheckNotificationSetting;
import utils.Connectivity;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.DualListView;


/**
 * Created by Admin on 03/02/2016.
 */
public class SendDataToBCServer extends AsyncTask<Object, Void, String> {
    private Context context;
    private ReqSent m_req;
    private byte m_data[];
    private ArrayList<Integer> scripCodes;
    eMessageCode m_msgCode;
    private final String m_className = getClass().getName();

    public SendDataToBCServer() {
        context = GlobalClass.latestContext;
    }

    public SendDataToBCServer(ReqSent req) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
    }

    public SendDataToBCServer(ReqSent req, eMessageCode emsgcode) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
        this.m_msgCode = emsgcode;
    }
    public SendDataToBCServer(ReqSent req, eMessageCode emsgcode, ArrayList<Integer> scrips) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
        this.m_msgCode = emsgcode;
        this.scripCodes = scrips;
    }
    public SendDataToBCServer(ReqSent req, eMessageCode msgCode, byte data[]) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
        this.m_msgCode = msgCode;
        this.m_data = data;
    }


    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            if (m_msgCode == eMessageCode.EXIT) {
               GlobalClass.showProgressDialog("Please wait...");
               setExitTimer();
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private CountDownTimer timer;

    private void setExitTimer() {
        timer = new CountDownTimer(1500,500) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                timer = null;
                exitApp();
            }
        };
        timer.start();
    }

    private void cancelExitTimer(){
        if (timer!=null){
            timer.onFinish();
        }
    }

    private void exitApp(){
        try {
            Activity activity = (Activity) GlobalClass.homeActivity;
            activity.finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.exit(0);
        }
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            switch (m_msgCode) {
                case CLIENT_BC_REGISTRATION:
                    sendRegistrationRequest();
                    break;
                case NEW_MULTIPLE_MARKETWATCH:
                    sendMutipleMarketWatchReq(scripCodes);
                    break;
                case EXIT:
                    sendExitReqToBCRC();
                    break;
                case LOGOFF_REQ:
                    sendLogoutReqBCRC();
                    break;
                default:
                    sendData(m_data);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            dismissDialog();
           // GlobalClass.reqResHandler.setMsgCodetoQueue(m_msgCode);
            if (m_msgCode == eMessageCode.EXIT) {
                cancelExitTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissDialog() {
        // dismiss snackbar
    }

    private void sendData(byte data[]) {
        try {
            //GlobalClass.log("BC Request Sent : " +m_msgCode.value);
            if (Connectivity.IsConnectedtoInternet(context)) {
                if (GlobalClass.tradeBCClient != null && GlobalClass.tradeBCClient.IsBroadcastConnected) {
                    GlobalClass.tradeBCClient.send(data);
                    if (m_req != null)
                        m_req.reqSent(m_msgCode.value);
                }
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    public void ScripNamesRequest(StructScripNamesReq req){
        try{
            m_msgCode = eMessageCode.SCRIP_NAMES;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }
    public void optionChainNewRequest(StructOptionChainReqNew req){
        try{
            m_msgCode = eMessageCode.ANALYTICS_OPTION_CHAIN_NEW;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }
    public void BondOrIPOAplly(StructBondIPOApply req){
        try{
            m_msgCode = eMessageCode.BONDORIPOAPPLY;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }
    public void getAlertSetting() {
        try {
            GetNotificationSetting gns = new GetNotificationSetting();
            gns.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            gns.mobileOs.setValue(eMobileOS.ANDROID.name);
            m_msgCode = eMessageCode.GETSCRIPRATE_FROMALERT;
            m_data = gns.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewsReq(StructNotificationReq snr) {
        try {
            m_msgCode = eMessageCode.NEWS_FETCH;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendResearchIdeaReq(StructNotificationReq snr) {
        try {
            m_msgCode = eMessageCode.RESEARCH_FETCH_SSO_LOGIN;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendNotificationReq(StructNotificationReq snr,eMessageCode _msgCode) {
        try {
            m_msgCode = _msgCode;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationAgreeReq(StructNewsDisclaimer snr) {
        try {
            m_msgCode = eMessageCode.NEWS_DISCLAIMER;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationSetting(CheckNotificationSetting cns) {
        try {
            SetNotificationSetting sns = new SetNotificationSetting();
            sns.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            sns.event.setValue(cns.getEvent());
            sns.fno.setValue(cns.getFno());
            sns.cash.setValue(cns.getCash());
            sns.research.setValue(cns.getResearch());
            sns.news.setValue(cns.getNews());
            sns.mobileOs.setValue("android");
            m_msgCode = eMessageCode.SETNOTIFICATION_SETTING;
            m_data = sns.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAlertReq(DeleteAlert deleteAlert) {
        try {
            m_msgCode = eMessageCode.DELETESCRIPT_FROMALERT;
            m_data = deleteAlert.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAlert(SetAlertReq setAlertReq) {
        try {
            m_msgCode = eMessageCode.ADDSCRIPT_INALERT;
            m_data = setAlertReq.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNotificationSetting() {
        try {
            GetNotificationSetting gns = new GetNotificationSetting();
            gns.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            gns.mobileOs.setValue(eMobileOS.ANDROID.name);
            m_msgCode = eMessageCode.GETNOTIFICATION_SETTING;
            m_data = gns.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLogoutReq() {
        try {
            m_msgCode = eMessageCode.LOGOFF_REQ;
            /*
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            logoutReq.emieNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            m_data = logoutReq.data.getByteArr((short) m_msgCode.value);*/
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendLogoutReqBCRC() {
        try {
            m_msgCode = eMessageCode.LOGOFF_REQ;
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            logoutReq.emieNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            m_data = logoutReq.data.getByteArr((short) m_msgCode.value);
            sendData(m_data);
            if(UserSession.getLoginDetailsModel().isTradeLogin() &&
                    GlobalClass.tradeRCClient != null && GlobalClass.isRCconnected){
                GlobalClass.tradeRCClient.setIsManualDisconnected(true);
                GlobalClass.tradeRCClient.send(m_data);

                GlobalClass.tradeRCClient.disconnect(true);
            }
            GlobalClass.tradeBCClient.disconnect(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendCurrSymbolList() {
        try {
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            logoutReq.emieNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            m_msgCode = eMessageCode.CURR_SYMBOL;
            m_data = logoutReq.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendExitReq() {
        m_msgCode = eMessageCode.EXIT;
        /*try {
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            logoutReq.emieNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            m_data = logoutReq.data.getByteArr((short) m_msgCode.value);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        this.execute();
    }
    public void sendExitReqToBCRC() {
        m_msgCode = eMessageCode.EXIT;
        try {
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            logoutReq.emieNo.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            m_data = logoutReq.data.getByteArr((short) m_msgCode.value);
            sendData(m_data);
            if(UserSession.getLoginDetailsModel().isTradeLogin() &&
                    GlobalClass.tradeRCClient != null && GlobalClass.isRCconnected){
                GlobalClass.tradeRCClient.setIsManualDisconnected(true);
                GlobalClass.tradeRCClient.send(m_data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendErrorLog(ErrorLOG snr) {
        try {
            m_msgCode = eMessageCode.ERROR_LOG;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendAndroidLog(ErrorLOG snr) {
        try {
            m_msgCode = eMessageCode.ANDROID_LOG;
            m_data = snr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMobileInfo(InsertMobileInfo insertMobileInfo) {
        try {
            m_msgCode = eMessageCode.INSERT_MOBILEINFO;
            m_data = insertMobileInfo.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMultipleScripDetailRequest(ArrayList<Integer> scripCodes) {
        try {
            MultipleScripDetail req = new MultipleScripDetail(scripCodes);
            m_msgCode = eMessageCode.MULTI_SCRIPTDETAILS;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendMultipleScripDetailRequest request:" + m_className, e);
        }
    }

    public void sendNSEBSEReq(int segment, int scripCode) {
        try {
            NseBseScripCode req = new NseBseScripCode();
            req.exchange.setValue(segment);
            req.scripCode.setValue(scripCode);
            m_msgCode = eMessageCode.NSEBSE_SCRIPTCODE;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendMoversReq request:" + m_className, e);
        }
    }

    public void sendMoversReq(int segment, int msgCode) {
        try {
            MktMoverReq req = new MktMoverReq();
            req.segment.setValue(segment);
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.valueOf(msgCode);
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendMoversReq request:" + m_className, e);
        }
    }

    public void sendScripDetailReq(int scripCode) {
        try {
            ScripDetailReq req = new ScripDetailReq();
            req.scripCode.setValue(scripCode);
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.SCRIPT_DETAILS_Request;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:" + m_className, e);
        }
    }

    public void sendSearchScripReq(SearchScript req, boolean isSymbolType) {
        try {
            if (isSymbolType) {
                m_msgCode = eMessageCode.SEARCHSCRIPT;
            } else {
                m_msgCode = eMessageCode.SEARCHSCRIPT_NAME;
            }
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:" + m_className, e);
        }
    }

    public void deleteScripFromGroupRequest(String grpName, int scripCode, long expiry) {
        try {
            AddScripGroup request = new AddScripGroup();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.groupName.setValue(grpName);
            request.scripCode.setValue(scripCode);
            request.expiry.setValue((int) expiry);
            m_msgCode = eMessageCode.DELETESCRIPT_FROMGROUP;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    public void addScripToGroupRequest(String grpName, int scripCode, int expiry) {
        try {
            AddScripGroup request = new AddScripGroup();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.groupName.setValue(grpName);
            request.scripCode.setValue(scripCode);
            request.expiry.setValue(expiry);
            m_msgCode = eMessageCode.ADDSCRIPT_TOGROUP;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }


    public void sendHeartBeattoServer(byte[] data) {
        try {
            m_msgCode = eMessageCode.CLIENT_BC_REGISTRATION;
            m_data = data;
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroupRequest(String grpName,long grpCode) {
        try {
            AddGroup request = new AddGroup();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.groupName.setValue(grpName);
            request.grpCode.setValue(grpCode);
            m_msgCode = eMessageCode.BROKERMODIFYORDER_CASH_BCDELETEGROUP;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    public void addGroupRequest(String grpName) {
        try {
            AddGroup request = new AddGroup();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.groupName.setValue(grpName);
            m_msgCode = eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    public void sendGroupDetailRequest(long grpCode) {
        try {
            GroupDetailReq request = new GroupDetailReq();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.groupCode.setValue(grpCode);
            request.collumnTag.setValue(DualListView.getColumnTag());
            m_msgCode = eMessageCode.NEW_GROUPDETAILS;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    public void sendGroupRequest(int segment) {
        try {
            GroupListReq request = new GroupListReq();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.segment.setValue(segment);
            m_msgCode = eMessageCode.NEW_GROUPLIST;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    private void sendRegistrationRequest() {
        try {
            BcClientRegistration registration = new BcClientRegistration();
            registration.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            registration.iMEI.setValue(MobileInfo.getDeviceID(context));
            registration.version.setValue(MobileInfo.getAppVersionCode());
            registration.clientType.setValue(UserSession.getLoginDetailsModel().isClient()?1:2);
            registration.auth.setValue(UserSession.getClientResponse().charAuthId.getValue());
            registration.slbm.setValue(UserSession.getClientResponse().isSLBMActivated()?'T':' ');
            registration.isReconnect.setValue(GlobalClass.mktDataHandler.hm_mkt5001Data.size()>0);
            registration.ip.setValue(MobileInfo.getIPAddress(true));
            m_msgCode = eMessageCode.CLIENT_BC_REGISTRATION;
            m_data = registration.data.getByteArr((short) m_msgCode.value);
            sendData(m_data);
            m_data = null;

            if(GlobalClass.currGrpCode > 0){
                ShortMktWatchRequest smr = new ShortMktWatchRequest();
                smr.prevGrp.setValue(-1);
                smr.currGrp.setValue(GlobalClass.currGrpCode);
                smr.columnTag.setValue(DualListView.getColumnTag());
                smr.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                smr.isInitialDataTobeSend.setValue(false);
                smr.currGrpSize.setValue(0);

                m_msgCode = eMessageCode.SHORT_MARKETWATCH;
                m_data = smr.data.getByteArr((short) m_msgCode.value);
                sendData(m_data);
                m_data = null;
            }
            if (GlobalClass.mktDepthScripcode > 0) {
                try {
                    MarketWatchRequest request = new MarketWatchRequest();
                    request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    request.token.setValue(GlobalClass.mktDepthScripcode);

                    m_msgCode = eMessageCode.NEW_MARKETDEPTH;
                    m_data = request.data.getByteArr((short) m_msgCode.value);
                    sendData(m_data);
                    m_data = null;
                } catch (Exception e) {
                    GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
                }
            }
            /*
            Object[] obkey = GlobalClass.mktDataHandler.hm_mkt5001Data.keySet().toArray();
            if (obkey.length > 0) {
                int maxArrayLength = 100;
                ArrayList<Integer> scrArrList = new ArrayList<>();
                for (Object obkey1 : obkey) {
                    try {
                        int token = Integer.parseInt(obkey1.toString().trim());
                        if (scrArrList.size() < maxArrayLength) {
                            scrArrList.add(token);
                        } else {
                            send5001Req(scrArrList);
                            sendData(m_data);
                            m_data = null;
                            scrArrList = new ArrayList<>();
                            scrArrList.add(token);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (scrArrList.size() > 0) {
                    send5001Req(scrArrList);
                    sendData(m_data);
                    m_data = null;
                }
            }
            if (GlobalClass.mktDepthScripcode > 0) {
                try {
                    MarketWatchRequest request = new MarketWatchRequest();
                    request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    request.token.setValue(GlobalClass.mktDepthScripcode);

                    m_msgCode = eMessageCode.NEW_MARKETDEPTH;
                    m_data = request.data.getByteArr((short) m_msgCode.value);
                    sendData(m_data);
                    m_data = null;
                } catch (Exception e) {
                    GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
                }
            }*/
            ArrayList<ErrorLOG> androidLOGList = PreferenceHandler.getAndroidLOGList();
            if(androidLOGList.size()>0){
               for(int i=0;i<androidLOGList.size();i++) {
                   try {
                       ErrorLOG snr = androidLOGList.get(i);
                       m_msgCode = eMessageCode.ERROR_LOG;

                       ErrorLOG snrTemp = new ErrorLOG();
                       snrTemp.clientCode.setValue(snr.clientCode.getValue());
                       snrTemp.errorMsg.setValue(snr.errorMsg.getValue());
                       snrTemp.logType.setValue(snr.logType.getValue());
                       snrTemp.errorTime.setValue(snr.errorTime.getValue());

                       m_data = snrTemp.data.getByteArr((short) m_msgCode.value);
                       sendData(m_data);
                       m_data = null;
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
                PreferenceHandler.getAndroidLOGList().clear();
                PreferenceHandler.setAndroidLOGList();
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:" + m_className, e);
        }
    }

    public void sendMarketDepthReq(int scripCode) {
        try {
            GlobalClass.log("Market Depth Req : " + scripCode);
            MarketWatchRequest request = new MarketWatchRequest();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.token.setValue(scripCode);
            m_msgCode = eMessageCode.NEW_MARKETDEPTH;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }
    public void sendMarketDepthForceReq(int scripCode) {
        try {
            GlobalClass.log("Market Depth Req force: " + scripCode);
            MarketWatchRequest request = new MarketWatchRequest();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.token.setValue(scripCode);
            m_msgCode = eMessageCode.MARKETDEPTH;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }
    public void sendMktDepthNewsReq(int scripCode,String symbol,int tag){
        try {
            NewsScripReq request = new NewsScripReq();
            request.ScripCode.setValue(scripCode);
            request.symbolId.setValue(symbol);
            request.Tag.setValue(tag);
            m_msgCode = eMessageCode.NEWS_SCRIP;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public void sendMarketWatchReq(int scripCode) {
        try {
            MarketWatchRequest request = new MarketWatchRequest();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.token.setValue(scripCode);
            m_msgCode = eMessageCode.NEW_MARKETWATCH;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }
    public void sendMarketWatchForceReq(int scripCode) {
        try {
            MarketWatchRequest request = new MarketWatchRequest();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.token.setValue(scripCode);
            m_msgCode = eMessageCode.MARKETWATCH;
            m_data = request.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }

    public void sendsShortMarketWatchReq(ShortMktWatchRequest smr) {
        try {

            m_msgCode = eMessageCode.SHORT_MARKETWATCH;
            m_data = smr.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }

    private void sendMutipleMarketWatchReq(ArrayList<Integer> scripCodeArr) {
        try {
            if (scripCodeArr.size() > 0) {
                int maxArrayLength = 100;
                ArrayList<Integer> scrArrList = new ArrayList<>();
                for (Integer obkey1 : scripCodeArr) {
                    try {
                        int token = obkey1;
                        if (scrArrList.size() < maxArrayLength) {
                            scrArrList.add(token);
                        } else {
                            send5001Req(scrArrList);
                            sendData(m_data);
                            m_data = null;
                            scrArrList = new ArrayList<>();
                            scrArrList.add(token);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (scrArrList.size() > 0) {
                    send5001Req(scrArrList);
                    sendData(m_data);
                    m_data = null;
                }
            }
           // this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendMarketWatchReq request:" + m_className, e);
        }
    }

    public void send5001Req(ArrayList<Integer> scripCodeArr) {
        try {
            MultipleMKtWatch request = new MultipleMKtWatch();
            request.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            request.noOfScrip.setValue(scripCodeArr.size());
            for (int i = 0; i < scripCodeArr.size(); i++) {
                request.scripCode[i].setValue(scripCodeArr.get(i));
            }
            request.complete.setValue(1);
            m_msgCode = eMessageCode.NEW_MULTIPLE_MARKETWATCH;
            m_data = request.data.getByteArr((short) m_msgCode.value);
        } catch (Exception e) {
            GlobalClass.onError("Error in sending send5001Req request:" + m_className, e);
        }
    }

    public void sendChartRequest(StructChartReq req, eMessageCode msgCode) {

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendChartRequest request:" + m_className, e);
        }

    }

    public void startStopReq(int startStop) {
        try {
            BCStopReq bcStopReq = new BCStopReq();
            bcStopReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            bcStopReq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            bcStopReq.stopstart.setValue(startStop);
            m_msgCode = eMessageCode.START_STOP;
            m_data = bcStopReq.data.getByteArr((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCustomDialogReq() {
        try{
            StructCustomDialogRequest req = new StructCustomDialogRequest();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            req.sl.setValue(PreferenceHandler.getCustomDialogMaxSL());
            GlobalClass.log(req.toString());
            m_msgCode = eMessageCode.CUSTOM_DIALOG;
            short mc = (short)m_msgCode.value;
            m_data = req.data.getByteArr(mc);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:"+ m_className, e);
        }
    }
    public void sendLedgerReq(int value) {
        try{
            StructCustomDialogRequest req = new StructCustomDialogRequest();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            req.sl.setValue(value);
            GlobalClass.log(req.toString());
            m_msgCode = eMessageCode.LEDGER_DATA;
            short mc = (short)m_msgCode.value;
            m_data = req.data.getByteArr(mc);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:"+ m_className, e);
        }
    }
    /*
    public void sendGroupTokenReq(long grpCode) {
        try{
            Grou req = new GroupTokensReq();
            req.groupCode.setValue(grpCode);
          //  m_msgCode = Constants.MSGCODE_GROUP_TOKEN_LIST;
            m_data = req.data.getByteArr((short)m_msgCode);
            this.execute();
        }
        catch (Exception e) {
            GlobalClass.onError("Error in sending sendGroupTokenReq request:"+ m_className, e);
        }
    }*/
}