package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public class FCMReq extends StructBase {
    public StructString charuserId;
    public StructString charImeiNo;
    public StructString charMobileNo;
    public StructString token;
    public StructShort osType;
    public FCMReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        charuserId=new StructString("charuserId",15,"");
        charImeiNo=new StructString("charImeiNo",40,"");
        charMobileNo=new StructString("charMobileNo",12,"");
        token=new StructString("charRegId",255,"");
        osType=new StructShort("osType",0);
        fields = new BaseStructure[]{
                charuserId,charImeiNo,charMobileNo,token,osType
        };
    }
}
