package Structure.Response.Group;


import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 8/8/2016.
 */
public class GroupTokenAddDelResp extends StructBase {

    public StructBoolean success;
    public StructString msg;
    public GroupTokenAddDelResp(byte[] bytes){
        init();
        data = new StructValueSetter(fields,bytes);
    }
    private void init() {
        success=new StructBoolean("success",false);
        msg=new StructString("msg",50,"");

        fields = new BaseStructure[]{
                success,msg
        };


    }

}
