package Structure.Request.BC;

import java.lang.reflect.Field;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructCustomDialogRequest extends StructBase {

    public StructString clientCode;//length=12
    public StructInt sl;

    public StructCustomDialogRequest() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructCustomDialogRequest(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void init() {
        className = getClass().getName();
        clientCode = new StructString("ClientCode", 12, "");
        sl = new StructInt("SL", 0);
        fields = new BaseStructure[]{
                clientCode,sl
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