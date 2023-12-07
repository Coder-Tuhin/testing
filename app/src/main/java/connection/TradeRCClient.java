package connection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.ventura.venturawealth.VenturaApplication;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import Structure.Response.BC.StructMaintenanceMsg;
import Structure.Response.RC.SLBMHoldingSummary;
import Structure.Response.RC.StructHoldingSummary_Pointer;
import Structure.Response.RC.StructLogOffResp;
import Structure.Response.RC.StructLoginFailure;
import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;
import Structure.Response.RC.StructMarginTradeSummary;
import Structure.Response.RC.StructMgnInfoRes;
import Structure.Response.RC.StructOCOOrderBook;
import Structure.Response.RC.StructOCOPosnBook;
import Structure.Response.RC.StructOrderBookResponse;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.RC.StructOrderResMsg;
import Structure.Response.RC.StructOrderSummary_Pointer;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeSummary_Pointer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eTagForErrorMsg;
import fragments.DashboardFragment;
import fragments.homeGroups.MktdepthFragmentRC;
import fragments.reports.BracketNetpositiondetailsFragment;
import fragments.reports.SLBMHoldingView;
import interfaces.OnAlertListener;
import models.MessageModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import structure.TypeConverter;
import utils.Connectivity;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class TradeRCClient extends WebSocketListener implements ReqSent, OnAlertListener {

    //region [veriables]
    private ConnectionProcess m_process;
    public boolean isNeedToReconnectFromBC;
    private boolean isManualDisconnected;
    private WebSocket mainWebSocket = null;
    private boolean onceInitialConnected = false;
    //private final URI serverURI;
    private String serverIP = "";
    private int serverPORT = 0;

    //endregion

    //region [Constructor]
    public TradeRCClient(ConnectionProcess process, URI serverURI) {
        //super(serverURI);
        //this.serverURI = serverURI;
        try {
            this.m_process = process;
            this.isNeedToReconnectFromBC = false;
            GlobalClass.tradeRCClient = this;

        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }
    //endregion

    //region [Override Methods]
    @Override
    public void onFailure(@NonNull WebSocket webSocket, Throwable t, Response response) {
        GlobalClass.log("RC QWS Error : " + t.getMessage() );
        if(response != null){
            GlobalClass.log("RC QWS Error Response: " + response);
        }
        onClosed(webSocket,1000,t.getMessage());
    }
    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        this.mainWebSocket = webSocket;
        try {
            GlobalClass.log("RC onOpen : " + response.toString());
            GlobalClass.isRCconnected = true;
            isManualDisconnected = false;
            onceInitialConnected = true;
            initReconnectionValues();
            if(UserSession.isTradeLogin()){
                SendDataToRCServer sendDataToRCServer = new SendDataToRCServer(this, eMessageCode.RC_RECONNECT);
                sendDataToRCServer.execute();
            }
            if (m_process != null) {
                m_process.connected();
            }
            m_process = null;

        } catch (Exception e) {
            onError("Error in " + getClass().getName(), e);
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, ByteString bytes) {
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
        if(timer == null) {
            startTimer();
        }
    }
    @Override
    public void onMessage(@NonNull WebSocket webSocket, String text) {
        GlobalClass.log("RC OnMessage : " + text);
        if(text.equalsIgnoreCase("htbt")) {
            startTimer();
        }
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        GlobalClass.log("onClosing RC_Code : " + code + " :reson: "+reason +  " : " );
        onClosed(webSocket,code,reason);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        GlobalClass.log("onClosed RC_Code : "+code +" :remote: "+reason);
        GlobalClass.isRCconnected = false;
        stopTimer();
        if(!onceInitialConnected){
            try {
                createLog("socketNotAvailable RC");
                GlobalClass.addAndroidLog(eLogType.RCConnectFailed.name, "rcip|"+ finalURL + "|" + MobileInfo.getMobileData()+"|"+reason,"");
                if (m_process != null) {
                    m_process.serverNotAvailable();
                    m_process = null;
                }else {
                    tryReconnect();
                }
            }catch (Exception e){
                onError("Error in "+ getClass().getName(),e);
            }
        }else{
            try {
                createLog("socketDisconnected RC");
                if (!isManualDisconnected){
                    tryReconnect();
                }
            } catch (Exception e) {
                onError("Error in " + getClass().getName(), e);
            }
        }
    }
    public void send(byte[] data) throws Exception{
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
    private String finalURL;

    public void connect( String ip, int port ){
        try {
            if(GlobalClass.isRCconnected) {
                disconnect(true);
            }
            //GlobalClass.log("RC IP1 : " + ip + " : " + port);
            try {
                serverIP = ip;
                serverPORT = port;
                GlobalClass.log("RC IP2 : " + ip + " : " + port);
                finalURL  = "wss://"+ip+":"+port;
                SSLContext sslContext = SSLContext.getDefault();
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory,getX509TrustManager())
                        .hostnameVerifier(getHostNameVerifier())
                        .build();
                Request request = new Request.Builder()
                        .url(finalURL)
                        //.addHeader("authorization",UserSession.getToken().getAuthHeader())
                        .build();
                client.newWebSocket(request, this);
                client.dispatcher().executorService().shutdown();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            VenturaException.Print(ex);
            //onClose(CloseFrame.NEVER_CONNECTED,"Not Connected",false);
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

    public void tryReconnect(){
        if(Connectivity.IsConnectedtoInternet(GlobalClass.latestContext)) {
            if (maxTry > 2) {
                GlobalClass.log("TradeRCClient", "Try", "maxtry > 2");
                openReconnectionDialog();
            } else {
                reConnection();
            }
        } else {
            isNeedToReconnectFromBC = true;
        }
    }
    private Timer timer;
    private int iddleCount = 0;
    private void startTimer(){
        iddleCount = 0;
        stopTimer();
        timer = new Timer();
        TimerTask newTask = new MyTimerTask();  // new instance
        timer.schedule(newTask, 20000, 20000);
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            try{
                if(mainWebSocket != null){
                    mainWebSocket.send("htbt");
                    iddleCount++;
                    GlobalClass.log("RC Iddle Count : " + iddleCount);
                    if(iddleCount == 3){
                        stopTimer();
                        onClosed(mainWebSocket,1001,"No Response for 45 seconds");
                    }
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void openReconnectionDialog(){
        isNeedToReconnectFromBC = true;
        new AlertBox(GlobalClass.latestContext,"Reconnect","Exit","Ventura Wealth have been disconnected from server.",TradeRCClient.this,"maxtry");
    }

    public void onError(String string, Exception excptn) {
        if (excptn!= null){
            excptn.printStackTrace();
        }else {
            GlobalClass.log(string);
        }
    }
    public void createLog(String string) {
        GlobalClass.log(getClass().getName(),"createLog",string + ":" + new Date());
    }
    @Override
    public void reqSent(int msgCode) {

        //GlobalClass.log("RC Msg Sent : "  +msgCode);
    }
    //endregion
    private long delay = 0;
    private int count = 1;
    private int maxTry = 0;
    private Handler handler;
    private Runnable runnable;

    public void initReconnectionValues() {
        try {
            if(cnt != null) {
                cnt.cancel(true);
                cnt = null;
            }
            delay = 0;
            count = 1;
            maxTry = 0;
            handler = null;
            runnable = null;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void reConnection() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (runnable != null){
            handler.removeCallbacks(runnable);
            runnable = null;
        }
        runnable = new Runnable() {
            public void run() {
                try {
                    if (maxTry < 3) {
                        if (count == 10) {
                            maxTry++;
                            count = 1;
                            delay = 6000;
                        } else {
                            count++;
                            delay = 6000;
                        }
                        if(cnt == null) {
                            cnt = new connect();
                        }
                        else{
                            cnt.cancel(true);
                            cnt = null;
                            cnt = new connect();
                        }
                        cnt.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable,delay);
    }

    private connect cnt = null;

    @Override
    public void onOk(String tag) {
        if(tag.equalsIgnoreCase("maxtry")){
            GlobalClass.isForceReconnect = true;
            GlobalClass.tradeBCClient.TryToReconnect();

            //initReconnectionValues();
            //reConnection();
        }
    }

    @Override
    public void onCancel(String tag) {
        if(tag.equalsIgnoreCase("maxtry")){
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.sendExitReq();
        }
    }

    private class connect extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                createLog("connecting in Reconenct: ");
                //Config config = VenturaApplication.getPreference().getConnectionConfig();
                connect(serverIP, serverPORT);

                //Config config = VenturaApplication.getPreference().getConnectionConfig();
                //connect(config.getTradeServerIP(), config.getRcServerPort());

                //reconnectBlocking();
                //mainWebSocket.recreate().connect();
            } catch (Exception e) {
                onError("Error in connect method: " + getClass().getName(), e);
                tryReconnect();
            }
            return null;
        }
    }

    private void onDataProcess(byte data[], int msgCode) {
        GlobalClass.log("RC MSG code: "+msgCode);
        try {
            eMessageCode emessagecode =  eMessageCode.valueOf(msgCode);
            switch (emessagecode) {
                case GUESTLOGIN_RCKEY_REQUEST:
                  /*boolean tradeLogin = VenturaApplication.getPreference().getSharedPrefFromTag(eConstant.TRADE_LOGIN.name,false);
                    if(tradeLogin){
                        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer(this, eMessageCode.RC_RECONNECT);
                        sendDataToRCServer.execute();
                    }
                    */
                    break;
                /*case CLIENT_AUTHENTICATION_RC:
                    handleAuthentication(data,msgCode);
                    break;*/
                case LOGIN_RC:
                case RESEARCH_FETCH_SSO_LOGIN:
                    handleLogin(data,msgCode);
                    break;
                case MPIN_REQ:
                    handleMPIN(data,msgCode);
                    break;
                case LOGIN_RC_FAILURE:
                    loginFailed(data,msgCode);
                    handleLogin(data,msgCode);
                    break;
                case MARGIN:
                case MARGIN_RC:
                    handleMargin(data,msgCode);
                    break;
                case ORDER_BOOk_RESP:
                    handleOrderBook(data,msgCode);
                    break;
                case TRADE_BOOK_RESP:
                    handleTradeBook(data,msgCode);
                    break;
                case CFD_BOOK_RESP:
                    handleCFDTradeBook(data,msgCode);
                    break;
                case BRACKET_ORDER_REPORT:
                    handleBracketOrderBook(data,msgCode);
                    break;
                case BRACKET_POSITION_REPORT:
                    handleBracketPositionBook(data,msgCode);
                    break;
                case BRACKET_ORDERDETAIL_REPORT:
                    handleBracketOrderDetailBook(data,msgCode);
                    break;
                case HOLDING_RESPONSE:
                case HOLDING_REQ:
                    handleHolding(data,msgCode);
                    break;
                case SLBM_HOLDING_REPORT:
                    handleSLBMHolding(data,msgCode);
                    break;
                case MARGINTRADE_REQ:
                    handleMarginTrade(data,msgCode);
                    break;
                case ORDERCONF:
                    handleOrderResponse(data);
                    break;
                case EXCHPLACEORDER_CASH_FORGOTPWD:
                case EXCHPLACEORDER_CASHINTRADEL:
                case PLACE_ORDER_CASH :
                case BROKERPLACEORDER_CASHINTRADEL:
                case PLACE_ORDER_CURR:
                case EXCHPLACEORDER_SLBS:
                case cMSGCD_EXCHPLACEORDER_SLBS:
                case cMSGCD_EXCHMODIFYORDER_SLBS:
                case cMSGCD_EXCHCANCELORDER_SLBS:
                case cMSGCD_EXCHKILLORDER_SLBS:
                case EXCHPLACEORDER_DERIV_MODIFY_ORDER_FNO:
                case EXCHPLACEORDER_CURR:
                case MODIFY_ORDER_CASH: {
                    StructOrderReportReplyRecord_Pointer ordPlaceEq = new StructOrderReportReplyRecord_Pointer(data);
                    //GlobalClass.log("OrdConf : " + ordPlaceEq.toString());
                    GlobalClass.getClsOrderBook().addSingleOrder(ordPlaceEq);
                    reloadOrderBook();
                }
                break;
                case EXCHMODIFYORDER_CASH :
                case EXCHMODIFYORDER_CASHINTRADEL:
                case MODIFY_ORDER_CURR:
                case BROKERMODIFYORDER_CASH_BCDELETEGROUP:
                case BROKERMODIFYORDER_CASHINTRADEL:
                case EXCHMODIFYORDER_SLBS:
                case EXCHCANCELORDER_DERIV:
                case EXCHCANCELORDER_CURR:
                case BROKERCANCELORDER_DERIV:
                case EXCHMODIFYORDER_DERIV:
                case EXCHMODIFYORDER_CURR:
                case CANCEL_ORDER_CASH:
                case EXCHCANCELORDER_CASH:
                case EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA:
                case BROKERCANCELORDER_CASH:
                case CANCEL_ORDER_CURR:
                case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP:
                case EXCHCANCELORDER_SLBS: {
                    StructOrderReportReplyRecord_Pointer ordPlaceEq = new StructOrderReportReplyRecord_Pointer(data);
                    GlobalClass.getClsOrderBook().upDateOrderBook(ordPlaceEq);
                    reloadOrderBook();
                }
                break;
                case EXCHTRADEORDER_CASHDERIV:
                {
                    try {
                        StructTradeReportReplyRecord_Pointer tradeResp = new StructTradeReportReplyRecord_Pointer(data);
                        boolean isfresh = GlobalClass.getClsTradeBook().addTradeBook(tradeResp, true);
                        //need to refetch order book notifyOrderBook_Refetch();
                        if (isfresh) {
                            try {
                                if (VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.BEEP_SOUND.name, true)) {
                                    GlobalClass.homeActivity.notifySystemSound();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String conf = tradeResp.getConfMsg();
                            handleOrderConf(conf, tradeResp.exchTradeTime.getValue(),true);
                            reloadOrderBook();
                            reloadTradeBook();
                        }
                        if(BracketNetpositiondetailsFragment.bracketnetPositionDetailUIHandler != null){
                            GlobalClass.sendMsg(BracketNetpositiondetailsFragment.bracketnetPositionDetailUIHandler,eMessageCode.TRADE_BOOK_RESP.value);
                        }
                    }
                    catch (Exception ex){
                        GlobalClass.showAlertDialog(Constants.ERR_MSG);
                    }
                }
                break;
                case SLTRIGGERED_CASH:
                case SLTRIGGERED_DERIV: {
                    StructOrderReportReplyRecord_Pointer ordPlaceEq = new StructOrderReportReplyRecord_Pointer(data);
                    GlobalClass.getClsOrderBook().upDateSLTriggeredOrderBook(ordPlaceEq);
                    reloadOrderBook();
                }
                break;
                case EXCHKILLORDER_BSE: {
                    StructOrderReportReplyRecord_Pointer ordPlaceEq = new StructOrderReportReplyRecord_Pointer(data);
                    GlobalClass.getClsOrderBook().upDateBSEKillOrderBook(ordPlaceEq);
                    reloadOrderBook();
                }
                break;
                case EXCHKILLORDER_NSE:
                case EXCHKILLORDER_DERIV: {
                    StructOrderReportReplyRecord_Pointer ordPlaceEq = new StructOrderReportReplyRecord_Pointer(data);
                    GlobalClass.getClsOrderBook().upDateNSEnDerivKillOrderBook(ordPlaceEq);
                    reloadOrderBook();
                }
                break;
                case POSITION_CONVERSION:{
                    StructTradeReportReplyRecord_Pointer tradeResp = new StructTradeReportReplyRecord_Pointer(data);
                    GlobalClass.getClsTradeBook().updateOrderType(tradeResp);
                    handlePositionConversion();
                }
                break;
                case CHGPWD:
                    handleChangePwd(data,msgCode);
                    break;
                case ORDERCONF_NEW:
                    handleOrderConf(data);
                    break;
                case LOGOFFRESP:
                    handleLogOffResp(data);
                    break;
                case ERROR_MSG:
                    handleErrorMsg(data);
                    break;
                default:
                    break;

            }
        } catch (Exception e) {
            onError("Error in in data receive:" + getClass().getName(), e);
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
                    }
                    new AlertBox(GlobalClass.latestContext, "", "OK", logOffResp.errMsg.getValue(), logOffResp.isExit.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }
    private void handleLogOffResp(final byte[] data) {
        Handler mainHandler = new Handler(GlobalClass.latestContext.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    GlobalClass.tradeBCClient.disconnect(true);
                    GlobalClass.tradeRCClient.disconnect(true);
                    StructLogOffResp logOffResp = new StructLogOffResp(data);
                    new AlertBox(GlobalClass.latestContext, "", "OK", logOffResp.msg.getValue(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }
    private void loginFailed(final byte[] data, final int msgCode) {

        final StructLoginFailure loginFailure = new StructLoginFailure(data);
        GlobalClass.log("RC loginFailed : " + loginFailure.toString());
        final boolean exit = loginFailure.errorTag.getValue()==eTagForErrorMsg.RELOGIN.value ;
        if(exit){
            try {
                GlobalClass.tradeBCClient.disconnect(true);
                GlobalClass.tradeRCClient.disconnect(true);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            new AlertBox(GlobalClass.latestContext, "", "Exit", loginFailure.msg.getValue(), true);
            //openReconnectionDialog();
        }/*
        else {

            Handler mainHandler = new Handler(GlobalClass.latestContext.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (loginFailure.msg.getValue().toLowerCase().contains("account locked")) {
                            String msgToShow = loginFailure.msg.getValue() + "\n\n" + "We request you kindly reset your password through forgot password.";
                            sharedPref.setTradeDetails(null);
                            new AlertBox(GlobalClass.latestContext, "Forgot Password", "Cancel", msgToShow, TradeRCClient.this, "acclock");
                        } else {
                            new AlertBox(GlobalClass.latestContext, "", "OK", loginFailure.msg.getValue(), exit);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } // This is your code
            };
            mainHandler.post(myRunnable);
        }*/
    }

    private void handlePositionConversion() {
        if (GlobalClass.tradeBKDetailUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.tradeBKDetailUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.POSITION_CONVERSION.value);
            msg.setData(confMsgBundle);
            GlobalClass.tradeBKDetailUIHandler.sendMessage(msg);
        }
    }
    private void handleChangePwd(byte[] data, int msgCode) {
        if (GlobalClass.changePwdHandler != null) {
            Message msg = Message.obtain(GlobalClass.changePwdHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
            msg.setData(confMsgBundle);
            GlobalClass.changePwdHandler.sendMessage(msg);
        }
        if (GlobalClass.changePwdHandlerActivity != null) {
            Message msg = Message.obtain(GlobalClass.changePwdHandlerActivity);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name, data);
            msg.setData(confMsgBundle);
            GlobalClass.changePwdHandlerActivity.sendMessage(msg);
        }
    }
    private void handleOrderConf(String conf, int time,boolean isTradeConf) {
      //  GlobalClass.log("DialogMessageTest3","   "+conf);
        MessageModel _messageModel = new MessageModel(conf, DateUtil.NToT(time));
        GlobalClass.addMessages(_messageModel,isTradeConf);
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
    private void handleOrderResponse(byte[] data) {
        try {
            StructOrderResMsg respMsg = new StructOrderResMsg(data);
          //  GlobalClass.log("DialogMessageTest2","   "+respMsg.msg.getValue());
            MessageModel _messageModel = new MessageModel(respMsg.msg.getValue(), DateUtil.NToT(respMsg.time.getValue()));
            GlobalClass.addMessages(_messageModel,false);
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void handleMarginTrade(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructMarginTradeSummary smts = new StructMarginTradeSummary(data);
        if (smts == null) return;
        GlobalClass.getClsMarginHolding().setMaginTradeData(smts);
        if (GlobalClass.marginTradeHandler != null) {
            Message msg = Message.obtain(GlobalClass.marginTradeHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            msg.setData(confMsgBundle);
            GlobalClass.marginTradeHandler.sendMessage(msg);
        }
    }

    private void handleSLBMHolding(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        SLBMHoldingSummary holdingSumm = new SLBMHoldingSummary(data);
        if(holdingSumm != null){
            GlobalClass.getClsMarginHolding().setSLBMHoldingData(holdingSumm);
            if (holdingSumm.complete.getValue() == 1) {
                if (SLBMHoldingView.slbmholdingUIHandler != null) {
                    Message msg = Message.obtain(SLBMHoldingView.slbmholdingUIHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                    msg.setData(confMsgBundle);
                    SLBMHoldingView.slbmholdingUIHandler.sendMessage(msg);
                }
            }
        }
    }
    private void handleHolding(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructHoldingSummary_Pointer holdingSumm = new StructHoldingSummary_Pointer(data);
        if(holdingSumm != null){
            GlobalClass.getClsMarginHolding().setHoldingData(holdingSumm);
            if (holdingSumm.complete.getValue() == 1) {
                if (GlobalClass.holdingEquityUIHandler != null) {
                    Message msg = Message.obtain(GlobalClass.holdingEquityUIHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                    msg.setData(confMsgBundle);
                    GlobalClass.holdingEquityUIHandler.sendMessage(msg);
                }
                handleHoldingFO(msgCode);
            }
        }
    }
    private void handleHoldingFO(int msgCode){

        if (GlobalClass.holdingFOUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.holdingFOUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            msg.setData(confMsgBundle);
            GlobalClass.holdingFOUIHandler.sendMessage(msg);
        }
    }

    private void handleBracketOrderBook(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructOCOOrderBook trdBk = new StructOCOOrderBook(data);
        if(trdBk != null){
            GlobalClass.getClsBracketOrderBook().AddOrder(trdBk);
            if (trdBk.downloadComplete.getValue() == 1) {
                if (GlobalClass.bracketOrderBkUIHandler != null) {
                    Message msg = Message.obtain(GlobalClass.bracketOrderBkUIHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                    msg.setData(confMsgBundle);
                    GlobalClass.bracketOrderBkUIHandler.sendMessage(msg);
                }
            }
        }
    }

    private void handleBracketOrderDetailBook(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructOrderBookResponse orderBookResponseBk = new StructOrderBookResponse(data);
        if(orderBookResponseBk != null){
            GlobalClass.getClsBracketPositionBook().AddOrder(orderBookResponseBk);
        }
    }

    private void handleBracketPositionBook(byte[] data, int msgCode) {

        GlobalClass.dismissdialog();
        StructOCOPosnBook trdBk = new StructOCOPosnBook(data);
        if(trdBk != null){
            GlobalClass.getClsBracketPositionBook().AddOrder(trdBk);
            if (trdBk.downloadComplete.getValue() == 1) {

                if (GlobalClass.bracketPositionBkUIHandler != null) {
                    Message msg = Message.obtain(GlobalClass.bracketPositionBkUIHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                    msg.setData(confMsgBundle);
                    GlobalClass.bracketPositionBkUIHandler.sendMessage(msg);
                }
            }
        }
    }
    private void handleCFDTradeBook(byte[] data, int msgCode) {

        GlobalClass.dismissdialog();
        StructTradeSummary_Pointer trdBk = new StructTradeSummary_Pointer(data);
        if(trdBk != null){
            GlobalClass.getClsCFDBook().AddTrade(trdBk);
            if (trdBk.complete.getValue() == 1) {
                handleHoldingFO(msgCode);
            }
        }
    }
    private void handleTradeBook(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructTradeSummary_Pointer trdBk = new StructTradeSummary_Pointer(data);
        if(trdBk != null){
            GlobalClass.getClsTradeBook().AddTrade(trdBk);
            if (trdBk.complete.getValue() == 1) {
                reloadTradeBook();
                handleHoldingFO(msgCode);
                if (GlobalClass.orderBkUIHandler != null) {
                    Message msg = Message.obtain(GlobalClass.orderBkUIHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.ORDER_BOOk_RESP.value);
                    msg.setData(confMsgBundle);
                    GlobalClass.orderBkUIHandler.sendMessage(msg);
                }
            }
        }

    }
    private void reloadTradeBook(){
        if (GlobalClass.tradeBKUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.tradeBKUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.TRADE_BOOK_RESP.value);
            msg.setData(confMsgBundle);
            GlobalClass.tradeBKUIHandler.sendMessage(msg);
        }
        else if(DashboardFragment.reportHandler != null){
            Message msg = Message.obtain(DashboardFragment.reportHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.TRADE_BOOK_RESP.value);
            msg.setData(confMsgBundle);
            DashboardFragment.reportHandler.sendMessage(msg);
        }
        if(GlobalClass.netPositionUIHandler != null){
            Message msg = Message.obtain(GlobalClass.netPositionUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.TRADE_BOOK_RESP.value);
            msg.setData(confMsgBundle);
            GlobalClass.netPositionUIHandler.sendMessage(msg);
        }
        if (MktdepthFragmentRC.mktDepthUiHandler != null){
            GlobalClass.sendMsg(MktdepthFragmentRC.mktDepthUiHandler,eMessageCode.REFRESHDEPTH.value);
        }

    }
    private void handleOrderBook(byte[] data, int msgCode) {
        GlobalClass.dismissdialog();
        StructOrderSummary_Pointer ordBk = new StructOrderSummary_Pointer(data);
        if(ordBk != null){
            GlobalClass.getClsOrderBook().AddOrder(ordBk);
            if (ordBk.complete.getValue() == 1){
                reloadOrderBook();
            }
        }
    }
    private void reloadOrderBook(){
        GlobalClass.getClsMarginHolding().clearMargin();
        if (GlobalClass.orderBkUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.orderBkUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.ORDER_BOOk_RESP.value);
            msg.setData(confMsgBundle);
            GlobalClass.orderBkUIHandler.sendMessage(msg);
        } else if(DashboardFragment.reportHandler != null){
            Message msg = Message.obtain(DashboardFragment.reportHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.ORDER_BOOk_RESP.value);
            msg.setData(confMsgBundle);
            DashboardFragment.reportHandler.sendMessage(msg);
        }
        if (MktdepthFragmentRC.mktDepthUiHandler != null){
            GlobalClass.sendMsg(MktdepthFragmentRC.mktDepthUiHandler,eMessageCode.REFRESHDEPTH.value);
        }
    }

    private void handleMargin(byte[] data, int msgCode) {
        if(msgCode == eMessageCode.MARGIN_RC.value) {
            StructMgnInfoRes marginData = new StructMgnInfoRes(data);
            if (marginData != null) {
                GlobalClass.getClsMarginHolding().setMarginDetail(marginData);
            }
        }else{
            StructMarginReportReplyRecord_Pointer marginData = new StructMarginReportReplyRecord_Pointer(data);
            if (marginData != null) {
                GlobalClass.getClsMarginHolding().setMarginDetail(marginData);
            }
        }
        if (GlobalClass.marginUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.marginUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.MARGIN.value);
            msg.setData(confMsgBundle);
            GlobalClass.marginUIHandler.sendMessage(msg);
        }
        else if (GlobalClass.tradeBKDetailUIHandler != null) {
            Message msg = Message.obtain(GlobalClass.tradeBKDetailUIHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.MARGIN.value);
            msg.setData(confMsgBundle);
            GlobalClass.tradeBKDetailUIHandler.sendMessage(msg);
        }
        else if (DashboardFragment.reportHandler != null) {
            Message msg = Message.obtain(DashboardFragment.reportHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.MARGIN.value);
            msg.setData(confMsgBundle);
            DashboardFragment.reportHandler.sendMessage(msg);
        }

    }

    private void handleAuthentication(final byte[] data, int msgCode){
        Handler handler = null;
        if (GlobalClass.RCAuthHandler != null){
            handler = GlobalClass.RCAuthHandler;
        }else if (GlobalClass.loginHandler != null){
            handler = GlobalClass.loginHandler;
        }
        if (handler != null) {
            Message msg = Message.obtain(handler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name,data);
            msg.setData(confMsgBundle);
            handler.sendMessage(msg);
        }
    }

    private void handleLogin(final byte[] data, int msgCode){
        if (GlobalClass.tradeLoginHandler != null) {
            Message msg = Message.obtain(GlobalClass.tradeLoginHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name,data);
            msg.setData(confMsgBundle);
            GlobalClass.tradeLoginHandler.sendMessage(msg);
        }
        if (GlobalClass.changePwdHandlerActivity != null) {
            Message msg = Message.obtain(GlobalClass.changePwdHandlerActivity);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name,data);
            msg.setData(confMsgBundle);
            GlobalClass.changePwdHandlerActivity.sendMessage(msg);
        }
    }

    private void handleMPIN(final byte[] data, int msgCode){
        Handler handler = GlobalClass.tradeLoginHandler;
        if (handler == null){
            handler = GlobalClass.changePwdHandler;
        }
        if (handler != null) {
            Message msg = Message.obtain(handler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
            confMsgBundle.putByteArray(eForHandler.RESDATA.name,data);
            msg.setData(confMsgBundle);
            handler.sendMessage(msg);
        }
    }
    //endregion
}