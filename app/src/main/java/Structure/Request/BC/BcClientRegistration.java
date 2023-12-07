package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class BcClientRegistration extends StructBase {
    public StructString clientCode;
    public StructString iMEI;
    public StructInt clientType;
    public StructInt version;
    public StructString auth;
    public StructChar slbm;
    public StructBoolean isReconnect;
    public StructString ip;
    public StructString reserve;

    public BcClientRegistration(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode = new StructString("ClientCode", 10, "");
        iMEI = new StructString("Imei", 40, "");
        version = new StructInt("Version", 0);
        clientType = new StructInt("ClientType", 0); // 1 client, 2 guest
        auth = new StructString("authid", 80, ""); //from 100 it reduce to 80
        slbm = new StructChar("slbm", ' ');
        isReconnect = new StructBoolean("isreconnect",false);
        ip = new StructString("ip",15,"");
        reserve = new StructString("reserve", 3, "");
        fields = new BaseStructure[]{
                clientCode, iMEI, version,clientType,auth,slbm,isReconnect,ip,reserve
        };
    }
}