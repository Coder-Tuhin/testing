package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 25-May-2018.
 */

public class StructPINGeneration  extends StructBase {
    public StructString clientCode;
    public StructString iMEI;
    public StructString mobileNo;
    public StructString mPin;
    public StructByte tag;

    public StructPINGeneration(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructPINGeneration(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }

    private void init() {
        clientCode = new StructString("clientCode",14,"");
        iMEI = new StructString("iMEI",40,"");
        mobileNo = new StructString("mobileNo",12,"");
        mPin = new StructString("mPin",10,"");
        tag = new StructByte("Tag",1);

        fields = new BaseStructure[]{
                clientCode,iMEI,mobileNo,mPin,tag
        };
    }
}
