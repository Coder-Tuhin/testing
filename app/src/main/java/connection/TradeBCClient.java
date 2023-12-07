package connection;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;


import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import Structure.BaseStructure.StructBase;
import Structure.Response.BC.BCMsgStructure;
import Structure.Response.BC.FnoNSE_OIData;
import Structure.Response.BC.FnoNSE_TradeExecution;
import Structure.Response.BC.LiteIndicesWatch;
import Structure.Response.BC.LiteMktDepth;
import Structure.Response.BC.LiteMktWatch;
import Structure.Response.BC.SetNotificationRes;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.BC.StaticWatch;
import Structure.Response.BC.StructADST;
import Structure.Response.BC.StructCurrSymbolList;
import Structure.Response.BC.StructMaintenanceMsg;
import Structure.Response.BC.StructMobTickDataResponse;
import Structure.Response.BC.StructNseBSECode;
import Structure.Response.BC.StructRegistrationResp;
import Structure.Response.BC.StructTopGainerLoser;
import Structure.Response.BC.StructTopTraded;
import Structure.Response.Group.GroupAddDelResp;
import Structure.Response.Group.GroupTokensResp;
import Structure.Response.Group.GroupsResp;
import Structure.Response.RC.StructOrderResMsg;
import Structure.Response.Scrip.MultipleScripDetailRes;
import Structure.Response.Scrip.ScripDetail;
import Structure.Response.Scrip.SearchScripResp;
import Structure.news.StructNews;
import Structure.news.StructNewsResp;
import chart.GraphFragment;
import enums.eForHandler;
import enums.eIndices;
import enums.eLogType;
import enums.eMessageCode;
import enums.eMsgType;
import enums.ePrefTAG;
import fragments.LatestResultFragment;
import fragments.OptionChainNew.OptionChainSelectionView;
import fragments.OptionChainNew.utility.StructScripNames;
import fragments.ValuetionFragment;
import fragments.homeGroups.MktdepthFragmentRC;
import fragments.reports.SLBMHoldingDetails;
import fragments.reports.SLBMHoldingView;
import fragments.research.ResearchFragment;
import models.MessageModel;
import models.NotificationModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import structure.TypeConverter;
import utils.Connectivity;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import view.BuySellWindowforQuickOrder;
import wealth.VenturaServerConnect;
import wealth.mv.HoldingMenuNew;
import wealth.mv.LongShortTableViewFragment;

public class TradeBCClient extends WebSocketListener implements ReqSent {

    //region [veriables]
    private final String m_className = getClass().getName();
    private ConnectionProcess m_process;
    final String DISCONNECTED_MSG = "Ventura Wealth have been disconnected from server";
    final String CONNECTED_MSG = "Ventura Wealth connected with server";
    final String CONNECTION_STATUS = "Connection Status!";
    public boolean IsBroadcastConnected = false;
    private boolean isManualDisconnected;
    private boolean sensexCame,niftyCame,onceConnected = false;

    private WebSocket mainWebSocket = null;
    private final URI serverURI;
    //endregion

