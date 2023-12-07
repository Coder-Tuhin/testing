    package Structure.Response.RC;

import android.graphics.Color;

import com.ventura.venturawealth.R;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import Structure.BaseStructure.StructBase;
import enums.eExch;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructOrderReportReplyRecord_Pointer extends StructBase {

    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructChar buySell;
    public StructInt qty;
    public StructChar atMarket;
    public StructFloat rate;
    public StructChar withSL;
    public StructFloat triggerRate;
    public StructChar sLTriggered;
    public StructInt tradedQty;
    public StructInt pendingQty;
    public StructInt brokerOrderID;
    public StructInt brokerOrderTime;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructByte statusLength;
    public StructString status ;
    public StructChar aHStatus;
    public StructByte orderType;
    public StructByte productType;
    public StructString slbmOrderType;
    public StructShort reserved1;
    public StructInt reserved2;

    public int finalexchtime;
    public int finaPendingQty;
    private int finaTradeQty;
    public double finaTradePrice;

    private String sentToExch;


    public StructOrderReportReplyRecord_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    public StructOrderReportReplyRecord_Pointer(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
        finalexchtime = exchOrderTime.getValue();
        finaPendingQty = pendingQty.getValue();
        finaTradeQty = tradedQty.getValue();
        if(exch.getValue() == 'C' && exchType.getValue() == 'D'){
            scripCode.setValue(scripCode.getValue() + GlobalClass.currScripCodeAddition);
        }else if(exch.getValue() == 'S'){
            scripCode.setValue(GlobalClass.getSLBS_ScripCodeForNeutrino(scripCode.getValue()));
        }
        setFinalPendingQtyWithTrdQty(finaTradeQty);
    }
    private void init() {
        exch = new StructChar("exch",'0');
        exchType = new StructChar("exchType",'0');
        scripCode = new StructInt("scripCode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",50,"");
        buySell = new StructChar("buySell",'0');
        qty = new StructInt("qty",0);
        atMarket = new StructChar("atMarket",'0');
        rate = new StructFloat("rate",0);
        withSL = new StructChar("withSL",'0');
        triggerRate = new StructFloat("triggerRate",0);
        sLTriggered = new StructChar("sLTriggered",'0');
        tradedQty = new StructInt("tradedQty",0);
        pendingQty = new StructInt("pendingQty",0);
        brokerOrderID = new StructInt("brokerOrderID",0);
        brokerOrderTime = new StructInt("brokerOrderTime",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchOrderTime =new StructInt("exchOrderTime",0);
        statusLength = new StructByte("statusLength",0);
        status = new StructString("status",20,"");
        aHStatus = new StructChar("aHStatus",'0');
        orderType = new StructByte("orderType",0);
        productType = new StructByte("productType",0);
        slbmOrderType = new StructString("SLBMOrderTye",2,"");
        reserved1 = new StructShort("reserved1",0);
        reserved2 = new StructInt("reserved2",0);

        sentToExch = UserSession.getClientResponse().isBOOM()?"Unconfirmed":"Sent To Exch";
        fields = new BaseStructure[]{
            exch,exchType,scripCode,scripNameLength,scripName,buySell,qty,atMarket,rate, withSL,
            triggerRate,sLTriggered,tradedQty,pendingQty,brokerOrderID,brokerOrderTime,
            exchOrderID,exchOrderTime,statusLength,status,aHStatus,orderType,productType,
                slbmOrderType,reserved1,reserved2
        };
    }
    public int getNoOfOrders(){
        int count = 0;
        String st = status.getValue().toLowerCase();
        if ((finaPendingQty <= 0 && !st.contains("cancelled"))
                ||((tradedQty.getValue()) >= qty.getValue() && (qty.getValue() != 0))) {
            // sStatus = "Fully Executed";
            count = 1;
        } else if (finaPendingQty > 0) {
            //sStatus = "Pending";
            count = 1;
        } else if (st.contains("cancelled")) {

        } else {
            //sStatus = "Other";
        }
        return count;
    }
    public int getPendingOrders(){
        int count = 0;
        if (finaPendingQty > 0) {
            count = 1;
        }
        return count;
    }

    public int getTradedOrders(){
        int count = 0;
        String st = status.getValue().toLowerCase();
        if ((finaPendingQty == 0 && !(st.contains("cancelled") || st.contains("rejected") || st.contains("frozen")))
                ||((tradedQty.getValue()) >= qty.getValue() && (qty.getValue() != 0))) {
            // sStatus = "Fully Executed";
            count = 1;
        }
        return count;
    }
    public void setFinalPendingQtyWithTrdQty(int trdQty){
        finaPendingQty = qty.getValue() - trdQty;
        finaTradeQty = trdQty;
        String st = status.getValue().toLowerCase();
        if(st.contains("cancelled") || st.contains("rejected") || st.contains("frozen")){
            finaPendingQty = 0;
        }
    }

    public String getFinalStatus(){
        String finalStatus = status.getValue();
        String st = finalStatus.toLowerCase();
        if(st.contains("rejected") || st.contains("cancelled") || st.contains("frozen")){
            finaPendingQty = 0;
        }
        else {
            if (finaPendingQty > 0) {
                if (aHStatus.getValue() == 'Y') {
                    finalStatus = "AH Pending";
                } else if(st.equalsIgnoreCase(sentToExch)){
                    finalStatus = status.getValue();
                }
                else{
                    finalStatus = "Pending";
                }
            }
            if (finaTradeQty == qty.getValue()) {
                finaPendingQty = 0;
                finalStatus = "Fully Executed";
            } else if (finaPendingQty > 0 && (finaTradeQty != qty.getValue()) && (finaTradeQty > 0)) {
                finalStatus = "Partly Executed";
            }
        }
        return finalStatus;
    }

    public boolean isOrderSentToExchModify(){
        if(getFinalStatus().equalsIgnoreCase(sentToExch)){
            return true;
        } else{
            return false;
        }
    }
    public boolean isOrderSentToExchCancel(){
        if(getFinalStatus().equalsIgnoreCase(sentToExch) && !UserSession.getClientResponse().isBOOM()){
            return true;
        } else{
            return false;
        }
    }

    public int getTextColor(){
        int textColor = Color.WHITE;
        String st = getFinalStatus().toLowerCase();
        if(st.contains("rejected") || st.contains("cancelled") || st.contains("frozen")){
            textColor = Color.rgb(189,183,107);
        }
        return textColor;
    }

    public String getFormatedScripName(boolean withIntrDel){
        char exchT = 'E';
        String strOrderType = "";
        if(exchType.getValue() == 'C'){
            exchT = 'E';
            if(UserSession.getLoginDetailsModel().isIntradayDelivery() && withIntrDel){
                strOrderType = (orderType.getValue() == 0)? "\n(Intraday)":"\n(Delivery)";
            }
        } else if (exch.getValue() == 'C' || exch.getValue() == 'D'){
            exchT = 'D';
        } else{
            exchT = 'F';
            String[] symbols = scripName.getValue().split("-");
            if(symbols.length > 2) {
                exchT = 'O';
                //withIntrDel = false;
            }
            else{
                String[] symbolArr = scripName.getValue().split(" ");
                if(symbolArr.length > 4){
                    exchT = 'O';
                    //withIntrDel = false;
                }
            }
            if(UserSession.getLoginDetailsModel().isFNOIntradayDelivery() && withIntrDel){
                strOrderType = (orderType.getValue() == 0)? "\n(Intraday)":"\n(Delivery)";
            }
        }
        String scriN = exch.getValue()+""+exchT+"-"+scripName.getValue()+strOrderType;
        return scriN;
    }

    public  boolean isIntraDelAllow(){
        if((exchType.getValue() == 'C' &&
                UserSession.getLoginDetailsModel().isIntradayDelivery())){
            return true;
        }
        else if(getExchange() == eExch.FNO.value){
            if(UserSession.getLoginDetailsModel().isFNOIntradayDelivery()){
                boolean isFuture = true;
                /*
                String[] symbols = scripName.getValue().split("-");
                if(symbols.length > 2) {
                    isFuture = false;
                } else{
                    String[] symbolArr = scripName.getValue().split(" ");
                    if(symbolArr.length > 4){
                        isFuture = false;
                    }
                }*/
                return isFuture;
            }
        }
        return false;
    }

    public String getOrderQtyRate(){

        NumberFormat formatter = Formatter.getFormatter(getExchange());
        String strOrdQtyRate = qty.getValue() + " @ "+ formatter.format(rate.getValue());
        if(atMarket.getValue() == 'Y'){
            strOrdQtyRate = qty.getValue() + " @ "+ "Market";
        }
        if(withSL.getValue() == 'Y' && sLTriggered.getValue() == 'N'){
            strOrdQtyRate = strOrdQtyRate+"\n( SL: "+ formatter.format(triggerRate.getValue())+" )";
        }
        return strOrdQtyRate;
    }
    public String getBuySell(){
        String strBS = "";
        if(buySell.getValue() == 'B'){
            strBS = getExchange() == eExch.SLBS.value? "Recall":"Buy";
        }
        else if(buySell.getValue() == 'S'){
            strBS = getExchange() == eExch.SLBS.value? "Lend":"Sell";
        }
        else{
            strBS = " ";
        }
        return strBS;
    }
    public String getStopLoss(){
        String finalWithSL = "No";
        if (withSL.getValue() == 'Y') {
            finalWithSL = "Yes";
        }
        if (sLTriggered.getValue() == 'Y') {
            finalWithSL = "No";
        }
        return finalWithSL;
    }
    public String getSLTiggered(){
        String strSLTrgrd = "";
        if (sLTriggered.getValue() == 'Y') {
            strSLTrgrd = "Yes";
        } else {
            strSLTrgrd = "No";
        }
        return strSLTrgrd;
    }
    public float getTiggeredRate(){
        float finalTriggerRate = triggerRate.getValue();
        if (sLTriggered.getValue() == 'Y') {
            finalTriggerRate = 0;
        }
        return finalTriggerRate;
    }

    public String getProductTypeStr() {
        if(orderType.getValue() == 0){
            return  "Intraday";
        } else{
            return  "Delivery";
        }
    }
    public int getExchange(){
        if(exch.getValue() == 'N'){
            if(exchType.getValue() == 'C'){
                return eExch.NSE.value;
            }
            else{
                return eExch.FNO.value;
            }
        }
        else if(exch.getValue() == 'B'){
            return eExch.BSE.value;
        }else if(exch.getValue() == 'S'){
            return eExch.SLBS.value;
        }
        else if(exch.getValue() == 'C'){
            return eExch.NSECURR.value;
        }
        return -1;
    }
    public String canOrderBeModified(int newQty, int newDiscQty, double limitprice, double triggerPrice){
        if(finaPendingQty > 0){
            if(newQty <= finaTradeQty){
                return Constants.CHK_QTY_FORMODIFY;
            }
            if (qty.getValue() == newQty) {
                if (limitprice == 0 && atMarket.getValue() == 'Y') {
                    return Constants.ERR_NO_CHANGE_MSG;
                } else {
                    int multipler = (getExchange() == eExch.NSECURR.value?10000:100);
                    int lp =  (int) Math.round(limitprice * multipler);
                    int plp =  (int) Math.round(rate.getValue() * multipler);
                    if (lp == plp) {
                        return Constants.ERR_NO_CHANGE_MSG;
                    }
                }
            }

            if(newDiscQty>0) {
                if (newDiscQty > (newQty - finaTradeQty)) {
                    return Constants.ERR_DISC_MODIFY_MSG;
                }
                if (newDiscQty < ((newQty - finaTradeQty) * 0.1)) {
                    return Constants.ERR_DISCPER_MODIFY_MSG;
                }
            }
            if (getExchange() == eExch.BSE.value ){
                if (sLTriggered.getValue() == 'Y' && triggerPrice > 0){
                    return Constants.ALREADY_TIGGERED;
                }
                if (atMarket.getValue() != 'Y' &&
                        withSL.getValue() != 'Y' && triggerPrice > 0){
                    return Constants.BSE_LIMIT_ORDER;
                }
            }
        }
        else {
           return Constants.MODIFY_PENDQTY_MSG;
        }
        return "";
    }

    public int getFinalTradeQty() {
        return finaTradeQty;
    }

    public String getERR_ORDER_MODIFY_SENTTOEXCH(){
        return "Order with status "+sentToExch+" can not be modified.";
    }
    public String getERR_ORDER_CANCEL_SENTTOEXCH(){
        return "Order with status "+sentToExch+" can not be cancelled.";
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}
