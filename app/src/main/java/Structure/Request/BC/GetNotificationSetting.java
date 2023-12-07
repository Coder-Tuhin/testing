package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 1/21/2017.
 */
public class GetNotificationSetting extends StructBase {
    public StructString clientCode;
    public StructString mobileOs;

    public GetNotificationSetting(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",15,"");
        mobileOs =new StructString("mobileOs",10,"");
        fields = new BaseStructure[]{
                clientCode, mobileOs
        };
    }
}
