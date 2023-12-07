package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/24/2017.
 */
public class SetNotificationRes extends StructBase {
    public StructString clientCode;
    public StructString msg;

    public SetNotificationRes() {
        init();
        data=new StructValueSetter(fields);
    }
    public SetNotificationRes(byte []bytes) {
        try {
            init();
            data=new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className, ex);
        }
    }
    private void init(){
        className=getClass().getName();
        clientCode=new StructString("clientCode",15 ,"");
        msg=new StructString("msg",50 ,"");

        fields=new BaseStructure[]{
                clientCode,msg
        };
    }
}
