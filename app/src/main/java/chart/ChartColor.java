package chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.ventura.venturawealth.R;
import utils.GlobalClass;


/**
 * Created by XtremsoftTechnologies on 02/08/16.
 */

public class ChartColor {
    public static final int CHART_COLOR[];
    public static final int EXPIRY_CHART_COLOR[];
    public static final int SELECTED_CHART_COLOR;
    public static final int EXCH_COLOR[];
    static {
        //CHART_COLOR = new int[]{Color.parseColor("#03B2E4"),Color.parseColor("#8DD02A"),Color.parseColor("#B35FCA")};
        CHART_COLOR = new int[]{
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col1),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col2),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col3),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col4),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col5),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col6),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col7),
        };
        EXPIRY_CHART_COLOR = new int[]{Color.parseColor("#03B2E4"),Color.parseColor("#8DD02A"),Color.parseColor("#B35FCA")};
        SELECTED_CHART_COLOR = Color.GREEN;
        EXCH_COLOR = new int[]{GlobalClass.latestContext.getResources().getColor(R.color.nse_color),
                GlobalClass.latestContext.getResources().getColor(R.color.bse_color),
                GlobalClass.latestContext.getResources().getColor(R.color.fno_color)};
    }

    public static int[] getOCChartColor(Context context){
        Resources res = context.getResources();
        int ocChartColor[] = {res.getColor(R.color.analytics_oc_color3),
                res.getColor(R.color.analytics_oc_color1),res.getColor(R.color.analytics_oc_color2)};
        return ocChartColor;
    }
}
