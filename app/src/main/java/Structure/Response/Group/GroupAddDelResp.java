package Structure.Response.Group;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 8/8/2016.
 */
public class GroupAddDelResp extends StructBase {

    public StructBoolean success;
    public StructLong groupCode;
    public StructString msg;

    public GroupAddDelResp(){
        init();
        data= new StructValueSetter(fields);
    }
    public GroupAddDelResp(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }

    private void init() {

        success=new StructBoolean("success",false);
        groupCode=new StructLong("groupCode",0);
        msg=new StructString("msg",50,"");
        fields = new BaseStructure[]{
                success,groupCode,msg
        };

    }

}
