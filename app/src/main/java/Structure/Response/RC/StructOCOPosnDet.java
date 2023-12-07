package Structure.Response.RC;

import android.graphics.Color;

import java.lang.reflect.Field;
import java.util.Date;

import Structure.BaseStructure.StructBase;
import enums.eExch;
import enums.eProductType;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructDate;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.Formatter;
import utils.GlobalClass;

public class StructOCOPosnDet extends StructBase {

    public StructInt segment;
    public StructInt scripCode;
    public StructString scripName;
    public StructInt positionID;
    public StructByte direction; //LONG = 1, SHORT = 2
    public StructShort productType; //LONG = 1, SHORT = 2
    public StructInt qty;
    public StructDate entryTime;
    public StructDate openTime;
    public StructMoney openPrice;
    public StructMoney takeProf;
    public StructMoney stopLoss;
    public StructString status;
    public StructMoney currRate;
    public StructMoney closePrice;
    public StructDate closeTime;
    public StructInt closeQty;
    public StructMoney bkpl;
    public StructMoney mtmpl;
    public StructMoney totpl;

    private void init() {

        className = getClass().getName();
        segment = new StructInt("Segment", 0);
        scripCode = new StructInt("ScripCode", 0);
        scripName = new StructString("scripname", 50, "");
        positionID = new StructInt("positionid", 0);
        direction = new StructByte("direction", 0);
        productType = new StructShort("ProductType",0);
        qty = new StructInt("Qty", 0);
        entryTime = new StructDate("entryTime", new Date());
        openTime = new StructDate("OpenTime", new Date());
        openPrice = new StructMoney("OpenPrice", 0);
        takeProf = new StructMoney("TakeProfit", 0);
        stopLoss = new StructMoney("StopLoss", 0);
        status = new StructString("Status", 20, "");
        currRate = new StructMoney("currRate", 0);
        closePrice = new StructMoney("ClosePrice", 0);
        closeTime = new StructDate("CloseTime", new Date());
        closeQty = new StructInt("closeQty",0);
        bkpl = new StructMoney("bkpl", 0);
        mtmpl = new StructMoney("MTMPL", 0);
        totpl = new StructMoney("TotPL", 0);

        fields = new BaseStructure[]{
            segment,scripCode,scripName,positionID,direction,productType,qty,entryTime,
            openTime,openPrice,takeProf,stopLoss,status,currRate,closePrice,closeTime,
            closeQty,bkpl,mtmpl,totpl
        };
    }
    public StructOCOPosnDet() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructOCOPosnDet(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
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

    public String getBuySell() {
        if(qty.getValue() > 0){
            return "Buy";
        }else{
            return "Sell";
        }
    }

    public String getNetQtyRate() {

        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(openPrice.getValue()):Formatter.toTwoDecimalValue(openPrice.getValue());
        String value =  qty.getValue() +" @ " + strRate;
        return value;
    }

    public String getFinalStatus() {
        return status.getValue();
    }
    public int getTextColor(){
        int textColor = Color.WHITE;
        String st = getFinalStatus().toLowerCase();
        if(st.contains("rejected") || st.contains("cancelled") || st.contains("frozen")){
            textColor = Color.rgb(189,183,107);
        }
        return textColor;
    }

    public int getOpenQty() {
        return qty.getValue() - closeQty.getValue();
    }

    public String getEntryDetail() {
        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(openPrice.getValue()):Formatter.toTwoDecimalValue(openPrice.getValue());

        String value = qty.getValue() +" @ " + strRate;
        return value;
    }
    public String getCloseDetail() {
        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(closePrice.getValue()):Formatter.toTwoDecimalValue(closePrice.getValue());

        String value = closeQty.getValue() +" @ " + strRate;
        return value;
    }

    public String getOrderTypeStr() {
        if(productType.getValue() == eProductType.BRACKETORDER.value){
            return eProductType.BRACKETORDER.name;
        }else if(productType.getValue() == eProductType.COVERORDER.value){
            return eProductType.COVERORDER.name;
        }else if(productType.getValue() == eProductType.DELIVERY.value){
            return eProductType.DELIVERY.name;
        }else if(productType.getValue() == eProductType.INTRADAY.value){
            return eProductType.INTRADAY.name;
        }
        return "";
    }

    public String getScripnameWithOrderType() {

        String  value = scripName.getValue() + " ("+getOrderTypeStr()+")";
        return value;
    }

    public String getCurrentRate() {
        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(currRate.getValue()):Formatter.toTwoDecimalValue(currRate.getValue());
        return strRate;
    }
    public String getTakeProfitValue() {
        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(takeProf.getValue()):Formatter.toTwoDecimalValue(takeProf.getValue());
        return strRate;
    }
    public String getStopLossValue() {
        String strRate = (segment.getValue() == eExch.NSECURR.value)?
                Formatter.getFourDigitFormatter(stopLoss.getValue()):Formatter.toTwoDecimalValue(stopLoss.getValue());
        return strRate;
    }

}