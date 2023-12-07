package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 12/2/2016.
 */
public class ForgotPassVerifyReq extends StructBase {
    public StructString charuserId;
    public StructString charPasscode;
    public StructString charSerial;
    public ForgotPassVerifyReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        charuserId=new StructString("charuserId",50,"");
        charPasscode=new StructString("charPasscode",50,"");
        charSerial=new StructString("charSerial",50,"");
        fields = new BaseStructure[]{
                charuserId,charPasscode,charSerial
        };
    }
}
