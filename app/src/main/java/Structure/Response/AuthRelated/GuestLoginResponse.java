package Structure.Response.AuthRelated;

import com.ventura.venturawealth.VenturaApplication;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructChar;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class GuestLoginResponse extends StructBase {
    public StructChar charSuccess;
    public StructString charUserName;
    public StructChar charIsUpdateAvailable;
    public StructChar charUserRight;
    public StructString charAuthId;
    public StructString charPAN;
    public StructString charLoginId;
    public StructChar charMsg;
    public StructString charServerIp;
    public StructInt intPortWealth;
    public StructInt intPortBC;
    public StructInt intPortRC;
    public GuestLoginResponse(byte[] bytes){
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }

    private void init() {
        charSuccess = new StructChar("charSuccess",'N');
        charUserName = new StructString("charUserName",40,"");
        charIsUpdateAvailable = new StructChar("charIsUpdateAvailable",'N');
        charUserRight = new StructChar("charUserRight",'N');
        charAuthId = new StructString("charAuthId",100,"");
        charPAN = new StructString("charPAN",10,"");
        charLoginId = new StructString("charLoginId",15,"");
        charMsg = new StructChar("charMsg",'N');
        charServerIp = new StructString("charServerIp",15,"");
        intPortWealth = new StructInt("charServerIp",0);
        intPortBC = new StructInt("intPortBC",0);
        intPortRC = new StructInt("intPortRC",0);
        fields = new BaseStructure[]{
                charSuccess, charUserName, charIsUpdateAvailable,charUserRight,charAuthId,
                charPAN,charLoginId,charMsg, charServerIp,intPortWealth,intPortBC,intPortRC
        };
    }

    public boolean getActiveUser(){
        return charUserRight.getValue()!='F';
    }



    private static final String BYPASS = "bypass";

    public eGuestLoginSuccess getSuccess(){
        if (charAuthId.getValue().equalsIgnoreCase(BYPASS)){
            return eGuestLoginSuccess.SUCCESS;
        }
        if (charSuccess.getValue()=='1'){
            return eGuestLoginSuccess.SUCCESS;
        }
        if (charSuccess.getValue()=='2'){
            return eGuestLoginSuccess.SUCCESS;
        }
        if (charSuccess.getValue()=='3'){
            return eGuestLoginSuccess.SYSTEM_UNDER_MAINTAINANCE;
        }
        return eGuestLoginSuccess.FALSE;
    }

    public eAppUpdate getAppUpdate(){
        if (charIsUpdateAvailable.getValue()=='N'){
            return eAppUpdate.TRUE;
        }
        return eAppUpdate.FALSE;
    }


    public enum eGuestLoginSuccess{
        SUCCESS,
        FALSE,
        SYSTEM_UNDER_MAINTAINANCE
    }


    public String guestValidity(){
        if (getSuccess() == eGuestLoginSuccess.SUCCESS&&charAuthId.getValue().length()>0){
            return charAuthId.getValue();
        }
        return "";
    }

    public enum eAppUpdate{
        TRUE,
        FALSE
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
