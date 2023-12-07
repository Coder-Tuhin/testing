package Structure.Request.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructChangePwdReq extends StructBase {

    public StructString clientCode;
    public StructString oldPassword;
    public StructString newPassword;
    public StructInt ipAdd;
    public StructString blank;//51 bytes
    public StructString imei;


    public StructChangePwdReq() {
        init();
        data=new StructValueSetter(fields);
    }

    public StructChangePwdReq(byte [] bytes) {
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("In class : "+className+" error : ", ex);
        }
    }
    private void init(){
        className = getClass().getName();
        clientCode = new StructString("ClientCode",12,"");
        oldPassword=new StructString("OldPassword",40,"");
        newPassword=new StructString("NewPassword",40,"");
        ipAdd=new StructInt("IPAdd",0);
        blank=new StructString("Blank", 51, "");
        imei=new StructString("imei",50,"");
        fields = new BaseStructure[]{
                clientCode,oldPassword,newPassword,ipAdd,blank,imei
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