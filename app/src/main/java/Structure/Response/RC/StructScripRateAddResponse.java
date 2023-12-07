package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructScripRateAddResponse extends StructBase {

    public StructString msg;

    public StructScripRateAddResponse(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructScripRateAddResponse(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        msg = new StructString("msg",50,"");


        fields = new BaseStructure[]{

                msg
        };

    }
}