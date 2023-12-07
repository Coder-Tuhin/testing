package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructHOMsg extends StructBase {
    public StructInt msgDT;
    public StructByte exchType;
    public StructString callMsg;

    public StructHOMsg(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructHOMsg(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        msgDT = new StructInt("msgDT",'0');
        exchType = new StructByte("exchType",'0');
        callMsg = new StructString("callMsg",25,"");
        fields = new BaseStructure[]{
                msgDT,exchType,callMsg
        };

    }
}