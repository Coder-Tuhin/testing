package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 1/27/2017.
 */
public class LogoutReq extends StructBase {
    public StructString clientCode;
    public StructString emieNo;

    public LogoutReq(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",14,"");
        emieNo = new StructString("emieNo",40,"");
        fields = new BaseStructure[]{
                clientCode ,emieNo
        };
    }
}
