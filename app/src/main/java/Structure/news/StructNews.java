package Structure.news;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 21-Nov-2017.
 */

public class StructNews extends StructBase {
    public StructString msg;       //3. Message---CHAR--50bytes
    public StructInt msgTime;       //3. Message---CHAR--50bytes
    public StructShort msgType;       //3. Message---CHAR--50bytes

    public StructNews() {
        init(400);
        data = new StructValueSetter(fields);
    }
    public StructNews(byte bytes[]) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }
    private void init(int length) {

        className = getClass().getName();
        msg = new StructString("Msg", 500, "");
        msgTime = new StructInt("msgtimw", 0);
        msgType = new StructShort("msgType", 0);
        fields = new BaseStructure[]{
                msg, msgTime, msgType
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
