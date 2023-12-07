package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class AddGroup extends StructBase {
    public StructString clientCode;
    public StructString groupName;
    public StructLong grpCode;

    public AddGroup(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",12,"");
        groupName =new StructString("groupName",30,"");
        grpCode = new StructLong("GroupCode",0);
        fields = new BaseStructure[]{
                clientCode, groupName,grpCode
        };
    }
}
