package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructLogOffResp extends StructBase {
    public StructString msg;
    //public StructShort errorTag;

    public StructLogOffResp(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructLogOffResp(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        msg = new StructString("msg",255,"");
        //errorTag = new StructShort("errorTag",0);
        fields = new BaseStructure[]{
            msg
        };
    }
}