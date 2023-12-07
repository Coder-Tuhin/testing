package Structure.Response.BC;

import java.lang.reflect.Field;
import java.util.Date;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructBoolean;
import structure.StructDate;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructCustomDialog extends StructBase {

    public StructInt sl;
    public StructString clientCode;
    public StructString title;
    public StructString description;
    public StructString positiveBtn;
    public StructString negativeBtn;
    public StructBoolean isCrossBtn;
    public StructDate dialogTime;

    public StructBoolean isShowImage;
    public StructString imageLink;
    public StructShort openScreen;
    public StructShort osType; //1 - android, 2- IOS, 3 - both

    public StructBoolean isImageClikable;
    public StructString imageClickLink;
    public StructString positiveBtnLink;

    public StructString reserve;
    public StructCustomDialog(){
        try {
            init();
            data=new StructValueSetter(fields);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    public StructCustomDialog(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {

        sl = new StructInt("sl", 0);
        clientCode = new StructString("clientCode",15,"");
        title = new StructString("title",50,"");
        description = new StructString("title",300,"");
        positiveBtn = new StructString("positiveBtn",20,"");
        negativeBtn = new StructString("negativeBtn",20,"");
        isCrossBtn = new StructBoolean("isCrossBtn",false);
        dialogTime = new StructDate("DialogDate", new Date());

        isShowImage = new StructBoolean("ShowImage", false);
        imageLink = new StructString("ImageLink", 300,"");
        openScreen = new StructShort("openScreen", 0);
        osType = new StructShort("osType", 0);

        isImageClikable = new StructBoolean("isImageClikable",false);
        imageClickLink = new StructString("imageClickLink", 100,"");
        positiveBtnLink = new StructString("positiveBtnLink", 100,"");

        reserve = new StructString("reserve",98,"");

        fields = new BaseStructure[]{
                sl,clientCode,title,description,positiveBtn,negativeBtn,isCrossBtn,dialogTime,
                isShowImage,imageLink,openScreen,osType,isImageClikable,imageClickLink,positiveBtnLink,
                reserve
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