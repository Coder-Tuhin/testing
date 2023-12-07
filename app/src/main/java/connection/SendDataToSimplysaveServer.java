package connection;

import android.content.Context;
import android.os.AsyncTask;

import Structure.simplysave.bankReq;
import Structure.simplysave.homeDetaiireq;
import Structure.simplysave.investReq;
import Structure.simplysave.passbookReq;
import Structure.simplysave.passbookrefdetail;
import Structure.simplysave.withdrawReq;
import enums.eMessageCode;
import utils.Connectivity;
import utils.Constants;
import utils.GlobalClass;
import utils.VenturaException;
import view.AlertBox;

/**
 * Created by XTREMSOFT on 18-Aug-2017.
 */
public class SendDataToSimplysaveServer extends AsyncTask<Object, Void, String> {
    private static final String tag = SendDataToSimplysaveServer.class.getSimpleName();
    private Context context;
    private byte m_data[];
    eMessageCode m_msgCode;
    private  final String m_className = getClass().getName();

    public SendDataToSimplysaveServer() {
        context = GlobalClass.latestContext;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            sendData(m_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendHomeDetails(homeDetaiireq homeDetaiireq){
        try {
            m_msgCode = eMessageCode.SIMPLYSAVEHOME;
            m_data = homeDetaiireq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendInvestDetail(investReq investReq){
        try {
            m_msgCode = eMessageCode.INVEST;
            m_data = investReq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendPassbookDetail(passbookReq passbookReq){
        try {
            m_msgCode = eMessageCode.PASSBOOK;
            m_data = passbookReq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bankDetail(bankReq bankReq){
        try {
            m_msgCode = eMessageCode.BANKDETAIL;
            m_data = bankReq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendWithDrawDetail(withdrawReq withDrawReq){
        try {
            m_msgCode = eMessageCode.WITHDRAW;
            m_data = withDrawReq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void follioReq(bankReq bankReq){
        try {
            m_msgCode = eMessageCode.PARK_EARNREQUEST;
            m_data = bankReq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendData(byte data[]) {
        try {
            if(Connectivity.IsConnectedtoInternet(context)){
                if (GlobalClass.simplysaveClient != null) {
                    GlobalClass.simplysaveClient.send(data);
                }else{
                    new AlertBox(context,"","OK", Constants.ERR_SERVER_CONNECTION,true);
                }
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    public void sendPassbookRefDetail(passbookrefdetail detailsreq) {

        try {
            m_msgCode = eMessageCode.PASSBOOK_REF;
            m_data = detailsreq.data.getByteArr((short)m_msgCode.value);
            this.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
