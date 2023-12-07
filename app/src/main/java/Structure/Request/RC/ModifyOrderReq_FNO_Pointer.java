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

public class ModifyOrderReq_FNO_Pointer extends StructBase {

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
    public StructByte ioc;
    public StructInt tradedQty;
    public StructInt pendingQty;
    public StructLong exchOrderID;
    public StructInt exchOrderTime;
    public StructInt discQty;
    public StructChar allowIntradayDelivery;
    public StructByte orderType;


    public ModifyOrderReq_FNO_Pointer(){
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
        ioc = new StructByte("ioc",0);
        tradedQty = new StructInt("tradedQty",0);
        pendingQty = new StructInt("pendingQty",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchOrderTime = new StructInt("exchOrderTime",0);
        discQty = new StructInt("DiscQty",0);
        allowIntradayDelivery = new StructChar("allowIntradayDelivery",'0');
        orderType = new StructByte("orderType",0);

        fields = new BaseStructure[]{
            clientCode,exch,exchType,scripCode,instrType,scripNameLength,scripName,expiry,strikePrice,cPType,cALevel,
            buySell,qty,atMarket,rate,withSL,triggerRate,tradeRequestID,sLTriggered,oldQty,aHStatus,ioc,tradedQty,
            pendingQty,exchOrderID,exchOrderTime,discQty,allowIntradayDelivery,orderType
        };
    }

    public void setDataForMOdifyCURR(StructBuySell structBuySell,boolean isStopLoss,boolean isIOC,String mktLimitSelection,String orderTypeSelection) {

        char _exch = 'N';
        char _exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int _ioc = 0;

        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            _exch = 'B';
        }
        else if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
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

        instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        expiry.setValue(structBuySell.scripDetails.expiry.getValue());
        strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        cALevel.setValue(calavel);
        discQty.setValue(structBuySell.discloseQty.getValue());

    }
        public void setDataForMOdifyFNO(StructBuySell structBuySell,boolean isStopLoss,boolean isIOC,String mktLimitSelection,String orderTypeSelection){

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
        }
        if (mktLimitSelection.equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }
        int intraDel = 1;
        if (UserSession.getLoginDetailsModel().isFNOIntradayDelivery() && structBuySell.scripDetails
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

        instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        expiry.setValue(structBuySell.scripDetails.expiry.getValue());
        strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        cALevel.setValue(calavel);
        allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery() ? 'Y' : 'N');
        orderType.setValue(intraDel);
    }
}