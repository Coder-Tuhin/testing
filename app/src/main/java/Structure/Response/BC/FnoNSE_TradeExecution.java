package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructMoney;
import structure.StructValueSetter;
import utils.GlobalClass;

public class FnoNSE_TradeExecution extends StructBase {

    public StructInt token;
    public StructMoney higherTrdExc;
    public StructMoney lowerTrdExc;

    public FnoNSE_TradeExecution() { this(null);}

    public FnoNSE_TradeExecution(byte[] bytes) {
        super();
        try {
            className = getClass().getName();

            token = new StructInt("token", 0);
            higherTrdExc = new StructMoney("higherTrdExc", 0);
            lowerTrdExc = new StructMoney("lowerTrdExc", 0);

            fields = new BaseStructure[]{
                    token,higherTrdExc,lowerTrdExc
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
