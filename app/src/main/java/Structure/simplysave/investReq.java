package Structure.simplysave;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDouble;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 14-Aug-2017.
 */


public class investReq extends StructBase {
    public StructString clientCode ;
    public StructString follio ;
    public StructString type ;
    public StructDouble amount ;
    public StructString imei ;
    public StructString appVersion;
    public investReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        type =new StructString("type ",20,"");
        amount =new StructDouble("follio ",0);
        imei =new StructString("imei ",40,"");
        appVersion=new StructString("appVersion",10,"");
        fields = new BaseStructure[]{
                clientCode,follio,type,amount,imei,appVersion
        };
    }
}

