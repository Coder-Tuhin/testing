package Structure.Response;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

public class Struct_RCAuth extends StructBase {
    public StructByte success;
    public StructString msg;
    public StructString otp;
    public StructString mobile;
    public StructString clientName;

    public Struct_RCAuth(){
        init();
        data= new StructValueSetter(fields);
    }
    public Struct_RCAuth(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        success = new StructByte("success",1);
        msg = new StructString("msg",50,"");
        otp = new StructString("otp",5,"");
        mobile = new StructString("mobile",10,"");
        clientName = new StructString("ClientName",50,"");
        fields = new BaseStructure[]{
                success,msg,otp,mobile,clientName
        };

    }
}