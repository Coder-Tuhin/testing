package chart;

import android.widget.LinearLayout;

import com.xtrem.chartxtrem.model.Axis;
import com.xtrem.chartxtrem.model.AxisValue;
import com.xtrem.chartxtrem.model.Column;
import com.xtrem.chartxtrem.model.ColumnChartData;
import com.xtrem.chartxtrem.model.SubcolumnValue;
import com.xtrem.chartxtrem.view.ColumnChartView;

import java.util.ArrayList;
import java.util.List;

import handler.StructCBDBChartResp;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.ScreenColor;

/**
 * Created by XtremsoftTechnologies on 30/08/16.
 */

public class ColumnChart {

    public void setChartData(ColumnChartView chart, ArrayList<StructCBDBChartResp> dataList) {
        try {
            int axisXTextSize = 11;
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) chart.getLayoutParams();
            if(dataList.size() == 2) {
                params.width=170;
                chart.setLayoutParams(params);
            }else if(dataList.size() < 2) {
                params.width=120;
                chart.setLayoutParams(params);
            }

                /*ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(50,100);
                chartwidth.setLayoutParams(layoutParams);*/


            final List<AxisValue> axisValuesX = new ArrayList<>();
            final List<AxisValue> axisValuesY = new ArrayList<>();
            List<Column> columns = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                List<SubcolumnValue> subcolumnValues = new ArrayList<>();
                StructCBDBChartResp data = dataList.get(i);
                float mtm = data.mtm.getValue();
                final SubcolumnValue smv = new SubcolumnValue();
                if(mtm > 0){
                    smv.setColor(ScreenColor.GREEN);
                }else{
                    smv.setColor(ScreenColor.RED);
                    mtm = mtm*-1;
                }
                smv.setValue(mtm);
                smv.setLabel(mtm + "");
               // axisValuesX.add(new AxisValue(i).setLabel(date));
                //axisValuesY.add(new AxisValue(i + 1).setValue(openPl));
                axisValuesX.add(new AxisValue(i).setLabel(data.getExch().name));
                axisValuesY.add(new AxisValue(i+1).setValue(mtm));
                subcolumnValues.add(smv);
                Column column = new Column(subcolumnValues);
                column.setHasLabelsOnlyForSelected(true);
                column.setHasLabels(true);
                columns.add(column);
            }
            int color = ScreenColor.textColor;
            ColumnChartData data = new ColumnChartData(columns);
            data.setValueLabelTextSize(8);
            //data.setValueLabelBackgroundAuto(false);
            data.setValueLabelBackgroundEnabled(false);
            data.setValueLabelsTextColor(color);
            Axis axisX = new Axis(axisValuesX);
            axisX.setTextColor(color);
            axisX.setTextSize(axisXTextSize);
            data.setAxisXBottom(axisX);
            Axis axisY = new Axis(axisValuesY);
            axisY.setTextColor(color);
            data.setAxisYLeft(axisY);
            chart.setColumnChartData(data);

        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }

}
