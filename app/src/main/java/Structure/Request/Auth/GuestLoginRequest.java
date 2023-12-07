package Structure.Request.Auth;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class GuestLoginRequest extends StructBase {
    public StructString charUserName;
    public StructString charMobileNo;
    public StructString charEmailID;
    public StructString charImeiNo;
    public StructString charState;
    public StructString charCity;
    public StructString charModelNo;
    public StructByte bcastType;

    public GuestLoginRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        charUserName=new StructString("charUserName",30,"");
        charMobileNo=new StructString("charMobileNo",10,"");
        charEmailID=new StructString("charEmailID",50,"");
        charImeiNo=new StructString("charImeiNo",40,"");
        charState=new StructString("charState",50,"");
        charCity=new StructString("charCity",50,"");
        charModelNo=new StructString("charModelNo",50,"");
        bcastType = new StructByte("bcastType",2); //2 for JBCast, 1 for dotnet bcast

        fields = new BaseStructure[]{
                charUserName,charMobileNo,charEmailID,charImeiNo,charState,charCity,charModelNo,bcastType
        };
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}
