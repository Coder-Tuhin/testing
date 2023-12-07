package Structure.simplysave;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11-Apr-2019.
 */

public class passbookrefdetail extends StructBase  {
    public StructString clientCode ;
    public StructString follio ;
    public StructString imei ;
    public StructString appVersion;
    public StructString refno;


    public passbookrefdetail(byte[] byteArr){
        try {
            init(byteArr.length);
            data=new StructValueSetter(fields,byteArr);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    public passbookrefdetail(){
        init(120);
        data= new StructValueSetter(fields);
    }
    private void init(int length) {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        imei =new StructString("imei ",40,"");
        appVersion=new StructString("appVersion",10,"");
        refno = new StructString("RefNo", 50, "");

            fields = new BaseStructure[]{
                    clientCode,follio,imei,appVersion,refno
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
