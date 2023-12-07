package Structure.Response.AuthRelated;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 12/2/2016.
 */
public class ForgotPassResponse extends StructBase {
    public StructByte success;
    public StructString forgotSerialMsg;
    public ForgotPassResponse(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {
        success = new StructByte("success",0);
        forgotSerialMsg = new StructString("forgotSerialMsg",1000,"");
        fields = new BaseStructure[]{
                success, forgotSerialMsg
        };
    }

    public boolean getSuccess(){
        return success.getValue() == 1;
    }
    public String getMessage(){
        return forgotSerialMsg.getValue();
    }
}
