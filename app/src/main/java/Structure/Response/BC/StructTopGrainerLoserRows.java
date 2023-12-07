package Structure.Response.BC;

/**
 * Created by xtremsoft on 8/29/16.
 */
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author XtremsoftTechnologies
 */
public class StructTopGrainerLoserRows extends StructBase {

    public StructInt token;
    public StructInt prevClose;
    public StructInt lastRate;
    public StructString scripName;

    public StructTopGrainerLoserRows() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructTopGrainerLoserRows(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        token = new StructInt("Token", 0);
        prevClose = new StructInt("PrevClose", 0);
        lastRate = new StructInt("LastRate", 0);
        scripName = new StructString("ScripName",50,"");
        fields = new BaseStructure[]{
                token, prevClose, lastRate,scripName
        };
    }
}
