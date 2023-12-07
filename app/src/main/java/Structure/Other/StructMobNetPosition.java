package Structure.Other;


import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import enums.eExch;
import enums.eProductTypeITS;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XPC on 29-03-2016.
 * * modified by dpk on 30/03/2016.
 */
public class StructMobNetPosition {
    public int scripCode;
    public String contract;
    public String scripName;
    public int bfdQty;
    public int buyQty;
    public int sellQty;
    public double bfdValue;
    public double buyValue;
    public double sellValue;
    private double lastRate;
    public boolean withCfd;
    public long brokerOrderId;
    public long exchangeOrderId;
    public char exch;
    public char exchType;
    public byte orderType;
    private ArrayList<StructTradeReportReplyRecord_Pointer> buyArrayStructure;
    private ArrayList<StructTradeReportReplyRecord_Pointer> sellArrayStructure;
    private ArrayList<String> exchTradeIdList;

    NumberFormat formatter;
    public int mktlot = 1;

    public String key;

    public StructMobNetPosition() {
        buyArrayStructure = new ArrayList<>();
        sellArrayStructure = new ArrayList<>();
        exchTradeIdList = new ArrayList<>();
    }

    public String getFormatedScripName(boolean withIntraday){
        char exchT = 'E';
        String strOrderType = "";
        if(exchType == 'C'){
            exchT = 'E';
            if(UserSession.getLoginDetailsModel().isIntradayDelivery() && withIntraday){
                strOrderType = "\n("+getOrderTypeStr()+")";
            }
        }
        else if (exch == 'C'){
            exchT = 'D';
        }
        else{
            exchT = 'F';
            String[] symbols = scripName.split("-");
            if(symbols.length > 2) {
                exchT = 'O';
                //withIntraday = false;
            }
            else{
                String[] symbolArr = scripName.split(" ");
                if(symbolArr.length > 4){
                    exchT = 'O';
                    //withIntraday = false;
                }
            }
            if(UserSession.getLoginDetailsModel().isFNOIntradayDelivery() && withIntraday){
                strOrderType = "\n("+getOrderTypeStr()+")";
            }
        }
        String scriN = exch+""+exchT+"-"+scripName+strOrderType;
        return scriN;
    }
    public  boolean isDeliveryAllow(){

        if(orderType == 1){
            return true;
        }
        else if(getExchange() == eExch.FNO.value){
            Boolean isDelivery = false;
            /*String[] symbols = scripName.split("-");
            if(symbols.length > 2) {
                isDelivery = true;
            }
            else{
                String[] symbolArr = scripName.split(" ");
                if(symbolArr.length > 4){
                    isDelivery = true;
                }
            }*/
            return isDelivery;
        }
        return false;
    }

    public  boolean isIntraDelAllow(){
        if((exchType == 'C' &&
                UserSession.getLoginDetailsModel().isIntradayDelivery())){
            return true;
        }
        else if(getExchange() == eExch.FNO.value && UserSession.getLoginDetailsModel().isFNOIntradayDelivery()){
            boolean isFuture = true;
            /*String[] symbols = scripName.split("-");
            if(symbols.length > 2) {
                isFuture = false;
            }
            else{
                String[] symbolArr = scripName.split(" ");
                if(symbolArr.length > 4){
                    isFuture = false;
                }
            }*/
            return isFuture;

        }
        return false;
    }

    public String getOrderTypeStr() {
        if(orderType == eProductTypeITS.INTRADAY.value){
            return eProductTypeITS.INTRADAY.name;
        }
        else if(orderType == eProductTypeITS.BRACKETORDER.value){
            return eProductTypeITS.BRACKETORDER.name;
        }
        else if(orderType == eProductTypeITS.COVERORDER.value){
            return eProductTypeITS.COVERORDER.name;
        }else{
            return eProductTypeITS.DELIVERY.name;
        }
    }

