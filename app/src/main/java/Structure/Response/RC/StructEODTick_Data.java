package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructEODTick_Data extends StructBase {

    public StructInt tickDt;
    public StructInt open;
    public StructInt high;
    public StructInt low;
    public StructInt close;
    public StructInt qty;

    public StructEODTick_Data(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructEODTick_Data(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {

        tickDt = new StructInt("tickDt",0);
        open = new StructInt("open",0);
        high = new StructInt("high",0);
        low = new StructInt("low",0);
        close = new StructInt("close",0);
        qty = new StructInt("qty",0);

        fields = new BaseStructure[]{
                tickDt,open,high,low,close,qty
                };
    }
}

