package chart;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Structure.Request.BC.StructChartReq;
import Structure.Response.BC.StructMobTick_Data;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.eDailyChart;
import handler.ChartDataProcess;
import structure.StructDate;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;

/**
 * Created by XtremsoftTechnologies on 07/08/15.
 */
public class ChartDataHolder {
    private final String className = getClass().getName();
    private ArrayList<StructMobTick_Data> tickList;

    public  StructMobTick_Data lastTick;

    int scripCode;

    public ChartDataHolder(int scripCode){
        this.scripCode = scripCode;
    }

    public ArrayList<StructMobTick_Data> getTickList(eDailyChart dailyChart) {
        if (tickList == null) {
            tickList = new ArrayList<>();
        }
        if(dailyChart != eDailyChart.MAX){
            if(tickList.size() > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -dailyChart.value);
                Date fromDate = cal.getTime();
                int daysSince1900 = DateUtil.getDaysSince1900(fromDate);
                ArrayList<StructMobTick_Data> finallist = new ArrayList<>();
                for (StructMobTick_Data smtd : tickList){
                    if(smtd.lastTimeInt.getValue() > daysSince1900){
                        finallist.add(smtd);
                    }/*else{
                        break;
                    }*/
                }
                return finallist;
            }else {
                return tickList;
            }
        }else{
            return tickList;
        }
    }

    public ArrayList<StructMobTick_Data> getOneMinTickList() {
        if (tickList == null) {
            tickList = new ArrayList<>();
        }
        return tickList;
    }

    public void setTickList(ArrayList<StructMobTick_Data> tickList) {
        this.tickList = tickList;
    }

    public void saveTickData(ArrayList<StructMobTick_Data> tickList,boolean isEOD) {
        try {
            Date fixTime = DateUtil.getFixTime(9, 14, 59);
            for (StructMobTick_Data tick_data : tickList) {

                Date date1 = null;
                if (isEOD){
                    addGraghStructure(tick_data);
                }else {
                    date1 = tick_data.lastTime.getValue();
                    if (date1 != null && DateUtil.compareTimes(date1, fixTime) > 0) {
                        addGraghStructure(tick_data);
                    }
                }

            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    public void addGraghStructure(StructMobTick_Data tickStruct) {
        try {
            if (this.tickList == null) {
                this.tickList = new ArrayList<>();
            }
            this.tickList.add(tickStruct);
            lastTick = tickStruct;

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    public void addGraghStructure(StructMobTick_Data tickStruct,boolean isFromUpdateMkt) {
        try {
            if (this.tickList == null) {
                this.tickList = new ArrayList<>();
            }
            this.tickList.add(tickStruct);
            lastTick = tickStruct;
            /*if(GlobalClass.chartDataProcess != null){
                GlobalClass.chartDataProcess.addIntradayTick(scripCode,tickStruct);
            }*/
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }


    private Date getDatefromDays(int days){
        Calendar cal = Calendar.getInstance();
        cal.set(1900,Calendar.JANUARY,1);
        cal.add(Calendar.DAY_OF_YEAR,days);
        Date d = cal.getTime();
        //android.util.GlobalClass.log("Date2"," :: "+d);
        return d;
    }



    public ArrayList<StructMobTick_Data> getListForMonth(int month) {
        ArrayList<StructMobTick_Data> finallist = new ArrayList<>();
        ArrayList<StructMobTick_Data> tickDataList = getTickList(eDailyChart.MAX);
        try {
            //Calendar cal2 = Calendar.getInstance();

            double finalopen = 0;
            double finalhigh = 0;
            double finallow = 0;
            double finalclose = 0;
            int finalqty = 0;
            int finalTotalQty = 0;
            int finalLastTime = 0;
            if (tickDataList.size()>=0){

                Date startDate = getDatefromDays(tickDataList.get(0).lastTimeInt.getValue());
                String startMonth = DateUtil.dateFormatter(startDate,Constants.MMMYYYY);

                finallow = tickDataList.get(0).low.getValue();
                finalopen = tickDataList.get(0).open.getValue();
                int lastTimeTickAddded = 0;
                int monthCount = 0;

                for (StructMobTick_Data smtd : tickDataList){

                    double open = smtd.open.getValue();
                    double high = smtd.high.getValue();
                    double low = smtd.low.getValue();
                    double close = smtd.close.getValue();

                    int qty = smtd.lastQty.getValue();
                    int totalqty = smtd.totalQty.getValue();
                    int lastTime = smtd.lastTimeInt.getValue();

                    Date currDt = getDatefromDays(lastTime);
                    String currMonth = DateUtil.dateFormatter(currDt,Constants.MMMYYYY);
                    if (startMonth.equalsIgnoreCase(currMonth)) {
                        if (high > finalhigh) {
                            finalhigh = high;
                        }
                        if (low < finallow) {
                            finallow = low;
                        }
                        finalclose = close;
                        finalqty = finalqty + qty;
                        finalTotalQty = totalqty;
                        finalLastTime = lastTime;

                    } else {
                        monthCount++;
                        if ((monthCount == month)) {// && (finalhigh > finalopen) && (finallow < finalopen)) {
                            monthCount = 0;
                            //Date tickDt = getDatefromDays(lastTime);
                            //GlobalClass.log("Chart1","O:" + finalopen + " C:" + finalclose + " Q:"+finalqty + " D:"+currDt.toString());
                            StructMobTick_Data tickData = new StructMobTick_Data(true);
                            tickData.setLastTimeI(finalLastTime);
                            tickData.open.setValue(finalopen);
                            tickData.high.setValue(finalhigh);
                            tickData.low.setValue(finallow);
                            tickData.close.setValue(finalclose);
                            tickData.lastQty.setValue(finalqty);
                            tickData.totalQty.setValue(finalTotalQty);
                            finallist.add(tickData);
                            lastTimeTickAddded = finalLastTime;

                            finalopen = open;
                            finalhigh = high;
                            finallow = low;
                            finalclose = close;
                            finalqty = qty;
                            finalTotalQty = totalqty;
                            finalLastTime = lastTime;
                            startDate = getDatefromDays(finalLastTime);
                            startMonth = DateUtil.dateFormatter(startDate,Constants.MMMYYYY);

                        }
                        else{
                            if (high > finalhigh) {
                                finalhigh = high;
                            }
                            if (low < finallow) {
                                finallow = low;
                            }
                            finalclose = close;
                            finalqty = finalqty + qty;
                            finalTotalQty = totalqty;
                            finalLastTime = lastTime;
                            startDate = getDatefromDays(finalLastTime);
                            startMonth = DateUtil.dateFormatter(startDate,Constants.MMMYYYY);
                        }
                    }
                }
                if(lastTimeTickAddded != finalLastTime){
                    StructMobTick_Data tickData = new StructMobTick_Data(true);
                    tickData.setLastTimeI(finalLastTime);
                    tickData.open.setValue(finalopen);
                    tickData.high.setValue(finalhigh);
                    tickData.low.setValue(finallow);
                    tickData.close.setValue(finalclose);
                    tickData.lastQty.setValue(finalqty);
                    tickData.totalQty.setValue(finalTotalQty);
                    finallist.add(tickData);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return finallist;
    }

    public ArrayList<StructMobTick_Data> getListForYear() {
        ArrayList<StructMobTick_Data> finallist = new ArrayList<>();
        ArrayList<StructMobTick_Data> tickDataList = getTickList(eDailyChart.MAX);
        try {
            //Calendar cal2 = Calendar.getInstance();

            double finalopen = 0;
            double finalhigh = 0;
            double finallow = 0;
            double finalclose = 0;
            int finalqty = 0;
            int finalTotalQty = 0;
            int finalLastTime = 0;
            if (tickDataList.size()>=0){

                Date startDate = getDatefromDays(tickDataList.get(0).lastTimeInt.getValue());
                String startYear = "";//DateUtil.dateFormatter(startDate,Constants.YYYY);

                finalopen = tickDataList.get(0).open.getValue();
                //cal2.setTime(startDate);
                //cal2.add(Calendar.MONTH,month);
                //cal2.set(startDate.getYear(),startDate.getMonth()+month+1,1,0,0);
                //Date endDate = cal2.getTime();
                int lastTimeTickAddded = 0;

                for (StructMobTick_Data smtd : tickDataList){

                    double open = smtd.open.getValue();
                    double high = smtd.high.getValue();
                    double low = smtd.low.getValue();
                    double close = smtd.close.getValue();

                    int qty = smtd.lastQty.getValue();
                    int totalqty = smtd.totalQty.getValue();
                    int lastTime = smtd.lastTimeInt.getValue();

                    Date currDt = getDatefromDays(lastTime);
                    String currYear = DateUtil.dateFormatter(currDt,Constants.YYYY);

                    if (startYear.equalsIgnoreCase(currYear)) {
                        if (high > finalhigh) {
                            finalhigh = high;
                        }
                        if (low < finallow) {
                            finallow = low;
                        }
                        finalclose = close;
                        finalqty = finalqty + qty;
                        finalTotalQty = totalqty;
                        finalLastTime = lastTime;

                    } else {

                        if (finalopen > 0) {// && (finalhigh > finalopen) && (finallow < finalopen)) {

                            //GlobalClass.log("Chart1","O:" + finalopen + " C:" + finalclose + " Q:"+finalqty + " D:"+currDt.toString());
                            StructMobTick_Data tickData = new StructMobTick_Data(true);
                            tickData.setLastTimeI(finalLastTime);
                            tickData.open.setValue(finalopen);
                            tickData.high.setValue(finalhigh);
                            tickData.low.setValue(finallow);
                            tickData.close.setValue(finalclose);
                            tickData.lastQty.setValue(finalqty);
                            tickData.totalQty.setValue(finalTotalQty);
                            finallist.add(tickData);
                            lastTimeTickAddded = finalLastTime;
                        }
                        finalopen = open;
                        finalhigh = high;
                        finallow = low;
                        finalclose = close;
                        finalqty = qty;
                        finalTotalQty = totalqty;
                        finalLastTime = lastTime;
                        startDate = getDatefromDays(finalLastTime);
                        startYear = DateUtil.dateFormatter(startDate,Constants.YYYY);
                        //cal2.setTime(startDate);
                        //cal2.add(Calendar.MONTH,month);
                        //cal2.set(startDate.getYear(),startDate.getMonth()+month+1,1,0,0);
                        //endDate = cal2.getTime();
                    }
                }
                if(lastTimeTickAddded != finalLastTime){
                    StructMobTick_Data tickData = new StructMobTick_Data(true);
                    tickData.setLastTimeI(finalLastTime);
                    tickData.open.setValue(finalopen);
                    tickData.high.setValue(finalhigh);
                    tickData.low.setValue(finallow);
                    tickData.close.setValue(finalclose);
                    tickData.lastQty.setValue(finalqty);
                    tickData.totalQty.setValue(finalTotalQty);
                    finallist.add(tickData);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return finallist;
    }

    /*
    public ArrayList<StructMobTick_Data> getListForMinute(int intMinute) {

        ArrayList<StructMobTick_Data> selectedMinuteList = new ArrayList<>();
        if(intMinute >1) {
            try {
                int seconds = intMinute * 60;
                int finalVolume = 0;
                double finalOpen = 0;
                double finalHigh = 0;
                double finalLow = 0;
                double finalClose = 0;
                double prevClose = 0;
                int iCurrElem = 0;
                int prevTime = 0;
                ArrayList<StructMobTick_Data> tickDataList = getTickList();
                if (tickDataList != null && tickDataList.size() > 0) {


                    //int startTime = tickDataList.get(0).lastTime.getDateInNumber();
                    //if(DateUtil.dateFormatter(startTime, Constants.DDMMMYY).equalsIgnoreCase(
                            DateUtil.dateFormatter(new StructDate("",new Date()).getDateInNumber(),Constants.DDMMMYY))){
                      //  startTime = (int) DateUtil.getFixTimeInNumber(9, 14, 59);
                    }
                    int startTime = (int) DateUtil.getFixTimeInNumber(9, 14, 59);
                    //int startTime = tickDataList.get(0).lastTime.getDateInNumber();
                    prevTime = startTime + 1;
                    for (int i = 0; i < tickDataList.size(); i++) {

                        StructMobTick_Data tick_data = tickDataList.get(i);
                        int iElem = (int) Math.ceil((double) (tick_data.lastTime.getDateInNumber()
                                - startTime) / seconds) - 1;

                        if (iElem > iCurrElem) {
                            for (int j = iCurrElem; j < iElem; j++) {
                                StructMobTick_Data tickData = new StructMobTick_Data();
                                prevTime = prevTime + seconds;
                                tickData.setLastTimeI(prevTime);
                                tickData.open.setValue(prevClose);
                                tickData.high.setValue(prevClose);
                                tickData.low.setValue(prevClose);
                                tickData.close.setValue(prevClose);
                                tickData.lastQty.setValue(0);
                                selectedMinuteList.add(tickData);
                                iCurrElem++;
                                //GlobalClass.log("added time:" + tickData.lastTime.getValue());
                            }
                        }

                        finalOpen = tick_data.open.getValue();
                        finalHigh = tick_data.high.getValue();
                        finalLow = tick_data.low.getValue();
                        finalClose = tick_data.close.getValue();
                        finalVolume = tick_data.lastQty.getValue();

                        int a = ((int) Math.ceil(((double) (tick_data.lastTime.getDateInNumber()
                                - startTime) / seconds)) - 1);
                        int count = 0;
                        //String time = DateUtil.dateFormatter(tick_data.lastTime.getDateInNumber()+seconds,DFConstraint.DDMMMYYHHMM);
                        while (a == iElem) {
                            if (count != 0) {
                                if (tickDataList.get(i).high.getValue() > finalHigh) {
                                    finalHigh = tickDataList.get(i).high.getValue();
                                }
                                if (tickDataList.get(i).low.getValue() < finalLow) {
                                    finalLow = tickDataList.get(i).low.getValue();
                                }
                                finalVolume = finalVolume + tickDataList.get(i).lastQty.getValue();
                                finalClose = tickDataList.get(i).close.getValue();
                            }
                            count++;
                            i += 1;

                            if (i == tickDataList.size()) {
                                break;
                            }
                            a = ((int) Math.ceil(((double) (tickDataList.get(i).lastTime.getDateInNumber() - startTime) / seconds)) - 1);
                        }

                        prevClose = finalClose;
                        prevTime = prevTime + seconds;
                        StructMobTick_Data tickData1 = new StructMobTick_Data();
                        tickData1.open.setValue(finalOpen);
                        tickData1.high.setValue(finalHigh);
                        tickData1.low.setValue(finalLow);
                        tickData1.close.setValue(finalClose);
                        tickData1.lastQty.setValue(finalVolume);
                        tickData1.setLastTimeI(prevTime);
                        selectedMinuteList.add(tickData1);
                        i--;
                        iCurrElem = iElem + 1;
                    }
                }
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
        }else{
            selectedMinuteList = getTickList();
        }
        return selectedMinuteList;
    }*/

    public ArrayList<StructMobTick_Data> getListForMinute(int intMinute) {

        ArrayList<StructMobTick_Data> selectedMinuteList = new ArrayList<>();

        if(intMinute >1) {
            try {
                int seconds = intMinute * 60;
                int iCurrElem = 0;
                int prevTime = 0;
                // double prevClose = 0;
                int finalVolume = 0;
                double finalOpen = 0;
                double finalHigh = 0;
                double finalLow = 0;
                double finalClose = 0;

                ArrayList<StructMobTick_Data> tickDataList = getTickList(eDailyChart.MAX);

                if (tickDataList != null && tickDataList.size() > 0) {
                    int startTime = (int) DateUtil.getFixTimeInNumber(9, 14, 59);
                    prevTime = startTime + 1;
                    for (int i = 0; i < tickDataList.size(); i++) {
                        StructMobTick_Data tick_data = tickDataList.get(i);
                        int iElem = (int) Math.ceil((double) (tick_data.lastTime.getDateInNumber()
                                - startTime) / seconds) - 1;
/*
                        if (iElem > iCurrElem) {
                            for (int j = iCurrElem; j < iElem; j++) {
                                StructMobTick_Data tickData = new StructMobTick_Data();
                                prevTime = prevTime + seconds;
                                tickData.setLastTimeI(prevTime);
                                tickData.open.setValue(prevClose);
                                tickData.high.setValue(prevClose);
                                tickData.low.setValue(prevClose);
                                tickData.close.setValue(prevClose);
                                tickData.lastQty.setValue(0);
                                //selectedMinuteList.add(tickData);
                                iCurrElem++;
                                //GlobalClass.log("added time:" + tickData.lastTime.getValue());
                            }
                        }
*/

                        finalOpen = tick_data.open.getValue();
                        finalHigh = tick_data.high.getValue();
                        finalLow = tick_data.low.getValue();
                        finalClose = tick_data.close.getValue();
                        finalVolume = tick_data.lastQty.getValue();

                        int a = ((int) Math.ceil(((double) (tick_data.lastTime.getDateInNumber()
                                - startTime) / seconds)) - 1);
                        int count = 0;
                        //String time = DateUtil.dateFormatter(tick_data.lastTime.getDateInNumber()+seconds,DFConstraint.DDMMMYYHHMM);
                        while (a == iElem) {
                            if (count != 0) {
                                if (tickDataList.get(i).high.getValue() > finalHigh) {
                                    finalHigh = tickDataList.get(i).high.getValue();
                                }
                                if (tickDataList.get(i).low.getValue() < finalLow) {
                                    finalLow = tickDataList.get(i).low.getValue();
                                }
                                finalVolume = finalVolume + tickDataList.get(i).lastQty.getValue();
                                finalClose = tickDataList.get(i).close.getValue();
                            }
                            count++;
                            i += 1;

                            if (i == tickDataList.size()) {
                                break;
                            }
                            a = ((int) Math.ceil(((double) (tickDataList.get(i).lastTime.getDateInNumber() - startTime) / seconds)) - 1);
                        }

                        prevTime = prevTime + seconds;
                        StructMobTick_Data tickData1 = new StructMobTick_Data(true);
                        tickData1.open.setValue(finalOpen);
                        tickData1.high.setValue(finalHigh);
                        tickData1.low.setValue(finalLow);
                        tickData1.close.setValue(finalClose);
                        tickData1.lastQty.setValue(finalVolume);
                        tickData1.setLastTimeI(tick_data.lastTime.getDateInNumber());
                        selectedMinuteList.add(tickData1);
                        i--;
                        iCurrElem = iElem + 1;
                    }
                }
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
        }else{
            selectedMinuteList = getTickList(eDailyChart.MAX);
        }
        return selectedMinuteList;
    }

    public void updateLastTick() {
        try {
            tickList.remove(tickList.size() - 1);
            tickList.add(lastTick);
            if(GlobalClass.chartDataProcess != null){
                GlobalClass.chartDataProcess.updateIntradayLastTick(scripCode,lastTick);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void updateCurrTick(StructMobTick_Data cData) {
        try {
            if(GlobalClass.chartDataProcess != null){
                GlobalClass.chartDataProcess.updateIntradayLastTick(scripCode,cData);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public StructMobTick_Data getNewCurrentData() {
        StructMobTick_Data currTick = new StructMobTick_Data(true);
        if(GlobalClass.chartDataProcess != null){
            GlobalClass.chartDataProcess.addIntradayTick(scripCode,currTick);
        }
        return currTick;
    }

/*
    public void sendChartReq(Context context, int scripCode, String scripName, ReqSent reqSent) {
        try {
            GlobalClass.chartDataProcess = new ChartDataProcess();
            GlobalClass.chartDataProcess.setSelectectedToken(scripCode);
            StructChartReq req = new StructChartReq();
            req.scripCode.setValue(scripCode);
            req.segment.setValue(GlobalClass.getExchCodeFromScripName(scripName));
            req.timeFrom.setValue(DateUtil.getFixTime(9, 15, 00));//9:15
            req.timeTo.setValue(DateUtil.getFixTime(3, 30, 00));//curr time but upto 3:30

            SendDataToBCServer sendData = new SendDataToBCServer();
            sendData.sendChartRequest(req);

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
*/
}
