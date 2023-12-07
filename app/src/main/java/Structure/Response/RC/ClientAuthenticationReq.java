package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

public class ClientAuthenticationReq extends StructBase {

    public StructString clientCode;
    public StructString password;
    public StructByte checkType;
    public StructString panNo;
    public StructString mobile;


    public ClientAuthenticationReq(){
        init();
        data= new StructValueSetter(fields);
    }


    private void init() {
        clientCode = new StructString("clientCode",15,"");
        password = new StructString("password",40,"");
        checkType = new StructByte("checkType",0);
        panNo = new StructString("panNo",10,"");
        mobile = new StructString("modelNo",15,"");

        fields = new BaseStructure[]{
                clientCode,password,checkType,panNo,mobile
        };
    }
}