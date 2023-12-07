package wealth.wealthStructure;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructChar;
import structure.StructDate;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class HoldingDateCheckRequest extends StructBase {

    public StructDate lastUpdateDateDP;
    public StructDate lastUpdateDateFNO;

    public HoldingDateCheckRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {

        lastUpdateDateDP=new StructDate("lastUpdateDateDP",0);
        lastUpdateDateFNO=new StructDate("lastUpdateDateFNO",0);

        fields = new BaseStructure[]{
                lastUpdateDateDP,lastUpdateDateFNO
        };
    }
}
