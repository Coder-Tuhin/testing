package Structure.simplysave;

/**
 * Created by XTREMSOFT on 14-Aug-2017.
 */

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDouble;
import structure.StructString;
import structure.StructValueSetter;

public class withdrawReq extends StructBase {
    public StructString clientCode ;

    public StructString follio ;
    public StructString redFlag ;
    public StructString unitamtFlag ;
    public StructDouble unitAmtValue ;
    public StructString Tpin ;
    public StructString Mstatus ;
    public StructString Fname ;
    public StructString Mname ;
    public StructString Lname ;
    public StructString Cuttime ;
    public StructString pangno ;
    public StructString bank ;
    public StructString ip ;
    public StructString oldihno ;
    public StructString trdate ;
    public StructString entdate ;
    public StructString imei ;
    public StructString appVersion;
    public withdrawReq(){
        init();
        data= new StructValueSetter(fields);
    }
    private void init() {
        clientCode =new StructString("clientCode ",12,"");
        follio =new StructString("follio ",20,"");
        redFlag =new StructString("redFlag ",20,"");
        unitamtFlag =new StructString("unitamtFlag ",20,"");
        unitAmtValue =new StructDouble("unitAmtValue ",0);
        Tpin =new StructString("Tpin ",20,"");
        Mstatus =new StructString("Mstatus ",20,"");
        Fname =new StructString("Fname ",20,"");
        Mname =new StructString("Mname ",20,"");
        Lname =new StructString("Lname ",20,"");
        Cuttime =new StructString("Cuttime ",20,"");
        pangno =new StructString("pangno ",20,"");
        bank =new StructString("bank ",20,"");
        ip =new StructString("ip ",20,"");
        oldihno =new StructString("oldihno ",20,"");
        trdate =new StructString("trdate ",20,"");
        entdate =new StructString("entdate ",20,"");
        imei =new StructString("imei ",40,"");
        appVersion=new StructString("appVersion",10,"");
        fields = new BaseStructure[]{
                clientCode,follio,redFlag,unitamtFlag,unitAmtValue,Tpin,Mstatus,Fname,Mname,Lname,
                Cuttime,pangno,bank,ip,oldihno,trdate,entdate,imei,appVersion
        };
    }
}
