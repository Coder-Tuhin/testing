package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructCurrSymbol extends StructBase {

    public StructString symbol;
    public StructInt mktLot;

    public StructCurrSymbol() {
        init(450);
        data = new StructValueSetter(fields);
    }

    public StructCurrSymbol(byte bytes[]) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }
    private void init(int length) {
        className = getClass().getName();
        symbol = new StructString("Symbol", 10, "");
        mktLot = new StructInt("mktLot", 1);
        fields = new BaseStructure[]{
                symbol,mktLot
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
