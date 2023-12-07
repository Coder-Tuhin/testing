package Structure.Other;

import Structure.BaseStructure.StructBase;
import Structure.Request.RC.StructOrderRequest;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructOCOOrdBkDet;
import Structure.Response.RC.StructOCOPosnDet;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.Scrip.ScripDetail;
import enums.eAbsTicks;
import enums.eDelvIntra;
import enums.eExch;
import enums.eOrderLimit;
import enums.eOrderType;
import enums.eShowDepth;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructValueSetter;
import utils.Constants;
import utils.Formatter;

/**
 * Created by xtremsoft on 12/1/16.
 */
public class StructBuySell extends StructBase {

    public StructInt qty;
    public StructFloat limitPrice;
    public StructInt discloseQty;
    public StructFloat triggerPrice;
    public StructFloat bracketSquareOFF;
    public StructFloat bracketStopLoss;
    public StructFloat bracketTrailingSTopLoss;

    public StructInt netQty;//populate from net position on square off and Orderbook qty on modify
    public StructLong brokerOrderId;

    public eOrderType buyOrSell;
    public eOrderType modifyOrPlace;
    public StaticLiteMktWatch mktWatch;
    public ScripDetail scripDetails;
    public StructOrderReportReplyRecord_Pointer order = null;
    public StructMobNetPosition netPosn = null;

    public StructBoolean isSquareOff;
    public eShowDepth showDepth;
    public StructBoolean isIoc;
    public StructBoolean isStopLoss;
    public StructBoolean isMarket;
    public boolean fromSave = false;
    public eDelvIntra delvIntra = eDelvIntra.DELIVERY;
    public eAbsTicks abcTicks = eAbsTicks.ABS;

    public StructOCOPosnDet bracketNetPosn = null;
    public StructOCOOrdBkDet bracketOrdDet = null;
    public StructOrderRequest bracketOrdReq = null;

    public StructBuySell(){
        init();
        data=new StructValueSetter(fields);
    }

    private void init(){
        className=getClass().getName();
        qty=new StructInt("Qty",0);
        limitPrice=new StructFloat("LimitPrice",0);
        discloseQty=new StructInt("DiscloseQty",0);
        triggerPrice=new StructFloat("TriggerPrice",0);
        netQty=new StructInt("NetQty",0);
        brokerOrderId=new StructLong("BrokerOrderId",0);
        isSquareOff = new StructBoolean("IsSquareOff",false);

        isIoc = new StructBoolean("isIoc",false);
        isStopLoss = new StructBoolean("isStopLoss",false);
        isMarket = new StructBoolean("isMarket",false);
        mktWatch=new StaticLiteMktWatch(-1,-1);
        scripDetails=new ScripDetail();
        //order=new StructOrderReportReplyRecord_Pointer();
        //netPosn = new StructMobNetPosition();

        bracketSquareOFF = new StructFloat("SquareOff",0);
        bracketStopLoss = new StructFloat("StopLoss",0);
        bracketTrailingSTopLoss = new StructFloat("TrailingStopLoss",0);

        fields=new BaseStructure[]{
                qty,limitPrice,discloseQty,triggerPrice,netQty,brokerOrderId
        };
    }
    public char getBuySell(){
        if(buyOrSell == eOrderType.BUY){
            return 'B';
        }
        else{
            return 'S';
        }
    }

    private String isQtyOk(int qty, int seg, int discQty) {
        int qtyLimit = eOrderLimit.MAXQTY.value;
        int squareOffQty = 0;
        try {
            if (isSquareOff.getValue()) {
                squareOffQty = netQty.getValue();
                if (squareOffQty < 0)
                    squareOffQty = squareOffQty * (-1);
            }
            if (qty > 0) {
                int multi = scripDetails.segment.getValue() == eExch.NSECURR.value?10000:100;
                if (Formatter.getModValue(qty, scripDetails.getMKtLotForPlaceOrdg(),multi) == 0) {
                    if ((isSquareOff.getValue()) && (qty > squareOffQty)) {
                        return showDepth == eShowDepth.HOLDINGEQUITY ? Constants.ERR_HOLDING_QTY_MSG:Constants.ERR_NET_QTY_MSG;
                    }
                    if (seg == eExch.FNO.value) {
                        qtyLimit = scripDetails.qtyLimit.getValue();
                    }
                    else if(seg == eExch.NSECURR.value){
                        char fDigit = scripDetails.symbol.getValue().charAt(0);
                        if(Character.isDigit(fDigit)){
                            qtyLimit = eOrderLimit.MAXCURRIRFCQTY.value;
                        }else {
                            qtyLimit = eOrderLimit.MAXCURRQTY.value;
                        }
                    }
                    if (qtyLimit > 0 && qty > qtyLimit) {
                      return Constants.ERR_QTY_LIMIT_MSG + "" + qtyLimit;
                    }
                    if(discQty > 0) {
                        if (discQty > qty) {
                            return Constants.ERR_DISC_QTY;
                        } else {
                            float qtyPer = (qty * 10) / 100.0f;
                            if (discQty >= qtyPer) {

                            } else {
                               return Constants.ERR_DISC_QTY_PER;
                            }
                        }
                    }
                } else {
                   return Constants.ERR_FNO_QTY_MSG + scripDetails.getMKtLotForPlaceOrdg();
                }
            } else {
               return Constants.ERR_QTY_MSG;
            }
        } catch (Exception e) {
            return "Order issue: "+e.getMessage();
        }
        return "";
    }


