package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import Structure.Other.StructBuySell;
import enums.eDelvIntra;
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

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class PlaceOrderReq_FNO_Pointer extends StructBase {
    public StructString clientCode;
    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte instrType;
    public StructByte symbolLength;
    public StructString symbol;
    public StructInt expiryDate;
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
    public StructByte iOC;
    public StructInt discQty;
    public StructChar allowIntradayDelivery;
    public StructByte orderType;

    public PlaceOrderReq_FNO_Pointer() {
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        clientCode = new StructString("clientCode", 12, "");
        exch = new StructChar("exch", '0');
        exchType = new StructChar("exchType", '0');
        scripCode = new StructInt("scripCode", 0);
        instrType = new StructByte("instrType", '0');
        symbolLength = new StructByte("symbolLength", 0);
        symbol = new StructString("symbol", 10, "");
        expiryDate = new StructInt("expiryDate", 0);
        strikePrice = new StructFloat("strikePrice", '0');
        cPType = new StructByte("cPType", 0);
        cALevel = new StructByte("cALevel", 0);
        buySell = new StructChar("buySell", '0');
        qty = new StructInt("qty", 0);
        atMarket = new StructChar("atMarket", '0');
        rate = new StructFloat("rate", 0);
        withSL = new StructChar("withSL", '0');
        triggerRate = new StructFloat("triggerRate", 0);
        tradeRequestID = new StructShort("tradeRequestID", 0);
        iOC = new StructByte("iOC", 0);
        discQty = new StructInt("DiscQty", 0);
        allowIntradayDelivery = new StructChar("allowIntradayDelivery", '0');
        orderType = new StructByte("orderType", 0);


        fields = new BaseStructure[]{
                clientCode, exch, exchType, scripCode, instrType, symbolLength, symbol, expiryDate, strikePrice, cPType, cALevel,
                buySell, qty, atMarket, rate, withSL, triggerRate, tradeRequestID, iOC, discQty, allowIntradayDelivery, orderType
        };
    }

    public void setDataForPlaceFNO(StructBuySell structBuySell, boolean isStopLoss, boolean isIOC, String mktLimitSelection, String orderTypeSelection) {

        char _exch = 'N';
        char _exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int _ioc = 0;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            _exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            _exchType = 'D';
        }
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
        int intraDel = eDelvIntra.DELIVERY.value;;
        if (UserSession.getLoginDetailsModel().isFNOIntradayDelivery()
                && structBuySell.scripDetails.enableIntraDelForCategory()) {
            if (orderTypeSelection.equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                intraDel = eDelvIntra.INTRADAY.value;
            }
        }

        clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        exch.setValue(_exch);
        exchType.setValue(_exchType);
        scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        symbolLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        symbol.setValue(structBuySell.scripDetails.symbol.getValue());
        buySell.setValue(structBuySell.getBuySell());
        qty.setValue(structBuySell.qty.getValue());
        atMarket.setValue(atMkt);
        rate.setValue(structBuySell.limitPrice.getValue());
        if (atMkt == 'Y') {
            rate.setValue(GlobalClass.mktDataHandler.getRateForMarketOrder(structBuySell.scripDetails.scripCode.getValue(), structBuySell.getBuySell()));
        }
        withSL.setValue(stopLoss);
        triggerRate.setValue(structBuySell.triggerPrice.getValue());
        tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        iOC.setValue(_ioc);

        instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        expiryDate.setValue(structBuySell.scripDetails.expiry.getValue());
        strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        cALevel.setValue(calavel);
        allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isFNOIntradayDelivery() ? 'Y' : 'N');
        orderType.setValue(intraDel);
    }
    public void setDataForPlaceCURR(StructBuySell structBuySell, boolean isStopLoss, boolean isIOC, String mktLimitSelection, String orderTypeSelection) {
        char _exch = 'N';
        char _exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int _ioc = 0;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            _exch = 'B';
        }
        else if (structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
            _exch = 'C';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value ||
                structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
            _exchType = 'D';
        }
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
        exch.setValue(_exch);
        exchType.setValue(_exchType);
        scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        symbolLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        symbol.setValue(structBuySell.scripDetails.symbol.getValue());
        buySell.setValue(structBuySell.getBuySell());
        qty.setValue(structBuySell.qty.getValue());
        atMarket.setValue(atMkt);
        rate.setValue(structBuySell.limitPrice.getValue());
        if (atMkt == 'Y') {
            rate.setValue(GlobalClass.mktDataHandler.getRateForMarketOrder(structBuySell.scripDetails.scripCode.getValue(), structBuySell.getBuySell()));
        }
        withSL.setValue(stopLoss);
        triggerRate.setValue(structBuySell.triggerPrice.getValue());
        tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        iOC.setValue(_ioc);

        instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        expiryDate.setValue(structBuySell.scripDetails.expiry.getValue());
        strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        cALevel.setValue(calavel);
        discQty.setValue(structBuySell.discloseQty.getValue());
    }
}