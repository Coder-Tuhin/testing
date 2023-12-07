package Structure.simplysave;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 18-Aug-2017.
 */
public class passbookReq extends StructBase {
    public StructString clientCode ;
    public StructString follio ;
    public passbookReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        fields = new BaseStructure[]{
                clientCode,follio
        };
    }
}
