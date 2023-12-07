package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class InsertMobileInfo  extends StructBase {
    public StructString clientCode;
    public StructString publicIP;
    public StructString imei;
    public StructString model;

    public StructInt appVersion;
    public StructString androidVersion;
    public StructString appVersionname;

    public InsertMobileInfo(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode=new StructString("clientCode",12,"");
        publicIP=new StructString("publicIP",15,"");
        imei = new StructString("imei",50,"");
        model = new StructString("model",15,"");
        appVersion = new StructInt("appVersion",4);
        androidVersion = new StructString("androidVersion",10,"");
        appVersionname = new StructString("appVersionname",15,"");

        fields = new BaseStructure[]{
                clientCode,publicIP,imei,model,appVersion,androidVersion,appVersionname
        };
    }
}
