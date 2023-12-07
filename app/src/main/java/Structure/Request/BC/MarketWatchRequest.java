package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class MarketWatchRequest extends StructBase {
    public StructInt token;
    public StructString clientCode;

    public MarketWatchRequest(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        token=new StructInt("token",4);
        clientCode=new StructString("clientCode",10,"");
        fields = new BaseStructure[]{
                token,clientCode
        };
    }
}
