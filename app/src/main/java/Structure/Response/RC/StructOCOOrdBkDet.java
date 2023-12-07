package Structure.Response.RC;

import android.graphics.Color;

import java.lang.reflect.Field;
import java.util.Date;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructDate;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOCOOrdBkDet extends StructBase {

    public StructInt segment;
    public StructInt scripCode;
    public StructString scripName;
    public StructInt positionID;
    public StructByte direction; //LONG = 1, SHORT = 2
    public StructShort productType;
    public StructInt qty;
    public StructMoney rate;
    public StructString entryEOI;
    public StructString tpEOI;
    public StructString slEOI;
    public StructDate entryTime;
    public StructDate openTime;
    public StructMoney openPrice;
    public StructString status;
    public StructByte ets; //None = 0,Entry = 1,TP = 2,SL = 3,Close = 4,All = 10

    public StructByte closeAtTP; //LTP = 1, ATP = 2
    public StructByte absTicksTP; //Abs = 1, Ticks = 2
    public StructMoney absValTP;
    public StructInt ticksTP;

    public StructByte closeAtSL; //LTP = 1, ATP = 2
    public StructByte absTicksSL; //Abs = 1, Ticks = 2
    public StructMoney absValSL;
    public StructInt ticksSL;

    public StructByte isTrailingSL;
    public StructMoney tickSize;
    public StructInt trlSL;

    public StructOCOOrdBkDet() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructOCOOrdBkDet(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        segment = new StructInt("Segment", 0);
        scripCode = new StructInt("ScripCode", 0);
        scripName = new StructString("scripname", 50, "");
        positionID = new StructInt("positionid", 0);
        direction = new StructByte("direction", 0);
        productType = new StructShort("producttype",0);
        qty = new StructInt("Qty", 0);
        rate = new StructMoney("rate", 0);
        entryEOI = new StructString("entryeoi", 20, "");
        tpEOI = new StructString("tpeoi", 20, "");
        slEOI = new StructString("sleoi", 20, "");
        entryTime = new StructDate("entryTime", new Date());
        openTime = new StructDate("OpenTime", new Date());
        openPrice = new StructMoney("OpenPrice", 0);
        status = new StructString("Status", 20, "");
        ets = new StructByte("ETS", 0);

        closeAtTP = new StructByte("closeAtTP", 0);
        absTicksTP = new StructByte("absTicksTP",0);
        absValTP = new StructMoney("absvaltp", 0);
        ticksTP = new StructInt("tickstp",0);

        closeAtSL = new StructByte("closeAtSL", 0);
        absTicksSL = new StructByte("absTicksSL",0);
        absValSL = new StructMoney("absvalSL", 0);
        ticksSL = new StructInt("ticksSL",0);

        isTrailingSL = new StructByte("istrailsl",0);
        tickSize = new StructMoney("ticksize", 0);
        trlSL = new StructInt("TrlSL", 0);

        fields = new BaseStructure[]{
                segment,scripCode,scripName,positionID,direction,productType,qty,rate,entryEOI,tpEOI,slEOI,entryTime,openTime,openPrice,
                status,ets,closeAtTP,absTicksTP,absValTP,ticksTP,closeAtSL,absTicksSL,absValSL,ticksSL,isTrailingSL,tickSize,trlSL
        };
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

    public String getOrderQtyRate() {

        String value = qty.getValue() +" @ " + rate.getValue();
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
}