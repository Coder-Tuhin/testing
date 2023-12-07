package Structure.Request.RC;

import java.util.Date;

import Structure.BaseStructure.ExtractPacket;
import Structure.Other.StructBuySell;
import enums.eAbsTicks;
import enums.eDelvIntra;
import enums.eETS;
import enums.eExch;
import enums.eOrderType;
import structure.BaseStructure;
import structure.StructBase;
import structure.StructByte;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;

public class StructOCOOrder  extends StructBase {

    public StructOrderRequest orderRequest;
    public StructOCOCloseDet TP;
    public StructOCOCloseDet SL;
    public StructOCOCloseDet OldSL;
    public StructByte isTrailingSL;
    public StructInt TickSize;
    public StructInt upperCkt;
    public StructInt lowerCkt;
    public StructInt TrlSL;
    public StructByte EntryChgd;
    public StructByte TPChgd;
    public StructByte SLChgd;
    public StructShort reqType;

    public StructOCOOrder(){
        init();
        data = new StructValueSetter(fields);
    }
    public StructOCOOrder(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int iSize = new StructOrderRequest().data.sizeOf();//415+8
            byte iBytes[] = new byte[iSize];
            System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
            aCount += iSize;
            orderRequest = new StructOrderRequest(iBytes);

            iSize = new StructOCOCloseDet().data.sizeOf();//415+8
            iBytes = new byte[iSize];
            System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
            aCount += iSize;
            TP = new StructOCOCloseDet(iBytes);

            iBytes = new byte[iSize];
            System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
            aCount += iSize;
            SL = new StructOCOCloseDet(iBytes);

            iBytes = new byte[iSize];
            System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
            aCount += iSize;
            OldSL = new StructOCOCloseDet(iBytes);

            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            data = new StructValueSetter(fields, ep.getExtractData());
            aCount += oPacketSize;
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    public void setByteData() throws Exception {

        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf() + (new StructOrderRequest().data.sizeOf())+ (new StructOCOCloseDet().data.sizeOf()*3)];

        byte ipData[] = orderRequest.data.getByteArr();
        System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
        aCont += ipData.length;

        ipData = TP.data.getByteArr();
        System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
        aCont += ipData.length;

        ipData = SL.data.getByteArr();
        System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
        aCont += ipData.length;

        ipData = OldSL.data.getByteArr();
        System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
        aCont += ipData.length;

        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
        this.orgData = orgData;
    }

    private void init(){
        orderRequest = new StructOrderRequest();
        TP = new StructOCOCloseDet();
        SL = new StructOCOCloseDet();
        OldSL = new StructOCOCloseDet();
        isTrailingSL = new StructByte("istrailingsl", 0);
        TickSize = new StructInt("ticksize", 0);
        upperCkt = new StructInt("upperCkt", 0);
        lowerCkt = new StructInt("lowerCkt", 0);
        TrlSL = new StructInt("trlSl", 0);
        EntryChgd = new StructByte("EntryChgd", 0);
        TPChgd = new StructByte("tpChgd", 0);
        SLChgd = new StructByte("slchgd", 0);
        reqType = new StructShort("ReqType",0);
        fields = new BaseStructure[]{
                isTrailingSL,TickSize,upperCkt, lowerCkt, TrlSL,EntryChgd,TPChgd,SLChgd,reqType
        };
    }

