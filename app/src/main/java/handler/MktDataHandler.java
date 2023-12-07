package handler;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.HashMap;

import Structure.Response.BC.FnoNSE_TradeExecution;
import Structure.Response.BC.LiteIndicesWatch;
import Structure.Response.BC.LiteMktDepth;
import Structure.Response.BC.LiteMktWatch;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.BC.StaticWatch;
import Structure.Response.BC.StructADST;
import Structure.Response.BC.StructCurrSymbol;
import Structure.Response.BC.StructCurrSymbolList;
import Structure.Response.Scrip.ScripDetail;
import connection.SendDataToBCServer;

/**
 * Created by Tapas on 19/08/16.
 */
public class MktDataHandler {

    public HashMap<Integer, LiteIndicesWatch> hm_previndicesData;
    public HashMap<Integer, LiteMktWatch> hm_prevmkt5001Data;
    public HashMap<Integer, LiteIndicesWatch> hm_indicesData;
    public HashMap<Integer, StaticLiteMktWatch> hm_mkt5001Data;
    public HashMap<Integer, LiteMktDepth> hm_mktDepthData;
    public HashMap<Integer, ScripDetail> hm_scripDetail;
    private HashMap<Integer,StructADST> hm_adst;

    private HashMap<String,StructCurrSymbol> currSymbolList;
    public MktDataHandler() {

        hm_previndicesData = new HashMap<>();
        hm_prevmkt5001Data = new HashMap<>();

        hm_mkt5001Data = new HashMap<>();
        hm_mktDepthData = new HashMap<>();
        hm_indicesData = new HashMap<>();
        hm_scripDetail = new HashMap<>();
        hm_adst = new HashMap<>();
        currSymbolList = new HashMap<>();
    }

    public  void handleCurrSumbolList(StructCurrSymbolList currList){
        if(currList != null){
            currSymbolList.clear();
            StructCurrSymbol[] allSymbol =  currList.currSymbol;
            for(StructCurrSymbol symbol : allSymbol){
                currSymbolList.put(symbol.symbol.getValue(),symbol);
            }
        }
        VenturaApplication.getPreference().setCurrencySymbol(currSymbolList);
    }

    public void setADSTData(StructADST adst) {
        hm_adst.put(adst.segment.getValue(), adst);
    }

    public StructADST getADSTData(int segment) {
        return hm_adst.get(segment);
    }

    public void setScripDetailData(ScripDetail scripDetail) {
        hm_scripDetail.put(scripDetail.scripCode.getValue(), scripDetail);
    }

    public ScripDetail getScripDetailData(int scripCode) {
        ScripDetail scripDetail =  hm_scripDetail.get(scripCode);
        if(scripDetail == null){
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.sendScripDetailReq(scripCode);
        }
        return scripDetail;
    }

    public void setMktIndicesData(LiteIndicesWatch indicesData) {
        int indicesCode = indicesData.token.getValue();
        LiteIndicesWatch prevIndices = hm_indicesData.get(indicesCode);
        setPrevMktIndicesData(prevIndices);
        hm_indicesData.put(indicesCode, indicesData);
    }

    private void setPrevMktIndicesData(LiteIndicesWatch indicesData) {
        if (indicesData != null) {
            hm_previndicesData.put(indicesData.token.getValue(), indicesData);
        }
    }


    public LiteIndicesWatch getPrevIndicesData(int indexCode) {
        LiteIndicesWatch index = hm_previndicesData.get(indexCode);
        if(index == null){
            index = new LiteIndicesWatch();
        }
        return index;
    }

    public LiteIndicesWatch getIndicesData(Object indexCode) {
        return hm_indicesData.get(Integer.parseInt(indexCode.toString()));
    }
    public Object[] getIndexCodes() {
        return hm_indicesData.keySet().toArray();
    }
    public void setMktDepthData(LiteMktDepth mktDepth) {
        hm_mktDepthData.put(mktDepth.token.getValue(), mktDepth);
    }


    public LiteMktDepth getMktDepthData(int scripCode) {

        LiteMktDepth mktDepth =  hm_mktDepthData.get(scripCode);
       // if(mktDepth == null){
            //SendDataToBCServer sendDataToServer = new SendDataToBCServer();
           // sendDataToServer.sendMarketDepthReq(scripCode);
           // mktDepth = new StructMktDepth();
           // mktDepth.token.setValue(scripCode);
      //  }
        return mktDepth;
    }

