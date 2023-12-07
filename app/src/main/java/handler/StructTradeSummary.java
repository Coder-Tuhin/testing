package handler;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructFloat;
import structure.StructInt;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XtremsoftTechnologies on 18/04/16.
 */
public class StructTradeSummary extends StructBase {
    public StructInt segment;
    public StructInt total;
    public StructFloat value;

    public StructTradeSummary(){
        init();
        data = new StructValueSetter(fields);
    }

    public StructTradeSummary(byte bytes[]) {
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
        value = new StructFloat("Value",0);

        fields = new BaseStructure[]{
                segment,total,value
        };
    }
}
