package Structure.Request.RC;

import java.lang.reflect.Field;

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
import utils.GlobalClass;

public class CancelOrderReq_SLBM_Pointer extends StructBase {

    public StructString clientCode;
    public StructChar exchange;
    public StructChar exchangeType;
    public StructInt scripCode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructString series;
    public StructByte instrType;
    public StructInt expityDate;
    public StructChar buySell;
    public StructInt qty;
    public StructChar atMarket;
    public StructFloat limitPrice;
    public StructChar withSL;
    public StructFloat tiggerPrice;
    public StructChar slTriggered;
    public StructInt tradeQty;
    public StructInt pendingQty;
    public StructShort traderRequseterID;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructByte iocSelected;
    public StructInt discQty;
    public StructChar ahStatus;
    public StructString recallRepayOrder;
    public StructString reserved;

    public CancelOrderReq_SLBM_Pointer() {
        init();
        data = new StructValueSetter(fields);
    }
    public CancelOrderReq_SLBM_Pointer(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void init() {
        className = getClass().getName();
        clientCode = new StructString("ClientCode", 12, "");
        exchange = new StructChar("Exchage", ' ');
        exchangeType = new StructChar("ExchageType", ' ');
        scripCode = new StructInt("ScripCode", 0);
        scripNameLength = new StructByte("scripnameLength", 0);
        scripName = new StructString("Scripname", 10, "");
        series = new StructString("Series", 2, "");
        instrType = new StructByte("Intrument", 0);
        expityDate = new StructInt("ExpiryDate", 0);
        buySell = new StructChar("BuySell", ' ');
        qty = new StructInt("Qty", 0);
        atMarket = new StructChar("AtMarket", ' ');
        limitPrice = new StructFloat("Limit Price", 0);
        withSL = new StructChar("StopLoss", ' ');
        tiggerPrice = new StructFloat("Tigger Price", 0);
        slTriggered = new StructChar("slTriggered", ' ');
        tradeQty = new StructInt("TradeQty", 0);
        pendingQty = new StructInt("PendQty", 0);
        traderRequseterID = new StructShort("TraderReqID", 0);
        exchOrderID = new StructLong("ExchOrderID", 0);
        exchOrderTime = new StructInt("ExchOrderTime", 0);
        iocSelected = new StructByte("IOCSelected", 0);
        discQty = new StructInt("DiscQty", 0);
        ahStatus = new StructChar("AH Status", ' ');
        recallRepayOrder = new StructString("RecallRepayOrder", 2, "");
        reserved = new StructString("Reserved", 50, "");

        fields = new BaseStructure[]{
                clientCode,exchange,exchangeType,scripCode,scripNameLength,scripName,series,instrType,
                expityDate,buySell,qty,atMarket,limitPrice,withSL,tiggerPrice,slTriggered,
                tradeQty,pendingQty,traderRequseterID,exchOrderID,exchOrderTime,
                iocSelected,discQty,ahStatus,recallRepayOrder,reserved
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

    public boolean isOrderOK(){

        boolean flag = true;
        if(clientCode.getValue().equalsIgnoreCase("") || exchange.getValue() == ' ' ||
                exchangeType.getValue() == ' ' || scripCode.getValue() <= 0
                || scripName.getValue().equalsIgnoreCase("") ||
                buySell.getValue() == ' ' || qty.getValue() <= 0 || atMarket.getValue() == ' '
                || (atMarket.getValue() == 'N' && limitPrice.getValue() <= 0) ||
                exchOrderID.getValue() <= 0 || exchOrderTime.getValue() <= 0){

            flag =false;
        }

        return flag;
    }
}