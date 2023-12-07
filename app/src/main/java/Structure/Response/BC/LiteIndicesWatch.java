package Structure.Response.BC;

import java.lang.reflect.Field;

import enums.eDivider;
import enums.eExch;
import structure.StructBase;
import structure.BaseStructure;
import structure.StructValueSetter;
import structure.StructInt;
import structure.StructShort;
import structure.StructMoney;
import structure.StructDate;
import utils.Formatter;
import utils.GlobalClass;

public class LiteIndicesWatch extends StructBase {

    public StructInt token;
    public StructShort priceDivisor;
    public StructMoney lastRate;
    public StructMoney prevClose;
    public StructDate time;

    private float divider;
    private int round;

    public LiteIndicesWatch() { this(null);}

    public LiteIndicesWatch(byte[] bytes) {
        super();
        try {
            className = getClass().getName();
            token = new StructInt("token",0);
            priceDivisor = new StructShort("priceDivisor",(short)0);
            lastRate = new StructMoney("lastRate",0);
            prevClose = new StructMoney("newHiLo",0);
            time = new StructDate("time",0);
            fields = new BaseStructure[]{
                    token,priceDivisor,lastRate, prevClose,time
            };
            data = new StructValueSetter(fields, bytes);

            divider = eDivider.DIVIDER1.value;
            round = 2;
            if (priceDivisor.getValue() == eExch.NSECURR.value){
                divider = eDivider.DIVIDER100000.value;;
                round = 4;
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }


    public float getIndexRate(){
        return (float) Formatter.round(lastRate.getValue()/divider,round);
    }
    public float getPClose(){
        return (float) Formatter.round(prevClose.getValue()/divider,round);
    }
    public double getAbsChg(){
        double absChange = 0, lastRate = this.getIndexRate(),
                prevClose = this.getPClose();
        if ((lastRate != 0) && (prevClose != 0)) {
            absChange = (lastRate - prevClose);
        }
        return absChange;
    }

    public double getPerChg(){
        double perChange = 0,prevClose = this.getPClose();
        if (prevClose != 0) {
            perChange = (getAbsChg() / prevClose) * 100.00;
        }
        return perChange;
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
