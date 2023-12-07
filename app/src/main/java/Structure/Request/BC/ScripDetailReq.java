package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by xtremsoft on 11/18/16.
 */
public class ScripDetailReq extends StructBase {
    public StructInt scripCode;
    public StructString clientCode;

    public ScripDetailReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        scripCode =new StructInt("scripCode",4);
        clientCode=new StructString("clientCode",14,"");
        fields = new BaseStructure[]{
                scripCode,clientCode
        };
    }
}