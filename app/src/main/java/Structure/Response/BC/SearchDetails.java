package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructMoney;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

import static utils.MobileInfo.className;

/**
 * Created by XTREMSOFT on 28-Dec-2017.
 */

public class SearchDetails extends StructBase {
    public StructString symbol; //20 bytes
    public StructString cpType;//2 bytes
    public StructInt expiry;
    public StructMoney strikeRate;
    public StructInt scripCode;
    public StructString cName; //2 byte
    public StructString series;//2

    public SearchDetails() {
        init();
        data = new StructValueSetter(fields);
    }
    private void init() {
        symbol = new StructString("Symbol", 20, "");
        cpType = new StructString("CpType", 2, "");
        expiry = new StructInt("Expiry", 0);
        strikeRate = new StructMoney("StrikeRate", 0);
        scripCode = new StructInt("ScripCode", 0);
        cName = new StructString("cName", 40, "");
        series = new StructString("Series", 2, "");

        fields = new BaseStructure[]{
                symbol,cpType,expiry,strikeRate,scripCode,cName,
                series
        };
    }
    public SearchDetails(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
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
            e.printStackTrace();
        }
        return sb.toString();
    }
}