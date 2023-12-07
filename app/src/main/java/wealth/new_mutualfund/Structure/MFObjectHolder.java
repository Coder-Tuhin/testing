package wealth.new_mutualfund.Structure;

import org.json.JSONObject;

import java.util.HashMap;

public class MFObjectHolder {

    public static HashMap<String,JSONObject> holdingReport = new HashMap();
    public static HashMap<String,JSONObject> runningSIP = new HashMap();
    public static HashMap<String,JSONObject> sipMandate = new HashMap();
    public static HashMap<String,JSONObject> assetAllocation = new HashMap();
    public static HashMap<String,JSONObject> capitalGain = new HashMap();
    public static HashMap<String,JSONObject> dividendSumm = new HashMap();

    public static JSONObject dashBoard = null;

    public static void initializeall(){
        holdingReport = new HashMap<>();
        runningSIP = new HashMap<>();
        sipMandate = new HashMap<>();
        assetAllocation = new HashMap<>();
        capitalGain = new HashMap<>();
        dividendSumm = new HashMap<>();

        dashBoard = null;
    }
}
