package utils;

import android.graphics.Color;
import android.os.Handler;

import connection.Config;
import enums.eTabPosition;

/**
 * Created by XTREMSOFT on 4/20/2017.
 */
public class ObjectHolder {
    //UNIVERSAL OBJECT
    public static Handler handler;
    public static int dWidth = 0;
    public static int dHeight = 0;
    public static int pendingScripCode = 0;

    public static Config connconfig = new Config();
    public static boolean isMarginTrade = false;
    public static boolean isCommodityAllow = false;
    public static boolean isOCOAllow = false;
    public static boolean isMarginBF = false; //used to show Margin B/F on Margin Report
    public static boolean isPOA = true; //used to display non poa value in Holding Report
    public static int GREEN = Color.argb(250,23,174,28);
    public static int RED = Color.argb(250, 252,3,3);
    public static String PranLink = "https://www.ventura1.com/NPS/NPSContributionAPI.aspx?PRAN=";
    public static boolean isNeedDisplayChange = false;

    //FRAGMENTS
    public static void intializeAll(){
        handler = null;
        isOCOAllow = false;
        upiArr = null;
    }
    public static String[] upiArr;
}
