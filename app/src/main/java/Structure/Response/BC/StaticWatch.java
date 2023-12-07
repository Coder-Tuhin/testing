package Structure.Response.BC;


import java.lang.reflect.Field;
import structure.StructBase;
import structure.BaseStructure;
import structure.StructValueSetter;
import structure.StructInt;
import structure.StructMoney;
import structure.StructDate;
import structure.StructLong;
import utils.GlobalClass;

public class StaticWatch extends StructBase {

    public StructInt token;
    public StructInt segment;
    public StructMoney high;
    public StructMoney low;
    public StructMoney openRate;
    public StructMoney pClose;
    public StructDate time;
    public StructInt dayHiOI;
    public StructInt dayLoOI;
    public StructMoney upperCircuit;
    public StructMoney lowerCircuit;
    public StructMoney highestEver;
    public StructMoney lowestEver;
    public StructLong xToken;
    public StructInt prevOI;
    public StructMoney higherTrdExc;
    public StructMoney lowerTrdExc;


    public StaticWatch() { this(null);}

    public StaticWatch(byte[] bytes) {
        super();
        try {
            className = getClass().getName();
            token = new StructInt("token",0);
            segment = new StructInt("segment",0);
            high = new StructMoney("high",0);
            low = new StructMoney("low",0);
            openRate = new StructMoney("openRate",0);
            pClose = new StructMoney("pClose",0);
            time = new StructDate("time",0);
            dayHiOI = new StructInt("dayHiOI",0);
            dayLoOI = new StructInt("dayLoOI",0);
            upperCircuit = new StructMoney("upperCircuit",0);
            lowerCircuit = new StructMoney("lowerCircuit",0);
            highestEver = new StructMoney("highestEver",0);
            lowestEver = new StructMoney("lowestEver",0);
            xToken = new StructLong("xToken",0);
            prevOI = new StructInt("prevOI",0);
            higherTrdExc = new StructMoney("higherTrdExc", 0);
            lowerTrdExc = new StructMoney("lowerTrdExc", 0);

            fields = new BaseStructure[]{
                    token,segment,high,low,openRate,pClose,time,dayHiOI,dayLoOI,upperCircuit,lowerCircuit
                    ,highestEver,lowestEver,xToken,prevOI,higherTrdExc,lowerTrdExc
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