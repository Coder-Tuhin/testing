package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class SendScripRateTokens  extends StructBase {
    public StructString ClientCode;

    public SendScripRateTokens(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        ClientCode=new StructString(" clientCode",15,"");
        fields = new BaseStructure[]{
                ClientCode
        };
    }
}
