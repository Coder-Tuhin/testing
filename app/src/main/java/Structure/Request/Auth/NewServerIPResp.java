package Structure.Request.Auth;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class NewServerIPResp extends StructBase {

    public StructString serverIp;
    public StructInt portWealth;
    public StructInt portBC;
    public StructInt portRC;
    public StructString wealthIP;
    public StructByte tradingServerType; //ITS(1,"ITS"), RC(2,"RC")
    public StructString tradeServerDomainName;


    public NewServerIPResp(){
        try {
            init(500);
            data=new StructValueSetter(fields);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    public NewServerIPResp(byte[] bytes){
        try {
            init(bytes.length);
            data=new StructValueSetter(fields,bytes);
            if(tradeServerDomainName.getValue().equalsIgnoreCase("")){
                tradeServerDomainName.setValue(serverIp.getValue());
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init(int length) {
        serverIp = new StructString("charServerIp",15,"");
        portWealth = new StructInt("charServerIp",0);
        portBC = new StructInt("intPortBC",0);
        portRC = new StructInt("intPortRC",0);
        wealthIP = new StructString("charWealthIP",15,"");
        tradingServerType = new StructByte("tradingServerType", 1);
        tradeServerDomainName = new StructString("tradeServerIP",30,"");

        fields = new BaseStructure[]{
                serverIp, portWealth, portBC, portRC, wealthIP,tradingServerType, tradeServerDomainName
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