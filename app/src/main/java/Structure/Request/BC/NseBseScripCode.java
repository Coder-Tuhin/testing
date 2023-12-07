package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class NseBseScripCode extends StructBase {
    public StructInt exchange;
    public StructInt scripCode;

    public NseBseScripCode(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        exchange=new StructInt("exchange",4);
        scripCode=new StructInt("scripCode",4);
        fields = new BaseStructure[]{
                exchange,scripCode
        };
    }
}
