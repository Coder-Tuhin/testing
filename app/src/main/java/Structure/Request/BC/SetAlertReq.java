package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/24/2017.
 */
public class SetAlertReq extends StructBase {
    public StructString clientCode;
    public StructInt  token;
    public StructFloat tokenRate;
    public StructShort condition;

    public SetAlertReq(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",15,"");
        token = new StructInt("token",0);
        tokenRate = new StructFloat("tokenRate",0);
        condition = new StructShort("condition",0);
        fields = new BaseStructure[]{
                clientCode ,token,tokenRate,condition
        };
    }
}
