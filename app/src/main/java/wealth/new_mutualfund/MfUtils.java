package wealth.new_mutualfund;

import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONObject;

import utils.VenturaException;

public class MfUtils {


    public static String getString(int tagRes, JSONObject data){
        try {
            String tag = VenturaApplication.getInstance().getString(tagRes);
            if (data.has(tag)){
                String msg = data.optString(tag);
                if (msg!=null && !msg.equals("null")){
                    return msg;
                }
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "";
    }

    public static int getInt(int tagRes,JSONObject data){
        try {
            String tag = VenturaApplication.getInstance().getString(tagRes);
            if (data.has(tag)){
                int value = data.optInt(tag);
                return value;
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return 0;
    }

    public static String getString(int tagRes){
        try {
            String tag = VenturaApplication.getInstance().getString(tagRes);
            return tag;
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "";
    }
}
