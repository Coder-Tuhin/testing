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

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class CancelOrderReq_FNO_Pointer extends StructBase {

    public StructString clientCode;
    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte instrType;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructInt expiry;
    public StructFloat strikePrice;
    public StructByte cPType;
    public StructByte cALevel;
    public StructChar buySell;
    public StructInt qty;
    public StructChar atMarket;
    public StructFloat rate;
    public StructChar withSL;
    public StructFloat triggerRate;
    public StructShort tradeRequestID;
    public StructChar sLTriggered;
    public StructInt oldQty;
    public StructChar aHStatus;
    public StructInt tradedQty;
    public StructInt pendingQty;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructInt discQty;
    public StructChar allowIntradayDelivery;
    public StructByte orderType;


    public CancelOrderReq_FNO_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init(){

        clientCode = new StructString("clientCode",15,"");
        exch = new StructChar("exch",'0');
        exchType = new StructChar("exchType",'0');
        scripCode = new StructInt("scripCode",0);
        instrType = new StructByte("instrType",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",10,"");
        expiry = new StructInt("expiry",0);
        strikePrice = new StructFloat("strikePrice",0);
        cPType = new StructByte("cPType",0);
        cALevel = new StructByte("cALevel",0);
        buySell = new StructChar("buySell",'0');
        qty = new StructInt("qty",0);
        atMarket = new StructChar("atMarket",'0');
        rate = new StructFloat("rate",0);
        withSL = new StructChar("withSL",'0');
        triggerRate = new StructFloat("triggerRate",0);
        tradeRequestID = new StructShort("tradeRequestID",0);
        sLTriggered = new StructChar("sLTriggered",'0');
        oldQty = new StructInt("oldQty",0);
        aHStatus = new StructChar("aHStatus",'0');
        tradedQty = new StructInt("tradedQty",0);
        pendingQty = new StructInt("pendingQty",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchOrderTime = new StructInt("exchOrderTime",0);
        discQty = new StructInt("discQty",0);
        allowIntradayDelivery = new StructChar("allowIntradayDelivery",'0');
        orderType = new StructByte("orderType",0);


        fields = new BaseStructure[]{
                clientCode,exch,exchType,scripCode,instrType,scripNameLength,scripName,expiry,strikePrice,cPType,cALevel,
                buySell,qty,atMarket,rate,withSL,triggerRate,tradeRequestID,sLTriggered,oldQty,aHStatus,tradedQty,pendingQty,
                exchOrderID,exchOrderTime,discQty,allowIntradayDelivery,orderType

        };
    }

    public boolean isOrderOK(){

        boolean flag = true;
        if(clientCode.getValue().equalsIgnoreCase("") || exch.getValue() == ' ' ||
                exchType.getValue() == ' ' || scripCode.getValue() <= 0
                || scripName.getValue().equalsIgnoreCase("") ||
                buySell.getValue() == ' ' || qty.getValue() <= 0 || atMarket.getValue() == ' '
                || (atMarket.getValue() == 'N' && rate.getValue() <= 0) ||
                exchOrderID.getValue() <= 0 || exchOrderTime.getValue() <= 0){

            flag =false;
        }

        return flag;
    }
}