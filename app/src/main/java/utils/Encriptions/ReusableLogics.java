package utils.Encriptions;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


public  class ReusableLogics {

    public static void setKeyStr(Context context , String appkey){
        SharedPreferences.Editor sharedPref = context.getSharedPreferences("UPIPSP", Context.MODE_PRIVATE).edit();
        sharedPref.putString("appkey",appkey);
        sharedPref.apply();
    }
    public static String getKeyStr(Context context){

        SharedPreferences sharedpreferences = context.getSharedPreferences("UPIPSP", Context.MODE_PRIVATE);
        String keyStr =  sharedpreferences.getString("appkey",null);
        return keyStr;
    }
    public static void setKeyPledgeActiveSession(Context context , String appkey){
        SharedPreferences.Editor sharedPref = context.getSharedPreferences("Pledge", Context.MODE_PRIVATE).edit();
        sharedPref.putString("pledgeKey",appkey);
        sharedPref.apply();
    }
    public static String getKeyPledgeActiveSession(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences("Pledge", Context.MODE_PRIVATE);
        String keyStr =  sharedpreferences.getString("pledgeKey",null);
        return keyStr;
    }
    public static void setEncryptionKey(Context context, String encryptedSalt){
        SharedPreferences.Editor sharedPref = context.getSharedPreferences("UPIPSP", Context.MODE_PRIVATE).edit();
        sharedPref.putString("encryptedSalt",encryptedSalt);
        sharedPref.apply();
    }
    public static String getEncryptionKey(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UPIPSP", Context.MODE_PRIVATE);
        String encryptedSalt = sharedPreferences.getString("encryptedSalt",null);
        return encryptedSalt;
    }
}
