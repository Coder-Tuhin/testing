package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class StateRequest extends StructBase {
    public StructString clientCode;
    public StateRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode=new StructString("clientCode",15,"");
        fields = new BaseStructure[]{
                clientCode
        };
    }
}