    public void setDataForModifyOrder(StructBuySell structBuySell) throws Exception{

        if(structBuySell.delvIntra == eDelvIntra.BRACKETORDER) {

            TP.AbsTicks.setValue(structBuySell.abcTicks.value);
            TP.CloseAt.setValue(2); //for LTP-1,ATP-2
            if (structBuySell.abcTicks == eAbsTicks.ABS) {
                TP.AbsVal.setValue(structBuySell.bracketSquareOFF.getValue());
            } else {
                TP.Ticks.setValue((int) structBuySell.bracketSquareOFF.getValue());
            }
        }

        SL.AbsTicks.setValue(structBuySell.abcTicks.value);
        SL.CloseAt.setValue(2); //for LTP-1,ATP-2
        if(structBuySell.abcTicks == eAbsTicks.ABS){
            SL.AbsVal.setValue(structBuySell.bracketStopLoss.getValue());
        }else{
            SL.Ticks.setValue((int) structBuySell.bracketStopLoss.getValue());
        }

        TickSize.setValue(structBuySell.scripDetails.tickSize.getIntValue());
        isTrailingSL.setValue(1);
        TrlSL.setValue(structBuySell.scripDetails.tickSize.getIntValue());
        upperCkt.setValue(structBuySell.mktWatch.getSw().upperCircuit.getIntValue());
        lowerCkt.setValue(structBuySell.mktWatch.getSw().lowerCircuit.getIntValue());

        OldSL.Ticks.setValue(structBuySell.bracketOrdDet.ticksSL.getValue());
        OldSL.CloseAt.setValue(structBuySell.bracketOrdDet.closeAtSL.getValue());
        OldSL.AbsTicks.setValue(structBuySell.bracketOrdDet.absTicksSL.getValue());
        OldSL.AbsVal.setValue(structBuySell.bracketOrdDet.absValSL.getValue());

        SLChgd.setValue(1);
        TPChgd.setValue(1);
        EntryChgd.setValue(0);
        String clientCode = UserSession.getLoginDetailsModel().getUserID();

        orderRequest = structBuySell.bracketOrdReq;

        int seg = structBuySell.scripDetails.segment.getValue();
        String symbol = structBuySell.scripDetails.symbol.getValue();

        int qty = structBuySell.qty.getValue();
        if(seg == eExch.FNO.value){
            qty = qty/structBuySell.scripDetails.mktLot.getValue();
        }
        //int _discQty = structBuySell.discloseQty.getValue()/ structBuySell.scripDetails.mktLot.getValue();
        //int _discQty = structBuySell.discloseQty.getValue()/ structBuySell.scripDetails.mktLot.getValue();

        orderRequest.underlyingType.setValue(structBuySell.scripDetails.underlyingType.getValue());
        orderRequest.preOpen.setValue(0);

        orderRequest.qty.setValue(qty);
        orderRequest.pendQty.setValue(qty - structBuySell.bracketOrdReq.trdQty.getValue());//pendingQty=txtQty-tradeqty
        orderRequest.trdQty.setValue(structBuySell.bracketOrdReq.trdQty.getValue());
        orderRequest.preMkt.setValue(false);
        orderRequest.orderPlaceReqCode.setValue(clientCode);
        orderRequest.exchSegment.setValue(seg);
        orderRequest.orderType.setValue(structBuySell.buyOrSell.value);


        //orderRequest.atMarket.setValue(1);
        //orderRequest.stopLoss.setValue(0);
        //orderRequest.ioc.setValue(1);
        //orderRequest.afterHours.setValue('N');
        orderRequest.clientCode.setValue(clientCode);
        orderRequest.orderRequestCode.setValue(clientCode);
        //orderRequest.proClient.setValue(1);
        orderRequest.expl.setValue('N');
        orderRequest.exerciseOrder.setValue('N');
        orderRequest.securityID.setValue(symbol);
        orderRequest.timeInForce.setValue(3);

        orderRequest.orderRequestCode.setValue(clientCode);
        orderRequest.localOrderID.setValue(DateUtil.getTimeDiffInSeconds());
        orderRequest.localOrderTime.setValue(new Date());
        orderRequest.appID.setValue(2);
        orderRequest.clientType.setValue(1);
        orderRequest.status.setValue("Sent to Broker");
        orderRequest.eets.setValue(eETS.Entry.value);
        orderRequest.oldBrokerOrderID.setValue(structBuySell.bracketOrdReq.brokerOrderID.getValue());
        orderRequest.oldQty.setValue(structBuySell.bracketOrdReq.qty.getValue());
        orderRequest.reqType.setValue(eOrderType.MODIFY.value);
        reqType.setValue(eOrderType.MODIFY.value);
        setByteData();
    }

