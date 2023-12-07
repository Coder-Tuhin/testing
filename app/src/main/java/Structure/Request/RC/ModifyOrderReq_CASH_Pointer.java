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
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by Xtremsoft on 11/17/2016.
 */

public class ModifyOrderReq_CASH_Pointer extends StructBase {
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
    public StructChar sLTriggered;
    public StructFloat triggerRate;
    public StructShort tradeRequestID;
    public StructInt oldQty;
    public StructInt tradedQty;
    public StructInt pendingQty;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructByte ioc;
    public StructInt disclosedQty;
    public StructChar aHStatus;
    public StructChar allowIntradayDelivery;
    public StructByte orderType;

    public ModifyOrderReq_CASH_Pointer(){
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
        sLTriggered = new StructChar("sLTriggered",'0');
        triggerRate = new StructFloat("triggerRate",0);
        tradeRequestID = new StructShort("tradeRequestID",0);
        oldQty = new StructInt("oldQty",0);
        tradedQty = new StructInt("tradedQty",0);
        pendingQty = new StructInt("pendingQty",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchOrderTime = new StructInt("exchOrderTime",0);
        ioc = new StructByte("ioc",0);
        disclosedQty = new StructInt("disclosedQty",0);
        aHStatus = new StructChar("aHStatus",'0');
        allowIntradayDelivery = new StructChar("allowIntradayDelivery",'0');
        orderType = new StructByte("orderType",0);

        fields = new BaseStructure[]{
                clientCode,exch,exchType,scripCode,scripNameLength,scripName,buySell,qty,atMarket,rate,withSL,
                sLTriggered,triggerRate,tradeRequestID,oldQty,tradedQty,pendingQty,
                exchOrderID,exchOrderTime,ioc,disclosedQty,aHStatus,allowIntradayDelivery,orderType
        };
    }

    public void setDataForModifyCASH(StructBuySell structBuySell, boolean isStopLoss, boolean isIOC, String mktLimitSelection, String orderTypeSelection){

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
        int intraDel = 1;
        if (UserSession.getLoginDetailsModel().isIntradayDelivery() && structBuySell.scripDetails
                .enableIntraDelForCategory()) {
            if (orderTypeSelection.equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                intraDel = eDelvIntra.INTRADAY.value;
            }
        }
        clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        exch.setValue(_exch);
        exchType.setValue(_exchType);
        scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        scripNameLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        scripName.setValue(structBuySell.scripDetails.symbol.getValue());
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
        ioc.setValue(_ioc);
        disclosedQty.setValue(structBuySell.discloseQty.getValue());
        allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery() ? 'Y' : 'N');
        orderType.setValue(intraDel);

        char slTigged = 'N';
        if(structBuySell.order.sLTriggered.getValue() != ' '){
            slTigged = structBuySell.order.sLTriggered.getValue();
        }
        sLTriggered.setValue(slTigged);
        oldQty.setValue(structBuySell.order.qty.getValue());
        tradedQty.setValue(structBuySell.order.getFinalTradeQty());
        pendingQty.setValue(structBuySell.qty.getValue() - structBuySell.order.getFinalTradeQty());
        exchOrderID.setValue(structBuySell.order.exchOrderID.getValue());
        exchOrderTime.setValue(Math.max(structBuySell.order.finalexchtime, structBuySell.order.exchOrderTime.getValue()));
        char ahStatus = 'N';
        if(structBuySell.order.aHStatus.getValue() != ' '){
            ahStatus = structBuySell.order.aHStatus.getValue();
        }
        aHStatus.setValue(ahStatus);
    }
}
