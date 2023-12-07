package Structure.news;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import enums.eMobileOS;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 24-Nov-2017.
 */

public class StructNewsDisclaimer extends StructBase {

    public StructString clientCode;
    public StructByte isAgree;
    public StructString model; //15
    public StructShort mobileOS;
    public StructString IMEI; //50

    public StructNewsDisclaimer() {
        try {
            clientCode = new StructString("ClientCode", 15, "");
            isAgree = new StructByte("isagree", 1);
            model = new StructString("model", 15, "");
            mobileOS = new StructShort("mobileos", eMobileOS.ANDROID.value);
            IMEI = new StructString("imei", 50, "");
            fields = new BaseStructure[]{
                    clientCode,isAgree,model,mobileOS,IMEI
            };
            data = new StructValueSetter(fields);
        } catch (Exception ex) {
            GlobalClass.onError("Error in StructMobAddScripReq", ex);
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