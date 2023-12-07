package handler;

import android.text.SpannableString;
import android.text.TextUtils;


import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import Structure.Other.StructTrdQtyPriceTime;
import Structure.Request.RC.PositionConversion_Pointer;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeSummary_Pointer;
import Structure.Response.Scrip.ScripDetail;
import connection.SendDataToRCServer;
import enums.eMessageCode;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by Tapas on 28-04-2016.
 */
public class ClsTradeBook {

    public HashMap<String,StructTradeReportReplyRecord_Pointer> m_hmTradeBook;
    long requestTime = 0;
    public CharSequence lasTradedScrip = "";
    public String lastTradeTime = "";
    private ArrayList<StructTradeSummary_Pointer> mList;
    public ClsTradeBook(){
        m_hmTradeBook = new HashMap<>();
        mList = new ArrayList<>();
    }

    public void AddTrade(StructTradeSummary_Pointer myTradeBook){
        mList.add(myTradeBook);
        if (myTradeBook.complete.getValue() == 1){
            GlobalClass.getClsNetPosn().clearNetPosition();
            for (int j = 0;j<mList.size();j++){
                StructTradeSummary_Pointer myTradeBooktemp = mList.get(j);
                StructTradeReportReplyRecord_Pointer[] singleTradeResp = myTradeBooktemp.tradeRequest;
                for(int i = 0 ; i < myTradeBooktemp.cNoOfRecs.getValue() ; i++){
                    addTradeBook(singleTradeResp[i]);
                }
            }
            mList.clear();
        }
    }

    public boolean addTradeBook(StructTradeReportReplyRecord_Pointer myTradeBook){
        try {
            StructTradeReportReplyRecord_Pointer isFresh = m_hmTradeBook.put(myTradeBook.getKey(), myTradeBook);
            GlobalClass.getClsNetPosn().AddTrade(myTradeBook);
            GlobalClass.getClsOrderBook().setFinalTdQtyfromTradeResponse(getTradeQtyPriceTimeForExchOrdId(myTradeBook.exchOrderID.getValue()));
            return (isFresh == null);
        } catch (Exception ex){
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
        }
        return true;
    }
    public boolean addTradeBook(StructTradeReportReplyRecord_Pointer tradeRow, boolean refresh) {
        //GlobalClass.log("TrdConf : " + tradeRow.toString());
        StructOrderReportReplyRecord_Pointer orderBookForExchOrdID = GlobalClass.getClsOrderBook().getOrderBookForExchOrdID(tradeRow.exchOrderID.getValue());
        if (orderBookForExchOrdID != null) {
            tradeRow.orderType.setValue(orderBookForExchOrdID.orderType.getValue());
        }
        boolean isfresh = addTradeBook(tradeRow);
        if(refresh){
            try {
                GlobalClass.getClsOrderBook().refreshOrderBookForTrade(tradeRow);
            }
            catch (Exception ex){
                GlobalClass.showAlertDialog(Constants.ERR_MSG);
            }
        }
        return isfresh;
    }

    // Updates order type after order conversion
    public void updateOrderType(StructTradeReportReplyRecord_Pointer myTradeBook){
        StructTradeReportReplyRecord_Pointer tradeReport = m_hmTradeBook.get(myTradeBook.getKey());
        tradeReport.orderType.setValue(myTradeBook.orderType.getValue());
        GlobalClass.getClsNetPosn().clearNetPosition();
        Set<String> keyset = m_hmTradeBook.keySet();
        for( String i : keyset){
            tradeReport = m_hmTradeBook.get(i);
            GlobalClass.getClsNetPosn().AddTrade(tradeReport);
        }
    }

    public void AddSortedTrade(StructTradeReportReplyRecord_Pointer myTradeBook){
        //m_hmOrderBook.clear();
        m_hmTradeBook.put(myTradeBook.getKey(),myTradeBook);
    }

    public ArrayList<String> getAllKeys(){
        return new ArrayList<>(m_hmTradeBook.keySet());
    }


