package handler;

import Structure.BaseStructure.StructBase;
import enums.eExch;
import structure.BaseStructure;
import structure.StructDate;
import structure.StructMoney;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 4/15/2017.
 */
public class StructCBDBChartResp  extends StructBase {

    public StructDate date;
    public StructMoney mtm;
    private eExch exch;


    public StructCBDBChartResp() {
        this(null);
    }
    public StructCBDBChartResp(byte bytes[]) {
        try{
            className = getClass().getName();
            date = new StructDate("",0);
            mtm = new StructMoney("", 0);
            fields = new BaseStructure[]{
                    date,mtm
            };
            data = new StructValueSetter(fields,bytes);
        }catch(Exception e){
            GlobalClass.onError("Error in "+className, e);
        }
    }


    public eExch getExch() {
        return exch;
    }

    public void setExch(eExch exch) {
        this.exch = exch;
    }
}
