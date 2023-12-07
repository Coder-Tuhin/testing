package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/25/2017.
 */
public class StructScripAlertRows extends StructBase {

    public StructLong srNo;
    public StructString scripName;
    public StructFloat scripRate;
    public StructShort rateAchive;
    public StructInt scripCode;
    public StructShort condition;


    public StructScripAlertRows() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructScripAlertRows(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        srNo = new StructLong("srno", 0);
        scripName = new StructString("scripname",50,"");
        scripRate = new StructFloat("scripRate", 0);
        rateAchive = new StructShort("rateachive",0);
        scripCode = new StructInt("scripCode",0);
        condition = new StructShort("condition",0);

        fields = new BaseStructure[]{
                srNo, scripName, scripRate,rateAchive,scripCode,condition
        };
    }


}
