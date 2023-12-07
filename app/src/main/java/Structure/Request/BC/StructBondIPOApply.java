package Structure.Request.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructBondIPOApply extends StructBase {

    public StructString clientCode;//10
    public StructString BondOrIPO;//15
    public StructShort BondORIPOTag; // 1 - Yes, 0- No



    public StructBondIPOApply() {
        this(null);
    }
    public StructBondIPOApply(byte bytes[]) {
        try {
            className = getClass().getName();
            clientCode = new StructString("clientcode", 10, "");
            BondOrIPO = new StructString("bondoripo", 15, "");
            BondORIPOTag = new StructShort("bondoripotag", 0);


            fields = new BaseStructure[]{
                    clientCode, BondOrIPO, BondORIPOTag
            };
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
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