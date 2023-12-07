package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class AddNotificationSettings extends StructBase {
    public StructString clientCode;
    public StructShort event;
    public StructShort fNO;
    public StructShort cash;
    public StructShort research;
    public StructString mobileOS;

    public AddNotificationSettings(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",15,"");
        event=new StructShort("event",2);
        fNO = new StructShort("fNO",2);
        cash = new StructShort("cash",2);
        research = new StructShort("research",2);
        mobileOS = new StructString("mobileOS",10,"");

        fields = new BaseStructure[]{
                clientCode,event,fNO,cash,research,mobileOS
        };
    }
}
