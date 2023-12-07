package fragments.OptionChainNew.utility;

import java.util.Date;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructDate;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructScripName extends StructBase {


    public StructString scripName;//15
    public StructDate date;
    public StructScripName() {
        this(null);
    }
    public StructScripName(byte bytes[]) {

        try{
            className = getClass().getName();
            scripName = new StructString("", 15, "");
            date = new StructDate("",new Date());
            fields = new BaseStructure[]{
                    scripName
            };
            data = new StructValueSetter(fields,bytes);
        }catch(Exception e){
            GlobalClass.onError("Error in "+className, e);
        }
    }

}