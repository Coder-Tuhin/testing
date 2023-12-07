package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 10-Oct-2017.
 */
public class BCMsgStructure extends StructBase {
    public StructByte byteMsgLength; //5.---ErrorMsgLength---BYTE---1bytes---
    public StructString charMsg;             //6.---ErrorMsg---CHAR---500bytes---

    public BCMsgStructure(){
        init();
        data= new StructValueSetter(fields);
    }
    public BCMsgStructure(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        byteMsgLength = new StructByte("msgLength",0);
        charMsg = new StructString("charMsg",500,"");

        fields = new BaseStructure[]{
                byteMsgLength,charMsg
        };

    }
}