package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class ClientChgPwdReq_Pointer extends StructBase {

    public StructString clientCode;
    public StructString oldPasswordNotUsed;
    public StructString newPasswordNotUsed;
    public StructString oldPINNotUsed;
    public StructString newPINNotUsed;
    public StructString iMEI;

    public StructString oldPasswordEncrypted;
    public StructString newPasswordEncrypted;
    public StructString oldPINEncrypted;
    public StructString newPINEncrypted;
    public StructBoolean isEncrypted;

    public ClientChgPwdReq_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",15,"");
        oldPasswordNotUsed = new StructString("oldPassword",15,"");
        newPasswordNotUsed = new StructString("newPassword",15,"");
        oldPINNotUsed = new StructString("oldPIN",15,"");
        newPINNotUsed = new StructString("newPIN",15,"");
        iMEI = new StructString("iMEI",40,"");

        oldPasswordEncrypted = new StructString("oldPasswordEncrypted",40,"");
        newPasswordEncrypted = new StructString("newPasswordEncrypted",40,"");
        oldPINEncrypted = new StructString("oldPINEncrypted",40,"");
        newPINEncrypted = new StructString("newPINEncrypted",40,"");
        isEncrypted = new StructBoolean("isencrypted",false);

        fields = new BaseStructure[]{
              clientCode,oldPasswordNotUsed,newPasswordNotUsed,oldPINNotUsed,newPINNotUsed,iMEI,
                oldPasswordEncrypted,newPasswordEncrypted,oldPINEncrypted,newPINEncrypted,isEncrypted
        };
    }
}
