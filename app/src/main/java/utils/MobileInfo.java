package utils;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ventura.venturawealth.BuildConfig;
import com.ventura.venturawealth.VenturaApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import enums.ePrefTAG;

/**
 * Created by Admin on 03/02/2016.
 */
public class MobileInfo {
    public static final String className = "MobileInfo";
    private static String id = "";

    public static String getDeviceID(Context context) {
        try {
            if (!id.equals("")) return id;
            if (context == null) {
                context = GlobalClass.latestContext;
            }
            String strimei = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.IMEI.name, "");
            if (!strimei.equalsIgnoreCase("")) {
                id = strimei;
                return id;
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                id = telephonyManager.getDeviceId();
            }catch (Exception ex){ex.printStackTrace(); }
            /*if (id == null || id.equalsIgnoreCase("")) {
                id = FirebaseInstanceId.getInstance().getToken();
            }*/
            if (id == null || id.equalsIgnoreCase("")) {
                id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            if (id == null || id.equalsIgnoreCase("")) {
                id = new Date().getTime() + "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            id = new Date().getTime() + "";
        }
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.IMEI.name,id);
        return id;
    }

/*
    public static String getDeviceID(TelephonyManager phonyManager) {
        try {
            String id = phonyManager.getDeviceId();
            if (id == null) {
                id = "not available";
            }
            int phoneType = phonyManager.getPhoneType();
            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_NONE:
                    //return "NONE: " + id;
                    return id;

                case TelephonyManager.PHONE_TYPE_GSM:
                    //return "GSM: IMEI=" + id;
                    return id;

                case TelephonyManager.PHONE_TYPE_CDMA:
                    //return "CDMA: MEID/ESN=" + id;
                    return id;

                default:
                    return "UNKNOWN: ID=" + id;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
*/

    public static String getIPAddress(boolean useIPv4) {
        try {
            if(!publicIP.equalsIgnoreCase("")){
                return publicIP;
            }
            getPublicIPFromSite();
            GlobalClass.log("Public IP1 : " + publicIP);
            return getIP();

        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now exceptions
        return "";
    }

    private static String publicIP = "";

    private static void getPublicIPFromSite() {
        new Thread(new Runnable(){
            public void run(){
                //TextView t; //to show the result, please declare and find it inside onCreate()
                try {
                    // Create a URL for the desired page
                    URL url = new URL("http://checkip.amazonaws.com"); //My text file location
                    //First open the connection
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000); // timing out in 6 second
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    publicIP = in.readLine();
                    GlobalClass.log("PUBLIC IP2 : " + publicIP);
                    in.close();
                } catch (Exception e) {
                    GlobalClass.log("MyTag",e.toString());
                }
            }
        }).start();
    }
    //NEW
    private static String getIP(){
        String ip = "";
        try{
            ConnectivityManager connMananger = (ConnectivityManager) GlobalClass.latestContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connMananger.getActiveNetworkInfo();
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")){
                try {
                    android.net.wifi.WifiManager wm = (android.net.wifi.WifiManager) VenturaApplication.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
                    @SuppressWarnings("deprecation")
                    String ipW = android.text.format.Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    return ipW;
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    ip = "127.0.0.1";
                }

            }else {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = (NetworkInterface) en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ip = inetAddress .getHostAddress().toString();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ip;
    }

    public static String manufacturer = Build.MANUFACTURER;

    public static String model = Build.MODEL;

    public static String versionRelease = Build.VERSION.RELEASE;

    public static  int getAppVersionCode(){
        try {
           return BuildConfig.VERSION_CODE;
        } catch (Exception e) {
           return 1000;
        }
    }

    public static String getAppVersionName(){
        try {
            PackageInfo pInfo = GlobalClass.latestContext.getPackageManager().getPackageInfo(GlobalClass.latestContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
           return "NA";
        }
    }

    public static int sdkVersion = Build.VERSION.SDK_INT;
    public static String getMobileData(){
        return "IP|"+getIPAddress(true)+"|AppVersion|"+getAppVersionName()+"|model|"+model+"|manufacturer|"+manufacturer+"|sdkVersion|"+sdkVersion;
    }
}
