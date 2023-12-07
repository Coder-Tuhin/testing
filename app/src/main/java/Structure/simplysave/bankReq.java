package Structure.simplysave;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 18-Aug-2017.
 */
public class bankReq extends StructBase {
    public StructString clientCode ;
    public StructString follio ;
    public StructString imei ;
    public StructString appVersion;
    public StructString authId;
    public bankReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        imei =new StructString("imei ",40,"");
        appVersion=new StructString("appVersion",10,"");
        authId=new StructString("authId",100,"");
        fields = new BaseStructure[]{
                clientCode,follio,imei,appVersion,authId
        };
    }
}
