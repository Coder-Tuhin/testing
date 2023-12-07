package Structure.Response.BC;

/**
 * Created by xtremsoft on 8/29/16.
 */
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author XtremsoftTechnologies
 */
public class StructTopTradedRows extends StructBase {
    public StructInt token;
    public StructLong tradedValue;
    public StructString scripName;

    public StructTopTradedRows() {
        init();
        data=new StructValueSetter(fields);
    }
    public StructTopTradedRows(byte []bytes) {
        try {
            init();
            data=new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className, ex);
        }
    }
    private void init(){
        className=getClass().getName();
        token=new StructInt("Token", 0);
        tradedValue=new StructLong("TradedValue",0);
        scripName = new StructString("ScripNmae",50,"");
        fields=new BaseStructure[]{
                token,tradedValue,scripName
        };
    }
}