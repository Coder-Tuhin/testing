package Structure.Request.BC;

import java.util.ArrayList;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class MultipleScripDetail  extends StructBase {
    public StructShort noOfScrip;
    public StructInt[] scripCodes;

    public MultipleScripDetail(ArrayList<Integer> scripCodes) {
        try {
            init(scripCodes);
            data = new StructValueSetter(fields);
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init(ArrayList<Integer> _scripCodes) {
        className = getClass().getName();
        noOfScrip = new StructShort("NumOfScrip", (short) 0);
        noOfScrip.setValue(_scripCodes.size());
        scripCodes = new StructInt[noOfScrip.getValue()];
        for (int i = 0; i < noOfScrip.getValue(); i++) {
            scripCodes[i] = new StructInt("ScripCode" + i, _scripCodes.get(i));
        }
        fields = new BaseStructure[1 + noOfScrip.getValue()];
        fields[0] = noOfScrip;
        for (int i = 0; i < scripCodes.length; i++) {
            fields[i + 1] = scripCodes[i];
        }
    }
}
