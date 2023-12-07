package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class NewsScripResponseInnerStructure extends StructBase {
    public StructInt sl;
    public StructString news;
    public StructString date;
    public StructString category;
    public StructString companyName;
    public StructString nse;
    public StructString bse;




    public NewsScripResponseInnerStructure(){
        init();
        data = new StructValueSetter(fields);

    }
    public NewsScripResponseInnerStructure(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {

        className = getClass().getName();
        sl = new StructInt("sl", 0);
        news = new StructString("title", 500, "");
        date = new StructString("date", 30,"");
        category = new StructString("categories",20 ,"");
        companyName = new StructString("company_name",50 , "");
        nse = new StructString("nse", 10, "");
        bse = new StructString("bse",10, "");
        fields = new BaseStructure[]{
                sl,news,date,category,companyName,nse,bse
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