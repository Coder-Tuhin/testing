package connection;

import android.content.Context;
import android.os.AsyncTask;

import enums.eMessageCode;
import utils.Connectivity;
import utils.GlobalClass;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 11/16/2016.
 */
public class SendDataToAuthServer extends AsyncTask<Object, Void, String> {
    private static final String TAG = SendDataToAuthServer.class.getSimpleName();
    private Context ctx = null;
    private ReqSent req = null;
    private byte data[] = null;
    private eMessageCode emsgcode = null;

    public SendDataToAuthServer(Context ctx,ReqSent req,eMessageCode emsgcode, byte data[]){
        this.ctx = ctx;
        this.req = req;
        this.data = data;
        this.emsgcode = emsgcode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       // GlobalClass.commonApi.showdialog(ctx);
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
       // GlobalClass.reqResHandler.setMsgCodetoQueue(emsgcode);
       // GlobalClass.commonApi.dismissdialog();
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            switch (emsgcode){
                default:
                    sendData();
                    break;
            }
        } catch (Exception e) {
            GlobalClass.log(TAG," :onDataProcess: ",""+e);
        }
        return "";
    }
    private void sendData() {
        try {
            if(Connectivity.IsConnectedtoInternet(ctx)){
                if (GlobalClass.authClient != null) {
                    GlobalClass.authClient.send(data);
                    if(req != null)
                        req.reqSent(emsgcode.value);
                }
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
}
