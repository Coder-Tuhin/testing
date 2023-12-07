package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class OTPRequest extends StructBase {
    public StructString clientCode;
    public StructString mobileNumber;
    public OTPRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode=new StructString("clientCode",12,"");
        mobileNumber=new StructString("mobileNumber",10,"");
        fields = new BaseStructure[]{
                clientCode,mobileNumber
        };
    }
}
