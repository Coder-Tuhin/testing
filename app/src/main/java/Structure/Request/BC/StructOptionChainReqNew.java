package Structure.Request.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOptionChainReqNew extends StructBase {

    public StructString clientCode;//10
    public StructString symbol;//15
    public StructShort expiryType; // 1 - weekly, 0- monthly
    public StructShort option; //Any - 0, Future - 1, All Option - 2 , Call - 3, Put - 4
    public StructShort expNo; // 0 - any 1 - Near  2 - Next 3 - Far  4 - Far Next
    public StructShort strikeNo; // 3,5,7,9

    /*
     NNF
     0 - any 1 - Near  2 - Next 3 - Far  4 - Far Next
     ExpiryType
     0 - MONTHLY  1 - WEEKLY
     Instrument
     Any - 0, Future - 1, All Option - 2 , Call - 3, Put - 4
    */
    public StructOptionChainReqNew() {
        this(null);
    }
    public StructOptionChainReqNew(byte bytes[]) {
        try {
            className = getClass().getName();
            clientCode = new StructString("", 10, "");
            symbol = new StructString("", 15, "");
            expiryType = new StructShort("expirytype", 0);
            option = new StructShort("option", 0);
            expNo = new StructShort("ExpNo", 0);
            strikeNo = new StructShort("StrikeNo", 0);

            fields = new BaseStructure[]{
                    clientCode, symbol, expiryType, option, expNo,strikeNo
            };
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}