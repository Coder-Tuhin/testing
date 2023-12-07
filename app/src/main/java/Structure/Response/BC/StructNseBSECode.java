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
 * Created by XTREMSOFT on 11/25/2016.
 */
public class StructNseBSECode extends StructBase {

    public StructString scripName;
    public StructInt token;


    public StructNseBSECode(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    public StructNseBSECode() {
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        className = getClass().getName();
        scripName = new StructString("scripname",20,"");
        token = new StructInt("Token", 0);

        fields = new BaseStructure[]{
                scripName,token
        };
    }
}
