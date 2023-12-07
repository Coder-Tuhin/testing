package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/30/2017.
 */
public class DuplicateLoginRes extends StructBase {

    public StructString MSG;

    public DuplicateLoginRes(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    public DuplicateLoginRes() {
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        className = getClass().getName();
        MSG = new StructString("MSG",250,"");
        fields = new BaseStructure[]{
                MSG
        };
    }
}
