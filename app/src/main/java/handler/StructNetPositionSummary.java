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
public class StructNetPositionSummary extends StructBase {
    public StructInt segment;
    public StructInt open;
    public StructInt closed;
    public StructFloat openPl;
    public StructFloat mtm;

    public StructNetPositionSummary(){
        init();
        data = new StructValueSetter(fields);
    }

    public StructNetPositionSummary(byte bytes[]) {
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
        open = new StructInt("Open",0);
        closed = new StructInt("Closed",0);
        openPl = new StructFloat("OpenPl",0);
        mtm = new StructFloat("MTM",0);
        fields = new BaseStructure[]{
                segment,open,closed, openPl,mtm
        };
    }
}
