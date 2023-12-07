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

public class PlaceOrderReq_CASH_Pointer extends StructBase {

        public StructString clientCode;
        public StructChar exchange;
        public StructChar exchangeTYpe;
        public StructInt scripCode;
        public StructByte scripNameLength;
        public StructString scripName;
        public StructChar buySell;
        public StructInt qty;
        public StructChar atMarket;
        public StructFloat limitPrice;
        public StructChar stopLoss;
        public StructFloat tiggerPrice;
        public StructShort traderRequesterID;
        public StructByte iOCSelected;
        public StructInt discQty;
        public StructChar allowIntradayDelivery;
        public StructByte orderType;

    public PlaceOrderReq_CASH_Pointer(){
            init();
            data= new StructValueSetter(fields);
        }

        private void init(){
            clientCode = new StructString("clientCode",12,"");
            exchange = new StructChar("exchange",'0');
            exchangeTYpe = new StructChar("exchangeTYpe",'0');
            scripCode = new StructInt("scripCode",0);
            scripNameLength = new StructByte("scripNameLength",0);
            scripName = new StructString("scripName",12,"");
            buySell = new StructChar("buySell",'0');
            qty = new StructInt("qty",0);
            atMarket = new StructChar("atMarket",'0');
            limitPrice = new StructFloat("limitPrice",0);
            stopLoss = new StructChar("stopLoss",'0');
            tiggerPrice = new StructFloat("tiggerPrice",0);
            traderRequesterID = new StructShort("traderRequesterID",0);
            iOCSelected = new StructByte("iOCSelected",0);
            discQty = new StructInt("discQty",0);
            allowIntradayDelivery = new StructChar("allowIntradayDelivery",'0');
            orderType = new StructByte("orderType",0);


            fields = new BaseStructure[]{
                    clientCode,exchange,exchangeTYpe,scripCode,scripNameLength,scripName,buySell,qty,atMarket,limitPrice,
                    stopLoss,tiggerPrice,traderRequesterID,iOCSelected,discQty,allowIntradayDelivery,orderType
            };
        }

    public void setDataForPlaceCASH(StructBuySell structBuySell, boolean isStopLoss, boolean isIOC, String mktLimitSelection, String orderTypeSelection) {

        char _exch = 'N';
        char _exchType = 'C';
        char atMkt = 'N';
        char _stopLoss = 'N';
        int _ioc = 0;
        int intraDel = eDelvIntra.DELIVERY.value;;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            _exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            _exchType = 'D';
        }
        if (isStopLoss) {
            _stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (isIOC) {
            _ioc = 1;
            _stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }
        if (mktLimitSelection.equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }
        if (UserSession.getLoginDetailsModel().isIntradayDelivery()
                && structBuySell.scripDetails.enableIntraDelForCategory()) {
            if (orderTypeSelection.equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                intraDel = eDelvIntra.INTRADAY.value;
            }
        }
        clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        exchange.setValue(_exch);
        exchangeTYpe.setValue(_exchType);
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
        stopLoss.setValue(_stopLoss);
        tiggerPrice.setValue(structBuySell.triggerPrice.getValue());
        traderRequesterID.setValue(DateUtil.getTimeDiffInSeconds());
        iOCSelected.setValue(_ioc);
        discQty.setValue(structBuySell.discloseQty.getValue());
        allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery() ? 'Y' : 'N');
        orderType.setValue(intraDel);
    }
}
