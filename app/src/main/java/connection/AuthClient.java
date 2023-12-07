package connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ventura.venturawealth.VenturaApplication;

import java.io.IOException;

import Structure.Response.AuthRelated.FCMres;
import Structure.Response.AuthRelated.GuestLoginResponse;
import Structure.Response.AuthRelated.ClientLoginResponse;
import Structure.Response.AuthRelated.OpenAccountResponse;
import Structure.Response.AuthRelated.StateResponse;
import enums.ELogicType;
import enums.eForHandler;
import fragments.sso.NonSSO_Login;
import fragments.sso.Sso_setPinFragmnet;
import fragments.sso.Sso_validatePin;
import lib.TCPClientProcess;
import lib.XtremClientHandler;
import enums.eMessageCode;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;
import utils.VenturaException;

import static enums.eMessageCode.CLIENT_LOGIN_WITH2FACTOR;
import static enums.eMessageCode.GUESTLOGIN_RCKEY_REQUEST;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class AuthClient extends Client implements TCPClientProcess, ReqSent {

    private ConnectionProcess m_process;
    public AuthClient(ConnectionProcess process) {
        try {
            this.m_process = process;
            client = new XtremClientHandler("AuthClient", "Connect to Server",
                    8, 24, this, ELogicType.NOCOMPRESSION, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketConnected() {
        try {
            GlobalClass.log("Auth client connected");
            if (m_process != null) {
                m_process.connected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void socketNotAvailable() {
        try {
            GlobalClass.log("Auth client socketNotAvailable");
            client = null;
            if (m_process != null) {
                m_process.serverNotAvailable();
                m_process = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketDisconnected() {
        try {
            GlobalClass.log("Auth client socketDisconnected");
            client = null;
            isManualDisconnected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dataSendingFailed(byte[] data) {
    }

    @Override
    public void onDataReceived(byte[] bytes, int i) {
        onDataProcess(bytes, i);
    }

    @Override
    public void dataSent(byte[] bytes) {

    }

    @Override
    public void onError(String string, Exception excptn) {
        if (excptn != null) {
            excptn.printStackTrace();
        } else {
            GlobalClass.log("AuthException", " :: " + string);
        }
    }

    @Override
    public void createLog(String string) {
        //  GlobalClass.log(TAG," :createLog: ","   "+string);
    }

    @Override
    public void reqSent(int msgCode) {
    }
    //endregion

    //region [Public Methods]
    private void connect() {
        try {
            GlobalClass.authClient = new AuthClient(null);
            GlobalClass.authClient.connect(ObjectHolder.connconfig.getAuthIP(), ObjectHolder.connconfig.getAuthServerPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopClient() {
        try {
            disconnect(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDataProcess(byte data[], int msgCode) {
        try {
            eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
            //GlobalClass.reqResHandler.deletefromQueue(emessagecode);
            switch (emessagecode) {
                case LOGIN_AUTH_RESPONSE:
                case CLIENT_LOGIN_WITH2FACTOR:
                case OTP_REQUEST:
                case GUESTLOGIN_RCKEY_REQUEST:
                    handleLogin(data, msgCode);
                    break;
                case FCM_REQUEST:
                    handleFCM(data, msgCode);
                    break;
                case GET_SERVER_SECONDIP:
                    SendData(GlobalClass.tradeLoginHandler, data, msgCode);
                    break;
                case GET_SERVER_SECONDIP_BCAST:
                    //SendData(SplashActivity.splashScreenHandler, data, msgCode);
                    SendData(Sso_validatePin.validatePinHandler, data, msgCode);
                    SendData(Sso_setPinFragmnet.setPinHandler, data, msgCode);
                    SendData(NonSSO_Login.nonSSOHandler, data, msgCode);

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handleOpenAccount(byte[] data, int msgCode) {
        try {
            OpenAccountResponse openAccountResponse = new OpenAccountResponse(data);
            if (GlobalClass.loginHandler != null) {
                Message msg = Message.obtain(GlobalClass.loginHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putSerializable(eForHandler.RESPONSE.name, openAccountResponse);
                msg.setData(confMsgBundle);
                GlobalClass.loginHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleStatelist(byte[] data, int msgCode) {
        try {
            StateResponse stateResponse = new StateResponse(data);
            if (GlobalClass.loginHandler != null) {
                Message msg = Message.obtain(GlobalClass.loginHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putSerializable(eForHandler.RESPONSE.name, stateResponse);
                msg.setData(confMsgBundle);
                GlobalClass.loginHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleFCM(byte[] data, int msgcode) {
        try {
            FCMres regResp = new FCMres(data);
            if (GlobalClass.loginHandler != null) {
                Message msg = Message.obtain(GlobalClass.loginHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgcode);
                confMsgBundle.putSerializable(eForHandler.RESPONSE.name, regResp);
                msg.setData(confMsgBundle);
                GlobalClass.loginHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(byte[] data, int msgcode) {
        try {
            if (CLIENT_LOGIN_WITH2FACTOR.value == msgcode){
                ClientLoginResponse clientLoginResponse = new ClientLoginResponse(data);
                GlobalClass.log("Login : " + clientLoginResponse.toString());
                UserSession.setClientResponse(clientLoginResponse);
                ObjectHolder.connconfig.setbCastServerIP(UserSession.getClientResponse().getBCastDomainName());
                ObjectHolder.connconfig.setTradeServerIP(UserSession.getClientResponse().getTradeDomainName());
                ObjectHolder.connconfig.setWealthServerIP(UserSession.getClientResponse().getWealthDomainName());

                ObjectHolder.connconfig.setBcServerPort(UserSession.getClientResponse().intPortBC.getValue());
                ObjectHolder.connconfig.setRcServerPort(UserSession.getClientResponse().intPortRC.getValue());
                ObjectHolder.connconfig.setWealthServerPort(UserSession.getClientResponse().intPortWealth.getValue());
                ObjectHolder.connconfig.setSearchEngineIP(UserSession.getClientResponse().searchIP.getValue());
                ObjectHolder.connconfig.setSearchEngineServerPort(UserSession.getClientResponse().searchPort.getValue());

                VenturaApplication.getPreference().setConnectionConfig(ObjectHolder.connconfig);

            }else if (GUESTLOGIN_RCKEY_REQUEST.value == msgcode){
                GuestLoginResponse guestL = new GuestLoginResponse(data);
                GlobalClass.log("GUEST : " + guestL.toString());
                UserSession.setGuestResponse(guestL);
            }
            GlobalClass.oneTimeTryForHoldingDetail = true;
            SendData(Sso_validatePin.validatePinHandler, data, msgcode);
            SendData(Sso_setPinFragmnet.setPinHandler, data, msgcode);
            SendData(NonSSO_Login.nonSSOHandler, data, msgcode);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }


    private void SendData(Handler handler, byte[] data, int msgcode) {
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgcode);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }


}
