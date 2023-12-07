package Structure.Response.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by xtremsoft on 11/23/16.
 */
public class StructLoginFailure extends StructBase {

    public StructString msg;
    public StructShort errorTag;

    public StructLoginFailure(){
        init(300);
        data= new StructValueSetter(fields);
    }
    public StructLoginFailure(byte[] bytes){
        init(bytes.length);
        data= new StructValueSetter(fields,bytes);
    }
    private void init(int dataLegth) {
        msg = new StructString("callMsg",200,"");
        errorTag = new StructShort("errorTag",0);

        if(dataLegth<=200) {
            fields = new BaseStructure[]{
                    msg
            };
        }
        else{
            fields = new BaseStructure[]{
                    msg, errorTag
            };
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