package Structure.Request.BC;

import java.util.Date;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDate;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class ErrorLOG  extends StructBase {
    public StructString clientCode;
    public StructString errorMsg;
    public StructString logType;
    public StructDate errorTime;
    public ErrorLOG(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode",12,"");
        errorMsg = new StructString("errorMsg",900,"");
        logType = new StructString("LogType",20,"");
        errorTime = new StructDate("ErrorTime",new Date());
        fields = new BaseStructure[]{
                clientCode,errorMsg,logType,errorTime
        };
    }
}

