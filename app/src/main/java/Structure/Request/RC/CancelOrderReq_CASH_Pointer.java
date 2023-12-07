package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.UserSession;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class CancelOrderReq_CASH_Pointer extends StructBase {

    public StructString clientCode;
    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructChar buySell;
    public StructInt qty;
    public StructChar atMarket;
    public StructFloat rate;
    public StructChar withSL;
    public StructFloat triggerRate;
    public StructChar sLTriggered;
    public StructInt tradedQty;
    public StructInt pendingQty;
    public StructShort tradeRequestID;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructChar aHStatus;
    public StructChar allowIntradayDelivery;
    public StructByte orderType;

    public CancelOrderReq_CASH_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init(){
        clientCode = new StructString("clientCode",15,"");
        exch = new StructChar("exch",'0');
        exchType = new StructChar("exchType",'0');
        scripCode = new StructInt("scripCode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",12,"");
        buySell = new StructChar("buySell",'0');
        qty = new StructInt("qty",0);
        atMarket = new StructChar("atMarket",'0');
        rate = new StructFloat("rate",0);
        withSL = new StructChar("withSL",'0');
        triggerRate = new StructFloat("triggerRate",0);
        sLTriggered = new StructChar("sLTriggered",'0');
        tradedQty = new StructInt("tradedQty",0);
        pendingQty = new StructInt("pendingQty",0);
        tradeRequestID = new StructShort("tradeRequestID",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchOrderTime = new StructInt("exchOrderTime",0);
        aHStatus = new StructChar("aHStatus",'0');
        allowIntradayDelivery = new StructChar("allowIntradayDelivery",'0');
        orderType = new StructByte("orderType",0);


        fields = new BaseStructure[]{
                clientCode,exch,exchType,scripCode,scripNameLength,scripName,buySell,qty,atMarket,rate,withSL,triggerRate,sLTriggered,
                tradedQty,pendingQty,tradeRequestID,exchOrderID,exchOrderTime,aHStatus,allowIntradayDelivery,orderType

        };
    }

    public boolean isOrderOK(){
        boolean flag = true;

        if(UserSession.getClientResponse().isBOOM()){
            if(clientCode.getValue().equalsIgnoreCase("") || exch.getValue() == ' ' ||
                    exchType.getValue() == ' ' || scripCode.getValue() <= 0 || scripName.getValue().equalsIgnoreCase("") ||
                    buySell.getValue() == ' ' || qty.getValue() <= 0 || atMarket.getValue() == ' ' ||(atMarket.getValue() != 'Y'&&rate.getValue() <= 0)){
                flag =false;
            }
        }else if(clientCode.getValue().equalsIgnoreCase("") || exch.getValue() == ' ' ||
                exchType.getValue() == ' ' || scripCode.getValue() <= 0 || scripName.getValue().equalsIgnoreCase("") ||
                buySell.getValue() == ' ' || qty.getValue() <= 0 || atMarket.getValue() == ' ' ||(atMarket.getValue() != 'Y'&&rate.getValue() <= 0)  ||
                exchOrderID.getValue() <= 0 || exchOrderTime.getValue() <= 0){
            flag =false;
        }
        return flag;
    }
}
