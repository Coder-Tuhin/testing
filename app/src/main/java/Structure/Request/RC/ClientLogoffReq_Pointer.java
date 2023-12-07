package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class ClientLogoffReq_Pointer extends StructBase {

    public StructString clientCode;


    public ClientLogoffReq_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("ClientCode",15,"");

        fields = new BaseStructure[]{
                clientCode
        };
    }
}
