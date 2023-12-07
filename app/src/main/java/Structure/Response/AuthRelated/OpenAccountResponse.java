package Structure.Response.AuthRelated;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/17/2016.
 */
public class OpenAccountResponse extends StructBase {
    public StructChar success;
    public StructString MSG;
    public OpenAccountResponse(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {
        success = new StructChar("success",'N');
        MSG = new StructString("MSG",120,"");
        fields = new BaseStructure[]{
                success, MSG
        };
    }
}
