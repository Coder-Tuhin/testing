package Structure.Request.RC;


import java.lang.reflect.Field;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructByte;
import structure.StructInt;
import structure.StructMoney;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOCOCloseDet extends StructBase {

    public StructByte CloseAt; //LTP = 1, ATP = 2
    public StructByte AbsTicks; //Abs = 1, Ticks = 2
    public StructMoney AbsVal;
    public StructInt Ticks;

    public StructOCOCloseDet() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructOCOCloseDet(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        CloseAt = new StructByte("closeat", 0);
        AbsTicks = new StructByte("absticks", 0);
        AbsVal = new StructMoney("absval", 0);//
        Ticks = new StructInt("ticks", 0);

        fields = new BaseStructure[]{
                CloseAt,AbsTicks,AbsVal,Ticks
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
}
