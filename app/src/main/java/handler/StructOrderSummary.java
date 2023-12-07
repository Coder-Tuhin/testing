package handler;



import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XtremsoftTechnologies on 18/04/16.
 */
public class StructOrderSummary extends StructBase {
    public StructInt segment;
    public StructInt total;
    public StructInt pending;
    public StructInt traded;

    public StructOrderSummary(){
        init();
        data = new StructValueSetter(fields);
    }

    public StructOrderSummary(byte bytes[]) {
        orgData = bytes;
        init();
        try{
           data = new StructValueSetter(fields, bytes);
        }catch (Exception e){
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init(){
        className = getClass().getName();

        segment = new StructInt("Segment",0);
        total = new StructInt("Total",0);
        pending = new StructInt("Pending",0);
        traded = new StructInt("Traded",0);
        fields = new BaseStructure[]{
                segment,total,pending,traded
        };
    }
}
