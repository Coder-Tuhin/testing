package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class AddScripGroup extends StructBase {
    public StructString clientCode;
    public StructString groupName;
    public StructInt scripCode;
    public StructInt expiry;


    public AddScripGroup(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode =new StructString("clientCode",12,"");
        groupName =new StructString("groupName",30,"");
        scripCode = new StructInt("scripCode",4);
        expiry = new StructInt("expiry",4);

        fields = new BaseStructure[]{
                clientCode, groupName,scripCode,expiry
        };
    }
}