    public TreeMap<Integer, StructTradeReportReplyRecord_Pointer> getTradeTreeMap(){
        TreeMap<Integer, StructTradeReportReplyRecord_Pointer> trdBookHM = new TreeMap<>();
        ArrayList<String> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            String key = allKey.get(i);
            StructTradeReportReplyRecord_Pointer myTradeBook = m_hmTradeBook.get(key);
            trdBookHM.put(myTradeBook.exchTradeID.getValue(), myTradeBook);
        }
        return trdBookHM;
    }

    public StructTrdQtyPriceTime getTradeQtyPriceTimeForExchOrdId(long exchOrderID){
        StructTrdQtyPriceTime qtyPT = new StructTrdQtyPriceTime();
        try {
            qtyPT.exchOrderID = exchOrderID;
            int finalTrdQty = 0, countForTrd = 0;
            double trdPrice = 0.00;
            int finalexchtime = 0;

            ArrayList<String> allKey = getAllKeys();

            for(int j=0;j<allKey.size();j++){
                String key = allKey.get(j);
                StructTradeReportReplyRecord_Pointer trdRow = m_hmTradeBook.get(key);
                if(exchOrderID == trdRow.exchOrderID.getValue()){
                    finalTrdQty += trdRow.qty.getValue();
                    trdPrice += trdRow.rate.getValue();
                    countForTrd++;
                    if(trdRow.exchTradeTime.getValue() > finalexchtime){
                        finalexchtime = trdRow.exchTradeTime.getValue();
                    }
                }
            }
            qtyPT.tradeQty = finalTrdQty;
            qtyPT.exchTime = finalexchtime;
            if(countForTrd > 0){
                qtyPT.tradePrice = (trdPrice / countForTrd);
            }
            else{
                qtyPT.tradePrice = 0.00;
            }
        }
        catch (Exception exception) {
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
            GlobalClass.onError("Exceion in structTrdqtypricetime : %@",exception);
        }
        return qtyPT;
    }

    public boolean sendTradeBookRequest(){
        Date date = new Date();
        long second = date.getTime();
        if(second - requestTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendOrderTradeReq(eMessageCode.TRADE_BOOK_REQ);
            requestTime = second;
            return true;
        }
        return false;
    }

    public void sendPositionConvertion(StructTradeReportReplyRecord_Pointer trade, ScripDetail scripDetail){

        PositionConversion_Pointer place = new PositionConversion_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(trade.exch.getValue());
        place.exchType.setValue(trade.exchType.getValue());
        place.scripCode.setValue(trade.scripCode.getValue());
        place.scripNameLength.setValue(scripDetail.symbol.getValue().length());
        place.scripName.setValue(scripDetail.symbol.getValue());
        place.instrumentType.setValue(scripDetail.getInstrumentType());
        place.cPType.setValue(scripDetail.getCPTypeForOrderPlace());
        byte calavel = 0;
        place.cALevel.setValue(calavel);
        place.expiry.setValue(scripDetail.expiry.getValue());
        place.strikePrice.setValue(scripDetail.getStrikeRateForOrderPlacing());
        place.buySell.setValue(trade.buySell.getValue());
        place.qty.setValue(trade.qty.getValue());
        place.rate.setValue(trade.rate.getValue());
        place.exchOrderID.setValue(trade.exchOrderID.getValue());
        place.exchTradeID.setValue(trade.exchTradeID.getValue());

        byte ordType = 0;
        if(trade.orderType.getValue() == 0){
            ordType = 1;
        }
        else{
            ordType = 0;
        }
        place.orderType.setValue(ordType);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPositionConversionReq(place);
    }


    public HashMap<Integer, StructTradeSummary> getTradeSummary(){

        HashMap <Integer, StructTradeSummary>summaryMap = new HashMap();
        Object keys[] = m_hmTradeBook.keySet().toArray();
        StructTradeReportReplyRecord_Pointer lastTradedSMTC = null;
        int latestTime = 0;
        for(Object key:keys){
            StructTradeReportReplyRecord_Pointer tc = m_hmTradeBook.get(key);

            StructTradeSummary tradeSummary = summaryMap.get(tc.getExchange());
            float value =  tc.qty.getValue() * tc.rate.getValue();
            if(tradeSummary != null){
                int totalTrades = tradeSummary.total.getValue()+1;
                value =  tradeSummary.value.getValue() + value;
                tradeSummary.value.setValue(value);
                tradeSummary.total.setValue(totalTrades);
            }
            else{
                int tradeCount = 1;
                tradeSummary = new StructTradeSummary();
                tradeSummary.value.setValue(value);
                tradeSummary.total.setValue(tradeCount);
                tradeSummary.segment.setValue(tc.getExchange());
                summaryMap.put(tc.getExchange(),tradeSummary);
            }
            if(tc.exchTradeTime.getValue() > latestTime) {
                latestTime = tc.exchTradeTime.getValue();
                lastTradedSMTC = tc;
            }
        }
        if(lastTradedSMTC != null) {
            String buySell = lastTradedSMTC.getBuySell().toLowerCase();
            int color = buySell.equalsIgnoreCase("Sell")? GlobalClass.latestContext.getResources().getColor(R.color.red)
                    : GlobalClass.latestContext.getResources().getColor(R.color.green1);
            SpannableString buySell_SSTR = GlobalClass.getSpannableString(buySell.toUpperCase(), 0, buySell.length(), 0f, color);
            lasTradedScrip = TextUtils.concat("Recent: ",buySell_SSTR, " "
                    , lastTradedSMTC.scripName.getValue(), " "
                    , lastTradedSMTC.qty.getValue() + "@"
                    , Formatter.toTwoDecimalValue(lastTradedSMTC.rate.getValue()));

            lastTradeTime = DateUtil.dateFormatter(lastTradedSMTC.exchTradeTime.getValue(), Constants.HHMMSS);
        }
        return summaryMap;
    }

}
