package Structure.Request.Auth;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class LoginRequest extends StructBase {
    public StructString username;
    public StructString password;
    public StructInt versionNumber;
    public StructString imeiNumber;
    public StructString pin;
    public StructString panDOB;
    public StructShort isPAN;
    public StructString deviceName;
    public StructString fcmToken;
    public StructShort osType;
    public StructString publicIP;
    public StructString androidVersion;
    public StructString appVersionname;
    public StructString mobileNumber;
    public StructByte loginType;
    public StructByte bcastType;
    public StructByte appType;

    public LoginRequest(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        username=new StructString("username",20,"");
        password=new StructString("password",40,"");
        versionNumber=new StructInt("versionNumber",0);
        imeiNumber=new StructString("imeiNumber",40,"");
        pin=new StructString("pin",20,"");
        panDOB=new StructString("panDOB",10,"");
        isPAN=new StructShort("isPAN",0);
        deviceName=new StructString("deviceName",20,"");
        fcmToken=new StructString("fcmToken",255,"");
        osType=new StructShort("osType",0);
        publicIP=new StructString("publicIP",15,"");
        androidVersion = new StructString("androidVersion",10,"");
        appVersionname = new StructString("appVersionname",15,"");
        mobileNumber = new StructString("mobileNumber",12,"");
        loginType = new StructByte("loginType",0);
        bcastType = new StructByte("bcastType",4); //4 for SSO client, 2 for JBCast, 1 for dotnet bcast
        appType = new StructByte("appType", GlobalClass.appType.value); //1 for Equity, 2 for commodity

        fields = new BaseStructure[]{
            username,password,versionNumber,imeiNumber,pin,panDOB,
            isPAN,deviceName,fcmToken,osType,publicIP,androidVersion,
            appVersionname,mobileNumber,loginType,bcastType,appType
        };
    }
}
