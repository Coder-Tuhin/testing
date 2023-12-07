package utils;

import android.content.Context;
import android.graphics.Color;

import com.ventura.venturawealth.R;

/**
 * Created by Administrator on 2/25/14.
 */
public class ScreenColor {
    public static float fontSize = 14.0F;
    public static float menufontSize = 13.0F;

    public static int textColor = Color.WHITE;
    public static int negativeColor = Color.parseColor("#FC0303");
    public static int positiveColor = Color.rgb(0, 255, 0);
    public static int iTableHeaderBackColor = Color.BLACK;
    public static int iTableHeaderTextColor = Color.rgb(196, 196, 196);
    public static int iTableRowOneBackColor = Color.BLACK;
    public static int iTableRowTwoBackColor = Color.rgb(25, 22, 17);
    public static int iTableRowTextColor = Color.WHITE;
    public static boolean isGridLineShow = true;
    public static int iTableGridColor = Color.parseColor("#a0a0a0");
    public static int iTableTotalRowBackColor = Color.BLACK;
    public static int seperatorHeight = 1;

    public static int GREEN = Color.rgb(23, 174, 28);//Color.parseColor("#17AE1C");
    public static int RED = Color.rgb(252, 3, 3);//Color.parseColor("#FC0303");
    public static int SILVER = Color.parseColor("#a0a0a0");
    public static int VENTURA = Color.parseColor("#F96129");

    public static int getGREEN(Context context){
        return GREEN;//context.getResources().getColor(R.color.green1);
    }
    public static int getRED(Context context){
        return RED;//context.getResources().getColor(R.color.red);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
