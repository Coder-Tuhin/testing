package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 13-Jul-2017.
 */
public class BCStopReq  extends StructBase {
    public StructString clientCode;
    public StructString imei;
    public StructByte stopstart;

    public BCStopReq(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",15,"");
        imei = new StructString("imei",40,"");
        stopstart = new StructByte("stopstart",0);
        fields = new BaseStructure[]{
                clientCode ,imei,stopstart
        };
    }
}
