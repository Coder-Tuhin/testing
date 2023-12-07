package Structure.Response.AuthRelated;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public class FCMres extends StructBase {
    public StructString clientCode;
    public StructString msg;
    public FCMres(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {
        clientCode = new StructString("clientCode",15,"");
        msg = new StructString("msg",50,"");
        fields = new BaseStructure[]{
                clientCode,msg
        };
    }
}
