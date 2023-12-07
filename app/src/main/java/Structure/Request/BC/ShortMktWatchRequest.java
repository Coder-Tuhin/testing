package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 30-Oct-2017.
 */
public class ShortMktWatchRequest extends StructBase {
    
    public StructString clientCode;
    public StructLong prevGrp;
    public StructLong currGrp;
    public StructByte columnTag;
    public StructBoolean isInitialDataTobeSend;
    public StructInt currGrpSize;

    public ShortMktWatchRequest(){
        init();
        data= new StructValueSetter(fields);
    }

    public ShortMktWatchRequest(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }

    private void init() {
        clientCode=new StructString("clientCode",14,"");
        prevGrp = new StructLong("prevGrp",0);
        currGrp = new StructLong("currGrp",0);
        columnTag = new StructByte("columnTag",0);
        isInitialDataTobeSend = new StructBoolean("isInitialDataTobeSend", false);
        currGrpSize = new StructInt("CurrGrpSize",0);

        fields = new BaseStructure[]{
                clientCode,prevGrp,currGrp,columnTag,isInitialDataTobeSend,currGrpSize
        };
    }
}
