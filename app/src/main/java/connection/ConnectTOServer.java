package connection;

import android.content.Context;
import android.os.AsyncTask;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.net.URI;

import enums.eSocketClient;
import utils.Connectivity;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;

/**
 * Created by Admin on 02/02/2016.
 */
public class ConnectTOServer extends AsyncTask<Object, Void, String> {

    //region [ Fields ]

    private Context m_context;
    private ConnectionProcess process;
    private eSocketClient client;
    //endregion

    //region [ Public methods ]

    public ConnectTOServer(Context context, ConnectionProcess process, eSocketClient client) {
        this.m_context = context;
        this.process=process;
        this.client = client;
    }
    //endregion

    //region [ Override methods of Async Task]

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //  GlobalClass.commonApi.showdialog(m_context);
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            connect(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //  GlobalClass.commonApi.dismissdialog();
    }

    //endregion

    //region [ Private methods ]
    private  void connect(eSocketClient tradeClient) {
        try {
            if(Connectivity.IsConnectedtoInternet(m_context)) {
                // eSocketClient eclient = eSocketClient.values()[tradeClient];
                switch (tradeClient){
                    case AUTH:
                        if(GlobalClass.authClient != null){
                            GlobalClass.authClient.isManualDisconnected = true;
                            GlobalClass.authClient.stopClient();
                        }
                        GlobalClass.log("Auth_IP : " + ObjectHolder.connconfig.getAuthIP() + ":"+ObjectHolder.connconfig.getAuthServerPort());
                        GlobalClass.authClient = null;
                        GlobalClass.authClient = new AuthClient(process);
                        GlobalClass.authClient.connect(ObjectHolder.connconfig.getAuthIP(), ObjectHolder.connconfig.getAuthServerPort());
                        break;
                    case BC: {
                        if (GlobalClass.tradeBCClient != null) {
                            GlobalClass.tradeBCClient.stopClient();
                        }
                        if (ObjectHolder.connconfig.getbCastServerIP().equalsIgnoreCase("")) {
                            ObjectHolder.connconfig = VenturaApplication.getPreference().getConnectionConfig();
                        }
                        GlobalClass.tradeBCClient = null;
                        String ip = ObjectHolder.connconfig.getbCastServerIP();
                        int port = ObjectHolder.connconfig.getBcServerPort();
                        if (!PreferenceHandler.getCallWithIP().equalsIgnoreCase("") && ip.contains(".com")) {
                            ip = UserSession.getClientResponse().getBCastServerIP();
                            port = UserSession.getClientResponse().getBCPort();
                        }
                        URI bcURI = new URI("wss://" + ip + ":" + port);
                        GlobalClass.tradeBCClient = new TradeBCClient(process, bcURI);
                        GlobalClass.tradeBCClient.connect();
                    }
                        break;
                    case INTERACTIVE: {
                        if (GlobalClass.tradeRCClient != null) {
                            GlobalClass.tradeRCClient.stopClient();
                        }
                        if (ObjectHolder.connconfig.getbCastServerIP().equalsIgnoreCase("")) {
                            ObjectHolder.connconfig = VenturaApplication.getPreference().getConnectionConfig();
                        }
                        String ip = ObjectHolder.connconfig.getTradeServerIP();
                        int port = ObjectHolder.connconfig.getRcServerPort();
                        if (!PreferenceHandler.getCallWithIP().equalsIgnoreCase("") && ip.contains(".com")) {
                            ip = UserSession.getClientResponse().getTradeServerIP();
                            port = UserSession.getClientResponse().getRCPort();
                        }
                        GlobalClass.tradeRCClient = null;
                        URI rcURI = new URI("wss://" + ip + ":" + port + "");

                        /*
                        URI rcURI = new URI("wss://" + "121.242.118.101" + ":" + "46051" + "");
                        ip = "121.242.118.101";
                        port = 46051;
                        */
                        /*
                        URI rcURI = new URI("wss://" + "43.242.213.77" + ":" + "46051" + "");
                        ip = "43.242.213.77";
                        port = 46051;
                        */
                        /*
                        URI rcURI = new URI("wss://" + "43.242.213.74" + ":" + "46051" + "");
                        ip = "43.242.213.74";
                        port = 51525;*/
                        GlobalClass.tradeRCClient = new TradeRCClient(process, rcURI);
                        GlobalClass.tradeRCClient.connect(ip, port);
                    }
                        break;
                    case SIMPLYSAVE:
                        if(GlobalClass.simplysaveClient != null){
                            GlobalClass.simplysaveClient.stopClient();
                            GlobalClass.simplysaveClient = null;
                        }
                        GlobalClass.simplysaveClient = new SimplysaveClient(process);
                        String simplySaveIP = GlobalClass.latestContext.getResources().getString(R.string.SIMPLYSAVE_SERVERIP);
                        String simplySavePort =GlobalClass.latestContext.getResources().getString(R.string.SIMPLYSAVE_SERVERPORT);
                        GlobalClass.simplysaveClient.connect(simplySaveIP,Integer.parseInt(simplySavePort));
                        break;
                    case SEARCHENGINE: {
                        if (GlobalClass.searchClient != null) {
                            GlobalClass.searchClient.stopClient();
                        }
                        if (ObjectHolder.connconfig.getSearchEngineIP().equalsIgnoreCase("")) {
                            ObjectHolder.connconfig = VenturaApplication.getPreference().getConnectionConfig();
                        }
                        GlobalClass.searchClient = null;
                        String ip = ObjectHolder.connconfig.getSearchEngineIP();
                        int port = ObjectHolder.connconfig.getSearchEngineServerPort();
                        URI bcURI = new URI("wss://" + ip + ":" + port);
                        GlobalClass.searchClient = new SearchClient(process, bcURI);
                        GlobalClass.searchClient.connect();
                    }
                    break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion
}