package handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import Structure.Response.RC.StructOCOOrdBkDet;
import Structure.Response.RC.StructOCOOrderBook;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeSummary_Pointer;
import connection.SendDataToRCServer;
import enums.eExch;
import enums.eMessageCode;
import enums.eOrderbookSpinnerItems;
import fragments.reports.OrderbookView;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;

public class ClsBracketOrderBook {

    public LinkedHashMap<String, StructOCOOrdBkDet> m_hmBracketOrderBook;
    private ArrayList<StructOCOOrderBook> mList;
    long requestTime = 0;

    public ClsBracketOrderBook(){
        m_hmBracketOrderBook = new LinkedHashMap<>();
        mList = new ArrayList<>();
    }

    public void AddOrder(StructOCOOrderBook myOrderBook){
        mList.add(myOrderBook);
        if (myOrderBook.downloadComplete.getValue() == 1){
            //GlobalClass.clsNetPosn.clearNetPosition();
            for (int j = 0;j<mList.size();j++){
                StructOCOOrderBook myTradeBooktemp = mList.get(j);
                StructOCOOrdBkDet[] singleTradeResp = myTradeBooktemp.orderBkDetails;
                for(int i = 0 ; i < singleTradeResp.length ; i++){
                    addOrder(singleTradeResp[i]);
                }
            }
            mList.clear();
        }
    }

    public boolean addOrder(StructOCOOrdBkDet myOrderBook){
        try {
            if(myOrderBook.scripCode.getValue() > 0){
                String key = myOrderBook.positionID.getValue()+"";
                m_hmBracketOrderBook.put(key,myOrderBook);
            }
        }
        catch (Exception ex){
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
        }
        return true;
    }
    public ArrayList<String> getAllKeys(){
        return new ArrayList<>(m_hmBracketOrderBook.keySet());
    }

    public TreeMap<String, StructOCOOrdBkDet> getTreeMap(String str, int scripCode){
        TreeMap<String, StructOCOOrdBkDet> ordBookHM = new TreeMap<>();
        ArrayList<String> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            String key = allKey.get(i);
            StructOCOOrdBkDet myOrderBook = m_hmBracketOrderBook.get(key);
            if (str.equalsIgnoreCase(eOrderbookSpinnerItems.ALL.name)){
                ordBookHM.put(myOrderBook.positionID.getValue()+"", myOrderBook);
            }else if (str.equalsIgnoreCase(eOrderbookSpinnerItems.PENDING.name) && myOrderBook.qty.getValue() > 0){
                if (ObjectHolder.pendingScripCode > 0){
                    if (myOrderBook.scripCode.getValue() == ObjectHolder.pendingScripCode){
                        ordBookHM.put(myOrderBook.positionID.getValue()+"", myOrderBook);
                    }
                }else {
                    ordBookHM.put(myOrderBook.positionID.getValue()+"", myOrderBook);
                }
            }

        }
        return ordBookHM;
    }

    public TreeMap<String, StructOCOOrdBkDet> getTreeMap(String str){
        TreeMap<String, StructOCOOrdBkDet> ordBookHM = new TreeMap<>();
        ArrayList<String> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            String key = allKey.get(i);
            StructOCOOrdBkDet myOrderBook = m_hmBracketOrderBook.get(key);
            if (str.equalsIgnoreCase(eOrderbookSpinnerItems.ALL.name)){
                ordBookHM.put(myOrderBook.positionID.getValue()+"", myOrderBook);
            }else if (str.equalsIgnoreCase(eOrderbookSpinnerItems.PENDING.name) && myOrderBook.qty.getValue() > 0){
                ordBookHM.put(myOrderBook.positionID.getValue()+"", myOrderBook);
            }/*
            else if(str.equalsIgnoreCase(OrderbookView.eOrderbookSpinnerItems.OTHERS.name)){
                if(!myOrderBook.getFinalStatus().equalsIgnoreCase(OrderbookView.eOrderbookSpinnerItems.CANCELLED.name)
                        && !myOrderBook.getFinalStatus().equalsIgnoreCase(OrderbookView.eOrderbookSpinnerItems.FULL_EXECUTED.name)
                        && (myOrderBook.finaPendingQty <= 0)){
                    ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
                }
            }
            else if(str.equalsIgnoreCase(myOrderBook.getFinalStatus())){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }*/
        }
        return ordBookHM;
    }

    public boolean sendBracketOrderBookRequest(){

        Date date = new Date();
        long second = date.getTime();
        if(second - requestTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendOrderTradeReq(eMessageCode.BRACKET_ORDER_REPORT);
            requestTime = second;
            return true;
        }
        return false;
    }

    public StructOCOOrdBkDet getOrderForPositionID(int positionID) {
        StructOCOOrdBkDet structOCOOrdBkDet = m_hmBracketOrderBook.get(positionID + "");
        return structOCOOrdBkDet;
    }
}
