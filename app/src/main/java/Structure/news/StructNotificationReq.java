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

public class StructNotificationReq extends StructBase {

    public StructString clientCode;
    public StructShort mobileOSType;
    public StructShort msgType;
    public StructInt fromMsgTime;
    public StructInt toMsgTime;
    public StructString companyName;


    public StructNotificationReq() {
        try {
            clientCode = new StructString("ClientCode", 15, "");
            mobileOSType = new StructShort("MobileOS", 1);
            msgType = new StructShort("MsgType", 0);
            fromMsgTime = new StructInt("fromMsgTime", 0);
            toMsgTime = new StructInt("toMsgTime", 0);
            companyName = new StructString("companyName", 50, "");

            fields = new BaseStructure[]{
                    clientCode,mobileOSType,msgType,fromMsgTime,toMsgTime,companyName
            };
            data= new StructValueSetter(fields);
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
