package connection;

import android.content.Context;
import android.os.AsyncTask;

import Structure.Request.BC.StructCustomDialogRequest;
import Structure.Request.RC.CancelOrderReq_CASH_Pointer;
import Structure.Request.RC.CancelOrderReq_FNO_Pointer;
import Structure.Request.RC.CancelOrderReq_SLBM_Pointer;
import Structure.Request.RC.ClientChgPwdReq_Pointer;
import Structure.Request.RC.ClientLoginReq_Pointer;
import Structure.Request.RC.MarginHoldingReq;
import Structure.Request.RC.ModifyOrderReq_CASH_Pointer;
import Structure.Request.RC.ModifyOrderReq_FNO_Pointer;
import Structure.Request.RC.ModifyOrderReq_SLBM_Pointer;
import Structure.Request.RC.PlaceOrderReq_CASH_Pointer;
import Structure.Request.RC.PlaceOrderReq_FNO_Pointer;
import Structure.Request.RC.PlaceOrderReq_SLBM_Pointer;
import Structure.Request.RC.PositionConversion_Pointer;
import Structure.Request.RC.RCClientRegistration;
import Structure.Request.RC.ReportReq_Pointer;
import Structure.Request.RC.StructChangePwdReq;
import Structure.Request.RC.StructOCOOrder;
import Structure.Request.RC.StructPINGeneration;
import Structure.Response.BC.StructCustomDialog;
import Structure.Response.BC.StructRegistrationResp;
import Structure.Response.RC.ClientAuthenticationReq;
import enums.eMessageCode;
import enums.eSocketClient;
import utils.Connectivity;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;

/**
 * Created by xtremsoft on 11/22/16.
 */
public class SendDataToRCServer extends AsyncTask<Object, Void, String>  {

    private static final String tag = SendDataToRCServer.class.getSimpleName();
    private Context context;
    private ReqSent m_req;
    private byte m_data[];
    private eMessageCode m_msgCode;

    private final String m_className = getClass().getName();

    public SendDataToRCServer() {
        context = GlobalClass.latestContext;
    }

