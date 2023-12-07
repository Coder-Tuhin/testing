package connection;

import android.content.Context;

import enums.eSocketClient;
import utils.Connectivity;
import utils.Constants;
import utils.GlobalClass;
import utils.VenturaException;


public class Connect {
    public static boolean connect(Context context, ConnectionProcess cProcess, eSocketClient client) {
        try {
            if (Connectivity.IsConnectedtoInternet(context)) {
                new ConnectTOServer(context,cProcess,client).execute();
                return true;
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return false;
    }
}
