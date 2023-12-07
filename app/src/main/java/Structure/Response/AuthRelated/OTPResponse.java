package Structure.Response.AuthRelated;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class OTPResponse extends StructBase {
    public StructByte success;
    public StructString OTP;
    public OTPResponse(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {
        success = new StructByte("success",0);
        OTP = new StructString("OTP",50,"");
        fields = new BaseStructure[]{
                success, OTP
        };
    }
    public String getOTP(){
        return OTP.getValue();
    }
}
