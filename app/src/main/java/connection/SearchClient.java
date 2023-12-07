package connection;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.os.Bundle;
import android.os.Message;

import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import Structure.Response.Scrip.SearchScripResp;
import Structure.Response.Scrip.StructSearchScrip;
import enums.eLogType;
import enums.eMessageCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import structure.TypeConverter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.VenturaException;
import wealth.VenturaServerConnect;

public class SearchClient extends WebSocketListener implements ReqSent {

    //region [veriables]
    private final String m_className = getClass().getName();
    private ConnectionProcess m_process;
    public boolean IsBroadcastConnected = false;
    private boolean isManualDisconnected;
    private boolean onceConnected = false;
    private WebSocket mainWebSocket = null;
    private final URI serverURI;
    //endregion

    //region [Constructor]
    public SearchClient(ConnectionProcess process, URI serverURI) {
        //super(serverURI);
        this.serverURI = serverURI;
        try {
            GlobalClass.log("Search URL : " + serverURI.toString());
            this.m_process = process;
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
        GlobalClass.log("Search QWS Error : " + t.getMessage());
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
            GlobalClass.log("Search onConnected : "+isManualDisconnected+" : " + response.toString() + " : "+webSocket.toString());
            IsBroadcastConnected = true;
            stopReconnectTimer();
            initReconnectionValues();
            GlobalClass.dismissdialog();
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
        if(htbttimer == null) {
            startTimer();
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        GlobalClass.log("Search str : " +text);
        if(text.equalsIgnoreCase("htbt")) {
            startTimer();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        GlobalClass.log("onClosing Search_Code : " + code + " :reson: "+reason +  " : " );
        onClosed(webSocket,code,reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        GlobalClass.log("Search_onClose : " + " :Remote " +reason + " : " +isManualDisconnected);
        IsBroadcastConnected = false;
        stopTimer();
        if(!onceConnected){
            GlobalClass.log("VW Search client socketNotAvailable");
            GlobalClass.addAndroidLog(eLogType.SearchConnectFailed.name, "ip|"+ serverURI.toString() + "|" + MobileInfo.getMobileData(),"");
            if (m_process != null) {
                m_process.serverNotAvailable();
                m_process = null;
            }else {
                if (!isManualDisconnected) {
                    TryToReconnect();
                }
            }
        } else{
            try {
                GlobalClass.log("VW Search client socketDisconnected : "+ isManualDisconnected);
                if (!isManualDisconnected) {
                    TryToReconnect();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
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
                GlobalClass.log("Search_IP1 : " + serverURI.toString());
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
    public void TryToReconnect() {
        GlobalClass.log("VW Search TryToReconnect : " );
        stopReconnectTimer();
        if(!IsBroadcastConnected) {

            reconnectTimer = new Timer();
            ReconnectTimerTask newTask = new ReconnectTimerTask();  // new instance
            reconnectTimer.schedule(newTask, delay);
        }
    }

    private void stopReconnectTimer(){
        if(reconnectTimer != null){
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
    }
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
                            GlobalClass.log("RECONNECT..Search.",  " Count: " + count + " Delay: " + delay);
                        } else {
                            GlobalClass.dismissdialog();
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
            GlobalClass.log("Search MyTimerTask Craeted : " + new Date() + " : " + id);
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

    private void initReconnectionValues() {
        delay = 0;
        count = 0;
        //MaxReconnectionTry = 0;
    }
    private void onDataProcess(byte data[], int msgCode) {
        try {
            //GlobalClass.log("BC MSGCode: " + msgCode);
            eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
            switch (emessagecode) {

                case SEARCHSCRIPT:
                    setSearchScrip(data, msgCode);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            GlobalClass.log("Error in data received : " + msgCode);
            onError("Error in data receive:"+msgCode+" : " + m_className, e);
        }
    }
    public StructSearchScrip sglrfinal = null;
    private void setSearchScrip(byte data[], int msgcC) {
        try {
            if(sglrfinal == null){
                sglrfinal = new StructSearchScrip();
                sglrfinal.finalList = new ArrayList<>();
            }
            StructSearchScrip sglr = new StructSearchScrip(data);
            sglrfinal.finalList.addAll(sglr.getSearchScripList());
            //notify home screen...
            if (GlobalClass.searchScripUIHandler != null && sglr.downloadComplete.getValue() == 1) {
                Message msg = Message.obtain(GlobalClass.searchScripUIHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgcC);
                confMsgBundle.putSerializable("struct", sglrfinal);
                msg.setData(confMsgBundle);
                GlobalClass.searchScripUIHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    //endregion
}
