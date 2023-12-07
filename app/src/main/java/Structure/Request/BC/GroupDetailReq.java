package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class GroupDetailReq extends StructBase {
    public StructLong groupCode;
    public StructString clientCode;
    public StructInt segment;
    public StructByte collumnTag;


    public GroupDetailReq(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        groupCode=new StructLong("groupCode",8);
        clientCode=new StructString("clientCode",12,"");
        segment = new StructInt("Segmeny",0);
        collumnTag = new StructByte("",0);
        fields = new BaseStructure[]{
                groupCode,clientCode,segment,collumnTag
        };
    }
}
