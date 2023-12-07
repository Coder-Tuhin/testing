package chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtrem.chartxtrem.piechart.PieHelper;
import com.xtrem.chartxtrem.piechart.PieView;

import java.util.ArrayList;
import java.util.List;

import com.ventura.venturawealth.R;
import models.StructNameValue;
import utils.Formatter;
import utils.GlobalClass;

/**
 * Created by XPC17 on 18-03-2017.
 */
public class PieChart {

    public void setChart(PieView pieView, List dataList,float totVal){
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        for (int i = 0; i < dataList.size(); i++) {
            float valPer = Float.parseFloat(dataList.get(i)+"")/totVal * 100;
            pieHelperArrayList.add(new PieHelper(valPer,dataList.get(i)+"",ChartColor.CHART_COLOR[i]));

        }
        pieView.setDate(pieHelperArrayList);
        pieView.showPercentLabel(false);
    }


    public void setChartWithValue(PieView pieView, List<StructNameValue> dataList, float totVal, LinearLayout pieIndicatorLayout){
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        for (int i = 0; i < dataList.size(); i++) {
            float valPer = dataList.get(i).getValue()/totVal * 100;

            LinearLayout piechart_indicator = (LinearLayout) ((Activity) GlobalClass.latestContext).getLayoutInflater().inflate(R.layout.piechart_indicator, null);
            TextView indicatorName = (TextView) piechart_indicator.findViewById(R.id.indicator_name);
            TextView indicatorColor = (TextView) piechart_indicator.findViewById(R.id.indicator_color);

            indicatorName.setText(dataList.get(i).getName() + " " + Formatter.toTwoDecimalValue(valPer) + "%");
            indicatorColor.setBackgroundColor(ChartColor.CHART_COLOR[i]);
            pieIndicatorLayout.addView(piechart_indicator);

            pieHelperArrayList.add(new PieHelper(valPer,ChartColor.CHART_COLOR[i]));

        }
        pieView.setDate(pieHelperArrayList);
        pieView.showPercentLabel(false);
    }

    // for fact sheet
    // by Mr Goutam D
    public void setChartView(PieView pieView, List<StructNameValue> dataList, float totVal, LinearLayout pieIndicatorLayout, Context context){
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();

        int CHART_COLOR[] = new int[]{
                Color.parseColor("#FA8110"),
                Color.parseColor("#2CAB2A"),
                Color.parseColor("#60DEAF"),
                Color.parseColor("#B5E521"),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col5),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col6),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col7),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col8),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col9),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col10),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col11),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col12),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col13),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col14),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col15),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col16),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col17),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col18),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col19),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col20),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col21),
                GlobalClass.latestContext.getResources().getColor(R.color.piechart_col22)
        };
        for (int i = 0; i < dataList.size(); i++) {
            float valPer = dataList.get(i).getValue()/totVal * 100;

            LinearLayout pieChart_indicator = (LinearLayout) ((Activity) GlobalClass.latestContext).getLayoutInflater().inflate(R.layout.piechart_indicator, null);
            TextView indicatorName = pieChart_indicator.findViewById(R.id.indicator_name);
            TextView indicatorColor = pieChart_indicator.findViewById(R.id.indicator_color);

            indicatorName.setText(dataList.get(i).getName() + " " + Formatter.mf_one_or_not_decimal(String.valueOf(valPer)) + "%");
            indicatorName.setTextColor(context.getResources().getColor(R.color.white));
            indicatorColor.setBackground(getDrawable(CHART_COLOR[i]));
            pieIndicatorLayout.addView(pieChart_indicator);

            pieHelperArrayList.add(new PieHelper(valPer, CHART_COLOR[i]));
        }
        pieView.setDate(pieHelperArrayList);
        pieView.showPercentLabel(false);
    }

    public GradientDrawable getDrawable(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(backgroundColor);
        return shape;
    }
}