    private String isPriceOk(double price, int isMarket, String msg) {
        String returnMsg = "";
        double selectedTickSize = scripDetails.getTickSize();
        if (isMarket != eOrderType.MARKET.value) {
            if (price > 0) {
                returnMsg = priceCuicuitChecker(msg, (float) price);
            } else {
                return Constants.ERR_PRICE_MSG;
            }
        }

        if (returnMsg.equals("")) {
            int multi = scripDetails.segment.getValue() == eExch.NSECURR.value?10000:100;
            if (Formatter.getModValue(price, selectedTickSize,multi) > 0) {
                if (selectedTickSize > 1) {
                    returnMsg = msg + Constants.ERR_PRICE + " Rs." + Formatter.formatter.format(selectedTickSize);
                } else {
                    if(scripDetails.segment.getValue() == eExch.NSECURR.value){
                        returnMsg = msg + Constants.ERR_PRICE + ((selectedTickSize * 100)) + " Paise.";
                    }
                    else {
                        returnMsg = msg + Constants.ERR_PRICE + ((int) (selectedTickSize * 100)) + " Paise.";
                    }
                }
            }
        }
        return returnMsg;
    }

    private String priceCuicuitChecker(String msg, float price) {
        String returnMsg = "";
        float upperCircuit = mktWatch.getUpperCkt(), lowerCircuit = mktWatch.getLowerCkt();
        if (upperCircuit > 0 && lowerCircuit > 0) {
            if (price >= lowerCircuit && price <= upperCircuit) {
                returnMsg = "";
            } else {
                returnMsg = msg + " "+Constants.ERR_LIMIT_PRICE;
            }
        }
        return returnMsg;
    }

    private String isTriggerPriceOk(double limitPrice, double trgrPrice, int isAtMkt, int withSL) {
        String returnMsg = "";
        if (withSL == 1) {
            returnMsg = isPriceOk(trgrPrice, isAtMkt, "Trigger price");

            if (returnMsg.equals("")) {
                switch (buyOrSell) {
                    case BUY:
                        if (trgrPrice <= limitPrice) {
                            returnMsg = "";
                        } else {
                            returnMsg = (scripDetails.segment.getValue() == eExch.SLBS.value?Constants.ERR_BUY_TRGR_PRICE_SLBS:
                                    Constants.ERR_BUY_TRGR_PRICE);
                        }
                        break;

                    case SELL:
                        if (trgrPrice >= limitPrice) {
                            returnMsg = "";
                        } else {
                            returnMsg = (scripDetails.segment.getValue() == eExch.SLBS.value?Constants.ERR_SELL_TRGR_PRICE_SLBS:
                                    Constants.ERR_SELL_TRGR_PRICE);
                        }
                        break;
                }
            }
        }
        return returnMsg;
    }


    private String isModify(double limitPrice, double trgrPrice, int qty, int discQty) {
        if (modifyOrPlace == eOrderType.MODIFY) {
            if(showDepth == eShowDepth.BRACKETPOS){
                return "";
            }else {
                String str = order.canOrderBeModified(qty, discQty, limitPrice, trgrPrice);
                if (!str.equals("")) {
                    return str;
                }
            }
        }
        return "";
    }


    private String maxFNOValue(int qty,double limitPrice){
        if (scripDetails.segment.getValue() != eExch.FNO.value
                &&  (qty*limitPrice)>10000000){
            return "Max Cash Value allowed is 10000000.";
        }
        return "";
    }


