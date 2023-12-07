package connection;

import android.os.Bundle;
import android.os.Message;

import java.io.IOException;

import enums.ELogicType;
import enums.eForHandler;
import enums.eMessageCode;
import fragments.simplysave.SimplysaveFragment;
import lib.TCPClientProcess;
import lib.XtremClientHandler;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 14-Aug-2017.
 */
public class SimplysaveClient extends Client implements TCPClientProcess, ReqSent {
    private static final String TAG = SimplysaveClient.class.getSimpleName();
    private ConnectionProcess m_process;

    public SimplysaveClient(ConnectionProcess process) {
        try {
            this.m_process = process;
            client = new XtremClientHandler("SimplySave", "Connect to Server", 8,
                    24, this, ELogicType.NOCOMPRESSION,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketConnected() {
        try {
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
            client = null;
            if (m_process != null) {
                m_process.serverNotAvailable();
                m_process = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void socketDisconnected() {
        try {
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
        if (excptn != null){
            excptn.printStackTrace();
        }else {
            GlobalClass.log(string);
        }

    }
    @Override
    public void createLog(String string) {
    }

    @Override
    public void reqSent(int msgCode) {
    }
    //endregion

    public void stopClient(){
        //cancelTimer();
        try {
            disconnect(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void onDataProcess(byte data[], int msgCode) {
        try {
            eMessageCode emessagecode =  eMessageCode.valueOf(msgCode);
            switch (emessagecode) {
                case SIMPLYSAVEHOME:
                    sendSimplysaveData(data);
                    break;
                case INVEST:
                    sendInvestData(data);
                    break;
                case WITHDRAW:
                    sendWithdrawData(data);
                    break;
                case BANKDETAIL:
                    sendBankData(data);
                    break;
                case PASSBOOK:
                case PASSBOOK_REF:
                    sendPassbookData(data,msgCode);
                    break;
                case PARK_EARNREQUEST:
                    sendFollioRes(data);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            GlobalClass.log(TAG," :onDataProcess: ",""+e);
        }
    }


    private void sendFollioRes(byte[] data) {
        try {
            if (SimplysaveFragment.parkAndEarnHandler != null) {
                Message msg = Message.obtain(SimplysaveFragment.parkAndEarnHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.PARK_EARNREQUEST.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                SimplysaveFragment.parkAndEarnHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendPassbookData(byte[] data,int msgCode) {
        try {
            if (GlobalClass.passbookHandler != null) {
                Message msg = Message.obtain(GlobalClass.passbookHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, msgCode);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.passbookHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBankData(byte[] data) {
        try {
            if (GlobalClass.withdrawHandler != null) {
                Message msg = Message.obtain(GlobalClass.withdrawHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.BANKDETAIL.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.withdrawHandler.sendMessage(msg);
            }
            if (GlobalClass.investHandler != null) {
                Message msg = Message.obtain(GlobalClass.investHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.BANKDETAIL.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.investHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendWithdrawData(byte[] data) {
        try {
            if (GlobalClass.withdrawHandler != null) {
                Message msg = Message.obtain(GlobalClass.withdrawHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.WITHDRAW.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.withdrawHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendInvestData(byte[] data) {
        try {
            if (GlobalClass.investHandler != null) {
                Message msg = Message.obtain(GlobalClass.investHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.INVEST.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.investHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSimplysaveData(byte[] data) {
        try {
            if (GlobalClass.summaryHandler != null) {
                Message msg = Message.obtain(GlobalClass.summaryHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.SIMPLYSAVEHOME.value);
                confMsgBundle.putByteArray(eForHandler.RESPONSE.name, data);
                msg.setData(confMsgBundle);
                GlobalClass.summaryHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}