    public StaticLiteMktWatch setMkt5001Data(LiteMktWatch mkt5001Data) {
        int token = mkt5001Data.token.getValue();
        StaticLiteMktWatch prevMktData = hm_mkt5001Data.get(token);
        if(prevMktData == null){
            prevMktData = new StaticLiteMktWatch(token,mkt5001Data.segment.getValue());
        }
        setPrevMkt5001Data(prevMktData.getLw());

        prevMktData.setLw(mkt5001Data);
        hm_mkt5001Data.put(token, prevMktData);
        return prevMktData;
    }
    public StaticLiteMktWatch setMkt5001Data(StaticWatch mkt5001Data) {
        int token = mkt5001Data.token.getValue();
        StaticLiteMktWatch prevMktData = hm_mkt5001Data.get(token);
        if(prevMktData == null){
            prevMktData = new StaticLiteMktWatch(token,mkt5001Data.segment.getValue());
        }
        prevMktData.setSw(mkt5001Data);
        hm_mkt5001Data.put(token, prevMktData);
        return prevMktData;
    }
    public void setTradeExecutionRangeData(FnoNSE_TradeExecution mkt5001Data) {
        int token = mkt5001Data.token.getValue();
        StaticLiteMktWatch prevMktData = hm_mkt5001Data.get(token);
        if(prevMktData != null){
            prevMktData.setTradeExecutionRange(mkt5001Data);
            hm_mkt5001Data.put(token, prevMktData);
        }
    }
    private void setPrevMkt5001Data(LiteMktWatch mkt5001Data) {
        if (mkt5001Data != null) {
            hm_prevmkt5001Data.put(mkt5001Data.token.getValue(), mkt5001Data);
        }
    }

    public StaticLiteMktWatch getMkt5001Data(int scripCode,boolean isReqSend) {
        StaticLiteMktWatch structMktWatch1 = hm_mkt5001Data.get(scripCode);
        if(structMktWatch1 != null){
            return  structMktWatch1;
        } else {
            StaticLiteMktWatch structMktWatch = new StaticLiteMktWatch(scripCode,-1);
            hm_mkt5001Data.put(scripCode,structMktWatch);
            if(isReqSend) {
                sendMktWatchReq(scripCode);
            }
            return structMktWatch;
        }
    }

    public StaticLiteMktWatch getMkt5001DataforDepth(int scripCode) {
        StaticLiteMktWatch structMktWatch1 = hm_mkt5001Data.get(scripCode);
        return  structMktWatch1;
    }


    public void sendMktWatchReq(int scripCode){
        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
        sendDataToServer.sendMarketWatchReq(scripCode);
    }

    public LiteMktWatch getPrevMkt5001Data(int scripCode) {
        LiteMktWatch mktWatch = hm_prevmkt5001Data.get(scripCode);
        if (mktWatch == null) {
            mktWatch = new LiteMktWatch();
            mktWatch.token.setValue(scripCode);
        }
        return mktWatch;
    }

    public ArrayList<String> getCurrSymbolList() {
        if(currSymbolList.size()<=0){
            currSymbolList = VenturaApplication.getPreference().getCurrSymbolList();
        }
        return new ArrayList<String>(currSymbolList.keySet());
    }

    public StructCurrSymbol getCurrencyStructure(String symbol){
        if(currSymbolList.size()<=0){
            currSymbolList = VenturaApplication.getPreference().getCurrSymbolList();
        }
        if (currSymbolList.containsKey(symbol)){
            return currSymbolList.get(symbol);
        }
        return new StructCurrSymbol();
    }
    public float getRateForMarketOrder(int scripCode, char BS) {

        StaticLiteMktWatch myMktWatch = getMkt5001Data(scripCode,false);//new StructMktWatchCom(sendData, sendData.length);
        float rate = myMktWatch.getLastRateForPlaceOrder();
        if (rate <= 0) {
            rate = myMktWatch.getPrevCloseForPlaceOrder();
        }
        if (rate <= 0) {
            if (BS == 'B') {
                rate = myMktWatch.getAskRateForPlaceOrder();
            } else {
                rate = myMktWatch.getBidRateForPlaceOrder();
            }
        }
        if (rate <= 0) {
            if (BS == 'B') {
                rate = myMktWatch.getUpperCircuitForPlaceOrder();
            } else {
                rate = myMktWatch.getLowerCircuitForPlaceOrder();
            }
        }
        return rate;
    }
}