    public void addTrade(StructTradeReportReplyRecord_Pointer structMobTradeRequest){

        if(!exchTradeIdList.contains(structMobTradeRequest.getKey())) {

            exchTradeIdList.add(structMobTradeRequest.getKey() + "");

            char ordType = structMobTradeRequest.buySell.getValue();
            if (ordType == 'B') {
                buyArrayStructure.add(structMobTradeRequest);
            } else if (ordType == 'S') {
                sellArrayStructure.add(structMobTradeRequest);
            }
            //TODO
            scripName = structMobTradeRequest.scripName.getValue();

            exch = structMobTradeRequest.exch.getValue();
            exchType = structMobTradeRequest.exchType.getValue();
            scripCode = structMobTradeRequest.scripCode.getValue();
            orderType = structMobTradeRequest.orderType.getValue();
            formatter = Formatter.getFormatter(getExchange());
            //TODO
            StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            if (mktWatch != null) {
                lastRate = mktWatch.getLastRate();
            }

            if (ordType == 'B') {

                buyQty = buyQty + structMobTradeRequest.qty.getValue();
                buyValue = buyValue + (structMobTradeRequest.qty.getValue() * structMobTradeRequest.rate.getValue());
            } else if (ordType == 'S') {
                sellQty = sellQty + structMobTradeRequest.qty.getValue();
                sellValue = sellValue + (structMobTradeRequest.qty.getValue() * structMobTradeRequest.rate.getValue());
            } else {
                bfdQty = bfdQty + structMobTradeRequest.qty.getValue();
                bfdValue = bfdValue + (structMobTradeRequest.qty.getValue() * structMobTradeRequest.rate.getValue());
            }
            brokerOrderId = structMobTradeRequest.exchOrderID.getValue();
        }
    }

