package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 12/2/2016.
 */
public class ForgotPassReq extends StructBase {
    public StructString charuserId;
    public StructString charPan;
    public StructString charImeiNo;
    public StructString charDob;
    public ForgotPassReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        charuserId=new StructString("charuserId",20,"");
        charPan=new StructString("charPan",20,"");
        charImeiNo=new StructString("charImeiNo",50,"");
        charDob=new StructString("charDob",20,"");
        fields = new BaseStructure[]{
                charuserId,charPan,charImeiNo,charDob
        };
    }
}
