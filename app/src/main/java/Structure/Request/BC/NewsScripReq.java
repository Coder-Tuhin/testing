package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class NewsScripReq extends StructBase {
    public StructInt ScripCode;
    public StructString symbolId;
    public StructInt Tag;

    public NewsScripReq() {
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        className = getClass().getName();
        ScripCode = new StructInt("ScripCode", 0);
        symbolId = new StructString("SymbolId", 10,"");
        Tag = new StructInt("tag", 0);
        fields = new BaseStructure[]{
                ScripCode,symbolId,Tag
        };
    }

}