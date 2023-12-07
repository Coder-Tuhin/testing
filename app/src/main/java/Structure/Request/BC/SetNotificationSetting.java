package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 1/21/2017.
 */
public class SetNotificationSetting extends StructBase {
    public StructString clientCode;
    public StructShort event;
    public StructShort fno;
    public StructShort cash;
    public StructShort research;
    public StructString mobileOs;
    public StructShort news;

    public SetNotificationSetting(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",15,"");
        event=new StructShort("event",0);
        fno=new StructShort("fno",0);
        cash=new StructShort("cash",0);
        research=new StructShort("research",0);
        mobileOs =new StructString("mobileOs",10,"");
        news=new StructShort("news",0);
        fields = new BaseStructure[]{
                clientCode,event,fno,cash,research, mobileOs,news
        };
    }
}