    public void setOrderType(StructTradeReportReplyRecord_Pointer structMobTradeRequest){
        try {
            orderType = structMobTradeRequest.orderType.getValue();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getBfdQty() {
        return 0;
    }

    public double getBfdValue() {
        return 0.0;
    }

    public double getBfdRate() {
        return 0.0;
    }

    public int getTotBuyQty() {
        return getBfdQty()>0? getBfdQty() + buyQty : buyQty;
    }

    public int getTotSellQty() {
        return getBfdQty()<0? -1*getBfdQty()+sellQty : sellQty;
    }

    public int getNetQty() {
        return getBfdQty() + buyQty - sellQty;
    }

    public double getTotBuyValue() {
        return getBfdValue()>0 ? getBfdValue() + buyValue : buyValue;
    }

    public double getTotSellValue() {
        return getBfdValue()<0 ? -1*getBfdValue()+sellValue : sellValue;
    }

    public double getNetValue() {
        return getBfdValue() +  buyValue - sellValue;
    }

    public double getAvgNetPrice() {
        return getNetQty()==0 ? 0 : getNetValue() / getNetQty();
    }

    public double getAvgBuyPrice() {
        return getTotBuyQty()==0 ? 0 : getTotBuyValue() / getTotBuyQty();
    }

    public double getAvgSellPrice() {
        return getTotSellQty()==0 ? 0 : getTotSellValue() / getTotSellQty();
    }

    public double getAvgBfdPrice() {
        return getBfdQty()==0 ? 0 : getBfdValue() / getBfdQty();
    }


    public double getCost(){
        int netqty = getNetQty();
        double cost = 0.0;
        if(netqty > 0){
            for(int i = buyArrayStructure.size()-1; i>=0; i--){
                StructTradeReportReplyRecord_Pointer tradeReport = buyArrayStructure.get(i);
                if(netqty >= tradeReport.qty.getValue()){
                    cost = cost + (tradeReport.rate.getValue() * tradeReport.qty.getValue());
                    netqty = netqty - tradeReport.qty.getValue();

                } else if (netqty > 0){
                    cost =  cost + (netqty * tradeReport.rate.getValue());
                    //cost = (-1 * cost);//kiu kiya.??
                    break;
                }
            }
            cost = (-1) * cost;
        } else if (netqty < 0){

            int positioveNetQty = (-1)*netqty;
            for(int i = sellArrayStructure.size()-1; i>=0; i--){
                StructTradeReportReplyRecord_Pointer tradeReport = sellArrayStructure.get(i);
                if(positioveNetQty >= tradeReport.qty.getValue()){
                    cost = cost + (tradeReport.rate.getValue() * tradeReport.qty.getValue());
                    positioveNetQty = positioveNetQty - tradeReport.qty.getValue();

                } else if (positioveNetQty > 0) {
                    cost = cost + ((positioveNetQty) * tradeReport.rate.getValue());
                    break;
                }
            }
        } else if(netqty == 0){
            cost = 0;
        }
        return cost;
    }

    public double getNetCost(){
        double netCost = 0.0;
        int netqty = getNetQty();
        netCost = (netqty * lastRate);

        /*
        if(netqty > 0)
            netCost = (getNetQty() * lastRate);
        else if(netqty < 0)
            netCost =  (getNetQty() * lastRate);
        else if (netqty == 0)
            netCost = (getNetQty() * lastRate);*/
        return netCost ;
    }

    public double getMTM() {
        double mtm = 0.0;
        //int netqty = getNetQty();
        //GlobalClass.log(" net : " + netqty + " cost : " + getCost() + " R .cost : " + getNetCost());
        /*if(netqty > 0)
            mtm = (getRealisableCost() - getCost());
        else if(netqty < 0)
            mtm =  (getRealisableCost() - getCost());
        else if (netqty == 0)
            mtm = (getRealisableCost() + getCost());*/
        if(lastRate > 0) {
            mtm = (getNetCost() + getCost())*mktlot;
        }
        return mtm;
    }

    public double getTotalPL(){
        if(lastRate > 0){
            return (getNetCost() + getTotSellValue() + ( -1 * getTotBuyValue()))*mktlot;
        }
        return 0;
    }
    public double getAVGBookedPL(){
        double bkpl = 0.00;
        if((getTotBuyQty() > 0) && (getTotSellQty() > 0)) {
            bkpl = (Math.min(getTotBuyQty(), getTotSellQty()) * (getAvgSellPrice() - getAvgBuyPrice())) * mktlot;
        }
        return bkpl;
    }

    public double getAVGMTM(){
        double mtm = 0;
        if(lastRate > 0) {
            mtm = getTotalPL() - getAVGBookedPL();
        }
        return mtm;
    }


    public double getBookedPL(){
        return getTotalPL() - getMTM();
    }

    public void updateLastRate(double mktWatchLastRate){
        lastRate = mktWatchLastRate;//Formatter.round(mktWatchLastRate,2) ;
    }

    public String getNetQtyRate(){
        String addOrSub = "";
        if(getNetQty() > 0){
            addOrSub = "+";
        }
        String qtyrate = addOrSub + " " + getNetQty() + " @ " + formatter.format(getAvgNetPrice());
        return qtyrate;
    }

    public String getBuyQtyAvgRate(){

        String strBuyDetails = getTotBuyQty() + " @ " + formatter.format(getAvgBuyPrice());
        return strBuyDetails;
    }

    public String getSellQtyAvgRate() {

        String strSellDetails = getTotSellQty() + " @ " + formatter.format(getAvgSellPrice());
        return strSellDetails;
    }
    public String getLastRateStr() {
        return formatter.format(lastRate);
    }
    public void setLtpWithTxtColor(TextView txtVal, double prevVal)throws  Exception{
        if(txtVal !=null ) {
            double lastRate = getLastRate();
            int txtColor;
            if (prevVal != 0) {
                if (prevVal < lastRate) {
                    txtColor = getTickUpColor();
                } else if (prevVal > lastRate) {
                    txtColor = getTickDownColor();
                } else {
                    txtColor = getTextColor();
                }
                txtVal.setTextColor(txtColor);
            }
            txtVal.setText(formatter.format(lastRate));
        }
    }
    public double getLastRate(){
        return lastRate;
    }

    private int getTickUpColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.green1);
    }

    private int getTickDownColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.red);
    }
    private int getTextColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.white);
    }
    public String getMTMStr() {
        return Formatter.toTwoDecimalValue(getMTM());
    }
    public String getBookedPLStr() {
        return Formatter.toTwoDecimalValue(getBookedPL());
    }
    public String getTotBuyValueStr() {
        return Formatter.toTwoDecimalValue(getTotBuyValue()*mktlot);
    }
    public String getTotSellValueStr() {
        return Formatter.toTwoDecimalValue(getTotSellValue()*mktlot);
    }

    public int getExchange(){
        if(exch == 'N'){
            if(exchType == 'C'){
                return eExch.NSE.value;
            }
            else{
                return eExch.FNO.value;
            }
        }
        else if(exch == 'B'){
            return eExch.BSE.value;
        }
        else if(exch == 'C'){
            return eExch.NSECURR.value;
        }
        return -1;
    }
}
