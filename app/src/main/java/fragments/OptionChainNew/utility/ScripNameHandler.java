package fragments.OptionChainNew.utility;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.HashMap;

import connection.ReqSent;
import connection.SendDataToBCServer;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;

public class ScripNameHandler implements ReqSent {

    private HashMap<Integer, ArrayList<StructScripName>> scripNameHM = new HashMap<>();

    public void setScripNames(StructScripNames scripNames) {
        try {
            int exch = scripNames.exch.getValue();
            ArrayList<StructScripName> list = scripNameHM.get(exch);
            if (list == null) list = new ArrayList<>();
            for (StructScripName scripName : scripNames.scripName) {
                list.add(scripName);
            }
            scripNameHM.put(exch, list);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }


    public ArrayList<StructScripName> getScripNameList(int exchange) {
        try {
            if (!scripNameHM.containsKey(exchange)) {
                StructScripNamesReq req = new StructScripNamesReq();
                req.clCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                req.exchange.setValue(exchange);
                new SendDataToBCServer().ScripNamesRequest(req);
            }else {
                return scripNameHM.get(exchange);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return null;
    }



    public void setAutoCompleteData(AutoCompleteTextView acTextView, int exch) {
        ArrayList<StructScripName> nameList = scripNameHM.get(exch);
        if (nameList != null) {
            String names[] = new String[nameList.size()];
            for (int i = 0; i < nameList.size(); i++) {
                names[i] = nameList.get(i).scripName.getValue();
            }
            ArrayAdapter adapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.simple_list_item_1, names);
            acTextView.setAdapter(adapter);
        }
    }

    @Override
    public void reqSent(int msgCode) {

    }
}