    public String orderValiDation(int qty,int discQty,int seg,
          double limitPrice,double trgrPrice,int isAtMkt,int withSL){
        String qtyMsg = isQtyOk(qty,seg,discQty);
        if (!qtyMsg.equals("")){
            return qtyMsg;
        }
        String priceMsg = isPriceOk(limitPrice, isAtMkt, "Price");
        if (!priceMsg.equals("")){
            return priceMsg;
        }
        String tiggerPriceMsg = isTriggerPriceOk(limitPrice, trgrPrice, isAtMkt, withSL);
        if (!tiggerPriceMsg.equals("")){
            return tiggerPriceMsg;
        }
        String modifyMsg = isModify(limitPrice, trgrPrice, qty, discQty);
        if (!modifyMsg.equals("")){
            return modifyMsg;
        }
        String maxFnoMsg = maxFNOValue(qty,limitPrice);
        if (!maxFnoMsg.equals("")){
            return maxFnoMsg;
        }
        return "";
    }

    public String validateBracketOrder(String takeProfit, String stopLoss) {
        String msg = "";
        if(takeProfit.equalsIgnoreCase("")){
            msg = "Please enter take profit value";
        }
        else if(stopLoss.equalsIgnoreCase("")){
            msg = "Please enter stop loss value";
        }
        else{
            double multiply = abcTicks == eAbsTicks.ABS?1:scripDetails.getTickSize();
            Double tpValue = Double.parseDouble(takeProfit) * multiply;
            Double slValue = Double.parseDouble(stopLoss) * multiply;
            double rate = 0;
            switch (buyOrSell){ // opposite rate
                case BUY:
                    rate = mktWatch.getBestBid()==0?mktWatch.getLastRate():mktWatch.getBestBid();
                    tpValue = rate + tpValue;
                    slValue = rate - slValue;
                    break;
                case SELL:
                    rate = mktWatch.getBestASK()==0?mktWatch.getLastRate():mktWatch.getBestASK();
                    tpValue = rate - tpValue;
                    slValue = rate + slValue;
                    break;
            }
            msg = isPriceOk(tpValue,eOrderType.LIMIT.value,"Take profit");
            if(msg.equalsIgnoreCase("")){
                msg = isPriceOk(slValue,eOrderType.LIMIT.value,"Stop Loss");
            }
            if(msg.equalsIgnoreCase("")) {
                double minTPSLVal = Math.ceil(mktWatch.getLastRate() * 0.0005);
                double maxTPSLVal = Math.floor(mktWatch.getUpperCkt() - mktWatch.getLastRate());
                if (Double.parseDouble(takeProfit) < minTPSLVal) {
                    msg = "Square Off value cannot be less than ";
                } else if (Double.parseDouble(takeProfit) > maxTPSLVal) {
                    msg = "Square Off of " + maxTPSLVal + " will be beyond lower circuit limit";
                } else if (Double.parseDouble(stopLoss) < minTPSLVal) {
                    msg = "Stop Loss value cannot be less than " + minTPSLVal;
                } else if (Double.parseDouble(stopLoss) > maxTPSLVal) {
                    msg = "Stop Loss of " + maxTPSLVal + " will be beyond lower circuit limit";
                }
            }
        }
        return msg;
    }
    public String validateCoverOrder(String stopLoss) {
        String msg = "";
        if(stopLoss.equalsIgnoreCase("")){
            msg =  "Please enter stop loss value";
        }else{
            double multiply = abcTicks == eAbsTicks.ABS?1:scripDetails.getTickSize();
            Double slValue = Double.parseDouble(stopLoss) * multiply;

            double rate = 0;
            switch (buyOrSell){ // opposite rate
                case BUY:
                    rate = mktWatch.getBestBid()==0?mktWatch.getLastRate():mktWatch.getBestBid();
                    slValue = rate - slValue;
                    break;
                case SELL:
                    rate = mktWatch.getBestASK()==0?mktWatch.getLastRate():mktWatch.getBestASK();
                    slValue = rate + slValue;
                    break;
            }
            msg = isPriceOk(slValue,eOrderType.LIMIT.value,"Stop Loss");
            if(msg.equalsIgnoreCase("")) {
                double minTPSLVal = Math.ceil(mktWatch.getLastRate() * 0.0005);
                double maxTPSLVal = Math.floor(mktWatch.getUpperCkt() - mktWatch.getLastRate());
                if (Double.parseDouble(stopLoss) < minTPSLVal) {
                    msg = "Stop Loss value cannot be less than " + minTPSLVal;
                } else if (Double.parseDouble(stopLoss) > maxTPSLVal) {
                    msg = "Stop Loss of " + maxTPSLVal + " will be beyond lower circuit limit";
                }
            }
        }
        return msg;
    }
}