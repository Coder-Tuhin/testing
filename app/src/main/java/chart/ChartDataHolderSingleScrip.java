package chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import Structure.Response.BC.StructMobTickDataResponse;
import Structure.Response.BC.StructMobTick_Data;

public class ChartDataHolderSingleScrip {

    private ArrayList<StructMobTick_Data> chartData;
    private int scripCode;
    //private StructMobTick_Data currTick;

    public ChartDataHolderSingleScrip(int scripC){
        this.scripCode = scripC;
        this.chartData = new ArrayList<>();
    }

    public void addChartData(StructMobTickDataResponse tickDataResponse){
        if(tickDataResponse != null) {
            for (int i=0;i<tickDataResponse.structTickData.length;i++) {
                chartData.add(tickDataResponse.structTickData[i]);
            }
            if(tickDataResponse.downloadComplete.getValue() == 1){

                Collections.sort(chartData, new Comparator<StructMobTick_Data>() {
                    @Override
                    public int compare(StructMobTick_Data lhs, StructMobTick_Data rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return lhs.lastTimeInt.getValue() > rhs.lastTimeInt.getValue()?1:-1;
                    }
                });
                //currTick = chartData.get(chartData.size()-1);
            }
        }
    }
    public void addSingleChartData(StructMobTick_Data mobTick_data){
        if(mobTick_data != null) {
            chartData.add(mobTick_data);
        }
    }

    public  int getSize(){
        return  chartData.size();
    }

    public ArrayList<StructMobTick_Data> getChartData() {

        return chartData;
    }

    public void updateLastTick(StructMobTick_Data tdata) {
        try{
            chartData.remove(chartData.size() - 1);
            chartData.add(tdata);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    /*public void  updateCurrTick(StructMobTick_Data currTick){
        this.currTick = currTick;
    }*/
}
