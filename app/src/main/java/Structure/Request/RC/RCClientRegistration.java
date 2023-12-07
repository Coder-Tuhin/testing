package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by xtremsoft on 1/16/17.
 */

public class RCClientRegistration extends StructBase{

    public StructString clientCode;
    public StructString iMEI;
    public StructByte isForceReconnect;

    public RCClientRegistration(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",14,"");
        iMEI=new StructString("iMEI",40,"");
        isForceReconnect = new StructByte("forceReconnect",0);
        fields = new BaseStructure[]{
                clientCode,iMEI,isForceReconnect
        };
    }
}
