package Structure.Response.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructChgPwdResp extends StructBase {
    public StructByte status;
    public StructString statusMsg;


    public StructChgPwdResp(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructChgPwdResp(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        status = new StructByte("clientCode",0);
        statusMsg = new StructString("exchSegment",50,"");
        fields = new BaseStructure[]{

                status,statusMsg
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