    public SendDataToRCServer(ReqSent req) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
    }

    public SendDataToRCServer(ReqSent req, eMessageCode emsgcode) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
        this.m_msgCode = emsgcode;
    }

    public SendDataToRCServer(ReqSent req, eMessageCode msgCode, byte data[], boolean isProgress) {
        this.context = GlobalClass.latestContext;
        this.m_req = req;
        this.m_msgCode = msgCode;
        this.m_data = data;
    }


    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            setDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            switch (m_msgCode) {
                case RC_RECONNECT:
                    sendRegistrationRequest();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDialog() {
        //show snackbar
        //GlobalClass.commonApi.showdialog(GlobalClass.latestContext);
    }

    private void dismissDialog() {
        // dismiss snackbar
        //GlobalClass.commonApi.dismissdialog();
    }
    private void sendRegistrationRequest(){
        try {
            RCClientRegistration registration = new RCClientRegistration();
            registration.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            registration.iMEI.setValue(MobileInfo.getDeviceID(context));
            registration.isForceReconnect.setValue(GlobalClass.isForceReconnect?1:0);
            m_msgCode = eMessageCode.RC_RECONNECT;
            m_data = registration.data.getByteArr((short)m_msgCode.value);
            sendData(m_data);
            m_data = null;
            GlobalClass.isForceReconnect = false;
            if(GlobalClass.listForRCRequestOnConnect.size()>0){
                for(int i=0;i<GlobalClass.listForRCRequestOnConnect.size();i++){
                    byte[] data = GlobalClass.listForRCRequestOnConnect.get(i);
                    sendData(data);
                }
                GlobalClass.listForRCRequestOnConnect.clear();
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in sending sendRegistrationRequest request:"+ m_className, e);
        }
    }

    public void sendChangePassreq(ClientChgPwdReq_Pointer req,eMessageCode mcode) {
        try{
            m_msgCode = mcode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:"+ m_className, e);
        }
    }

    private void sendData(byte data[]) {
        try {
            if (Connectivity.IsConnectedtoInternet(context) && GlobalClass.isRCconnected) {
                GlobalClass.tradeRCClient.send(data);
                if (m_req != null) m_req.reqSent(m_msgCode.value);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    public void sendLoginRequest(ClientLoginReq_Pointer req) {
        try{
            m_msgCode = eMessageCode.RESEARCH_FETCH_SSO_LOGIN;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMPINRequest(StructPINGeneration req) {
        try{
            m_msgCode = eMessageCode.MPIN_REQ;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHoldingReq() {
        try{
            MarginHoldingReq req = new MarginHoldingReq();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.HOLDING_REQ;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSLBMHoldingReq() {
        try{
            MarginHoldingReq req = new MarginHoldingReq();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.SLBM_HOLDING_REPORT;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMarginTradeReq() {
        try{
            MarginHoldingReq req = new MarginHoldingReq();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.MARGINTRADE_REQ;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMarginReq() {
        try{
            MarginHoldingReq req = new MarginHoldingReq();
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            m_msgCode = eMessageCode.MARGIN_NEW;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:"+ m_className, e);
        }
    }
    public void sendOrderTradeReq(eMessageCode msgCode) {
        try{
            ReportReq_Pointer req = new ReportReq_Pointer();
            //req.clientCode.setValue("99986897");
            req.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            int startTime = (int) DateUtil.DTToN(DateUtil.getCurrentDate() + " 09:00:00");//(RCConnection.systemDateTimeFromLoginResp - 28855 - 365) * 86400;
            int endTime = (int) (DateUtil.CurrentTimeToN() + 86400000);//(RCConnection.systemDateTimeFromLoginResp - 28855 - 365 + 1) * 86400;
            req.startTime.setValue(startTime);
            req.endTime.setValue(endTime);
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendSearchScripReq request:"+ m_className, e);
        }
    }
    public void sendPlaceOrderReq_CASH(PlaceOrderReq_CASH_Pointer req){

        try {
            m_msgCode = eMessageCode.PLACE_ORDER_CASH;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendPlaceOrderReq_FNOCURR(PlaceOrderReq_FNO_Pointer req,eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendPlaceOrderReq_SLBM(PlaceOrderReq_SLBM_Pointer req, eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendModifyOrderReq_CASH(ModifyOrderReq_CASH_Pointer req){

        try {
            m_msgCode = eMessageCode.MODIFY_ORDER_CASH;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendModifyOrderReq_FNOCURR(ModifyOrderReq_FNO_Pointer req,eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendModifyOrderReq_SLBM(ModifyOrderReq_SLBM_Pointer req, eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendCancelOrderReq_CASH(CancelOrderReq_CASH_Pointer req){

        try {
            m_msgCode = eMessageCode.CANCEL_ORDER_CASH;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendCancelOrderReq_FNOCURR(CancelOrderReq_FNO_Pointer req,eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendCancelOrderReq_SLBM(CancelOrderReq_SLBM_Pointer req, eMessageCode msgCode){

        try {
            m_msgCode = msgCode;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }

    public void sendPositionConversionReq(PositionConversion_Pointer req){
        try {
            m_msgCode = eMessageCode.POSITION_CONVERSION;
            m_data = req.data.getByteArr((short)m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }

    public void sendPlaceBracketOrderReq(StructOCOOrder ocoOrder) {
        try {
            m_msgCode = eMessageCode.OCOPLACEREQ;
            m_data = ocoOrder.getByteArrForInnerStruct((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendModifyBracketOrderReq(StructOCOOrder ocoOrder) {
        try {
            m_msgCode = eMessageCode.OCOMODIFYREQ;
            m_data = ocoOrder.getByteArrForInnerStruct((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }
    public void sendEXITBracketOrderReq(StructOCOOrder ocoOrder) {
        try {
            m_msgCode = eMessageCode.OCOEXITREQ;
            m_data = ocoOrder.getByteArrForInnerStruct((short) m_msgCode.value);
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.onError("Error in sending sendPlaceOrderReq_CASH request:"+ m_className, e);
        }
    }

}