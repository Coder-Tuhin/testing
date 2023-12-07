package handler;

import java.util.ArrayList;
import java.util.HashMap;

import Structure.Other.StructMobNetPosition;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import enums.eProductTypeITS;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;

/**
 * Created by Tapas on 1/5/2016.
 * modified by dpk on 30/03/2016.
 */
public class ClsNetPosn {
    private HashMap<String, StructMobNetPosition> m_hmNetPosn;
    public ArrayList<String> m_allScripCode;
    public ClsNetPosn(){
        m_hmNetPosn = new HashMap<>();
        m_allScripCode = new ArrayList<>();
    }

    public void AddTrade(StructTradeReportReplyRecord_Pointer singleTradeResp){
        String key;
        int order = singleTradeResp.orderType.getValue();
        String orderType= singleTradeResp.getOrderTypeStr();
        if(singleTradeResp.isIntraDelAllow()) {
            key = singleTradeResp.scripCode.getValue() + "#" + orderType;
        } else{
            key = singleTradeResp.scripCode.getValue()+"";
        }
        StructMobNetPosition netPosn = m_hmNetPosn.get(key);
        if(netPosn != null){
            netPosn.addTrade(singleTradeResp);
        }
        else {
            netPosn = new StructMobNetPosition();
            String symbol = singleTradeResp.getSymbol();
            netPosn.mktlot =  GlobalClass.mktDataHandler.getCurrencyStructure(symbol).mktLot.getValue();
            if(singleTradeResp.exch.getValue() == 'C' && (netPosn.mktlot <= 1)){
                netPosn.mktlot = 1000;
            }
            netPosn.key = key;
            netPosn.addTrade(singleTradeResp);
            m_hmNetPosn.put(key+"", netPosn);
            if(!m_allScripCode.contains(singleTradeResp.scripCode.getValue()+"")){
                m_allScripCode.add(singleTradeResp.scripCode.getValue()+"");
            }
            GlobalClass.mktDataHandler.sendMktWatchReq(singleTradeResp.scripCode.getValue());
        }
    }

    public StructMobNetPosition getPositionForkey(String key){
        return m_hmNetPosn.get(key);
    }

