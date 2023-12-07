package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructMaintenanceMsg extends StructBase {

    public StructString errMsg;//length=200
    public StructShort errTag;
    public StructBoolean isExit;
    public StructString clientCode;

    public StructMaintenanceMsg() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructMaintenanceMsg(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void init() {
        className = getClass().getName();
        errMsg = new StructString("ErrorMsg", 255, "");
        errTag = new StructShort("ErrorTag", 0);
        isExit = new StructBoolean("isExit", false);
        clientCode = new StructString("clientCode", 10, "");
        fields = new BaseStructure[]{
                errMsg,errTag,isExit,clientCode
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
