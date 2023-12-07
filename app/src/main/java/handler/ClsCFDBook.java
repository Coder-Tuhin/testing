package handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeSummary_Pointer;
import connection.SendDataToRCServer;
import enums.eExch;
import enums.eExchSegment;
import enums.eMessageCode;
import utils.Constants;
import utils.GlobalClass;

public class ClsCFDBook {

    public HashMap<Integer, StructTradeReportReplyRecord_Pointer> m_hmTradeBook;
    public HashMap<Integer, StructHoldingsReportReplyRecord_Pointer> m_hmHoldingFO;

    long requestTime = 0;
    private ArrayList<StructTradeSummary_Pointer> mList;
    public ClsCFDBook(){
        m_hmTradeBook = new HashMap<>();
        m_hmHoldingFO = new HashMap<>();
        mList = new ArrayList<>();
    }

    public void AddTrade(StructTradeSummary_Pointer myTradeBook){
        mList.add(myTradeBook);
        if (myTradeBook.complete.getValue() == 1){
            ArrayList<String> currSymbolList = GlobalClass.mktDataHandler.getCurrSymbolList();
            m_hmTradeBook.clear();
            m_hmHoldingFO.clear();
            for (int j = 0;j<mList.size();j++){
                StructTradeSummary_Pointer myTradeBooktemp = mList.get(j);
                StructTradeReportReplyRecord_Pointer[] singleTradeResp = myTradeBooktemp.tradeRequest;
                for(int i = 0 ; i < myTradeBooktemp.cNoOfRecs.getValue() ; i++){
                    StructTradeReportReplyRecord_Pointer sTrade = singleTradeResp[i];
                    String sName = sTrade.getSymbol();
                    if (currSymbolList.contains(sName)) {
                        sTrade.exchange = eExchSegment.NSECURR.value;
                        sTrade.mktLot = GlobalClass.mktDataHandler.getCurrencyStructure(sName).mktLot.getValue();
                    }else{
                        sTrade.exchange = sTrade.getExchange();
                        sTrade.mktLot = 1;
                    }
                    addTradeBook(sTrade);
                }
            }
            mList.clear();
        }
    }

    private boolean addTradeBook(StructTradeReportReplyRecord_Pointer myTradeBook){
        try {
            StructTradeReportReplyRecord_Pointer isFresh = m_hmTradeBook.put(myTradeBook.exchTradeID.getValue(), myTradeBook);
            if(myTradeBook.getExchange() == eExch.FNO.value || myTradeBook.getExchange() == eExch.NSECURR.value) {
                StructHoldingsReportReplyRecord_Pointer hp = m_hmHoldingFO.get(myTradeBook.scripCode.getValue());
                if (hp == null) {
                    hp = new StructHoldingsReportReplyRecord_Pointer(500);
                    m_hmHoldingFO.put(myTradeBook.scripCode.getValue(), hp);
                }
                hp.updateFromTradeBook(myTradeBook);
            }
            return (isFresh == null);
        }
        catch (Exception ex){
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
        }
        return true;
    }
    public ArrayList<Integer> getAllKeys(){
        return new ArrayList<>(m_hmTradeBook.keySet());
    }

    public boolean sendCFDBookRequest(){
        Date date = new Date();
        long second = date.getTime();
        if(second - requestTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendOrderTradeReq(eMessageCode.CFD_BOOK_RESP);
            requestTime = second;
            return true;
        }
        return false;
    }

    public ArrayList<StructHoldingsReportReplyRecord_Pointer> getHoldingFNO() {
        return new ArrayList<>(m_hmHoldingFO.values());
    }
    public boolean isConatinScripCode(int value) {
        return m_hmHoldingFO.get(value) != null;
    }
    public void updateRate(StaticLiteMktWatch mktWatch) {

        StructHoldingsReportReplyRecord_Pointer holdingRow = m_hmHoldingFO.get(mktWatch.getToken());
        if(holdingRow != null) {
            if (mktWatch.getSegment() == eExch.BSE.value) {
                holdingRow.setBseRate(mktWatch.getLastRate());
            } else {
                holdingRow.setNseRate(mktWatch.getLastRate());
            }
        }
    }
}
