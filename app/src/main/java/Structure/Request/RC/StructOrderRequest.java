package Structure.Request.RC;

import java.lang.reflect.Field;
import java.util.Date;

import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.Scrip.ScripDetail;
import enums.eExch;
import structure.BaseStructure;
import structure.StructBase;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructDate;
import structure.StructInt;
import structure.StructLong;
import structure.StructMoney;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOrderRequest extends StructBase {

    public StructShort reqType;
    public StructInt exchSegment;
    public StructShort orderType;
    //Order Type---SHORT---2 bytes--Must be >=0, 0-Buy, 1-Sell
    public StructInt token;
    public StructString symbol;
    //Symbol---CHAR(10)---10 bytes--Can be left blank
    public StructString scripName;
    //Scrip Name---CHAR(50)---50 bytes--Can be left blank
    public StructString series;
    //Setries---CHAR(2)---2 bytes--Can be left blank
    public StructShort instType;
    //InstType---SHORT---2 bytes---Must be >=0, 0-Futures, 1-Calls, 2-puts
    public StructShort underlyingType;
    //UnderlyingType---SHORT---2 bytes---Must be >=0, 0-Index, 1-Stock
    public StructDate expiryDate;
    //ExpiryDate---INT---4 bytes---no of seconds from 01-01-1980 midnight
    public StructMoney strikePrice;

    public StructInt qtyType;
    //-QtyType---INT---4 bytes---Leave blank or set to 0
    public StructInt qty;
    //Must be > 0
    public StructInt discQty;
    //Must be >= 0
    public StructMoney limitPrice;
    //multiply by 100. Id AtMarket set to 0
    public StructMoney triggerPrice;
    public StructByte atMarket;
    //Market Order send 'Y'
    public StructByte stopLoss;
    //For StopLoss Order send 'Y'
    public StructByte ioc;
    //For ImmediateOrCancel send 'Y'
    public StructShort prodType;
    //Must be > 0, 1-Intraday, 2-Delivery, 3-StopLoss
    public StructByte afterHours;
    //For AfterHours Order send 'Y'
    public StructString clientCode;
    //length=10
    public StructString orderRequestCode;
    //10 bytes
    public StructShort proClient;
    //bytes---Must be > 0, 1-Client, 2-Pro
    public StructShort localOrderID;
    //-Leave as blank
    public StructDate localOrderTime;
    //Leave as blank
    public StructLong brokerOrderID;
    //Leave as blank
    public StructDate brokerOrderTime;
    //Leave as blank
    public StructShort localStratID;
    //Leave as blank
    public StructShort stratID;
    //Leave as blank
    public StructString status;
    //length=20
    public StructString msg;
    //length=100
    public StructByte slTriggered;
    //Leave as blank
    public StructLong exchOrderID;
    //Leave as blank
    public StructDate exchOrderTime;
    //Leave as blank
    public StructDate lastModifiedTime;
    //Leave as blank
    public StructDate lastTradedTime;
    //Leave as blank
    public StructInt trdQty;
    //Leave as blank
    public StructInt pendQty;
    //Leave as blank
    public StructInt oldQty;
    //In Modify/Cancel fill this with earlier Qty
    public StructLong oldBrokerOrderID;
    //In Modify/Cancel order fill this with earlier BrokerOrderID
    public StructLong nnfField;
    //To be used in case of dealer
    public StructByte expl;
    //43.---EXPL---CHAR---1 byte
    public StructByte exerciseOrder;
    //44.---ExerciseOrder---CHAR---1 byte
    public StructString securityID;
    //length=20 Leave as blank
    public StructShort timeInForce;
    public StructInt marketLot;
    //Market Lot as per ScripMaster
    public StructString imOrderID;
    // length=20 Leave as blank
    public StructByte preOpen;
    //Used for preopen orders. Y-Preopen, N-Normal
    public StructInt ecip;
    //Leave as blank
    public StructString routeID;
    // length=10 Modify/Cancel order fill this with RouteID received on Order Confirmation
    public StructByte retry;
    //Leave as blank
    public StructDate validityDate;
    //Leave as blank
    public StructShort seqNo;
    public StructByte bookType;
    public StructByte reserve;
    //Leave as blank
    public StructInt mktProt;
    //-Leave as blank
    public StructByte appID;
    public StructShort clientType;
    public StructBoolean preMkt;
    public StructString orderPlaceReqCode;//10
    public StructLong lastActivityReference;//8 added on 22Jul19
    public StructByte eets;//8 added on 22Jul19
    public StructInt positionID;//8 added on 22Jul19
    public StructMoney avgTrdPrice;

    public StructOrderRequest() { //415+8
        init();
        data = new StructValueSetter(fields);
        setDefValue();
    }

    public StructOrderRequest(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
            setDefValue();
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {

        className = getClass().getName();
        reqType = new StructShort("ReqType", 0);
        exchSegment = new StructInt("ExchSegment", 0);
        orderType = new StructShort("OrderType ", 0);
        token = new StructInt("Token", 0);
        symbol = new StructString("Symbol", 10, "");
        scripName = new StructString("ScripName", 50, "");
        series = new StructString("Series", 2, "");
        instType = new StructShort("InstType", 0);
        underlyingType = new StructShort("UnderlyingType", 0);
        expiryDate = new StructDate("ExpiryDate", 0);
        strikePrice = new StructMoney("StrikePrice", 0);
        qtyType = new StructInt("QtyType", 0);
        qty = new StructInt("Qty", 0);
        discQty = new StructInt("DiscQty", 0);
        limitPrice = new StructMoney("LimitPrice", 0);
        triggerPrice = new StructMoney("TriggerPrice", 0);
        atMarket = new StructByte("AtMarket", 0);
        stopLoss = new StructByte("StopLoss", 0);
        ioc = new StructByte("Ioc", 0);
        prodType = new StructShort("ProdType", 0);
        afterHours = new StructByte("AfterHours", 0);
        clientCode = new StructString("ClientCode", 10, "");
        orderRequestCode = new StructString("OrderRequestCode", 10, "");
        proClient = new StructShort("ProClient", 1);
        localOrderID = new StructShort("LocalOrderID", 0);
        localOrderTime = new StructDate("LocalOrderTime", 0);
        brokerOrderID = new StructLong("BrokerOrderID", 0);
        brokerOrderTime = new StructDate("BrokerOrderTime", 0);
        localStratID = new StructShort("LocalStratID", 0);
        stratID = new StructShort("StratID", 0);
        status = new StructString("Status", 20, "");
        msg = new StructString("Msg", 100, "");
        slTriggered = new StructByte("SlTriggered", 0);
        exchOrderID = new StructLong("ExchOrderID", 0);
        exchOrderTime = new StructDate("ExchOrderTime", 0);
        lastModifiedTime = new StructDate("LastModifiedTime", 0);
        lastTradedTime = new StructDate("LastTradedTime", 0);
        trdQty = new StructInt("TrdQty", 0);
        pendQty = new StructInt("PendQty", 0);
        oldQty = new StructInt("OldQty", 0);
        oldBrokerOrderID = new StructLong("OldBrokerOrderID", 0);
        nnfField = new StructLong("NnfField", 0);
        expl = new StructByte("Expl", 0);
        exerciseOrder = new StructByte("ExerciseOrder", 0);
        securityID = new StructString("SecurityID", 20, "");
        timeInForce = new StructShort("TimeInForce", 0);
        marketLot = new StructInt("MarketLot", 0);
        imOrderID = new StructString("ImOrderID", 20, "");
        preOpen = new StructByte("PreOpen", 0);
        ecip = new StructInt("Ecip", 0);
        routeID = new StructString("RouteID", 10, "");
        retry = new StructByte("Retry", 0);
        validityDate = new StructDate("ValidityDate", 0);
        seqNo = new StructShort("SeqNo", 0);
        bookType = new StructByte("SeqNo", 0);
        reserve = new StructByte("SeqNo", 0);
        mktProt = new StructInt("MktProt", 0);
        appID = new StructByte("AppID", 0);
        clientType = new StructShort("ClientType", 0);
        preMkt = new StructBoolean("PreMkt",false);
        orderPlaceReqCode = new StructString("OrderRequestCode", 10, "");
        lastActivityReference = new StructLong("lastActivityReference", 0);
        eets = new StructByte("eets", 0);
        positionID = new StructInt("positionID", 0);
        avgTrdPrice = new StructMoney("AvgTrdPrice",0);

        fields = new BaseStructure[]{
                reqType, exchSegment, orderType, token, symbol, scripName, series,
                instType, underlyingType, expiryDate, strikePrice, qtyType, qty, discQty,
                limitPrice, triggerPrice, atMarket, stopLoss, ioc, prodType, afterHours,
                clientCode, orderRequestCode, proClient, localOrderID, localOrderTime,
                brokerOrderID, brokerOrderTime, localStratID, stratID, status, msg, slTriggered,
                exchOrderID, exchOrderTime, lastModifiedTime, lastTradedTime, trdQty, pendQty,
                oldQty, oldBrokerOrderID, nnfField, expl, exerciseOrder, securityID, timeInForce,
                marketLot, imOrderID, preOpen, ecip, routeID, retry, validityDate, seqNo,bookType,
                reserve,mktProt,appID, clientType,preMkt,orderPlaceReqCode,lastActivityReference,
                eets, positionID,avgTrdPrice
        };
    }

    private String getCpType(int instrument) {
        //  0 - XX Future, -1 - All Option, 1 - CE Call Option, 2 - PE put option ,spread 3
        String cpType = "";
        switch (instrument) {
            case 0:
                cpType = "XX";
                break;
            case -1:
                cpType = "";
                break;
            case 1:
                cpType = "CE";
                break;
            case 2:
                cpType = "PE";
                break;
            case 3:
                cpType = "SE";//as of now may be change
                break;
        }
        return cpType;
    }

    private void setDefValue() {
        if (marketLot.getValue() == 0) {
            marketLot.setValue(1);
        }
        clientCode.setValue(clientCode.getValue().toUpperCase());
        orderRequestCode.setValue(orderRequestCode.getValue().toUpperCase());
    }



    public int getMktLotQty() {
        return qty.getValue() * marketLot.getValue();
    }

    public int getPendMktLotQty(){
        return pendQty.getValue() * marketLot.getValue();
    }

    public int getMktLotPendQty() {
        return (qty.getValue()-trdQty.getValue()) * marketLot.getValue();
    }

    public int getMktLotTrdQty() {
        return trdQty.getValue() * marketLot.getValue();
    }

    public int getMktLotDiscQty() {
        return discQty.getValue() * marketLot.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append( f.get(this) + ",");
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
    public String getHeader() {
        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + "," );
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
    public char getExch() {

        if(exchSegment.getValue() == eExch.BSE.value){
            return 'B';
        }else if(exchSegment.getValue() == eExch.NSE.value){
            return 'N';
        }else if(exchSegment.getValue() == eExch.FNO.value){
            return 'N';
        }else if(exchSegment.getValue() == eExch.NSECURR.value){
            return 'C';
        }
        return 'N';
    }

    public char getExchType() {

        if(exchSegment.getValue() == eExch.BSE.value){
            return 'C';
        }else if(exchSegment.getValue() == eExch.NSE.value){
            return 'C';
        }else if(exchSegment.getValue() == eExch.FNO.value){
            return 'D';
        }else if(exchSegment.getValue() == eExch.NSECURR.value){
            return 'D';
        }
        return 'C';
    }
    public char getBS() {

        if(orderType.getValue() == 0){
            return 'B';
        }else if(orderType.getValue() == 1){
            return 'S';
        }
        return 'B';
    }
    public double getlimitPriceRC(){
        if(exchSegment.getValue() == eExch.NSECURR.value){
            return limitPrice.getValue() / 100000;
        }else{
            return limitPrice.getValue();
        }
    }
    public double getTiggerPriceRC(){
        if(exchSegment.getValue() == eExch.NSECURR.value){
            return triggerPrice.getValue() / 100000;
        }
        else{
            return triggerPrice.getValue();
        }
    }
    public void setNNNField(){
        if(exchSegment.getValue() == eExch.NSE.value || exchSegment.getValue() == eExch.FNO.value
                || exchSegment.getValue() == eExch.NSECURR.value){
            nnfField.setValue(Long.parseLong("333333333333100"));
        }else if(exchSegment.getValue() == eExch.BSE.value){
            nnfField.setValue(Long.parseLong("5555555555555588"));
        }

    }
    public StructOrderReportReplyRecord_Pointer getGETPointerStructure(ScripDetail ssd){

        StructString scripNameTemp = new StructString("ScripName", 50, "");
        //scripNameTemp.setValue(ssd.getRCOrderBookScripName());

        StructOrderReportReplyRecord_Pointer orderRequest = new StructOrderReportReplyRecord_Pointer();
        orderRequest.exch.setValue(getExch());
        orderRequest.exchType.setValue(getExchType());
        int tokenTemp = this.token.getValue();
        if(exchSegment.getValue() == eExch.NSECURR.value){
            tokenTemp = tokenTemp - GlobalClass.currScripCodeAddition;
        }
        int tempQty = qty.getValue();
        int temppendQty = pendQty.getValue();
        int temptrdQty = trdQty.getValue();
        if(exchSegment.getValue() == eExch.FNO.value){
            tempQty = qty.getValue()* ssd.mktLot.getValue();
            temppendQty = pendQty.getValue() * ssd.mktLot.getValue();
            temptrdQty = trdQty.getValue()* ssd.mktLot.getValue();
        }
        orderRequest.scripCode.setValue(tokenTemp);
        orderRequest.scripNameLength.setValue(scripNameTemp.getValue().length());
        orderRequest.scripName.setValue(scripNameTemp.getValue());
        orderRequest.buySell.setValue(getBS());
        orderRequest.qty.setValue(tempQty);
        orderRequest.atMarket.setValue(atMarket.getValue() == 0?'N':'Y');
        orderRequest.rate.setValue(getlimitPriceRC());
        orderRequest.withSL.setValue(stopLoss.getValue() == 0?'N':'Y');
        orderRequest.triggerRate.setValue(getTiggerPriceRC());
        orderRequest.sLTriggered.setValue(slTriggered.getValue() == 0?'N':'Y');
        orderRequest.tradedQty.setValue(temptrdQty);
        orderRequest.pendingQty.setValue(temppendQty);
        orderRequest.brokerOrderID.setValue((int)brokerOrderID.getValue());
        orderRequest.brokerOrderTime.setValue(brokerOrderTime.getDateInNumber());
        orderRequest.exchOrderID.setValue(exchOrderID.getValue());
        orderRequest.exchOrderTime.setValue(exchOrderTime.getDateInNumber());
        orderRequest.statusLength.setValue(status.getLength());
        orderRequest.status.setValue(status.getValue());
        orderRequest.aHStatus.setValue(afterHours.getValue() == 0?'N':'Y');
        //orderRequest.orderType.setValue(getPointerOrderType());
        //orderRequest.productType.setValue(getPointerOrderType());
        //orderRequest.productType.setValue(getPointerOrderType());

        return orderRequest;
    }

    public Date getLastTime() {
        Date orderTime = new Date(80, 0, 1);
        if (lastModifiedTime.getDateInNumber() > 0) {
            orderTime = lastModifiedTime.getValue();
        } else if (exchOrderTime.getDateInNumber() > 0) {
            orderTime = exchOrderTime.getValue();
        }
        return orderTime;
    }
}