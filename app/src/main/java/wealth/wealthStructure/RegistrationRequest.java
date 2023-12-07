package wealth.wealthStructure;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class RegistrationRequest extends StructBase {

    public StructString userID;
    public StructString imeiNumber;
    public StructString panDOB;
    public StructString loginID;
    public StructString password;
    public StructShort isPAN;
    public StructString reserve;
    public StructString authID;
    public StructChar isNewWealthAvl;

    public RegistrationRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {

        userID=new StructString("userID",12,"");
        imeiNumber=new StructString("imeiNumber",40,"");
        panDOB=new StructString("panDOB",10,"");
        loginID=new StructString("loginID",15,"");
        password=new StructString("password",40,"");
        isPAN=new StructShort("isPAN",0);
        reserve=new StructString("reserve",8,"");
        authID=new StructString("authid",100,"");
        isNewWealthAvl = new StructChar("success",' ');

        fields = new BaseStructure[]{
                userID,imeiNumber,panDOB,loginID,password,isPAN,reserve,authID,isNewWealthAvl
        };
    }
}
