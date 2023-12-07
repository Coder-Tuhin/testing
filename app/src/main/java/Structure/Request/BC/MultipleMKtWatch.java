package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class MultipleMKtWatch extends StructBase {
    public StructString clientCode;
    public StructShort noOfScrip;
    public StructInt[] scripCode;
    public StructShort complete;

    public int scripArrLength = 100;

    public MultipleMKtWatch(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    public MultipleMKtWatch() {
        try {
            init();
            data = new StructValueSetter(fields);
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init() {
        className = getClass().getName();
        clientCode = new StructString("clientCode",10,"");
        noOfScrip = new StructShort("NumOfScrip", (short) 0);
        scripCode = new StructInt[scripArrLength];
        for (int i = 0; i < scripArrLength; i++) {
            scripCode[i] = new StructInt("ScripCode" + i, 0);
        }
        complete = new StructShort("DComplete", (short) 0);
        fields = new BaseStructure[3 + scripArrLength];
        fields[0] = clientCode;
        fields[1] = noOfScrip;
        for (int i = 0; i < scripCode.length; i++) {
            fields[i + 2] = scripCode[i];
        }
        fields[fields.length - 1] = complete;
    }
}
