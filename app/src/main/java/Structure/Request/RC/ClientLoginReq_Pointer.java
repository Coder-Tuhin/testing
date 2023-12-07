package Structure.Request.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class ClientLoginReq_Pointer extends StructBase {

    public StructString clientCode;
    public StructString password;
    public StructString pIN;
    public StructByte checkType;
    public StructString panNo;
    public StructInt versionNo;
    public StructString iMEI;
    public StructString modelNo;
    public StructString publicIP;
    public StructString strMPIN;

    public StructString encryptedPasswordUpperCase;
    public StructString encryptedPIN;
    public StructString encryptedPINUpperCase;
    public StructBoolean isEncripted;

    public StructString sessionId;
    public StructString authToken;
    public StructString platform;

    public ClientLoginReq_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {

        clientCode = new StructString("clientCode",15,"");
        password = new StructString("password",40,"");
        pIN = new StructString("pIN",15,"");
        checkType = new StructByte("checkType",0);
        panNo = new StructString("panNo",10,"");
        versionNo = new StructInt("versionNo",0);
        iMEI = new StructString("iMEI",40,"");
        modelNo = new StructString("modelNo",20,"");
        publicIP = new StructString("publicIP",15,"");
        strMPIN = new StructString("strMPIN",40,"");

        encryptedPasswordUpperCase = new StructString("encryptedPasswordUpperCase",40,"");
        encryptedPIN= new StructString("encryptedPIN",40,"");
        encryptedPINUpperCase = new StructString("encryptedPINUpperCase",40,"");
        isEncripted = new StructBoolean("isEncrypted",false);

        sessionId = new StructString("sessionId", 50, "");
        authToken = new StructString("authtoken", 4096, "");
        platform = new StructString("platform", 20, "");

        fields = new BaseStructure[]{
                clientCode,password,pIN,checkType,panNo,versionNo,iMEI,modelNo,publicIP,strMPIN,encryptedPasswordUpperCase
                ,encryptedPIN,encryptedPINUpperCase,isEncripted,sessionId,authToken,platform
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