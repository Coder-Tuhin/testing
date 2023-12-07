package Structure.Response.BC;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;
import utils.GlobalClass;

public class FnoNSE_OIData extends StructBase {

    public StructInt token;
    public StructInt openInterest;
    public StructInt dayHiOI;
    public StructInt dayLoOI;

    public FnoNSE_OIData() { this(null);}

    public FnoNSE_OIData(byte[] bytes) {
        super();
        try {
            className = getClass().getName();

            token = new StructInt("token", 0);
            openInterest = new StructInt("openInterest", 0);
            dayHiOI = new StructInt("dayHiOI", 0);
            dayLoOI = new StructInt("dayLoOI", 0);

            fields = new BaseStructure[]{
                    token,openInterest,dayHiOI,dayLoOI
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
