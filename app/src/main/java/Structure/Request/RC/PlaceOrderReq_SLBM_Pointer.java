package Structure.Request.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import Structure.Other.StructBuySell;
import enums.eExch;
import enums.eOrderType;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;

public class PlaceOrderReq_SLBM_Pointer extends StructBase {

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
    public StructShort traderRequseterID;
    public StructByte iocSelected;
    public StructInt discQty;
    public StructString recallRepayOrder;
    public StructString reserved;


    public PlaceOrderReq_SLBM_Pointer() {
        init();
        data = new StructValueSetter(fields);
    }
    public PlaceOrderReq_SLBM_Pointer(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void init() {
        className = getClass().getName();
        clientCode = new StructString("ClientCode",12,"");
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
        traderRequseterID = new StructShort("TraderReqID", 0);
        iocSelected = new StructByte("IOCSelected", 0);
        discQty = new StructInt("DiscQty", 0);
        recallRepayOrder = new StructString("RecallRepayOrder", 2, "");
        reserved = new StructString("Reserved", 50, "");
        fields = new BaseStructure[]{
            clientCode,exchange,exchangeType,scripCode,scripNameLength,scripName,series,instrType,expityDate,
            buySell,qty,atMarket,limitPrice,withSL,tiggerPrice,traderRequseterID,iocSelected,discQty,
            recallRepayOrder,reserved
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

    public void setDataForPlaceSLBM(StructBuySell structBuySell, boolean isStopLoss, boolean isIOC, String mktLimitSelection, String orderTypeSelection) {

        char _exch = 'N';
        char _exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int _ioc = 0;
        _exch = 'S';
        _exchType = 'D';
        if (isStopLoss) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (isIOC) {
            _ioc = 1;
            stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }

        if (mktLimitSelection.equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }

        clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        exchange.setValue(_exch);
        exchangeType.setValue(_exchType);
        scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        scripNameLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        scripName.setValue(structBuySell.scripDetails.symbol.getValue());
        buySell.setValue(structBuySell.getBuySell());
        qty.setValue(structBuySell.qty.getValue());
        atMarket.setValue(atMkt);
        limitPrice.setValue(structBuySell.limitPrice.getValue());
        if (atMkt == 'Y') {
            limitPrice.setValue(GlobalClass.mktDataHandler.getRateForMarketOrder(structBuySell.scripDetails.scripCode.getValue(), structBuySell.getBuySell()));
        }
        withSL.setValue(stopLoss);
        tiggerPrice.setValue(structBuySell.triggerPrice.getValue());
        traderRequseterID.setValue(DateUtil.getTimeDiffInSeconds());
        iocSelected.setValue(_ioc);
        instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        expityDate.setValue(structBuySell.scripDetails.expiry.getValue());
        series.setValue(structBuySell.scripDetails.series.getValue());
        discQty.setValue(structBuySell.discloseQty.getValue());
    }
}