package fragments.OptionChainNew.utility;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructScripNamesReq extends StructBase {

    public StructString clCode;//10
    public StructByte exchange;
    public StructScripNamesReq() {
        this(null);
    }

    public StructScripNamesReq(byte bytes[]) {
        try {
            className = getClass().getName();
            clCode = new StructString("", 10, "");
            exchange = new StructByte("", 0);

            fields = new BaseStructure[]{
                    clCode, exchange
            };
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
}
