package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 1/25/2017.
 */
public class DeleteAlert extends StructBase {
    public StructString clientCode;
    public StructInt token;
    public StructFloat tokenRate;

    public DeleteAlert(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",15,"");
        token = new StructInt("token",0);
        tokenRate = new StructFloat("tokenRate",0);
        fields = new BaseStructure[]{
                clientCode ,token,tokenRate
        };
    }
}
