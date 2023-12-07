package Structure.simplysave;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 14-Aug-2017.
 */
public class homeDetaiireq extends StructBase {
    public StructString clientCode ;
    public StructString follio ;
    public StructString imei ;
    public StructString appVersion;
    public homeDetaiireq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        imei =new StructString("imei ",40,"");
        appVersion=new StructString("appVersion",10,"");
        fields = new BaseStructure[]{
                clientCode,follio,imei,appVersion
        };
    }
}
