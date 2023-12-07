package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class GroupListReq extends StructBase {
    public StructInt segment;
    public StructString clientCode;

    public GroupListReq(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        segment=new StructInt("scripCode",4);
        clientCode=new StructString("clientCode",14,"");
        fields = new BaseStructure[]{
                segment,clientCode
        };
    }
}