    public StructMobNetPosition getPositionForscripcode(int scripCode){

        StructMobNetPosition mobNet = null;
        String key  = scripCode + "";
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        key = scripCode+"#"+ eProductTypeITS.INTRADAY.name;
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        key = scripCode+"#"+ eProductTypeITS.DELIVERY.name;
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        key = scripCode+"#"+ eProductTypeITS.BRACKETORDER.name;
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        key = scripCode+"#"+ eProductTypeITS.COVERORDER.name;
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        return null;
    }
    public StructOpenPositionSummary getOpenPositionsForScripCode(int scripCode) {

        StructOpenPositionSummary posSummary = new StructOpenPositionSummary();
        posSummary.scripCode.setValue(scripCode);
        StructMobNetPosition mobNet = null;
        String key  = scripCode + "";
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            posSummary.scripName.setValue(mobNet.getFormatedScripName(false));
            posSummary.totalPosition.setValue(posSummary.totalPosition.getValue() + mobNet.getNetQty());
            posSummary.openPositions.add(mobNet);
        }else {
            key = scripCode + "#" + eProductTypeITS.INTRADAY.name;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                posSummary.scripName.setValue(mobNet.getFormatedScripName(false));
                posSummary.totalPosition.setValue(posSummary.totalPosition.getValue() + mobNet.getNetQty());
                posSummary.openPositions.add(mobNet);
            }
            key = scripCode + "#" + eProductTypeITS.DELIVERY.name;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                posSummary.scripName.setValue(mobNet.getFormatedScripName(false));
                posSummary.totalPosition.setValue(posSummary.totalPosition.getValue() + mobNet.getNetQty());
                posSummary.openPositions.add(mobNet);
            }
            key = scripCode + "#" + eProductTypeITS.BRACKETORDER.name;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                posSummary.scripName.setValue(mobNet.getFormatedScripName(false));
                posSummary.totalPosition.setValue(posSummary.totalPosition.getValue() + mobNet.getNetQty());
                posSummary.openPositions.add(mobNet);
            }
            key = scripCode + "#" + eProductTypeITS.COVERORDER.name;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                posSummary.scripName.setValue(mobNet.getFormatedScripName(false));
                posSummary.totalPosition.setValue(posSummary.totalPosition.getValue() + mobNet.getNetQty());
                posSummary.openPositions.add(mobNet);
            }
        }
        return posSummary;
    }

    public StructMobNetPosition getPositionForScripcodeDerivativeNetObligation(int scripCode){
        StructMobNetPosition mobNet = null;
        String key  = scripCode + "";
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        key = scripCode+"#"+ Constants.delivery ;
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            return mobNet;
        }
        return null;
    }

    public void clearNetPosition(){
        try{
            m_hmNetPosn.clear();
            m_allScripCode.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //BE-RTINFRA|Buy 1 @3.51|openPL|BookedPL
    //need method that returns an arraylist of arraylist of 3 elements - Symbol, Order & status
    public ArrayList<StructMobNetPosition> getAllNetPosition() {
        ArrayList<StructMobNetPosition> alNets = new ArrayList();
        for (StructMobNetPosition netRes : m_hmNetPosn.values()) {
            if (!(netRes.getBfdQty() == 0 && netRes.buyQty== 0 && netRes.sellQty== 0)) {
                alNets.add(netRes);
            }
        }
        return alNets;
    }

    public ArrayList<StructMobNetPosition> getAllNetPosition(String strFilter) {
        if(strFilter.equalsIgnoreCase("all")){
            return getAllNetPosition();
        }
        ArrayList<StructMobNetPosition> alNets = new ArrayList();
        for (StructMobNetPosition netRes : m_hmNetPosn.values()){
            if(strFilter.equalsIgnoreCase("open")) {
                if (netRes.getNetQty() != 0) {
                    alNets.add(netRes);
                }
            }
            else if(strFilter.equalsIgnoreCase("squared")){
                if (netRes.getNetQty() == 0) {
                    alNets.add(netRes);
                }
            }
            else if(strFilter.equalsIgnoreCase("intraday")){
                if (netRes.orderType == eProductTypeITS.INTRADAY.value) {
                    alNets.add(netRes);
                }
            }
            else if(strFilter.equalsIgnoreCase("delivery")){
                if (netRes.orderType == eProductTypeITS.DELIVERY.value) {
                    alNets.add(netRes);
                }
            }
        }
        return alNets;
    }

    public String getTotalBookedPL(){
        double totalBPL  = 0.0;
        for (StructMobNetPosition netRes : m_hmNetPosn.values()){
            totalBPL = totalBPL + netRes.getBookedPL();
        }
        return Formatter.formatter.format(totalBPL);
    }

    public String getTotalMTMPL(){
        double totalMPL  = 0.0;
        for (StructMobNetPosition netRes : m_hmNetPosn.values()){
            totalMPL = totalMPL + netRes.getMTM();
        }
        return Formatter.formatter.format(totalMPL);
    }

    public String getTotalBuyValue(){
        double totalBuyValue  = 0.0;
        for (StructMobNetPosition netRes : m_hmNetPosn.values()){
            totalBuyValue = totalBuyValue + (netRes.getTotBuyValue()*netRes.mktlot);
        }
        return Formatter.formatter.format(totalBuyValue);
    }

    public String getTotalSellValue(){
        double totalSellValue  = 0.0;
        for (StructMobNetPosition netRes : m_hmNetPosn.values()){
            totalSellValue = totalSellValue + (netRes.getTotSellValue()*netRes.mktlot);
        }
        return Formatter.formatter.format(totalSellValue);
    }

    public int getNetPositionSize(){
        return m_hmNetPosn.size();
    }

    public HashMap<Integer,StructNetPositionSummary> getNetSummary(){
        HashMap <Integer,StructNetPositionSummary>summaryMap = new HashMap();
        Object keys[] = m_hmNetPosn.keySet().toArray();

        for(Object key:keys){
            StructMobNetPosition np = m_hmNetPosn.get(key);

            int open,close;
            open = close = 0;
            open = np.getNetQty() != 0 && (np.buyQty > 0 || np.sellQty > 0 ) ? 1 : 0 ;
            close = np.getNetQty() == 0 && (np.buyQty > 0 || np.sellQty > 0 ) ? 1 : 0;
            StructNetPositionSummary npSummary = summaryMap.get(np.getExchange());
            if(npSummary != null){
                npSummary.open.setValue(npSummary.open.getValue() + open);
                npSummary.closed.setValue(npSummary.closed.getValue() + close);
                npSummary.openPl.setValue(npSummary.openPl.getValue() + np.getBookedPL());
                npSummary.mtm.setValue(npSummary.mtm.getValue() + np.getMTM());
            }
            else{
                npSummary = new StructNetPositionSummary();
                npSummary.open.setValue(open);
                npSummary.closed.setValue(close);
                npSummary.openPl.setValue(np.getBookedPL());
                npSummary.mtm.setValue(np.getMTM());
                npSummary.segment.setValue(np.getExchange());
                summaryMap.put(np.getExchange(),npSummary);
            }
        }
        return summaryMap;
    }

    public boolean isConatinScripCode(int value) {
        return m_allScripCode.contains(value+"");
    }

    public void updateLastRate(StaticLiteMktWatch mktWatch) {
        int scripCode = mktWatch.getToken();
        StructMobNetPosition mobNet = null;
        String key  = scripCode + "";
        mobNet = m_hmNetPosn.get(key);
        if(mobNet != null){
            mobNet.updateLastRate(mktWatch.getLastRate());
        }
        else {
            key = scripCode + "#" + Constants.intraday;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                mobNet.updateLastRate(mktWatch.getLastRate());
            }
            key = scripCode + "#" + Constants.delivery;
            mobNet = m_hmNetPosn.get(key);
            if (mobNet != null) {
                mobNet.updateLastRate(mktWatch.getLastRate());
            }
        }
    }
}