    //region [Constructor]
    public TradeBCClient(ConnectionProcess process, URI serverURI) {
        //super(serverURI);
        this.serverURI = serverURI;
        try {
            GlobalClass.log("BC URL : " + serverURI.toString());
            this.m_process = process;
            GlobalClass.tradeBCClient = this;
            IsBroadcastConnected = false;
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }
    //endregion



    //region [Override Methods]
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        GlobalClass.log("BC QWS Error : " + t.getMessage());
        try {
            onClosed(webSocket,1000,"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.mainWebSocket = webSocket;
        try {
            isManualDisconnected = false;
            GlobalClass.log("BC onConnected : "+isManualDisconnected+" : " + response.toString() + " : "+webSocket.toString());
            IsBroadcastConnected = true;
            stopReconnectTimer();
            setImage(true);

            new SendDataToBCServer(this, eMessageCode.CLIENT_BC_REGISTRATION).execute();
            initReconnectionValues();
            GlobalClass.dismissdialog();
            checkForRCConnect();
            if (m_process != null) {
                m_process.connected();
            }
            onceConnected = true;
        } catch (Exception e) {
            VenturaException.Print(e);
            try {
                GlobalClass.dismissdialog();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        byte[] data = bytes.toByteArray();
        if(data.length > 4){
            /*
            byte[] dataML = new byte[2];
            System.arraycopy(data, 0, dataML, 0, dataML.length);
            short msgLength = TypeConverter.byteToShort(dataML);
            */
            byte[] dataMC = new byte[2];
            System.arraycopy(data, 2, dataMC, 0, dataMC.length);
            short msgCode = TypeConverter.byteToShort(dataMC);

            byte[] dataPacket = new byte[data.length - 4];
            System.arraycopy(data, 4, dataPacket, 0, dataPacket.length);

            onDataProcess(dataPacket, msgCode);
        }
        //if(!GlobalClass.broadCastReg.isNormalMKt()){
        if(htbttimer == null) {
            startTimer();
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        GlobalClass.log("BC str : " +text);
        if(text.equalsIgnoreCase("htbt")) {
            startTimer();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        GlobalClass.log("onClosing BC_Code : " + code + " :reson: "+reason +  " : " );
        onClosed(webSocket,code,reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        GlobalClass.log("BC_onClose : " + " :Remote " +reason + " : " +isManualDisconnected);
        IsBroadcastConnected = false;
        stopTimer();
        setImage(false);
        if(!onceConnected){
            GlobalClass.log("VW BC client socketNotAvailable");
            GlobalClass.addAndroidLog(eLogType.BCConnectFailed.name, "bcip|"+ serverURI.toString() + "|" + MobileInfo.getMobileData(),"");
            if (m_process != null) {
                m_process.serverNotAvailable();
                m_process = null;
            }else {
                TryToReconnect();
            }
        } else{
            try {
                GlobalClass.log("VW BC client socketDisconnected : "+ isManualDisconnected);
                if (!isManualDisconnected) {
                    TryToReconnect();
                }
                VenturaServerConnect.closeSocket();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
    public void createLog(String string) {
        GlobalClass.log("", "createLog", string + ":" + new Date());
    }
    public void onError(String msg , Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        } else {
            GlobalClass.log("BCClientException", " :: "+msg);
        }
    }

    @Override
    public void reqSent(int msgCode) {
        //createLog("Request Sent : " + msgCode);
    }
    //endregion

    //region [Public Methods]
    public void send(byte[] data) {
        if(mainWebSocket != null){
            mainWebSocket.send(ByteString.of(data));
        }
    }
    public void disconnect(boolean isManualDisconnected) throws IOException {
        try {
            this.isManualDisconnected = isManualDisconnected;
            if (mainWebSocket != null) {
                this.mainWebSocket.close(1001, "force closed from android");
                this.mainWebSocket = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void setIsManualDisconnected(boolean isManualDisconnected){
        this.isManualDisconnected = isManualDisconnected;
    }
    public void connect(){
        try {
            //GlobalClass.log("BC_IP1 : " + ip + " : " + port);
            if(IsBroadcastConnected) {
                disconnect(true);
            }
            try {
                GlobalClass.log("BC_IP1 : " + serverURI.toString());
                SSLContext sslContext = SSLContext.getDefault();
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory,getX509TrustManager())
                        .hostnameVerifier(getHostNameVerifier())
                        .build();
                Request request = new Request.Builder()
                        .url(serverURI.toString())
                        //.addHeader("authorization",UserSession.getToken().getAuthHeader())
                        .build();
                client.newWebSocket(request, this);
                client.dispatcher().executorService().shutdown();
            } catch (Exception e) {
                VenturaException.Print(e);
            }

        }catch (Exception ex){
            VenturaException.Print(ex);
            //onClose(-1,"Not Connected",false);
        }
    }
    public X509TrustManager getX509TrustManager(){
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,String authType) {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        };
    }
    public HostnameVerifier hostnameVerifier;
    public  HostnameVerifier getHostNameVerifier() {
        if (hostnameVerifier == null) {
            hostnameVerifier = (arg0, arg1) -> true;
        }
        return hostnameVerifier;
    }
    public void stopClient() {
        try {
            disconnect(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkForRCConnect() {
        try {
            if (UserSession.isTradeLogin() && GlobalClass.tradeRCClient.isNeedToReconnectFromBC && !GlobalClass.isMaintanceMode) {
                GlobalClass.tradeRCClient.isNeedToReconnectFromBC = false;
                GlobalClass.tradeRCClient.initReconnectionValues();
                GlobalClass.tradeRCClient.tryReconnect();
            }
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }
    public void TryToReconnect() {
        GlobalClass.log("VW BC TryToReconnect : " );
        stopReconnectTimer();
        if(!IsBroadcastConnected) {
            if (GlobalClass.homeActivity != null) {
                HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
                if (!homeActivity.isActivityResumed()) {
                    homeActivity.setResumeListener(arl);
                    return;
                }
            }
            reconnectTimer = new Timer();
            ReconnectTimerTask newTask = new ReconnectTimerTask();  // new instance
            reconnectTimer.schedule(newTask, delay);

            /*
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    GlobalClass.dismissdialog();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                if (!IsBroadcastConnected) {
                    if (GlobalClass.homeActivity != null) {
                        HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
                        if (count < 150) {

                            count++;
                            delay = DELAY_6SEC;
                            if (homeActivity.isActivityResumed()) {
                                Config config = VenturaApplication.getPreference().getConnectionConfig();
                                connect();

                            } else {
                                TryToReconnect();
                            }
                            GlobalClass.log("RECONNECT.....",  " Count: " + count + " Delay: " + delay);
                        } else {
                            GlobalClass.dismissdialog();
                            if (homeActivity.isActivityResumed()) {
                                ReconnectionDialog(homeActivity);
                            } else {
                                homeActivity.setResumeListener(arl);
                            }
                        }
                    }
                }
            });
            thread.start();*/
        }
    }

    private void stopReconnectTimer(){
        if(reconnectTimer != null){
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
    }
    private HomeActivity.ActivityResumeListiner arl = () -> {
        if (GlobalClass.homeActivity != null) {
            //HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
            //ReconnectionDialog(homeActivity);
            TryToReconnect();
        }
    };

    private class ReconnectTimerTask extends TimerTask{
        @Override
        public void run() {
            try{
                if (!IsBroadcastConnected) {
                    if (GlobalClass.homeActivity != null) {
                        HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
                        //GlobalClass.showdialog("Connecting...1");
                        if (count < 150) {
                            count++;
                            delay = DELAY_6SEC;
                            if (homeActivity.isActivityResumed()) {
                                Config config = VenturaApplication.getPreference().getConnectionConfig();
                                connect();

                            } else {
                                TryToReconnect();
                            }
                            GlobalClass.log("RECONNECT.....",  " Count: " + count + " Delay: " + delay);
                        } else {
                            GlobalClass.dismissdialog();
                            if (homeActivity.isActivityResumed()) {
                                ReconnectionDialog(homeActivity);
                            } else {
                                homeActivity.setResumeListener(arl);
                            }
                        }
                    }
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private Timer htbttimer;
    //private int iddleCount = 0;
    private void startTimer(){
        try {
            //iddleCount = 0;
            stopTimer();
            htbttimer = new Timer();
            TimerTask newTask = new MyTimerTask(new Date().getTime());  // new instance
            htbttimer.schedule(newTask, 15000, 15000);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void stopTimer(){
        try {
            if (htbttimer != null) {
                htbttimer.cancel();
                htbttimer = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private class MyTimerTask extends TimerTask{
        long id;
        int iddleCount = 0;
        MyTimerTask(long _id){
            this.id = _id;
            GlobalClass.log("BC MyTimerTask Craeted : " + new Date() + " : " + id);
        }
        @Override
        public void run() {
            try{
                if(mainWebSocket != null){
                    mainWebSocket.send("htbt");
                    iddleCount++;
                    GlobalClass.log("BC Iddle Count : " + iddleCount + " : " + id + " : " +new Date());
                    if(iddleCount == 3){
                        stopTimer();
                        onClosed(mainWebSocket,1001,"No Response for 45 seconds");
                    }else if(iddleCount > 3){
                        this.cancel();
                    }
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private Timer reconnectTimer;
    private long delay = 0;
    private int count = 0;
    //private long MaxReconnectionTry = 0;
    private final long DELAY_6SEC = 6000;
    private final long DELAY_2SEC = 2000;

    private void ReconnectionDialog(HomeActivity homeActivity) {

        if(!GlobalClass.isSessionExpired) {
            homeActivity.showMsgDialog("Connection Lost!", DISCONNECTED_MSG,
                    "Reconnect", "Exit", (dialogInterface, i) -> {
                        GlobalClass.isForceReconnect = true;
                        GlobalClass.showProgressDialog("Connecting...2");
                        initReconnectionValues();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (Connectivity.IsConnectedtoInternet(homeActivity)) {
                                TryToReconnect();
                            } else {
                                GlobalClass.dismissdialog();
                                homeActivity.showMsgDialog("Failed to Connect!", R.string.No_internet, true);
                            }
                        }, DELAY_2SEC);

                    }, (dialogInterface, i) -> {
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendExitReq();
                    });
        }
    }
    private void initReconnectionValues() {
        delay = 0;
        count = 0;
        //MaxReconnectionTry = 0;
    }
    private void setImage(final boolean isConnectted) {
        try {
            if (GlobalClass.homeActivity != null) {
                HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
                ImageView connectionImage = homeActivity.findViewById(R.id.connection_logo);
                View imageContainer = homeActivity.findViewById(R.id.relative);
                int imageResource = isConnectted ? R.drawable.connect : R.drawable.disconnect;
                String connectionMsg = isConnectted ? CONNECTED_MSG : DISCONNECTED_MSG;
                homeActivity.runOnUiThread(() -> {
                    connectionImage.setImageResource(imageResource);
                    imageContainer.setOnClickListener(view -> {
                        if(isConnectted) {
                            homeActivity.showMsgDialog(CONNECTION_STATUS, connectionMsg, false);
                        }
                        else {
                            homeActivity.showToast(connectionMsg);
                            initReconnectionValues();
                            TryToReconnect();
                        }
                    });
                });

                try {
                    GlobalClass.dismissdialog();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void onDataProcess(byte data[], int msgCode) {
        try {
            //GlobalClass.log("BC MSGCode: " + msgCode);
            eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
            //GlobalClass.reqResHandler.deletefromQueue(emessagecode);
            switch (emessagecode) {
                case BC_MSG:
                    handleMsg(data);
                    break;
                case ERROR_MSG:
                    handleErrorMsg(data);
                    break;
                case MAINTENANCE_MSG:
                    handleMaintenanceMsg(data);
                    break;
                case STATIC_MW:
                    setStaticMktWatchData(data,msgCode);
                    break;
                case LITE_MW:
                    setLiteMktWatchData(data,msgCode);
                    break;
                case OpenInt:
                    setOpenIntData(data,msgCode);
                    break;
                case LITE_MD:
                    setMarketDepthData(data,msgCode);
                    break;
                case TRADE_EXECUTION:
                    setTradeExecutionRange(data,msgCode);
                    break;
                case DARTEVENT:
                    if (UserSession.getLoginDetailsModel().isClient()) {
                        setEvent(data);
                    }
                    break;
                case ADST:
                    setADST(data);
                    break;
                case CUSTOM_DIALOG:
                    setCUSTOMDialog(data,msgCode);
                    break;
                case TOP_TRADED:
                    handleTopTradedResp(data);
                    break;
                case TOP_GAINER:
                case TOP_LOOSER:
                    handleTopGNRLSR(data, msgCode);
                    break;
                case NSEBSE_SCRIPTCODE:
                    handleNSEBSECode(data);
                    break;
                case INDICE_CODE_LITE_PRIME:
                    setIndicesData(data,msgCode);
                    break;
                case NEW_GROUPLIST:
                    GlobalClass.log("BC MSGCode: " + msgCode);
                    setGroups(data);
                    break;
                case NEW_GROUPDETAILS:
                    GlobalClass.log("BC MSGCode: " + msgCode);
                    setGroupDetails(data);
                    break;
                case ANALYTICS_OPTION_CHAIN_NEW:
                    setOptionChainGroupDetails(msgCode,data);
                    break;
                case LEDGER_DATA:
                    setLedgerDetails(msgCode,data);
                    break;
                case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP:
                    setADDGroup(data);
                    break;
                case BROKERMODIFYORDER_CASH_BCDELETEGROUP:
                    setDELETEGroup(data);
                    break;
                case SEARCHSCRIPT:
                    setSearchScrip(data, msgCode);
                    break;
                case SEARCHSCRIPT_NAME:
                    setSearchScripName(data, msgCode);
                    break;
                case SCRIPT_DETAILS_Response:
                    setScripDetail(data);
                    break;
                case MULTI_SCRIPTDETAILS:
                    setMultipleScripDetail(data);
                    break;
                case EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA:
                case INTRADAY_CHART: {
                    StructMobTickDataResponse ataResponse = new StructMobTickDataResponse(data, false);
                    GlobalClass.chartDataProcess.putData(ataResponse, msgCode);
                    if (ataResponse.downloadComplete.getValue() == 1) {
                        GlobalClass.log("", "", "cart data complete :");
                        notifyGraph(msgCode, ataResponse.token.getValue());
                    }
                }
                    break;
                case EOD_CHART: {
                    StructMobTickDataResponse ataResponse = new StructMobTickDataResponse(data, true);
                    GlobalClass.chartDataProcess.putData(ataResponse, msgCode);
                    if (ataResponse.downloadComplete.getValue() == 1) {
                        GlobalClass.log("", "", "cart data complete :");
                        notifyGraph(msgCode, ataResponse.token.getValue());
                    }
                }
                    break;
                case SETNOTIFICATION_SETTING:
                case DELETESCRIPT_FROMALERT:
                case ADDSCRIPT_INALERT:
                    HandleREsponse(data, msgCode);
                    break;
                case GETNOTIFICATION_SETTING:
                case GETSCRIPRATE_FROMALERT:
                    handleGetNotification(data, msgCode);
                    break;
                case CLIENT_BC_REGISTRATION:
                    handleHeartBeat(data, msgCode);
                    break;
                case NEWS_FETCH:
                    handleNotification(data, msgCode);
                    break;
                case NOTIFICATION_FETCH:
                    handleTradingCallNotification(data);
                    break;
                case NEWS_DISCLAIMER:
                    PreferenceHandler.setNotificationActive(true);
                    break;
                case CURR_SYMBOL:
                    handleCurrSumbolList(data);
                    break;
                case RESEARCH_FETCH_SSO_LOGIN:
                    handleResearchIdea(data);
                    break;
                case NEWS_SCRIP:
                    if(GlobalClass.newsScripHandler != null){
                        NotifyData(GlobalClass.newsScripHandler,msgCode,data);
                    } else {
                        NotifyData(MktdepthFragmentRC.mktDepthUiHandler, msgCode, data);
                    }
                    break;
                case SCRIP_NAMES:
                    StructScripNames scripNames = new StructScripNames(data);
                    GlobalClass.scripNameHandler.setScripNames(scripNames);
                    GlobalClass.dismissdialog();
                    sendDataToHandler(OptionChainSelectionView._handler,msgCode);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            GlobalClass.log("Error in data received : " + msgCode);
            onError("Error in data receive:"+msgCode+" : " + m_className, e);
        }
    }

    private void sendDataToHandler(Handler handler, int msgCode) {
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void handleResearchIdea(byte[] data) {
        if (ResearchFragment.researchHandler != null) {
            Message msg = Message.obtain(ResearchFragment.researchHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.RESEARCH_FETCH_SSO_LOGIN.value);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
            msg.setData(confMsgBundle);
            ResearchFragment.researchHandler.sendMessage(msg);
        }
    }

    private void handleCurrSumbolList(byte[] data) {
        StructCurrSymbolList symbolList = new StructCurrSymbolList(data);
        GlobalClass.mktDataHandler.handleCurrSumbolList(symbolList);
    }

    private void handleNotification(byte[] data, int msgCode) {
        try {
            StructNewsResp response = new StructNewsResp(data);
            StructNews[] newsDetail = response.newsDetail;
            String msg = "";
            for (int i = 0; i < newsDetail.length; i++) {
                StructNews sn = newsDetail[i];
                String title =  eMsgType.NEWS.name;
                msg = sn.msg.getValue();
                String time = sn.msgTime.getValue() + "";
                GlobalClass.log("TIME", " ::" + time);
                String id = sn.msgTime.getValue() + "";

                NotificationModel notificationModel = new NotificationModel(title,msg,time,id,0,"");
                PreferenceHandler.getNotificationList().put(notificationModel.getIDForSaving(), notificationModel);
                if (i == newsDetail.length - 1) {
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.LAST_NEWSTIME.name, sn.msgTime.getValue());
                    //Toast.makeText(GlobalClass.latestContext, ""+newsDetail.length, Toast.LENGTH_SHORT).show();
                }
            }
            PreferenceHandler.setNotificationList();
            if (GlobalClass.clsNewsHandler != null) {
                GlobalClass.clsNewsHandler.loadNewsData();
            }

            if (GlobalClass.notificationHandler != null) {
                Message msgS = Message.obtain(GlobalClass.notificationHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                msgS.setData(confMsgBundle);
                GlobalClass.notificationHandler.sendMessage(msgS);
            } else if (GlobalClass.mktWatchUiHandler != null) {
                Message mssg = Message.obtain(GlobalClass.mktWatchUiHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.NOTIFICATION.value);
                confMsgBundle.putInt(eForHandler.SCRIPCODE.name, 0);
                confMsgBundle.putString(eForHandler.RESPONSE.name, eMsgType.NEWS.name);
                mssg.setData(confMsgBundle);
                GlobalClass.mktWatchUiHandler.sendMessage(mssg);
            }

        } catch (Exception e) {
            onError("Error in in notify:" + m_className, e);
        }
    }

    private void handleTradingCallNotification(byte[] data) {
        try {
            StructNewsResp response = new StructNewsResp(data);
            StructNews[] newsDetail = response.newsDetail;
            String msg = "";
            for (int i = 0; i < newsDetail.length; i++) {
                StructNews sn = newsDetail[i];
                eMsgType notificationType = eMsgType.getEnumByValue(sn.msgType.getValue());
                //if (sn.msgType.getValue() == eMsgType.TRADING_CALL.value){
                    String title =  notificationType.name;//eMsgType.TRADING_CALL.name;
                    msg = sn.msg.getValue();
                    String time = sn.msgTime.getValue() + "";
                    GlobalClass.log("TIME", " ::" + time);
                    String id = sn.msgTime.getValue() + "";
                    NotificationModel notificationModel = new NotificationModel(title,msg,time,id,0,"");

                    PreferenceHandler.getNotificationList().put(notificationModel.getIDForSaving(), notificationModel);
                    if (i == newsDetail.length - 1) {
                        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.LAST_NOTIFICATION_TIME.name, sn.msgTime.getValue());
                        //Toast.makeText(GlobalClass.latestContext, ""+newsDetail.length, Toast.LENGTH_SHORT).show();
                    }
                //}
            }
            PreferenceHandler.setNotificationList();
            if (GlobalClass.notificationHandler != null) {
                Message msgS = Message.obtain(GlobalClass.notificationHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.NEWS_FETCH.value);//Static for refresh,other than  request should be continue
                msgS.setData(confMsgBundle);
                GlobalClass.notificationHandler.sendMessage(msgS);
            }
        } catch (Exception e) {
            onError("Error in in notify:" + m_className, e);
        }
    }

    private void handleHeartBeat(byte[] data, int msgCode) {
        try {
            if(m_process != null && GlobalClass.isCommodity()) {
                m_process.sensexNiftyCame();
                m_process = null;
            }
           /*SendDataToBCServer sendDataToBCServer = new SendDataToBCServer();
            sendDataToBCServer.sendHeartBeattoServer(data);*/
            GlobalClass.broadCastReg = new StructRegistrationResp(data);
            GlobalClass.log(GlobalClass.broadCastReg.toString());
            //this.client.setIsHtBt(GlobalClass.broadCastReg.getHeartBit());
            if(GlobalClass.mktDataHandler.getCurrSymbolList().size() <= 0) {
                new SendDataToBCServer().sendCurrSymbolList();
            }
            if(GlobalClass.broadCastReg.isNormalMKt()){
                stopTimer();
            }else{
                startTimer();

                if(GlobalClass.isMaintanceMode != GlobalClass.broadCastReg.isMaintenanceMode()) {

                    GlobalClass.isMaintanceMode = GlobalClass.broadCastReg.isMaintenanceMode();
                    Handler mainHandler = new Handler(GlobalClass.latestContext.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GlobalClass.homeActivity.showMaintanceMode(GlobalClass.isMaintanceMode);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMsg(byte[] data) {
        final BCMsgStructure bcms = new BCMsgStructure(data);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new AlertBox(GlobalClass.latestContext, "", "Ok", bcms.charMsg.getValue(), false);
            }
        });
    }

    private void HandleREsponse(byte[] data, int msgCode) {
        try {
            SetNotificationRes setNotificationRes = new SetNotificationRes(data);
            GlobalClass.showToast(GlobalClass.latestContext, setNotificationRes.msg.getValue());
            try {
                if (GlobalClass.alertHandler != null) {
                    Message msg = Message.obtain(GlobalClass.alertHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                    confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                    msg.setData(confMsgBundle);
                    GlobalClass.alertHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                onError("Error in in notify:" + m_className, e);
            }
        } catch (Exception e) {
            onError("Error in in notify:" + m_className, e);
        }
    }

    private void handleGetNotification(byte[] data, int msgCode) {
        try {
            if (GlobalClass.alertSettingHandler != null) {
                Message msg = Message.obtain(GlobalClass.alertSettingHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.alertSettingHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            onError("Error in in notify:" + m_className, e);
        }
    }

    private void notifyGraph(int msgCode, int scripCode) {
        try {
            sendDataToHandler(GraphFragment.graphHandler, msgCode, scripCode, new byte[0]);

        } catch (Exception e) {
            onError("Error in in notify:" + getClass().getName(), e);
        }
    }

    private void notifyGraphUpdate(int msgCode, StaticLiteMktWatch mktWatch) {
        try {
            if (GraphFragment.uiHandler != null) {
                int selToken = GlobalClass.chartDataProcess.getSelectectedToken();
                if (selToken == mktWatch.getToken()) {
                    sendDataToHandler(GraphFragment.uiHandler, msgCode);
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void sendDataToHandler(Handler handler, int msgCode, StructBase struct) {
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putSerializable(eForHandler.RESPONSE.name, struct);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            onError("Error in " + getClass().getName(), e);
        }
    }

    private void sendDataToHandler(Handler handler, int msgCode, int scripCode, byte data[]) {
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putInt(eForHandler.SCRIPCODE.name, scripCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            onError("Error in " + getClass().getName(), e);
        }
    }

    private void setMultipleScripDetail(byte[] data) {
        MultipleScripDetailRes multipleScripDetail = new MultipleScripDetailRes(data);
        for (int i = 0; i < multipleScripDetail.noOfSrip.getValue(); i++) {
            ScripDetail sglr = multipleScripDetail.scripDetails[i];
            GlobalClass.mktDataHandler.setScripDetailData(sglr);
        }
        if (GlobalClass.orderBkUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.orderBkUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.MULTI_SCRIPTDETAILS.value);
            msg.setData(confMsgBundle);
            GlobalClass.orderBkUIHandler.sendMessage(msg);
        }
    }

    private void handleErrorMsg(final byte[] data) {
        Handler mainHandler = new Handler(GlobalClass.latestContext.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StructMaintenanceMsg logOffResp = new StructMaintenanceMsg(data);

                    if(logOffResp.isExit.getValue()) {
                        GlobalClass.tradeBCClient.disconnect(true);
                        GlobalClass.tradeRCClient.disconnect(true);
                        count = Integer.MAX_VALUE;
                    }
                    new AlertBox(GlobalClass.latestContext, "", "OK", logOffResp.errMsg.getValue(), logOffResp.isExit.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }
    private void handleMaintenanceMsg(final byte[] data) {
        System.out.println("handleMaintenanceMsg : ");
        Handler mainHandler = new Handler(GlobalClass.latestContext.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StructMaintenanceMsg logOffResp = new StructMaintenanceMsg(data);
                    System.out.println("handleMaintenanceMsg : "+logOffResp.toString());
                    /*if(logOffResp.isExit.getValue()) {
                        GlobalClass.tradeBCClient.disconnect(true);
                        GlobalClass.tradeRCClient.disconnect(true);
                        count = Integer.MAX_VALUE;
                        new AlertBox(GlobalClass.latestContext, "", "OK", logOffResp.errMsg.getValue(), logOffResp.isExit.getValue());
                    }*/
                    GlobalClass.isMaintanceMode = logOffResp.isExit.getValue();
                    if(GlobalClass.isMaintanceMode){
                        if(UserSession.isTradeLogin()){
                            UserSession.setTradeLogin(false);
                            GlobalClass.tradeRCClient.disconnect(true);
                        }
                    }
                    GlobalClass.homeActivity.showMaintanceMode(logOffResp.isExit.getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    private void handleOrderConf(byte[] data) {
        try {
            StructOrderResMsg respMsg = new StructOrderResMsg(data);
            String _message = respMsg.msg.getValue();
        //    GlobalClass.log("DialogMessageTest4","   "+_message);
            MessageModel _messageModel = new MessageModel(_message, DateUtil.NToT(respMsg.time.getValue()));
            GlobalClass.addMessages(_messageModel,false);
            if (respMsg.msg.getValue().toLowerCase().contains(("Traded Qty Mismatch").toLowerCase())) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    GlobalClass.getClsTradeBook().sendTradeBookRequest();
                    GlobalClass.getClsOrderBook().sendOrderBookRequest();
                });
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void handleNSEBSECode(byte[] data) {
        try {
            StructNseBSECode nseBse = new StructNseBSECode(data);
            try {
                if (MktdepthFragmentRC.mktDepthUiHandler != null) {
                    Message msg = Message.obtain(MktdepthFragmentRC.mktDepthUiHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt("msgCode", eMessageCode.NSEBSE_SCRIPTCODE.value);
                    confMsgBundle.putInt("scripCode", nseBse.token.getValue());
                    confMsgBundle.putString("scripName", nseBse.scripName.getValue());
                    msg.setData(confMsgBundle);
                    MktdepthFragmentRC.mktDepthUiHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                onError("Error in in notify:" + m_className, e);
            }
            //notify depth screen...

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }


    private void setScripDetail(byte data[]) {
        try {
            ScripDetail sglr = new ScripDetail(data);
            GlobalClass.mktDataHandler.setScripDetailData(sglr);
            //notify depth screen...
            NotifyScripCode(BuySellWindowforQuickOrder.handler, eMessageCode.SCRIPT_DETAILS_Response.value, sglr.scripCode.getValue());
            NotifyScripCode(MktdepthFragmentRC.mktDepthUiHandler, eMessageCode.SCRIPT_DETAILS_Response.value, sglr.scripCode.getValue());

            if (GlobalClass.orderBkUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.orderBkUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.SCRIPT_DETAILS_Response.value);
                msg.setData(confMsgBundle);
                GlobalClass.orderBkUIHandler.sendMessage(msg);
            }
            if (GlobalClass.orderBkDetailUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.orderBkDetailUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.SCRIPT_DETAILS_Response.value);
                msg.setData(confMsgBundle);
                GlobalClass.orderBkDetailUIHandler.sendMessage(msg);
            }
            if (GlobalClass.tradeBKDetailUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.tradeBKDetailUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.SCRIPT_DETAILS_Response.value);
                msg.setData(confMsgBundle);
                GlobalClass.tradeBKDetailUIHandler.sendMessage(msg);
            }
            if (GlobalClass.mktWatchUiHandler != null) {
                GlobalClass.showmarginDetailDialog(sglr.scripCode.getValue());
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setSearchScrip(byte data[], int msgcC) {
        try {
            SearchScripResp sglr = new SearchScripResp(data);
            //notify home screen...
            if (GlobalClass.searchScripUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.searchScripUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgcC);
                confMsgBundle.putSerializable("struct", sglr);
                msg.setData(confMsgBundle);
                GlobalClass.searchScripUIHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setSearchScripName(byte data[], int msgcC) {
        try {
            //notify home screen...
            if (GlobalClass.searchScripUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.searchScripUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgcC);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.searchScripUIHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setDELETEGroup(byte data[]) {
        try {
            GroupAddDelResp sglr = new GroupAddDelResp(data);
            //notify home screen...

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setADDGroup(byte data[]) {
        try {
            GroupAddDelResp sglr = new GroupAddDelResp(data);
            if (GlobalClass.searchScripUIHandler != null) {
                Message msg = Message.obtain(GlobalClass.searchScripUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
                confMsgBundle.putSerializable("struct", sglr);
                msg.setData(confMsgBundle);
                GlobalClass.searchScripUIHandler.sendMessage(msg);
            } else if (GlobalClass.readerWatchUiHandler != null) {
                Message msg = Message.obtain(GlobalClass.readerWatchUiHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
                confMsgBundle.putSerializable("struct", sglr);
                msg.setData(confMsgBundle);
                GlobalClass.readerWatchUiHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setGroupDetails(byte data[]) {
        try {
            GroupTokensResp sglr = new GroupTokensResp(data);
            GlobalClass.groupHandler.addTokensFromGroupTokenRes(sglr);
            GlobalClass.log("Grp : " + sglr.segment.getValue() + " : " + sglr.groupCode.getValue()+" : "+sglr.noOfRecs.getValue()+" : " + sglr.grpTokenDetails.length + " : " +sglr.complete.getValue());
            //notify home screen...
            if(sglr.complete.getValue() == 1) {
                notifyMktWatchScreen(eMessageCode.NEW_GROUPDETAILS.value, -1, data);
                notifyMyStockScreen(eMessageCode.NEW_GROUPDETAILS.value, -1);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setOptionChainGroupDetails(int msgCode,byte data[]) {
        try {
            GroupTokensResp sglr = new GroupTokensResp(data);
            GlobalClass.groupHandler.setOptionChainResp(sglr);

            if(GlobalClass.optionChainHandler != null) {
                Message msg = Message.obtain(GlobalClass.optionChainHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.optionChainHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setLedgerDetails(int msgCode,byte data[]) {
        try {
            if(GlobalClass.ledgerHandler != null) {
                Message msg = Message.obtain(GlobalClass.ledgerHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.ledgerHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setGroups(byte data[]) {
        try {
            GroupsResp sglr = new GroupsResp(data);
            GlobalClass.groupHandler.addGroupFromGroupRes(sglr);
            notifyMktWatchScreen(eMessageCode.NEW_GROUPLIST.value, 0, data);
            if (GlobalClass.homeScrUiHandler != null &&
                    GlobalClass.fragmentManager.getBackStackEntryCount() < 1) {
                Message msg = Message.obtain(GlobalClass.homeScrUiHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.NEW_GROUPLIST.value);
                msg.setData(confMsgBundle);
                GlobalClass.homeScrUiHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void handleTopTradedResp(byte data[]) {
        try {
            StructTopTraded topTraded = new StructTopTraded(data);
            GlobalClass.groupHandler.handleTopTradedResp(topTraded);
            // notify using searchScripUIHandler
            notifyMktWatchScreen(eMessageCode.TOP_TRADED.value, topTraded.segment.getValue(), data);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void handleTopGNRLSR(byte data[], int msgCode) {
        try {
            StructTopGainerLoser topGainerLoser = new StructTopGainerLoser(data);
            if (msgCode == eMessageCode.TOP_GAINER.value) {
                GlobalClass.groupHandler.handleGainerResp(topGainerLoser);
            } else {
                GlobalClass.groupHandler.handleLooserResp(topGainerLoser);
            }
            notifyMktWatchScreen(msgCode, topGainerLoser.segment.getValue(), data);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setCUSTOMDialog(byte[] data,int msgCode) {
        try {
            if (GlobalClass.homeScrUiHandler != null) {
                Message msg = Message.obtain(GlobalClass.homeScrUiHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.homeScrUiHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setADST(byte[] data) {
        try {
            StructADST adst = new StructADST(data);
            GlobalClass.mktDataHandler.setADSTData(adst);
            notifyHomeScreen(eMessageCode.ADST.value, adst.segment.getValue());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setEvent(byte[] data) {
        try {
            // GlobalClass.latestEvent = new StructxMKTEventRes(data);
            notifyMktWatchScreen(eMessageCode.DARTEVENT.value, 0, data);
            NotifyData(MktdepthFragmentRC.mktDepthUiHandler,eMessageCode.DARTEVENT.value,0,data);

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setMarketDepthData(byte data[],int msgCode) {
        try {
            LiteMktDepth structMktDepth = new LiteMktDepth(data);
            GlobalClass.mktDataHandler.setMktDepthData(structMktDepth);
            NotifyScripCode(MktdepthFragmentRC.mktDepthUiHandler, msgCode, structMktDepth.token.getValue());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setTradeExecutionRange(byte data[],int msgCode) {
        try {
            FnoNSE_TradeExecution structMktDepth = new FnoNSE_TradeExecution(data);
            GlobalClass.mktDataHandler.setTradeExecutionRangeData(structMktDepth);
            NotifyScripCode(MktdepthFragmentRC.mktDepthUiHandler, msgCode, structMktDepth.token.getValue());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setIndicesData(byte data[],int msgCode) {
        try {
            LiteIndicesWatch indices = new LiteIndicesWatch(data);
            GlobalClass.mktDataHandler.setMktIndicesData(indices);
            if(m_process != null) {
                if (indices.token.getValue() == eIndices.NIFTY.value) {
                    niftyCame = true;
                }
                if (indices.token.getValue() == eIndices.SENSEX.value) {
                    sensexCame = true;
                }
                if (niftyCame && sensexCame) {
                    m_process.sensexNiftyCame();
                    m_process = null;
                }
            }
            notifyHomeScreen(msgCode, indices.token.getValue());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setOpenIntData(byte data[],int msgCode) {
        try {
            FnoNSE_OIData oiData = new FnoNSE_OIData(data);
            int token = oiData.token.getValue();
            StaticLiteMktWatch mktW  = GlobalClass.mktDataHandler.getMkt5001Data(token,false);
            mktW.getLw().openInterest.setValue(oiData.openInterest.getValue());
            mktW.getSw().dayHiOI.setValue(oiData.dayHiOI.getValue());
            mktW.getSw().dayLoOI.setValue(oiData.dayLoOI.getValue());
            notifyMktWatchScreen(msgCode, token, data);
            NotifyScripCode(MktdepthFragmentRC.mktDepthUiHandler, msgCode, token);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setLiteMktWatchData(byte data[],int msgCode) {
        try {
            LiteMktWatch mktWatch = new LiteMktWatch(data);
            StaticLiteMktWatch slw = GlobalClass.mktDataHandler.setMkt5001Data(mktWatch);
            notifyMktData(data,msgCode,slw);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    private void setStaticMktWatchData(byte data[],int msgCode) {
        try {
            StaticWatch mktWatch = new StaticWatch(data);
            //GlobalClass.log("Static Watch : " + mktWatch.token.getValue());
            StaticLiteMktWatch slw = GlobalClass.mktDataHandler.setMkt5001Data(mktWatch);
            notifyMktData(data,msgCode,slw);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void notifyMktData(byte data[],int msgCode,StaticLiteMktWatch slw) {
        try {
                int token = slw.getToken();
                notifyMktWatchScreen(msgCode, token, data);
                NotifyScripCode(MktdepthFragmentRC.mktDepthUiHandler, msgCode, token);
                notifyGraphUpdate(msgCode, slw);
                if (GlobalClass.getClsMarginHolding() != null &&
                        GlobalClass.getClsMarginHolding().isConatinScripCode(token)) {
                    GlobalClass.getClsMarginHolding().updateRate(slw);
                    sendLiteMktWatchForPositionandHolding(token);
                }
                if (GlobalClass.getClsNetPosn().isConatinScripCode(token)) {
                    GlobalClass.getClsNetPosn().updateLastRate(slw);
                    sendLiteMktWatchForPositionandHolding(token);
                }
                if (GlobalClass.groupHandler.getOptionChainResp() != null) {
                    if (GlobalClass.optionChainHandler != null &&
                            GlobalClass.groupHandler.getOptionChainResp().isContainScripCode(token)) {
                        sendLiteMktWatchToHandler(GlobalClass.optionChainHandler, token);
                    }
                }
                if (SLBMHoldingView.slbmholdingUIHandler != null) {
                    sendLiteMktWatchToHandler(SLBMHoldingView.slbmholdingUIHandler, token);
                } else if (SLBMHoldingDetails.holdingSLBMdetailsHandler != null) {
                    sendLiteMktWatchToHandler(SLBMHoldingDetails.holdingSLBMdetailsHandler, token);
                } else if (LatestResultFragment.latestResultHandler != null) {
                    sendLiteMktWatchToHandler(LatestResultFragment.latestResultHandler, token);
                } else if (ValuetionFragment.valuationHandler != null) {
                    sendLiteMktWatchToHandler(ValuetionFragment.valuationHandler, token);
                } else if (HoldingMenuNew.dpHoldingUIHandler != null && (GlobalClass.broadCastReg.isNormalMKt())) {
                    sendLiteMktWatchToHandler(HoldingMenuNew.dpHoldingUIHandler, token);
                } else if (LongShortTableViewFragment.longShortUIHandler != null && (GlobalClass.broadCastReg.isNormalMKt())) {
                    sendLiteMktWatchToHandler(LongShortTableViewFragment.longShortUIHandler, token);
                } else if (GlobalClass.orderBkUIHandler != null) {
                    sendLiteMktWatchToHandler(GlobalClass.orderBkUIHandler, token);
                }
                if (GlobalClass.orderBkDetailUIHandler != null) {
                    sendLiteMktWatchToHandler(GlobalClass.orderBkDetailUIHandler, token);
                }
           // }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    //region notify screens
    private void notifyHomeScreen(int msgCode, int indexCode) {
        try {
            if (GlobalClass.homeScrUiHandler != null) {
                Message msg = Message.obtain(GlobalClass.homeScrUiHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putInt("indexCode", indexCode);
                msg.setData(confMsgBundle);
                GlobalClass.homeScrUiHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            onError("Error in in notify:" + m_className, e);
        }
    }

    private void notifyMktWatchScreen(int msgCode, int scripCode, byte[] data) {
        try {
            Handler handler;
            if (GlobalClass.mktWatchUiHandler != null) {
                handler = GlobalClass.mktWatchUiHandler;
            } else if (GlobalClass.nseUiHandler != null) {
                handler = GlobalClass.nseUiHandler;
            } else if (GlobalClass.bseUiHandler != null) {
                handler = GlobalClass.bseUiHandler;
            } else if (GlobalClass.slbsUiHandler != null) {
                handler = GlobalClass.slbsUiHandler;
            } else if (GlobalClass.mktMoversUiHandler != null) {
                handler = GlobalClass.mktMoversUiHandler;
            } else {
                return;
            }
            Message msg = Message.obtain(handler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putInt(eForHandler.SCRIPCODE.name, scripCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
            msg.setData(confMsgBundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NotifyData(Handler handler,int msgCode,byte[] data){
        NotifyData(handler,msgCode,0,data);
    }

    private void NotifyData(Handler handler,int msgCode,int scripCode,byte[] data){
        try {
            if (handler!=null){
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putInt(eForHandler.SCRIPCODE.name,scripCode);
                confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }


    private void notifyMyStockScreen(int msgCode, int scripCode) {
        if (GlobalClass.myStockUiHandler != null) {
            Message msg = Message.obtain(GlobalClass.myStockUiHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt("msgCode", msgCode);
            confMsgBundle.putInt("scripCode", scripCode);
            msg.setData(confMsgBundle);
            GlobalClass.myStockUiHandler.sendMessage(msg);
        }
    }

    private void NotifyScripCode(Handler handler, int msgCode, int scripCode) {
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(StaticVariables.MSGCODE, msgCode);
                confMsgBundle.putInt(StaticVariables.SCRIPCODE, scripCode);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }


    private void sendLiteMktWatchForPositionandHolding(int scripCode) {
        try {
            if (GlobalClass.netPositionUIHandler != null) {
                sendLiteMktWatchToHandler(GlobalClass.netPositionUIHandler, scripCode);
            }
            if (GlobalClass.netPositionDetailUIHandler != null) {
                sendLiteMktWatchToHandler(GlobalClass.netPositionDetailUIHandler, scripCode);
            }
            if (GlobalClass.holdingFOUIHandler != null) {
                sendLiteMktWatchToHandler(GlobalClass.holdingFOUIHandler, scripCode);
            }
            if (GlobalClass.holdingEquityUIHandler != null) {
                sendLiteMktWatchToHandler(GlobalClass.holdingEquityUIHandler, scripCode);
            }
            if (GlobalClass.holdingFOdetailsUIHandler != null) {
                sendLiteMktWatchToHandler(GlobalClass.holdingFOdetailsUIHandler, scripCode);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void sendLiteMktWatchToHandler(Handler handler, int scripCode) {
        if (handler != null) {
            Message msg = Message.obtain(handler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.LITE_MW.value);
            confMsgBundle.putInt(eForHandler.SCRIPCODE.name, scripCode);
            msg.setData(confMsgBundle);
            handler.sendMessage(msg);
        }
    }
    //endregion
}
