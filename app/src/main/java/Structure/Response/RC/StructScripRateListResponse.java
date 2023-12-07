package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructScripRateListResponse  extends StructBase {

    public StructShort cNoOfRecs;
    public StructLong srNo;
    public StructString scripName;
    public StructFloat scripRate;
    public StructShort rateAchieved;


    public StructScripRateListResponse(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructScripRateListResponse(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        cNoOfRecs = new StructShort("cNoOfRecs",0);
        srNo = new StructLong("srNo",0);
        scripName = new StructString("scripName",25,"");
        scripRate = new StructFloat("scripRate",0);
        rateAchieved = new StructShort("rateAchieved",0);

        fields = new BaseStructure[]{

                cNoOfRecs,srNo,scripName,scripRate,rateAchieved
        };

    }
}