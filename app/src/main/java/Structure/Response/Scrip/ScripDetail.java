package Structure.Response.Scrip;

import Structure.BaseStructure.StructBase;
import enums.eExch;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.VenturaException;

/**
 * Created by xtremsoft on 11/18/16.
 */
public class ScripDetail extends StructBase {

    public StructString symbol;
    public StructString cpType;
    public StructInt expiry;
    public StructInt strikePrice;
    public StructInt underlyingType;
    public StructInt mktLot;
    public StructMoney tickSize;
    public StructInt segment;
    public StructInt scripCode;
    public StructInt qtyLimit;
    public StructShort isNearNext;

    public StructString category;
    public StructString series;

    public StructByte ban;
    public StructByte T4T;
    public StructByte Ill;
    public StructByte isAllowtoTrade;
    public StructMoney spanMargin;
    public StructMoney exposerMargin;
    public StructByte isIntradayAllow; //added on 24Jul19
    public StructMoney scripMargin;
    public StructByte isPhysicalDelivery;
    public StructByte recall;
    public StructByte repay;
    public StructString isin; //20 byte
    public StructString underlying; //20 byte
    public StructInt nseScripCode; //20 byte
    public StructString surveillanceMassage;

    public ScripDetail(){
        init();
        data = new StructValueSetter(fields);

    }
    public ScripDetail(byte[] bytes){
        init();
        data = new StructValueSetter(fields,bytes);
    }

    private void init() {
        symbol=new StructString("symbol",20,"");
        cpType=new StructString("cpType",2,"");
        expiry=new StructInt("expiry",0);
        strikePrice=new StructInt("strikePrice",0);
        underlyingType=new StructInt("undType",0);
        mktLot=new StructInt("mktLot",1);
        tickSize=new StructMoney("tickSize",1);
        segment=new StructInt("seg",0);
        scripCode=new StructInt("scripCode",0);
        qtyLimit=new StructInt("limitQty",0);
        isNearNext=new StructShort("isnearnext",0);

        category=new StructString("category",2,"");
        series=new StructString("series",2,"");

        ban = new StructByte("ban",0);
        T4T = new StructByte("T4T",0);
        Ill = new StructByte("Ill",0);
        isAllowtoTrade = new StructByte("isAllowtoTrade",0);
        spanMargin=new StructMoney("spanMargin",0);
        exposerMargin = new StructMoney("exposerMargin",0);
        isIntradayAllow = new StructByte("isIntradayAllow",0);
        scripMargin = new StructMoney("ScripMargin",0);
        isPhysicalDelivery = new StructByte("isPhysicalDelivery",0);
        recall = new StructByte("recall", 0);
        repay = new StructByte("repay", 0);
        isin = new StructString("ISIN", 20, "");
        underlying = new StructString("underlying", 10, "");
        nseScripCode = new StructInt("nseScripCode", 0);
        surveillanceMassage = new StructString("SurveillanceMassage",250,"");

        fields = new BaseStructure[]{
                symbol,cpType,expiry,strikePrice,underlyingType
                ,mktLot,tickSize,segment,scripCode,qtyLimit,
                isNearNext,category,series,ban,T4T,Ill,isAllowtoTrade,
                spanMargin,exposerMargin,isIntradayAllow,scripMargin,
                isPhysicalDelivery,recall,repay,isin,underlying,nseScripCode,
                surveillanceMassage
        };
    }

