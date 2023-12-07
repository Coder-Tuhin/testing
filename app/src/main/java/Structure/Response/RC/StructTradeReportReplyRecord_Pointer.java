package Structure.Response.RC;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import Structure.BaseStructure.StructBase;
import enums.eDelvIntra;
import enums.eExch;
import enums.eOrderType;
import enums.eProductTypeITS;
import enums.eServerType;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructTradeReportReplyRecord_Pointer extends StructBase {

    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructChar buySell;
    public StructInt qty;
    public StructFloat rate;
    public StructInt orgQty;
    public StructInt pendingQty;
    public StructLong exchOrderID;
    public StructInt exchTradeID;
    public StructInt exchTradeTime;
    public StructByte orderType;
    public StructByte productType;
    public StructString slbmType;
    public StructShort reserved1;
    public StructInt reserved2;

    public int mktLot;
    public int exchange;

    public StructTradeReportReplyRecord_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructTradeReportReplyRecord_Pointer(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
        if(exch.getValue() == 'C' && exchType.getValue() == 'D'){
            scripCode.setValue(scripCode.getValue() + GlobalClass.currScripCodeAddition);
            orderType.setValue(eDelvIntra.DELIVERY.value);
        }else if(exch.getValue() == 'S'){
            scripCode.setValue(GlobalClass.getSLBS_ScripCodeForNeutrino(scripCode.getValue()));
            orderType.setValue(eDelvIntra.DELIVERY.value);
        }
        else if(!isIntraDelAllow()){
            orderType.setValue(eDelvIntra.DELIVERY.value);
        }
    }
    private void init() {

        exch = new StructChar("exch",'0');
        exchType = new StructChar("exchType",'0');
        scripCode = new StructInt("scripCode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",50,"");
        buySell = new StructChar("buySell",'0');
        qty = new StructInt("qty",0);
        rate = new StructFloat("rate",0);
        orgQty = new StructInt("orgQty",0);
        pendingQty = new StructInt("pendingQty",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchTradeID = new StructInt("exchTradeID",0);
        exchTradeTime = new StructInt("exchTradeTime",0);
        orderType = new StructByte("orderType",0);
        productType = new StructByte("productType",0);
        slbmType = new StructString("scripName",2,"");
        reserved1 = new StructShort("reserved1",0);
        reserved2 = new StructInt("reserved2",0);

        fields = new BaseStructure[]{
                exch,exchType,scripCode,scripNameLength,scripName,buySell,qty,rate,orgQty,
                pendingQty,exchOrderID,exchTradeID,exchTradeTime,orderType,productType,
                slbmType,reserved1,reserved2
        };
    }


    public String getFormatedScripName(boolean withIntrDel){
        char exchT = 'E';
        String strOrderType = "";
        if(exchType.getValue() == 'C'){
            exchT = 'E';
            if(UserSession.getLoginDetailsModel().isIntradayDelivery() && withIntrDel){
                strOrderType = " ("+getOrderTypeStr()+")";
            }
        }
        else if (exch.getValue() == 'C' || exch.getValue() == 'S'){
            exchT = 'D';
        }
        else{
            exchT = 'F';
            String[] symbols = scripName.getValue().split("-");
            if(symbols.length > 2) {
                exchT = 'O';
                //withIntrDel = false;
                //orderType.setValue(1);
            }
            else{
                String[] symbolArr = scripName.getValue().split(" ");
                if(symbolArr.length > 4){
                    exchT = 'O';
                    //withIntrDel = false;
                    //orderType.setValue(1);
                }
            }
            if(UserSession.getLoginDetailsModel().isFNOIntradayDelivery() && withIntrDel){
                strOrderType = "\n("+getOrderTypeStr()+")";
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
                /*String[] symbols = scripName.getValue().split("-");
                if(symbols.length > 2) {
                    isFuture = false;
                }
                else{
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

    public String getTradeQtyRate(){
        NumberFormat formatter = Formatter.getFormatter(getExchange());
        String strOrdQtyRate = qty.getValue() + " @ "+ formatter.format(rate.getValue());
        return strOrdQtyRate;
    }
    public String getBuySell(){
        String strBS = "";
        if(buySell.getValue() == 'B'){
            strBS = getExchange() == eExch.SLBS.value? "Recall":"Buy";
        }
        else if(buySell.getValue() == 'S'){
            strBS = getExchange() == eExch.SLBS.value? "Lend":"Sell";
        }else{
            strBS = " ";
        }
        return strBS;
    }

    public String getOrderTypeStr() {
        if(orderType.getValue() == eProductTypeITS.INTRADAY.value){
            return eProductTypeITS.INTRADAY.name;
        }
        else if(orderType.getValue() == eProductTypeITS.BRACKETORDER.value){
            return eProductTypeITS.BRACKETORDER.name;
        }
        else if(orderType.getValue() == eProductTypeITS.COVERORDER.value){
            return eProductTypeITS.COVERORDER.name;
        }else{
            return eProductTypeITS.DELIVERY.name;
        }
    }
    public int getExchange(){
        if(exch.getValue() == 'N'){
            if(exchType.getValue() == 'C'){
                return eExch.NSE.value;
            }else{
                return eExch.FNO.value;
            }
        }else if(exch.getValue() == 'B'){
            return eExch.BSE.value;
        }else if(exch.getValue() == 'S'){
            return eExch.SLBS.value;
        }else if(exch.getValue() == 'C'){
            return eExch.NSECURR.value;
        }
        return -1;
    }

    private String _symbol = "";

    public String getSymbol(){
        if (_symbol.equalsIgnoreCase(""))
        try {
            String _tempSymbol = scripName.getValue();
            String[] _tempSplit = _tempSymbol.split("-");
            if (_tempSplit.length>1){
                if(UserSession.getClientResponse().getServerType() == eServerType.RC){
                    _tempSymbol = _tempSplit[0];
                }else {
                    _tempSymbol = _tempSplit[1];
                }
            }
            _tempSplit = _tempSymbol.split(" ");
            if (_tempSplit.length>0){
                _tempSymbol = _tempSplit[0];
            }
            _symbol = _tempSymbol;
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return _symbol;
    }
    public String getConfMsg(){
        String conf = "";
        NumberFormat formatter = Formatter.getFormatter(getExchange());
        if (buySell.getValue() == 'B') {
            conf = "Traded : Buy " + qty.getValue() + " " + getFormatedScripName(false) + " @ " + formatter.format(rate.getValue());
        } else {
            conf = "Traded : Sell " + qty.getValue() + " " + getFormatedScripName(false) + " @ " + formatter.format(rate.getValue());
        }
        return conf;
    }
    public String getKey(){
        return exchOrderID.getValue()+""+exchTradeID.getValue();
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