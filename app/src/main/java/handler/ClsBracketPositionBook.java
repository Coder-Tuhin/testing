package handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import Structure.Request.RC.StructOrderRequest;
import Structure.Response.RC.StructOCOPosnBook;
import Structure.Response.RC.StructOCOPosnDet;
import Structure.Response.RC.StructOrderBookResponse;
import connection.SendDataToRCServer;
import enums.eETS;
import enums.eMessageCode;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;

public class ClsBracketPositionBook {

    public LinkedHashMap<String, StructOCOPosnDet> m_hmBracketPosBook;
    private ArrayList<StructOCOPosnBook> mList;

    public LinkedHashMap<String, StructOrderRequest> m_hmBracketOrderBook;
    private ArrayList<StructOrderBookResponse> mListOrd;
    long requestTime = 0;

    public ClsBracketPositionBook(){

        m_hmBracketPosBook = new LinkedHashMap<>();
        mList = new ArrayList<>();

        m_hmBracketOrderBook = new LinkedHashMap<>();
        mListOrd = new ArrayList<>();
    }
    public void AddOrder(StructOrderBookResponse myOrderBook){

        mListOrd.add(myOrderBook);
        if (myOrderBook.downloadComplete.getValue() == 1){
            for (int j = 0;j<mListOrd.size();j++){
                StructOrderBookResponse myTradeBooktemp = mListOrd.get(j);
                StructOrderRequest[] singleTradeResp = myTradeBooktemp.orderRequest;
                for(int i = 0 ; i < singleTradeResp.length ; i++){
                    addOrder(singleTradeResp[i]);
                }
            }
            mListOrd.clear();
        }
    }

    public boolean addOrder(StructOrderRequest myOrderBook){
        try {
            if(myOrderBook.token.getValue() > 0 && myOrderBook.eets.getValue() == eETS.Entry.value){
                String key = myOrderBook.positionID.getValue()+"";
                m_hmBracketOrderBook.put(key,myOrderBook);
            }
        }
        catch (Exception ex){
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
        }
        return true;
    }
    public StructOrderRequest getOrderForPositionID(int positionID) {
        StructOrderRequest structOCOOrdBkDet = m_hmBracketOrderBook.get(positionID + "");
        return structOCOOrdBkDet;
    }

    public void AddOrder(StructOCOPosnBook myOrderBook){

        mList.add(myOrderBook);
        if (myOrderBook.downloadComplete.getValue() == 1){
            for (int j = 0;j<mList.size();j++){
                StructOCOPosnBook myTradeBooktemp = mList.get(j);
                StructOCOPosnDet[] singleTradeResp = myTradeBooktemp.posBkDetails;
                for(int i = 0 ; i < singleTradeResp.length ; i++){
                    addOrder(singleTradeResp[i]);
                }
            }
            mList.clear();
        }
    }

    public boolean addOrder(StructOCOPosnDet myOrderBook){
        try {
            if(myOrderBook.scripCode.getValue() > 0){
                String key = myOrderBook.positionID.getValue()+"";
                m_hmBracketPosBook.put(key,myOrderBook);
            }
        }
        catch (Exception ex){
            GlobalClass.showAlertDialog(Constants.ERR_MSG);
        }
        return true;
    }
    public ArrayList<String> getAllKeys(){
        return new ArrayList<>(m_hmBracketPosBook.keySet());
    }

    public boolean sendBracketPositionBookRequest(){

        Date date = new Date();
        long second = date.getTime();
        if(second - requestTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendOrderTradeReq(eMessageCode.BRACKET_POSITION_REPORT);
            requestTime = second;
            return true;
        }
        return false;
    }

    public ArrayList<StructOCOPosnDet> getAllNetPosition(String toString) {

        if(toString.equalsIgnoreCase("all")){
            return new ArrayList<>(m_hmBracketPosBook.values());
        }else {
            ArrayList<StructOCOPosnDet> allPos =  new ArrayList<>(m_hmBracketPosBook.values());
            ArrayList<StructOCOPosnDet> shortedPos = new ArrayList<>();
            for(int i=0;i<allPos.size();i++) {
                StructOCOPosnDet posnDet = allPos.get(i);
                if (toString.equalsIgnoreCase("open") && posnDet.getOpenQty()!= 0) {
                    shortedPos.add(posnDet);
                }
                else if(toString.equalsIgnoreCase("close") && posnDet.getOpenQty() == 0){
                    shortedPos.add(posnDet);
                }
            }
            return shortedPos;
        }
    }

    public int getNetPositionSize() {
        return m_hmBracketPosBook.size();
    }
    public String getTotalBookedPL(){
        double totalBPL  = 0.0;
        for (StructOCOPosnDet netRes : m_hmBracketPosBook.values()){
            totalBPL = totalBPL + netRes.bkpl.getValue();
        }
        return Formatter.formatter.format(totalBPL);
    }

    public String getTotalMTMPL(){
        double totalMPL  = 0.0;
        for (StructOCOPosnDet netRes : m_hmBracketPosBook.values()){
            totalMPL = totalMPL + netRes.mtmpl.getValue();
        }
        return Formatter.formatter.format(totalMPL);
    }
}