    public int getMKtLotForPlaceOrdg(){
        if(segment.getValue() == eExch.NSECURR.value){
            return 1;
        }
        else{
            return mktLot.getValue();
        }
    }
    public String getFormattedScripName() {

        String scripName = "";
        try {
            switch (segment.getValue()) {
                case 0:
                    scripName = "NE-" + symbol.getValue();
                    break;
                case 1:
                    scripName = "BE-" + symbol.getValue();
                    break;
                case 2:
                    scripName = "N";
                    String cptype = cpType.getValue();
                    int expiryl = expiry.getValue();
                    String strikRate = Formatter.formatter.format(((double)strikePrice.getValue())/100);
                    if (cptype.equalsIgnoreCase("") || cptype.equalsIgnoreCase("XX")) {
                        scripName = scripName + "F-" + symbol.getValue() + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy");
                    } else {
                        scripName = scripName + "O-" + symbol.getValue() + "-" + cptype + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy") + "-" + strikRate;
                    }
                    break;
                case 3:
                    scripName = "C";
                    cptype = cpType.getValue();
                    expiryl = expiry.getValue();
                    strikRate = Formatter.formatter.format(((double)strikePrice.getValue())/100);
                    if (cptype.equalsIgnoreCase("") || cptype.equalsIgnoreCase("XX")) {
                        scripName = scripName + "D-" + symbol.getValue() + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy");
                    } else {
                        scripName = scripName + "D-" + symbol.getValue() + "-" + cptype + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy") + "-" + strikRate;
                    }
                    break;
                case 19:
                    scripName = "S";
                    cptype = series.getValue();
                    expiryl = expiry.getValue();
                    scripName = scripName + "D-" + symbol.getValue() + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy") + "-" + cptype;
                    break;

            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
        return scripName;
    }

    //to check whether scrip is allowed for intraday/delivery or not
    public boolean enableIntraDelForCategory() {
        boolean scripCat = false;
        try{
            /*
            String [] cat = {"Q","A"};      //only Q & A are allowed(enter category that are allowed to trade)
            for (String aCat : cat) {
                if (category.getValue().equalsIgnoreCase(aCat)) {
                    scripCat = true;
                    break;
                }
            }*/
            scripCat = (isIntradayAllow.getValue()==1);

        } catch (Exception e){
            VenturaException.Print(e);
        }
        return scripCat;
    }

    public byte getInstrumentType(){
        //73 is index,
        if(segment.getValue() == eExch.SLBS.value){
            return (byte)19;
        }
        else if (cpType.getValue().equalsIgnoreCase("XX")) {
            if(segment.getValue() == eExch.NSECURR.value){
                char fDigit = symbol.getValue().charAt(0);
                return (byte) (Character.isDigit(fDigit)?9:6);
            }else{
                return (byte) ((underlyingType.getValue() == 73)?1:3);
            }
        } else {
            if(segment.getValue() == eExch.NSECURR.value){
                char fDigit = symbol.getValue().charAt(0);
                return (byte) (Character.isDigit(fDigit)?9:7);
            }
            else{
                return (byte) (underlyingType.getValue() == 73?2:4);
            }
        }
    }
    public byte getCPTypeForOrderPlace(){
        if(cpType.getValue().equalsIgnoreCase("CE")){
            return 3;
        }
        else if (cpType.getValue().equalsIgnoreCase("PE")){
            return 4;
        }
        else{
            return 0;
        }
    }
    public  float getStrikeRateForOrderPlacing(){

        if(getCPTypeForOrderPlace() == 0){
            if(segment.getValue() == eExch.NSECURR.value){
                return 0;
            }
            return -0.01f;
        } else{
            return (float)strikePrice.getValue()/100;
        }
    }
    public boolean isT4TILLequide(){
        if(segment.getValue() != eExch.FNO.value){
            if(T4T.getValue() == 1 && Ill.getValue() == 1){
                return true;
            }
        }
        return false;
    }
    public String getT4TMsg(){
        if(T4T.getValue() == 1){
            String strMsg = getFormattedScripName() + " is in Trade To Trade Segment. Square Off is not allowed. You will have to take delivery of shares. Do you really wish to place the order?";
            return strMsg;
        }
        return "";
    }
    public String getIllequideMsg(){
        if(Ill.getValue() == 1){
            String strMsg = getFormattedScripName() + " is in Illiquid as per\nExchange circular and order matching will be as per auction session. Do you really wish to place the order? ";
            return strMsg;
        }
        return "";
    }
    public String getBanScripMsg(){
        if(ban.getValue() == 1){
            String strMsg = getFormattedScripName() + "  is in Ban Period. Position increased will be penalised by NSE. Do you really wish to place the order? ";
            return strMsg;
        }
        return "";
    }
    public String getPhysicalDeliveryMsg(){
        if(isPhysicalDelivery.getValue() == 1){
            String strMsg = getFormattedScripName() + " is under Physical Delivery Settlement. Exchange will levy additional delivery margin which needs to be made available before end of the day to avoid unilateral Square off.";
            return strMsg;
        }
        return "";
    }
    public double getTickSize(){
        if(segment.getValue() == eExch.NSECURR.value){
            //double val
            //double r = Math.round(tickSize.getValue()*10000);
            //valaue = r/10000;
            return (double) tickSize.getValue()/100;
        }
        else{
            return  (double) tickSize.getValue();
        }
    }

    public String getExpiryDDMMMYYYY() {
        return DateUtil.dateFormatter(expiry.getValue(),"dd-MMM-YYYY");
    }

    public String getUnderlyingTyep() {
       return underlyingType.getValue() == 73? "I":"S";
    }
}