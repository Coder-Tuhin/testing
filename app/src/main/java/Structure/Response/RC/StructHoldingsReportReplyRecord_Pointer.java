package Structure.Response.RC;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import Structure.BaseStructure.StructBase;
import Structure.Other.StructMobNetPosition;
import Structure.Response.BC.StaticLiteMktWatch;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.Formatter;
import utils.GlobalClass;
import utils.VenturaException;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructHoldingsReportReplyRecord_Pointer extends StructBase {

    public StructInt bSECode;
    public StructInt nSECode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructInt totalQty; // for new system this is the successfull EDIS qty
    public StructInt soldQty;
    public StructInt brokerBeniQty;
    public StructInt exchRcvQty;
    public StructInt clientDPQty;

    public StructInt FundedStockQty;
    public StructInt CollateralStockQty;
    public StructInt slbmqty;
    public StructInt sr2qty;
    public StructInt CollateralMTFStockQty;
    public StructString reserved;
    public StructInt pOA; // for new system this is the qty need to do EDIS qty
    public StructInt reserved1;
    public StructInt reserved2;
    public StructInt reserved3;
    public StructInt reserved4;
    public StructInt reserved5;
    public StructInt reserved6;
    public StructInt reserved7;
    public StructInt reserved8;
    public StructInt reserved9;
    public StructInt reserved10;

    public double nseRate = 0.00;
    public double bseRate = 0.00;
    public boolean fromNetPosition = false;
    public int mktlot = 1;
    public int exchange;

    public double ltp = 0.00;
    public double prevclose = 0.00;
    public double netObligation = 0.00;

    public StructHoldingsReportReplyRecord_Pointer(int length){
        init(length);
        data= new StructValueSetter(fields);
    }
    public StructHoldingsReportReplyRecord_Pointer(byte[] bytes){
        init(bytes.length);
        data= new StructValueSetter(fields,bytes);
    }
    private void init(int length) {
        bSECode = new StructInt("bSECode",0);
        nSECode = new StructInt("nSECode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",50,"");
        totalQty = new StructInt("totalQty",0);
        soldQty = new StructInt("soldQty",0);
        brokerBeniQty = new StructInt("brokerBeniQty",0);
        exchRcvQty = new StructInt("exchRcvQty",0);
        clientDPQty = new StructInt("clientDPQty",0);

        FundedStockQty = new StructInt("FundedStockQty",0);
        CollateralStockQty = new StructInt("CollateralStockQty",0);
        slbmqty = new StructInt("slbmqty",0);
        sr2qty = new StructInt("sr2qty",0);
        CollateralMTFStockQty = new StructInt("CollateralMTFStockQty",0);
        reserved = new StructString("reserved4",30,"");
        pOA = new StructInt("pOA",0);
        reserved1 = new StructInt("reserved1",0);
        reserved2 = new StructInt("reserved2",0);
        reserved3 = new StructInt("reserved3",0);
        reserved4 = new StructInt("reserved4",0);
        reserved5 = new StructInt("reserved5",0);
        reserved6 = new StructInt("reserved6",0);
        reserved7 = new StructInt("reserved7",0);
        reserved8 = new StructInt("reserved8",0);
        reserved9 = new StructInt("reserved9",0);
        reserved10 = new StructInt("reserved10",0);
        if(length > 130) {
            fields = new BaseStructure[]{
                    bSECode, nSECode, scripNameLength, scripName, totalQty, soldQty, brokerBeniQty, exchRcvQty, clientDPQty,
                    FundedStockQty, CollateralStockQty, slbmqty, sr2qty, CollateralMTFStockQty, reserved,pOA,
                    reserved1,reserved2,reserved3,reserved4,reserved5,reserved6,reserved7,reserved8,reserved9,
                    reserved10
            };
        }else{
            fields = new BaseStructure[]{
                    bSECode, nSECode, scripNameLength, scripName, totalQty, soldQty, brokerBeniQty, exchRcvQty, clientDPQty,
                    FundedStockQty, CollateralStockQty, slbmqty, sr2qty, CollateralMTFStockQty, reserved
            };
        }
    }

    public double getNseRate(){
        return nseRate;
    }
    public double getBseRate(){
        return bseRate;
    }

    public void setNseRate(double nseRate) {
        this.nseRate = nseRate;
    }

    public void setBseRate(double bseRate) {
        this.bseRate = bseRate;
    }

    public double getNseTOTALRate(){
        if(nSECode.getValue() > 0){
            return nseRate*totalQty.getValue();
        }
        return 0.00;
    }
    public double getBseTOTALRate(){
        if(bSECode.getValue() > 0){
            return bseRate*totalQty.getValue();
        }
        return 0.00;
    }
    public int getNetQty(){
        return totalQty.getValue() + soldQty.getValue();
    }

    private int callputVal = Integer.MIN_VALUE;

    public boolean isCallPut(){
        if (callputVal == Integer.MIN_VALUE){
            String[] scripSeperate = scripName.getValue().split(" ");
            if(scripSeperate.length<= 3){
                scripSeperate = scripName.getValue().split("-");
            }
            List<String> _tempCPlist = Arrays.asList(scripSeperate);
            callputVal = (_tempCPlist.contains("PE") || _tempCPlist.contains("CE"))? 1:0;
        }
        return callputVal>0;
    }

    public StructNetOblization getNetOblization() {
        StructNetOblization netObj = new StructNetOblization();
        try{
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(nSECode.getValue(),false);
            if (smw !=null){
                ltp = smw.getLastRate()>0 ? smw.getLastRate() : smw.getPClose();
                prevclose = smw.getPClose();
            }
            StructMobNetPosition smnp = GlobalClass.getClsNetPosn().getPositionForScripcodeDerivativeNetObligation(nSECode.getValue());

            int bfQty = totalQty.getValue();
            if (fromNetPosition) bfQty = 0;
            int totalSell = 0;
            double totalSellAvg = 0;
            int totalBuy = 0;
            double totalBuyAvg = 0;
            int netQty = bfQty;
            if (smnp != null){
                totalSell = smnp.getTotSellQty();
                totalSellAvg = smnp.getAvgSellPrice();
                totalBuy = smnp.getTotBuyQty();
                totalBuyAvg = smnp.getAvgBuyPrice();
                netQty = netQty  + smnp.getNetQty();
            }
            netObj.setNetQty(String.valueOf(netQty));
            //totalValue.setText(String.valueOf(netQty));
            NumberFormat formatter = Formatter.getFormatter(exchange);
            DecimalFormat df = new DecimalFormat("#.##");// (exchange == eExch.NSECURR.value)?new DecimalFormat("#.####"):new DecimalFormat("#.##");
            netObj.setLtpValue(formatter.format(ltp));
            //ltpValue.setText(formatter.format(ltp));
            netObligation = (totalSell*totalSellAvg - totalBuy*totalBuyAvg);
            //netObligation = (totalSell*Round(totalSellAvg,df)) - (totalBuy*Round(totalBuyAvg,df));

            if (!isCallPut()){
                netObligation = netObligation+(netQty*ltp - bfQty*prevclose);
            }
            netObligation = netObligation * mktlot;
            netObj.setNetOblization(Formatter.toTwoDecimalValue(Round((netObligation),df)));
            return netObj;//formatter.format(Round((netObg*holdingRow.mktlot),df));
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return netObj;
    }

    private double Round(double value,DecimalFormat df){
        return Double.valueOf(df.format(value));
    }

    public double getMTM() {

        double mtm = 0.0;
        StructMobNetPosition smnp = GlobalClass.getClsNetPosn().getPositionForScripcodeDerivativeNetObligation(nSECode.getValue());
        if(fromNetPosition){
            mtm = smnp.getAVGMTM();
        }
        else{
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(nSECode.getValue(),true);
            if (smw !=null){
                ltp = smw.getLastRate()>0 ? smw.getLastRate() : smw.getPClose();
                prevclose = smw.getPClose();
            }
            int bfQty = totalQty.getValue();
            int totalSell = 0;
            double totalSellAvg = 0;
            int totalBuy = 0;
            double totalBuyAvg = 0;
            int netQty = 0;
            if (smnp != null){
                totalSell = smnp.getTotSellQty();
                totalSellAvg = smnp.getAvgSellPrice();
                totalBuy = smnp.getTotBuyQty();
                totalBuyAvg = smnp.getAvgBuyPrice();
                //netQty = netQty  + smnp.getNetQty();
            }
            double finaltotalBuy = totalBuy * totalBuyAvg;
            double finaltotalSell = totalSell * totalSellAvg;
            if(bfQty > 0){
                finaltotalBuy = (totalBuy*totalBuyAvg + bfQty*prevclose);
                totalBuy = totalBuy + bfQty;
                //totalBuyAvg = (totalBuyAvg + prevclose)/2;
            }
            else if(bfQty < 0){
                finaltotalSell = (totalSell*totalSellAvg + (-1)*bfQty*prevclose);
                totalSell = totalSell + (-1)*bfQty;
                //totalSellAvg = (totalSellAvg + prevclose)/2;
            }
            netQty = totalBuy - totalSell;
            double totalPL = ((netQty*ltp) + (finaltotalSell) - (finaltotalBuy));
            double bkpl = 0;
            if(totalBuy>0 && totalSell>0) {
                bkpl = Math.min(totalBuy, totalSell) * ((finaltotalSell / totalSell) - (finaltotalBuy / totalBuy));
            }
            mtm  = (totalPL - bkpl) * mktlot;
        }
        return mtm;
    }

    public double getBookedPL(){
        //return getTotalPL() - getMTM() ;
        double bkpl = 0.0;
        StructMobNetPosition smnp = GlobalClass.getClsNetPosn().getPositionForScripcodeDerivativeNetObligation(nSECode.getValue());
        if(fromNetPosition){
            bkpl = smnp.getAVGBookedPL();
        }
        else{
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(nSECode.getValue(),true);
            if (smw !=null){
                ltp = smw.getLastRate()>0 ? smw.getLastRate() : smw.getPClose();
                prevclose = smw.getPClose();
            }
            int bfQty = totalQty.getValue();
            int totalSell = 0;
            double totalSellAvg = 0;
            int totalBuy = 0;
            double totalBuyAvg = 0;
            if (smnp != null){
                totalSell = smnp.getTotSellQty();
                totalSellAvg = smnp.getAvgSellPrice();
                totalBuy = smnp.getTotBuyQty();
                totalBuyAvg = smnp.getAvgBuyPrice();
            }
            double finaltotalBuyAvg = totalBuyAvg;
            double finaltotalSellAvg = totalSellAvg;
            if(bfQty > 0){
                finaltotalBuyAvg = (totalBuy*totalBuyAvg + bfQty*prevclose)/(totalBuy + bfQty);
                totalBuy = totalBuy + bfQty;
            }
            else if(bfQty < 0){
                finaltotalSellAvg = (totalSell*totalSellAvg + (-1)*bfQty*prevclose)/(totalSell + (-1)*bfQty);
                totalSell = totalSell + (-1)*bfQty;
            }
            bkpl = 0.00;
            if(totalBuy > 0 && totalSell>0) {
                bkpl = Math.min(totalBuy, totalSell) * (finaltotalSellAvg - finaltotalBuyAvg);
            }
            bkpl = bkpl * mktlot;
        }
        return bkpl;
    }
    public void updateFromTradeBook(StructTradeReportReplyRecord_Pointer myTradeBook) {

        scripName.setValue(myTradeBook.scripName.getValue());
        nSECode.setValue(myTradeBook.scripCode.getValue());
        totalQty.setValue(totalQty.getValue() + myTradeBook.qty.getValue());
        exchange = myTradeBook.exchange;
        mktlot = myTradeBook.mktLot;
    }
}