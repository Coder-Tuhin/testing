package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class ReportReq_Pointer extends StructBase {

    public StructString clientCode;
    public StructInt startTime;
    public StructInt endTime;

    public ReportReq_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",14,"");
        startTime = new StructInt("startTime",0);
        endTime = new StructInt("endTime",0);


        fields = new BaseStructure[]{
                clientCode,startTime,endTime
        };
    }
}