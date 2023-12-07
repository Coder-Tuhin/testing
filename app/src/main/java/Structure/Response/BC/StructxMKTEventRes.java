package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by xtremsoft on 11/18/16.
 */
public class StructxMKTEventRes extends StructBase {

    public StructInt token;
    public StructString scripName;
    public StructMoney lastRate;
    public StructShort event;

    public StructxMKTEventRes(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    public StructxMKTEventRes() {
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        className = getClass().getName();
        token = new StructInt("Token", 0);
        scripName = new StructString("scripname",50,"");
        lastRate = new StructMoney("Price", 0);
        event = new StructShort("OrdType", 0);
        fields = new BaseStructure[]{
                token, scripName, lastRate, event
        };
    }
}
