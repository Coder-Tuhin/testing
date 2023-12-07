package Structure.Response.BC;

import java.lang.reflect.Field;

import structure.StructBase;
import structure.BaseStructure;
import structure.StructValueSetter;
import structure.StructInt;
import structure.StructShort;
import structure.StructMoney;
import structure.StructDate;
import utils.GlobalClass;

public class LiteMktWatch extends StructBase {

    public StructInt token;
    public StructShort segment;
    public StructMoney lastRate;
    public StructInt totalQty;
    public StructMoney newHiLo;
    public StructMoney avgRate;
    public StructInt bidQty;
    public StructMoney bidRate;
    public StructInt offQty;
    public StructMoney offRate;
    public StructDate time;
    public StructInt openInterest;
    public StructInt lastQty;
    public StructMoney pClose;

    public LiteMktWatch() { this(null);}

    public LiteMktWatch(byte[] bytes) {
        super();
        try {
            className = getClass().getName();
            token = new StructInt("token",0);
            segment = new StructShort("priceDivisor",(short)0);
            lastRate = new StructMoney("lastRate",0);
            totalQty = new StructInt("totalQty",0);
            newHiLo = new StructMoney("newHiLo",0);
            avgRate = new StructMoney("avgRate",0);
            bidQty = new StructInt("bidQty",0);
            bidRate = new StructMoney("bidRate",0);
            offQty = new StructInt("offQty",0);
            offRate = new StructMoney("offRate",0);
            time = new StructDate("time",0);
            openInterest = new StructInt("openInterest",0);
            lastQty = new StructInt("lastQty",0);
            pClose = new StructMoney("pClose",0);

            fields = new BaseStructure[]{
                    token,segment,lastRate,totalQty,newHiLo,avgRate,bidQty,bidRate,
                    offQty,offRate,time,openInterest,lastQty,pClose
            };
            data = new StructValueSetter(fields, bytes);


        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
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
}
