package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class AddScripInScripRateAlert  extends StructBase {
    public StructString ClientCode;
    public StructInt token;
    public StructFloat tokenRate;
    public StructShort condition;

    public AddScripInScripRateAlert(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        ClientCode=new StructString("clientCode",15,"");
        token=new StructInt("token",4);
        tokenRate = new StructFloat("tokenRate",4);
        condition = new StructShort("condition",2);


        fields = new BaseStructure[]{
                ClientCode,token,tokenRate
        };
    }
}
