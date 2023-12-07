package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 10-Oct-2017.
 */
public class StructmarginTrade extends StructBase {
    public StructChar reserved1;
    public StructChar reserved2;
    public StructInt reserved3;
    public StructInt reserved4;
    public StructByte scripnameLength;
    public StructString scripName;
    public StructInt fundedStockQty;
    public StructFloat fundedStockValue;
    public StructInt callateralStockQty;
    public StructFloat callateralStockValue;
    public StructByte reserved5;
    public StructByte reserved6;
    public StructInt reserved7;
    public StructInt reserved8;

    public StructmarginTrade(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructmarginTrade(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        reserved1 = new StructChar("reserved1",'0');
        reserved2 = new StructChar("reserved2",'0');
        reserved3 = new StructInt("reserved3",0);
        reserved4 = new StructInt("reserved4",0);
        scripnameLength = new StructByte("scripnameLength",0);

        scripName = new StructString("scripName",12,"");
        fundedStockQty = new StructInt("fundedStockQty",0);
        fundedStockValue = new StructFloat("fundedStockValue",0);
        callateralStockQty = new StructInt("callateralStockQty",0);
        callateralStockValue = new StructFloat("callateralStockValue",0);
        reserved5 = new StructByte("reserved5",0);
        reserved6 = new StructByte("reserved6",0);
        reserved7 = new StructInt("reserved7",0);
        reserved8 = new StructInt("reserved8",0);
        fields = new BaseStructure[]{
                reserved1,reserved2,reserved3,reserved4,scripnameLength,scripName,fundedStockQty,
                fundedStockValue,callateralStockQty,callateralStockValue,reserved5,reserved6,
                reserved7,reserved8
        };

    }
}