    public void setDataForPlaceOrder(StructBuySell structBuySell) throws Exception{

        if(structBuySell.delvIntra == eDelvIntra.BRACKETORDER) {

            TP.AbsTicks.setValue(structBuySell.abcTicks.value);
            TP.CloseAt.setValue(2); //for LTP-1, ATP-2
            if (structBuySell.abcTicks == eAbsTicks.ABS) {
                TP.AbsVal.setValue(structBuySell.bracketSquareOFF.getValue());
            } else {
                TP.Ticks.setValue((int) structBuySell.bracketSquareOFF.getValue());
            }
        }

        SL.AbsTicks.setValue(structBuySell.abcTicks.value);
        SL.CloseAt.setValue(2); //for LTP-1,ATP-2
        if(structBuySell.abcTicks == eAbsTicks.ABS){
            SL.AbsVal.setValue(structBuySell.bracketStopLoss.getValue());
        }else{
            SL.Ticks.setValue((int) structBuySell.bracketStopLoss.getValue());
        }

        TickSize.setValue(structBuySell.scripDetails.tickSize.getIntValue());

        String clientCode = UserSession.getLoginDetailsModel().getUserID();

        int seg = structBuySell.scripDetails.segment.getValue();
        String symbol = structBuySell.scripDetails.symbol.getValue();

        int qty = structBuySell.qty.getValue();
        int _discQty = structBuySell.discloseQty.getValue();
        if(seg == eExch.FNO.value){
            qty = qty/ structBuySell.scripDetails.mktLot.getValue();
            _discQty = _discQty/ structBuySell.scripDetails.mktLot.getValue();
        }
        orderRequest.underlyingType.setValue(structBuySell.scripDetails.underlyingType.getValue());
        orderRequest.preOpen.setValue(0);
        orderRequest.pendQty.setValue(qty);//pendingQty=txtQty-tradeqty
        orderRequest.qty.setValue(qty);

        orderRequest.preMkt.setValue(false);
        orderRequest.orderPlaceReqCode.setValue(clientCode);
        orderRequest.exchSegment.setValue(seg);
        orderRequest.orderType.setValue(structBuySell.buyOrSell.value);
        orderRequest.token.setValue(structBuySell.scripDetails.scripCode.getValue());
        orderRequest.prodType.setValue(structBuySell.delvIntra.value);

        if (seg == eExch.FNO.value || seg == eExch.NSECURR.value) {
            _discQty = 0;
        }
        orderRequest.discQty.setValue(_discQty);
        orderRequest.qtyType.setValue(0);
        if (seg == eExch.NSECURR.value) {
            orderRequest.limitPrice.setValue(structBuySell.limitPrice.getValue() * 100000);
            orderRequest.triggerPrice.setValue(structBuySell.triggerPrice.getValue() * 100000);
        } else {
            orderRequest.limitPrice.setValue(structBuySell.limitPrice.getValue());
            orderRequest.triggerPrice.setValue(structBuySell.triggerPrice.getValue());
        }
        orderRequest.atMarket.setValue(1);
        orderRequest.stopLoss.setValue(0);
        orderRequest.ioc.setValue(1);
        orderRequest.afterHours.setValue('N');
        orderRequest.clientCode.setValue(clientCode);
        orderRequest.orderRequestCode.setValue(clientCode);
        orderRequest.proClient.setValue(1);
        orderRequest.localOrderID.setValue(DateUtil.getTimeDiffInSeconds());
        orderRequest.localOrderTime.setValue(new Date());
        orderRequest.expl.setValue('N');
        orderRequest.exerciseOrder.setValue('N');
        orderRequest.securityID.setValue(symbol);
        orderRequest.timeInForce.setValue(3);

        orderRequest.appID.setValue(2);
        orderRequest.clientType.setValue(1);
        orderRequest.status.setValue("Sent to Broker");
        orderRequest.eets.setValue(eETS.Entry.value);
        orderRequest.reqType.setValue(eOrderType.PLACE.value);
        reqType.setValue(eOrderType.PLACE.value);
        isTrailingSL.setValue(1);
        TickSize.setValue(structBuySell.scripDetails.tickSize.getIntValue());
        upperCkt.setValue(structBuySell.mktWatch.getSw().upperCircuit.getIntValue());
        lowerCkt.setValue(structBuySell.mktWatch.getSw().lowerCircuit.getIntValue());
        TrlSL.setValue(structBuySell.scripDetails.tickSize.getIntValue());

        setByteData();
    }
}
