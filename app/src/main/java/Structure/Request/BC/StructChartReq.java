package Structure.Request.BC;


import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDate;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XtremsoftTechnologies on 04/03/16.
 */
public class StructChartReq extends StructBase {
    public StructString clientCode;//length=15
    public StructInt scripCode;
    public StructInt segment;
    public StructInt timeFrom;
    public StructInt timeTo;

    public  StructChartReq(){
        init();
        data=new StructValueSetter(fields);

    }
    public  StructChartReq(byte []bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void init(){
        className=getClass().getName();
        clientCode=new StructString("ClientCode",15, UserSession.getLoginDetailsModel().getUserID());
        scripCode=new StructInt("ScripCode",0);
        segment=new StructInt("Segment",0);
        timeFrom=new StructInt("TimeFrom",0);
        timeTo=new StructInt("TimeTo",0);
        fields=new BaseStructure[]{
                clientCode,scripCode,segment,timeFrom,timeTo
        };
    }
}
