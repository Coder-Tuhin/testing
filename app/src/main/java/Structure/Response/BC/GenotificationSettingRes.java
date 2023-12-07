package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/21/2017.
 */
public class GenotificationSettingRes extends StructBase {
    public StructShort event;
    public StructShort fno;
    public StructShort cash;
    public StructShort research;
    public StructShort news;

    public GenotificationSettingRes() {
        init(8);
        data=new StructValueSetter(fields);
    }
    public GenotificationSettingRes(byte []bytes) {
        try {
            init(bytes.length);
            data=new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className, ex);
        }
    }


    private void init(int len){
        className=getClass().getName();
        event=new StructShort("Token", 0);
        fno=new StructShort("fno", 0);
        cash=new StructShort("cash", 0);
        research=new StructShort("research", 0);
        news=new StructShort("news", 1);


        if (len > 8){
            fields=new BaseStructure[]{
                    event,fno,cash,research,news
            };
        }else {
            fields=new BaseStructure[]{
                    event,fno,cash,research
            };
        }

    }

    public boolean isValueChange(short levent,short lcash, short lfno, short lresearchCall){
        boolean isChange = true;
        if(!(event.getValue() == levent && cash.getValue() == lcash && fno.getValue() == lfno && research.getValue() == lresearchCall) ){
            isChange = true;
            event.setValue(levent);
            cash.setValue(lcash);
            fno.setValue(lfno);
            research.setValue(lresearchCall);
        }
        else{
            isChange = false;
        }
        return isChange;
    }
}
