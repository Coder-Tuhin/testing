package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 11/17/2016.
 */
public class OpenAccountReq extends StructBase {
    public StructString charUserName ;
    public StructString charMobileNo ;
    public StructString charEmailID ;
    public StructString charState;
    public StructString charCity;
    public OpenAccountReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        charUserName =new StructString("charUserName ",30,"");
        charMobileNo =new StructString("charMobileNo ",10,"");
        charEmailID =new StructString("charEmailID ",50,"");
        charState=new StructString("charState",50,"");
        charCity=new StructString("charCity",50,"");
        fields = new BaseStructure[]{
                charUserName,charMobileNo,charEmailID,charState,charCity
        };
    }
}
