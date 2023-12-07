package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class DeleteScripFromScripRateAlert  extends StructBase {
    public StructString clientCode;
    public StructInt token;

    public DeleteScripFromScripRateAlert(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",15,"");
        token=new StructInt("token",4);
        fields = new BaseStructure[]{
                clientCode,token
        };
    }
}
