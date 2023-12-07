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

public class NewServerIPRequest extends StructBase {

    public StructString username;
    public StructInt versionNumber;
    public StructShort osType;
    public StructString currentIP;
    public StructInt noOfTy;
    public StructByte bcastType;

    public NewServerIPRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    public NewServerIPRequest(byte[] bytedata){
        init();
        data= new StructValueSetter(fields,bytedata);
        username.setValue(username.getValue().toUpperCase());
    }

    private void init() {
        username=new StructString("username",20,"");
        versionNumber=new StructInt("versionNumber",0);
        osType=new StructShort("osType",0);
        currentIP=new StructString("publicIP",15,"");
        noOfTy = new StructInt("NoOfTry",0);
        bcastType = new StructByte("bcastType",2);
        fields = new BaseStructure[]{
                username,versionNumber,osType,currentIP,noOfTy,bcastType
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
