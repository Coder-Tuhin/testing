package handler;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Structure.Response.BC.StructMobTickDataResponse;
import Structure.Response.BC.StructMobTick_Data;
import chart.ChartDataHolderSingleScrip;
import enums.EMsgCodes;
import enums.eMessageCode;
import utils.GlobalClass;

/**
 * Created by XtremsoftTechnologies on 04/03/16.
 */
public class ChartDataProcess {

    private int selectectedToken;
    HashMap<String, ChartDataHolderSingleScrip> chartDataMap;
    public ChartDataProcess(){
        chartDataMap=new HashMap();
    }

    public void putData(StructMobTickDataResponse tickDataResponse,int msgC){

        int scripCode = tickDataResponse.token.getValue();
        String keyV = scripCode + ":"+msgC;
        ChartDataHolderSingleScrip tick_DataList = chartDataMap.get(keyV);
        if (tick_DataList == null) {
            tick_DataList = new ChartDataHolderSingleScrip(scripCode);
            chartDataMap.put(keyV, tick_DataList);
        }
        tick_DataList.addChartData(tickDataResponse);
    }

    public ArrayList<StructMobTick_Data> getChartData(int scripCode,int msgC){

        String keyV = scripCode + ":"+msgC;
        ArrayList<StructMobTick_Data> tickList = new ArrayList<>();
        ChartDataHolderSingleScrip tick_DataList = chartDataMap.get(keyV);
            if (tick_DataList != null && tick_DataList.getSize() > 0) {
                tickList.addAll(tick_DataList.getChartData());
                if(msgC == eMessageCode.INTRADAY_CHART.value){
                    String keyVV = scripCode + ":"+eMessageCode.EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA.value;
                    ChartDataHolderSingleScrip tick_DataListDay = chartDataMap.get(keyVV);
                    if(tick_DataListDay != null && tick_DataListDay.getSize()>0){
                        tickList.addAll(tick_DataListDay.getChartData());
                    }
                }
                Collections.sort(tickList, new Comparator<StructMobTick_Data>() {
                    @Override
                    public int compare(StructMobTick_Data lhs, StructMobTick_Data rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return lhs.lastTimeInt.getValue() > rhs.lastTimeInt.getValue()?1:-1;
                    }
                });
            return tickList;
        }
        else{
            return null;
        }
    }

    public  void  addIntradayTick(int scripCode,StructMobTick_Data tdata){

        String keyVV = scripCode + ":"+eMessageCode.EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA.value;
        ChartDataHolderSingleScrip tick_DataListDay = chartDataMap.get(keyVV);
        if(tick_DataListDay != null){
            tick_DataListDay.addSingleChartData(tdata);
        }
    }

    public  void  updateIntradayLastTick(int scripCode,StructMobTick_Data tdata){

        String keyVV = scripCode + ":"+eMessageCode.EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA.value;
        ChartDataHolderSingleScrip tick_DataListDay = chartDataMap.get(keyVV);
        if(tick_DataListDay != null && tick_DataListDay.getSize()>0){
            tick_DataListDay.updateLastTick(tdata);
        }
    }

    public int getSelectectedToken() {
        return selectectedToken;
    }

    public void setSelectectedToken(int selectectedToken) {
        this.selectectedToken = selectectedToken;
    }

    public void clearData(int scripCode, int value) {
        try {
            String keyV = scripCode + ":" + value;
            chartDataMap.remove(keyV);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
